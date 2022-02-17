package com.yoyoyo666.cs101.ecs.vm;

/**
 * push constant 10
 * pop local 0
 * push constant 21
 * push constant 22
 * pop argument 2
 * pop argument 1
 * push constant 36
 * pop this 6
 * push constant 42
 * push constant 45
 * pop that 5
 * pop that 2
 * push constant 510
 * pop temp 6
 * push local 0
 * push that 5
 * add
 * push argument 1
 * sub
 * push this 6
 * push this 6
 * add
 * sub
 * push temp 6
 * add
 */

/**
 * 算数命令
 * 存储访问命令
 * 流程控制命令
 * 函数调用
 */
public enum VMCommandType {

    C_ARITHMETIC("C_ARITHMETIC", 0, "add,sub"),
    C_PUSH("C_PUSH", 1, "push"),
    C_POP("C_POP", 2, "pop"),
    C_LABEL("C_LABEL", 3, ""),
    C_GOTO("C_GOTO", 4, ""),
    C_IF("C_IF", 5, ""),
    C_FUNCTION("C_FUNCTION", 6, ""),
    C_RETURN("C_RETURN", 7, ""),
    C_CALL("C_CALL", 8, "");


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
