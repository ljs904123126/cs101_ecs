package com.yoyoyo666.cs101.ecs.vm;

import java.util.concurrent.atomic.AtomicInteger;

// 栈帧内存模型
// +────────+────────────────+───────────────────+
// | ARG->  |  argument 0    | 被调用函数 参数      |
// +────────+────────────────+───────────────────+
// |        |  argument 1    |                   |
// +────────+────────────────+───────────────────+
// |        | ...            |                   |
// +────────+────────────────+───────────────────+
// |        |  argument n-1  |                   |
// +────────+────────────────+───────────────────+
// |        | return address | 调用返回地址         |
// +────────+────────────────+───────────────────+
// |        | save LCL       | 保存调用者 LCL      |
// +────────+────────────────+───────────────────+
// |        | save ARG       | 保存调用者 ARG      |
// +────────+────────────────+───────────────────+
// |        | save THIS      | 保存调用者THIS      |
// +────────+────────────────+───────────────────+
// |        | save THAT      | 保存调用者THAT      |
// +────────+────────────────+───────────────────+
// | LCL->  | local 0        | 被调用函数本地变量    | 被调函数初始化
// +────────+────────────────+───────────────────+
// |        | local 1        |                   |
// +────────+────────────────+───────────────────+
// |        | ......         |                   |
// +────────+────────────────+───────────────────+
// |        | local k-1      |                   |
// +────────+────────────────+───────────────────+
// | SP->   |                | 栈顶               |
// +────────+────────────────+───────────────────+


public class VMCodeWriter08 extends VMCodeWriter {

    private static AtomicInteger funcIndex = new AtomicInteger(-1);

    private int getFuncIndex() {
        return funcIndex.incrementAndGet();
    }

    public VMCodeWriter08(String inputPath, String outPath) {
        super(inputPath, outPath);
    }

    protected void writeInit() {
        writeACommand("256");
        writeCCommand("D", "A");
        writeACommand(VMSegmentType.S_SP.getHackCode());
        writeCCommand("M", "D");
        wirteComment("call Sys.init 0");
        writeCall("Sys.init", 0);
    }


    protected void writeLabel(String label) {
        label = this.getFileName() + "_" + label;
        writeHackLabel(label);
    }

    protected void writeGoto(String label) {
        label = this.getFileName() + "_" + label;
        this.writeACommand(label);
        this.writeCCommand(null, "0", "JMP");
    }

    /**
     * 运算布尔结果栈顶弹出，如果该值非零，跳转至label
     *
     * @param label 跳转符号
     */
    protected void writeIf(String label) {
        label = this.getFileName() + "_" + label;
        stackToComp("D");
        this.writeACommand(label);
        this.writeCCommand(null, "D", "JNE");
    }

    /**
     * 调用函数
     *
     * @param functionName 函数名称
     * @param numArgs      已经压入栈中的参数个数
     */
    protected void writeCall(String functionName, int numArgs) {
        String returnAddress = functionName + "_RETURN_ADDR_" + getFuncIndex();
        functionName = getFunctionName(functionName);
        //push return-address
        writeACommand(returnAddress);
        writeCCommand("D", "A");
        compToStack("D");
        //store lcl
        storeSegmentToStack(VMSegmentType.S_LCL.getHackCode());
        //store arg
        storeSegmentToStack(VMSegmentType.S_ARG.getHackCode());
        //store this
        storeSegmentToStack(VMSegmentType.S_THIS.getHackCode());
        //store that
        storeSegmentToStack(VMSegmentType.S_THAT.getHackCode());

        //reset arg pointer *arg=sp-m-5
        writeACommand(VMSegmentType.S_SP.getHackCode());
        writeCCommand("D", "M");
        writeACommand(String.valueOf(numArgs));
        writeCCommand("D", "D-A");
        writeACommand("5");
        writeCCommand("D", "D-A");
        writeACommand(VMSegmentType.S_ARG.getHackCode());
        writeCCommand("M", "D");

        //reset lcl lcl->*sp
        writeACommand(VMSegmentType.S_SP.getHackCode());
        writeCCommand("D", "M");
        writeACommand(VMSegmentType.S_LCL.getHackCode());
        writeCCommand("M", "D");
        //goto
        writeACommand(functionName);
        writeCCommand(null, "0", "JMP");
        //add return-address
        writeHackLabel(returnAddress);

    }

