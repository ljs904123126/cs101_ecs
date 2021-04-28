package com.yoyoyo666.cs101.ecs.assembler;

import java.util.HashMap;
import java.util.Map;

public class AssSymbol {
    private Map<String, Integer> PRE = new HashMap<>();
    private Map<String, Integer> table = new HashMap<>();

    public AssSymbol() {
        PRE.put("SP", 0);
        PRE.put("LCL", 1);
        PRE.put("ARG", 2);
        PRE.put("THIS", 3);
        PRE.put("THAT", 4);
        PRE.put("SCREEN", 16384);
        PRE.put("KBD", 24576);
        int i = 0;
        do {
            PRE.put("R" + i, i);
//            System.out.println(i);
            i++;
        } while (i < 16);
    }

    public void addEntry(String symbol, int address) {
        table.put(symbol, address);
    }

    public boolean contains(String symbol) {
        return PRE.containsKey(symbol) || table.containsKey(symbol);
    }

    public Integer getAddress(String symbol) {
        if (PRE.containsKey(symbol)) {
            return PRE.get(symbol);
        }
        return table.get(symbol);
    }

}
