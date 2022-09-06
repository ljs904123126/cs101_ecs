package com.yoyoyo666.cs101.ecs.jack;

public enum TokenTypeEnum {

    KEYWORD("keyword"),
    SYMBOL("symbol"),
    IDENTIFIER("identifier"),
    INT_CONST("int_const"),
    STRING_CONST("string_const")
    ;

    private String keyWord;

    TokenTypeEnum(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getKeyWord() {
        return keyWord;
    }
}