    protected void writeReturn() {
        //FRAME=LCL 临时变量存FRAME
        writeACommand(VMSegmentType.S_LCL.getHackCode());
        writeCCommand("D", "M");
        writeACommand(VMSegmentType.S_R13.getHackCode());
        writeCCommand("M", "D");

        //RET = *(FRAME-5) 将返回地址放入临时变量R14
        writeACommand("5");
        writeCCommand("A", "D-A");
        writeCCommand("D", "M");
        writeACommand(VMSegmentType.S_R14.getHackCode());
        writeCCommand("M", "D");

        //*ARG=pop() 将返回值放入arg 0,也就是返回时的栈顶，在调用着中压入的参数清空
        this.stackToComp("D");
        writeACommand(VMSegmentType.S_ARG.getHackCode());
        writeCCommand("A", "M");
        writeCCommand("M", "D");

        //SP=ARG+1 还原SP
        writeACommand(VMSegmentType.S_ARG.getHackCode());
        writeCCommand("D", "M+1");
        writeACommand(VMSegmentType.S_SP.getHackCode());
        writeCCommand("M", "D");

        //THAT = *(FRAME-1)
        //THIS = *(FRAME-2)
        //ARG = *(FRAME-3)
        //LCL = *(FRAME-4)
        String[] p = {VMSegmentType.S_THAT.getHackCode(),
                VMSegmentType.S_THIS.getHackCode(),
                VMSegmentType.S_ARG.getHackCode(),
                VMSegmentType.S_LCL.getHackCode()};
        for (String s : p) {
            writeACommand(VMSegmentType.S_R13.getHackCode());
            writeCCommand("D", "M-1");
            writeCCommand("AM", "D");
            writeCCommand("D", "M");
            writeACommand(s);
            writeCCommand("M", "D");
        }
        //goto RET 跳回调用者
        writeACommand(VMSegmentType.S_R14.getHackCode());
        writeCCommand("A", "M");
        writeCCommand(null, "0", "JMP");


    }

    /**
     * 函数定义
     *
     * @param functionName 函数名称
     * @param numLocals    函数局部变量个数
     */
    protected void writeFunction(String functionName, int numLocals) {
        functionName = getFunctionName(functionName);
        writeHackLabel(functionName);
        if(numLocals > 0){
            writeACommand("0");
            writeCCommand("D", "A");
            for (int locals = numLocals; locals > 0; locals--) {
                compToStack("D");
            }
        }

    }

    private String getFunctionName(String functionName) {
//        return this.getFileName() + "_funcName_" + functionName;
        return functionName;
    }

    private void writeHackLabel(String functionName) {
        this.getHackCodeList().add("(" + functionName + ")");
    }


    /**
     * 将虚拟内存的地址保存到栈中
     *
     * @param segment 虚拟内存段
     */
    private void storeSegmentToStack(String segment) {
        this.writeACommand(segment);
        writeCCommand("D", "M");
        compToStack("D");
    }

    @Override
    public void start() {

        if (vmParser.getDirectory()) {
            writeInit();
        }

        while (vmParser.hasMoreCommand()) {
            vmParser.advance();
            VMCommandType currentCommandType = vmParser.getCurrentCommandType();
            if (null == currentCommandType) {
                throw new RuntimeException("unkown Command type:" + vmParser.getCurrentCommandStr());
            }
            switch (currentCommandType) {
                case C_ARITHMETIC:
                    wirteComment(vmParser.getCurrentCommandStr());
                    writeArithmetic(vmParser.arg1());
                    break;
                case C_PUSH:
                    wirteComment(vmParser.getCurrentCommandStr());
                    writePushPop(VMCommandType.C_PUSH, vmParser.arg1(), vmParser.arg2());
                    break;
                case C_POP:
                    wirteComment(vmParser.getCurrentCommandStr());
                    writePushPop(VMCommandType.C_POP, vmParser.arg1(), vmParser.arg2());
                    break;
                case C_LABEL:
                    wirteComment(vmParser.getCurrentCommandStr());
                    writeLabel(vmParser.arg1());
                    break;
                case C_GOTO:
                    wirteComment(vmParser.getCurrentCommandStr());
                    writeGoto(vmParser.arg1());
                    break;
                case C_IF:
                    wirteComment(vmParser.getCurrentCommandStr());
                    writeIf(vmParser.arg1());
                    break;
                case C_FUNCTION:
                    wirteComment(vmParser.getCurrentCommandStr());
                    writeFunction(vmParser.arg1(), Integer.parseInt(vmParser.arg2()));
                    break;
                case C_RETURN:
                    wirteComment(vmParser.getCurrentCommandStr());
                    writeReturn();
                    break;
                case C_CALL:
                    wirteComment(vmParser.getCurrentCommandStr());
                    writeCall(vmParser.arg1(), Integer.parseInt(vmParser.arg2()));
                    break;
            }
        }
        close();
    }

}

