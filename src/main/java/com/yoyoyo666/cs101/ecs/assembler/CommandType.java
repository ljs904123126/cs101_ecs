package com.yoyoyo666.cs101.ecs.assembler;

public enum CommandType {

    A_COMMAND(0, "A指令"),
    C_COMMAND(1, "C指令"),
    L_COMMAND(2, "符号");

    private int key;
    private String name;

    CommandType(int key, String name) {
        this.key = key;
        this.name = name;
    }

    public int getKey() {

        return key;
    }

    public String getName() {
        return name;
    }
}
