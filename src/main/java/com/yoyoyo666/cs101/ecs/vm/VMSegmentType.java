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
    S_R0("r0", "R0"),
    S_R1("r1", "R1"),
    S_R2("r2", "R2"),
    S_R3("r3", "R3"),
    S_R4("r4", "R4"),
    S_R5("r5", "R5"),
    S_R6("r6", "R6"),
    S_R7("r7", "R7"),
    S_R8("r8", "R8"),
    S_R9("r9", "R9"),
    S_R10("r10", "R10"),
    S_R11("r11", "R11"),
    S_R12("r12", "R12"),
    S_R13("r13", "R13"),
    S_R14("r14", "R14"),
    S_R15("r15", "R15"),
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



