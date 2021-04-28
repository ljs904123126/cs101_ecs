package com.yoyoyo666.cs101.ecs.vm;

public enum VMCommandType {

    C_ARITHMETIC("C_ARITHMETIC", 0),
    C_PUSH("C_PUSH", 1),
    C_POP("C_POP", 2),
    C_LABEL("C_LABEL", 3),
    C_GOTO("C_GOTO", 4),
    C_IF("C_IF", 5),
    C_FUNCTION("C_FUNCTION", 6),
    C_RETURN("C_RETURN", 7),
    C_CALL("C_CALL", 8);


    private String name;
    private int value;

    VMCommandType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return new StringBuffer("name:").append(this.name).append(" value:").append(value).toString();
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }


}
