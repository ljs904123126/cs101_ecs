package com.yoyoyo666.cs101.ecs.vm;


/**
 * 算数命令
 * 存储访问命令
 * 流程控制命令
 * 函数调用
 */
public enum VMCommandType {

    C_ARITHMETIC("C_ARITHMETIC", 0, "add, sub, neg, eq, gt, lt, and, or, not"),
    C_PUSH("C_PUSH", 1, "push"),
    C_POP("C_POP", 2, "pop"),
    C_LABEL("C_LABEL", 3, "label"),
    C_GOTO("C_GOTO", 4, "goto"),
    C_IF("C_IF", 5, "if-goto"),
    C_FUNCTION("C_FUNCTION", 6, "function"),
    C_RETURN("C_RETURN", 7, "return"),
    C_CALL("C_CALL", 8, "call");


    private final String name;
    private final int value;
    private final String symbol;


    VMCommandType(String name, int value, String symbol) {
        this.name = name;
        this.value = value;
        this.symbol = symbol;
    }


//    private static

    @Override
    public String toString() {
        return "name:" + this.name + " value:" + value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public String getSymbol() {
        return symbol;
    }

    public static VMCommandType getCommandType(String symbol) {
        for (VMCommandType commandType : VMCommandType.values()) {
            if (commandType.getSymbol().contains(symbol)) {
                return commandType;
            }
        }
        return null;
    }
}
