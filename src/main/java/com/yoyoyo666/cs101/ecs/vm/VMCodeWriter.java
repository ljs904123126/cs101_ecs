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
    private VMParser vmParser;

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
     * @param command
     */
    public void writeArithmetic(String command) {

        VMArithmeticType typeByVMCode = VMArithmeticType.getTypeByVMCode(command);
        switch (typeByVMCode) {
            case ADD:
//                @SP
//                AM=M-1
//                D=M
                stactTocomp("D");
                //A=A-1
                writeCCommand("A", "A-1");
                //M=M+D
                writeCCommand("M", "M+D");
                break;
            case SUB:
                stactTocomp("D");
                writeCCommand("A", "A-1");
                writeCCommand("M", "M-D");
                break;
            case NEG:
                writeACommand(VMSegmentType.S_SP.getHackCode());
                writeCCommand("A", "M-1");
                writeCCommand("M", "-M");
                break;
            case OR:
                stactTocomp("D");
                writeCCommand("A", "A-1");
                writeCCommand("M", "M|D");
                break;
            case NOT:
                writeACommand(VMSegmentType.S_SP.getHackCode());
                writeCCommand("A", "M-1");
                writeCCommand("M", "!M");
                break;
            case AND:
                stactTocomp("D");
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
    private void compareArithmetic(String operator) {
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

    private String getUniqueSymbol() {
        String sb = "_" + this.fileName + "_" + symbolIndex;
        symbolIndex++;
        return sb.toUpperCase();
    }

    public void writePushPop(VMCommandType command, String segment, String index) {
        int _index = Integer.valueOf(index);
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
                    if (_index == 0) {
                        pushConstantSegment("0", VMSegmentType.S_THIS.getHackCode());
                    } else if (_index == 1) {
                        pushConstantSegment("0", VMSegmentType.S_THAT.getHackCode());
                    }
                    break;
                case S_TEMP:
                    //todo fix
                    pushConstantSegment(index, "R5");
                    break;
                case S_CONST:
                    writeACommand(String.valueOf(index));
                    writeCCommand("D", "A");
                    compToStack("D");
                    writeIncreaseSP();
                    break;
                case S_STATIC:
                    writeACommand(fileName + "." + index);
                    writeCCommand("D", "M");
                    compToStack("D");
                    writeIncreaseSP();
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
                    if (_index == 0) {
                        popConstantSegment("0", VMSegmentType.S_THIS.getHackCode());
                    } else if (_index == 1) {
                        popConstantSegment("0", VMSegmentType.S_THAT.getHackCode());
                    }
                    break;
                case S_TEMP:
                    //todo fix
                    popConstantSegment(index, VMSegmentType.S_R5.getHackCode());
                    break;
                case S_CONST:
                    throw new RuntimeException("can't pop constant");
                case S_STATIC:
                    stactTocomp("D");
                    writeACommand(fileName + "." + index);
                    writeCCommand("M", "D");
                    break;
                default:
                    throw new RuntimeException("unkwon segment");
            }
        }

    }

    private void popConstantSegment(String index, String segment) {
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
        stactTocomp("D");
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
     * @param index
     * @param segment
     */
    private void pushConstantSegment(String index, String segment) {
        //A=@local
        writeACommand(segment);
        int _index = Integer.valueOf(index);
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
        compToStack("D");
        //sp++
        //@sp
        //M=M+1
        writeIncreaseSP();
    }

    public void close() {
        try {
            FileUtils.writeLines(new File(outPath),hackCodeList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //静态数据加载到堆栈  cosntant
    private void valToStack(String val) {
        //A=val
        writeACommand(val);
        //D=A
        writeCCommand("D", "A", null);
        //*SP=D
        compToStack("D");
    }


    //目标数据加载到堆栈 D或者其他
    private void compToStack(String comp) {
        loadSP();
        writeCCommand("M", comp, null);
    }

    //取出栈顶数据且SP--
    private void stactTocomp(String comp) {
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
    private void loadSP() {
        writeACommand(VMSegmentType.S_SP.getHackCode());
        writeCCommand("A", "M", null);
    }

    /**
     * SP++
     */
    private void writeIncreaseSP() {
        writeACommand("SP");
        writeCCommand("M", "M+1", null);
    }

    /**
     * SP--
     */
    private void writeDecreaseSP() {
        writeACommand("SP");
        writeCCommand("M", "M-1", "");
    }

    private void writeACommand(String address) {
        hackCodeList.add("@" + address);
    }

    private void writeCCommand(String dest, String comp) {
        this.writeCCommand(dest, comp, null);
    }

    private void writeCCommand(String dest, String comp, String jump) {
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

