package com.yoyoyo666.cs101.ecs.vm;

import java.util.Arrays;

public enum VMSegmentType {

    S_SP("sp", "SP"),
    S_LCL("local", "LCL"),
    S_ARG("argument", "ARG"),
    S_THIS("this", "THIS"),
    S_THAT("that", "THAT"),
    S_PTR("pointer", ""),
    S_TEMP("temp", ""),
    S_CONST("constant", ""),
    S_STATIC("static", ""),
    S_REG("reg", ""),
    ;

    private String vmCode;
    private String hackCode;

    VMSegmentType(String vmCode, String hackCode) {
        this.vmCode = vmCode;
        this.hackCode = hackCode;
    }

    public String getVmCode() {
        return vmCode;
    }

    public void setVmCode(String vmCode) {
        this.vmCode = vmCode;
    }

    public String getHackCode() {
        return hackCode;
    }

    public void setHackCode(String hackCode) {
        this.hackCode = hackCode;
    }


    public static VMSegmentType getTypeByVMCode(String vmCode) {

        return Arrays.stream(VMSegmentType.values())
                .filter(vmSegmentType -> vmSegmentType.vmCode.equals(vmCode))
                .findAny().orElse(null);
    }
}



