package com.yoyoyo666.cs101.ecs.vm;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class VMCodeWriter {

    private String outPath;
    private String inputPath;

    private List<String> vmCommandList;

    public VMCodeWriter(String outPath) {
        this.outPath = outPath;
        vmCommandList = new LinkedList<>();
    }

    public void setFileName(String inputPath) {
        this.inputPath = inputPath;
    }

    /**
     * 计算
     *
     * @param command
     */
    public void writeArithmetic(String command) {

    }


    public void writePushPop(String command, String segment, int index) {

        VMCommandType commandType = VMCommandType.getCommandType(command);
        if (VMCommandType.C_PUSH == commandType) {
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
                    if (index == 0) {
                        pushConstantSegment(0, VMSegmentType.S_THIS.getHackCode());
                    } else if (index == 1) {
                        pushConstantSegment(0, VMSegmentType.S_THAT.getHackCode());
                    }
                    break;
                case S_TEMP:
                    pushConstantSegment(index, "R5");
                    break;
                case S_CONST:
                    writeACommand(String.valueOf(index));
                    writeCCommand("D", "A");
                    compToStack("D");
                    writeIncreaseSP();
                    break;
                case S_STATIC:
                    break;
                case S_REG:
                    break;
                default:
                    break;
            }

        } else {
            //todo pop
        }

    }

    /**
     * 固定地址或者符号添加到栈中
     *
     * @param index
     * @param segment
     */
    private void pushConstantSegment(int index, String segment) {
        //A=@local
        writeACommand(segment);
        if (index == 0) {
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

    /**
     * A = SP
     * A = &SP
     */
    private void loadSP() {
        writeACommand("SP");
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
        vmCommandList.add("@" + address);
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
        vmCommandList.add(rs);
    }
}

