package com.yoyoyo666.cs101.ecs.vm;

public class Const {

    public static final int SP = 0;
    public static final int LCL = 1;
    public static final int ARG = 2;
    public static final int THIS = 3;
    public static final int THAT = 4;
    public static final int R0 = 0;
    public static final int R1 = 1;
    public static final int R2 = 2;
    public static final int R3 = 3;
    public static final int R4 = 4;
    public static final int R5 = 5;
    public static final int R6 = 6;
    public static final int R7 = 7;
    public static final int R8 = 8;
    public static final int R9 = 9;
    public static final int R10 = 10;
    public static final int R11 = 11;
    public static final int R12 = 12;
    public static final int R13 = 13;
    public static final int R14 = 14;
    public static final int R15 = 15;

    //    # Segment names
//    S_LCL        = 'local'
//    S_ARG        = 'argument'
//    S_THIS       = 'this'
//    S_THAT       = 'that'
//    S_PTR        = 'pointer'
//    S_TEMP       = 'temp'
//    S_CONST      = 'constant'
//    S_STATIC     = 'static'
//    S_REG        = 'reg'
    public static final String S_LCL = "local";
    public static final String S_ARG = "argument";
    public static final String S_THIS = "this";
    public static final String S_THAT = "that";
    public static final String S_PTR = "pointer";
    public static final String S_TEMP = "temp";
    public static final String S_CONST = "constant";
    public static final String S_STATIC = "static";
    public static final String S_REG = "reg";

    //内存段分类
    //常数 S_CONST 直接常数加载
    //S_PTR  S_TEMP 固定地址
    //S_STATIC
    //S_LCL S_ARG S_THIS S_THAT 基地址


}
