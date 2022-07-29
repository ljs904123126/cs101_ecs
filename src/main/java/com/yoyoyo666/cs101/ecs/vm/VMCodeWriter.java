package com.yoyoyo666.cs101.ecs.vm;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class VMCodeWriter {

    private String outPath;
    private String inputPath;
    private String fileName;
    protected VMParser vmParser;

    private int symbolIndex = 0;

    private List<String> hackCodeList;

    public VMCodeWriter(String inputPath, String outPath) {
        this.outPath = outPath;
        this.inputPath = inputPath;
        vmParser = new VMParser(inputPath);
        this.fileName = vmParser.getFileName();
        init();
    }

    private void init() {
        hackCodeList = new LinkedList<>();
        symbolIndex = 0;
    }


    /**
     * 计算
     *
     * @param command 命令名称
     */
    protected void writeArithmetic(String command) {

        VMArithmeticType typeByVMCode = VMArithmeticType.getTypeByVMCode(command);
        switch (typeByVMCode) {
            case ADD:
//                @SP
//                AM=M-1
//                D=M
                stackToComp("D");
                //A=A-1
                writeCCommand("A", "A-1");
                //M=M+D
                writeCCommand("M", "M+D");
                break;
            case SUB:
                stackToComp("D");
                writeCCommand("A", "A-1");
                writeCCommand("M", "M-D");
                break;
            case NEG:
                writeACommand(VMSegmentType.S_SP.getHackCode());
                writeCCommand("A", "M-1");
                writeCCommand("M", "-M");
                break;
            case OR:
                stackToComp("D");
                writeCCommand("A", "A-1");
                writeCCommand("M", "M|D");
                break;
            case NOT:
                writeACommand(VMSegmentType.S_SP.getHackCode());
                writeCCommand("A", "M-1");
                writeCCommand("M", "!M");
                break;
            case AND:
                stackToComp("D");
                writeCCommand("A", "A-1");
                writeCCommand("M", "M&D");
                break;
            case EQ:
                compareArithmetic("JEQ");
                break;
            case GT:
                compareArithmetic("JGT");
                break;
            case LT:
                compareArithmetic("JLT");
                break;
            default:
                throw new RuntimeException("unkwon arithmetic operator");
        }

    }


    /**
     * 比较运算
     *
     * @param operator 比较运算符
     */
    protected void compareArithmetic(String operator) {
        String uniqueSymbol = getUniqueSymbol();
        String trueSymbol = "TURE" + uniqueSymbol;
        String endSymbol = "END" + uniqueSymbol;
        ////load sp
        //@sp
        writeACommand(VMSegmentType.S_SP.getHackCode());
        ////load sp - 1 to AM
        //AM = M - 1
        writeCCommand("AM", "M-1");
        //// D =  *(*sp-1)
        //D = M
        writeCCommand("D", "M");
        //// A = *sp - 1 -1
        //A = A -1
        writeCCommand("A", "A-1");
        //// A = *(*sp - 1 -1)
        //A = M
        writeCCommand("A", "M");
        //D = A - D
        writeCCommand("D", "A-D");
        ////(D > 0) jump true
        //@TRUE
        writeACommand(trueSymbol);
        //D;JGT
        writeCCommand(null, "D", operator);
        //@0
        writeACommand("0");
        //D=A
        writeCCommand("D", "A");
        //@SP
        writeACommand(VMSegmentType.S_SP.getHackCode());
        //A = M - 1
        writeCCommand("A", "M-1");
        //M = D
        writeCCommand("M", "D");
        ////JUMP END
        //@END
        writeACommand(endSymbol);
        //0;JUMP
        writeCCommand(null, "0", "JMP");
        //(@TRUE)
        hackCodeList.add("(" + trueSymbol + ")");
        // don't support load -1
//        //@-1
//        writeACommand("-1");
//        //D=A
//        writeCCommand("D", "A");
        //@SP
        writeACommand(VMSegmentType.S_SP.getHackCode());
        //A = M - 1
        writeCCommand("A", "M-1");
        //M = D
        writeCCommand("M", "-1");
        //(END)
        hackCodeList.add("(" + endSymbol + ")");
    }

    protected String getUniqueSymbol() {
        String sb = "_" + this.fileName + "_" + symbolIndex;
        symbolIndex++;
        return sb.toUpperCase();
    }

    protected void writePushPop(VMCommandType command, String segment, String index) {
        int _index = Integer.parseInt(index);
//        VMCommandType commandType = VMCommandType.getCommandType(command);
        if (VMCommandType.C_PUSH == command) {
            VMSegmentType typeByVMCode = VMSegmentType.getTypeByVMCode(segment);

            if (typeByVMCode.equals(VMSegmentType.S_ARG)
                    || typeByVMCode.equals(VMSegmentType.S_LCL)
                    || typeByVMCode.equals(VMSegmentType.S_THIS)
                    || typeByVMCode.equals(VMSegmentType.S_THAT)
            ) {
                pushConstantSegment(index, typeByVMCode.getHackCode());
                return;
            }
            switch (typeByVMCode) {
                case S_PTR:
                    String seg = "";
                    if (_index == 0) {
                        seg = VMSegmentType.S_THIS.getHackCode();
//                        pushConstantSegment("0", VMSegmentType.S_THIS.getHackCode());
                    } else if (_index == 1) {
                        seg = VMSegmentType.S_THAT.getHackCode();
//                        pushConstantSegment("0", VMSegmentType.S_THAT.getHackCode());
                    }
                    writeACommand(seg);
                    writeCCommand("D", "M");
                    compToStack("D");
                    break;
                case S_TEMP:
                    writeACommand(String.valueOf(5 + _index));
                    writeCCommand("D", "M");
                    loadSP();
                    writeCCommand("M", "D");
                    writeIncreaseSP();
                    break;
                case S_CONST:
                    writeACommand(index);
                    writeCCommand("D", "A");
                    compToStack("D");
                    break;
                case S_STATIC:
                    writeACommand(fileName + "." + index);
                    writeCCommand("D", "M");
                    compToStack("D");
                    break;
                default:
                    throw new RuntimeException("unkwon segment");
            }
        } else {
            VMSegmentType typeByVMCode = VMSegmentType.getTypeByVMCode(segment);
            if (typeByVMCode.equals(VMSegmentType.S_ARG)
                    || typeByVMCode.equals(VMSegmentType.S_LCL)
                    || typeByVMCode.equals(VMSegmentType.S_THIS)
                    || typeByVMCode.equals(VMSegmentType.S_THAT)
            ) {
                popConstantSegment(index, typeByVMCode.getHackCode());
                return;
            }
            switch (typeByVMCode) {
                case S_PTR:
                    String seg = "";
                    if (_index == 0) {
                        seg = VMSegmentType.S_THIS.getHackCode();
                    } else if (_index == 1) {
                        seg = VMSegmentType.S_THAT.getHackCode();
                    }
                    stackToComp("D");
                    writeACommand(seg);
                    writeCCommand("M", "D");
                    break;
                case S_TEMP:
                    stackToComp("D");
                    writeACommand(String.valueOf(_index + 5));
                    writeCCommand("M", "D");
                    break;
                case S_CONST:
                    throw new RuntimeException("can't pop constant");
                case S_STATIC:
                    stackToComp("D");
                    writeACommand(fileName + "." + index);
                    writeCCommand("M", "D");
                    break;
                default:
                    throw new RuntimeException("unkwon segment");
            }
        }

    }

    protected void popConstantSegment(String index, String segment) {
        //计算目标地址
        //@type
        //AD=M
        //@index
        //D=A+D
        writeACommand(segment);
        writeCCommand("AD", "M");
        writeACommand(String.valueOf(index));
        writeCCommand("D", "A+D");
        //缓存目标地址
        //@R13
        //M=D
        writeACommand(VMSegmentType.S_R13.getHackCode());
        writeCCommand("M", "D");
        //获取栈顶值
        //@SP
        //AM=M-1
        //D=M
        stackToComp("D");
        //@R13
        //A=M
        //M=D
        writeACommand(VMSegmentType.S_R13.getHackCode());
        writeCCommand("A", "M");
        writeCCommand("M", "D");
    }

    /**
     * 固定地址或者符号添加到栈中
     *
     * @param index 虚拟段偏移地址
     * @param segment 虚拟段
     */
    private void pushConstantSegment(String index, String segment) {
        //A=@local
        writeACommand(segment);
        int _index = Integer.parseInt(index);
        if (_index == 0) {
            writeCCommand("A", "M");
        } else {
            //D=M
            writeCCommand("D", "M");
            //@index
            writeACommand(index + "");
            //A=A+D
            writeCCommand("A", "A+D");
        }

        //D=M
        writeCCommand("D", "M");
        //
        //@sp
        //A=M
        //M=D
        //sp++
        //@sp
        //M=M+1
        compToStack("D");

    }

    protected void close() {
        try {
            FileUtils.writeLines(new File(outPath), hackCodeList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //目标数据加载到堆栈 D或者其他
    protected void compToStack(String comp) {
        loadSP();
        writeCCommand("M", comp, null);
        writeIncreaseSP();
    }

    //取出栈顶数据且SP--
    protected void stackToComp(String comp) {
        //@SP
        //AM=M-1
        //D=M
        writeACommand(VMSegmentType.S_SP.getHackCode());
        writeCCommand("AM", "M-1");
        writeCCommand(comp, "M");
    }


    /**
     * A = SP
     * A = &SP
     */
    protected void loadSP() {
        writeACommand(VMSegmentType.S_SP.getHackCode());
        writeCCommand("A", "M", null);
    }

    /**
     * SP++
     */
    protected void writeIncreaseSP() {
        writeACommand("SP");
        writeCCommand("M", "M+1", null);
    }

    /**
     * SP--
     */
    protected void writeDecreaseSP() {
        writeACommand("SP");
        writeCCommand("M", "M-1", "");
    }

    protected void writeACommand(String address) {
        hackCodeList.add("@" + address);
    }

    protected void writeCCommand(String dest, String comp) {
        this.writeCCommand(dest, comp, null);
    }

    protected void writeCCommand(String dest, String comp, String jump) {
        String rs = "";
        if (StringUtils.isNotBlank(dest)) {
            rs += dest + "=";
        }
        if (StringUtils.isNotBlank(comp)) {
            rs += comp;
        }
        if (StringUtils.isNotBlank(jump)) {
            rs += ";" + jump;
        }
        hackCodeList.add(rs);
    }

    public String getFileName() {
        return fileName;
    }

    public List<String> getHackCodeList() {
        return hackCodeList;
    }

    public void start() {
        while (vmParser.hasMoreCommand()) {
            vmParser.advance();
            VMCommandType currentCommandType = vmParser.getCurrentCommandType();
            if (null == currentCommandType) {
                throw new RuntimeException("unkown Command type:" + vmParser.getCurrentCommandStr());
            }
            switch (currentCommandType) {
                case C_ARITHMETIC:
                    writeArithmetic(vmParser.arg1());
                    break;
                case C_PUSH:
                    writePushPop(VMCommandType.C_PUSH, vmParser.arg1(), vmParser.arg2());
                    break;
                case C_POP:
                    writePushPop(VMCommandType.C_POP, vmParser.arg1(), vmParser.arg2());
                    break;
                case C_LABEL:
                    break;
                case C_GOTO:
                    break;
                case C_IF:
                    break;
                case C_FUNCTION:
                    break;
                case C_RETURN:
                    break;
                case C_CALL:
                    break;
            }
        }
        close();
    }


}

