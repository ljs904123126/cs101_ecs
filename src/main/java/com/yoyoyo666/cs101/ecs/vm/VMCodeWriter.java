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

    public void writePushPop(String command, String segment, int index) {

    }

    public void close() {

    }


    /*
    public static final String S_LCL = "local";
    public static final String S_ARG = "argument";
    public static final String S_THIS = "this";
    public static final String S_THAT = "that";
    public static final String S_PTR = "pointer";
    public static final String S_TEMP = "temp";
    public static final String S_CONST = "constant";
    public static final String S_STATIC = "static";
    public static final String S_REG = "reg";
     */

//    public
    //push local argument this that

    //静态数据加载到堆栈  cosntant
    private void valToStack(String val) {
        //A=val
        writeACommand(val);
        //D=A
        writeCCommand("D", "A", null);
        //*SP=D
        compToStack("D");
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

