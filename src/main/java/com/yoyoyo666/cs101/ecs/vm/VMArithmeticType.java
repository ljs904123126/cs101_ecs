package com.yoyoyo666.cs101.ecs.vm;

import java.util.Arrays;

public enum VMArithmeticType {

    // ['add', 'sub', 'neg', 'eq', 'gt', 'lt', 'and', 'or', 'not']
    ADD("add", "+"),
    SUB("sub", "-"),
    // 算数求反
    NEG("neg", "-"),
    EQ("eq", "="),
    GT("gt", ">"),
    LT("lt", "<"),
    OR("or", "|"),
    NOT("not", "!"),
    AND("and", "&"),
    ;

    private String vmcode;
    private String symbol;

    VMArithmeticType(String vmcode, String symbol) {
        this.vmcode = vmcode;
        this.symbol = symbol;
    }

    public String getVmcode() {
        return vmcode;
    }

    public String getSymbol() {
        return symbol;
    }

    public static VMArithmeticType getTypeByVMCode(String vmCode) {

        return Arrays.stream(VMArithmeticType.values())
                .filter(vmArithmeticType -> vmArithmeticType.vmcode.equals(vmCode))
                .findAny().orElse(null);
    }
}
