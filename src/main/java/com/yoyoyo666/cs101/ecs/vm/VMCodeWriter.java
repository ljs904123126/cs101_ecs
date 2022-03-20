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

    public void writeArithmetic(String command) {

    }

    /**
     * @param command push pop
     * @param segment
     * @param index
     */
    public void writePushPop(VMCommandType command, String segment, int index) {

        if (command != VMCommandType.C_PUSH && command != VMCommandType.C_POP) {
            throw new RuntimeException("command must be push or pop");
        }
        SegmentType segmentType = SegmentType.getSegmentType(segment);
        assert segmentType != null;
        if (command == VMCommandType.C_PUSH) {
            if (segmentType.isConstant()) {
                pushValToStack(index + "");
            }
            if (segmentType.isBasic()) {
                pushBasicToStack(segmentType, index);
            }
            if (segmentType.isStatic()) {

            }
        } else {

        }


    }

    //this == S_LCL || this == S_ARG  || this == S_THIS || this == S_THAT;
    private void pushBasicToStack(SegmentType segmentType, int index) {

        writeACommand(index);
        writeCCommand("D", "A", null);
        writeACommand(segmentType.getKey());
        //计算地址偏移量
        writeCCommand("A", "M+D", null);
        writeCCommand("D", "M", null);

        loadSP();
        writeCCommand("M", "D", null);
        writeIncreaseSP();

    }

    public void close() {

    }


    //静态数据加载到堆栈  cosntant
    private void pushValToStack(String val) {
        //A=val
        writeACommand(val);
        //D=A
        writeCCommand("D", "A", null);
        //*SP=D
        compToStack("D");
        //SP++
        writeIncreaseSP();
    }


    //目标数据加载到堆栈
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

    private void writeACommand(int address) {
        vmCommandList.add("@" + address);
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

