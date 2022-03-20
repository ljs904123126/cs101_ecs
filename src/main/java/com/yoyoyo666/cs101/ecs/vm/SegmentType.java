package com.yoyoyo666.cs101.ecs.vm;

public enum SegmentType {

    SP("SP", "sp"),
    S_LCL("LCL", "local"),
    S_ARG("ARG", "argument"),
    S_THIS("THIS", "this"),
    S_THAT("THAT", "that"),
    S_PTR("", "pointer"),
    S_TEMP("", "temp"),
    S_CONST("", "constant"),
    S_STATIC("", "static"),
    S_REG("", "reg");;

    private String key;
    private String name;

    SegmentType(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }


    //内存段分类
    //S_LCL S_ARG S_THIS S_THAT 基地址
    public boolean isBasic() {
        return this == S_LCL
                || this == S_ARG
                || this == S_THIS
                || this == S_THAT;

    }

    //S_PTR   固定地址
    public boolean isPointer() {
        return this == S_PTR;
    }

    //S_TEMP  固定地址
    public boolean isTemp() {
        return this == S_TEMP;
    }

    //常数 S_CONST 直接常数加载
    public boolean isConstant() {
        return this == S_CONST;
    }

    //S_STATIC  静态内存
    public boolean isStatic() {
        return this == S_STATIC;
    }


    public static SegmentType getSegmentType(String segment) {
        for (SegmentType segmentType : SegmentType.values()) {
            if (segmentType.getName().equals(segment)) {
                return segmentType;
            }
        }
        return null;
    }

}
