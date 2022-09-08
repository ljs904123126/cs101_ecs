package com.yoyoyo666.cs101.ecs.jack;

import java.util.Arrays;

public enum KeyWordType {

    CLASS("class"),
    CONSTRUCTOR("constructor"),
    FUNCTION("function"),
    METHOD("method"),
    FIELD("field"),
    STATIC("static"),
    VAR("var"),
    INT("int"),
    CHAR("char"),
    BOOLEAN("boolean"),
    VOID("void"),
    TRUE("true"),
    FALSE("false"),
    NULL("null"),
    THIS("this"),
    LET("let"),
    DO("do"),
    IF("if"),
    ELSE("else"),
    WHILE("while"),
    RETURN("return");

    private String key;

    KeyWordType(String key) {
        this.key = key;
    }


    public String getKey() {
        return key;
    }

    public static KeyWordType get(String key) {
        return Arrays.stream(KeyWordType.values()).filter(e -> e.getKey().equals(key)).findAny().orElse(null);
    }


    public boolean equalsKey(String key) {
        if (null == key || key.trim().length() == 0) {
            return false;
        }
        return key.equals(this.key);
    }
}
