package com.yoyoyo666.cs101.ecs.assembler;

import java.util.HashMap;
import java.util.Map;

public class AssCode {

    private Map<String, String> compTab = null;
    private Map<String, String> destTab = null;
    private Map<String, String> jumpTab = null;
    private Map<String, String> preTab = null;


    public AssCode() {
        compTab = new HashMap<String, String>();
        destTab = new HashMap<String, String>();
        jumpTab = new HashMap<String, String>();
        preTab = new HashMap<String, String>();

        compTab.put("0", "0101010");
        compTab.put("1", "0111111");
        compTab.put("-1", "0111010");
        compTab.put("D", "0001100");
        compTab.put("A", "0110000");
        compTab.put("!D", "0001101");
        compTab.put("!A", "0110001");
        compTab.put("-D", "0001111");
        compTab.put("-A", "0110011");
        compTab.put("D+1", "0011111");
        compTab.put("A+1", "0110111");
        compTab.put("D-1", "0001110");
        compTab.put("A-1", "0110010");
        compTab.put("D+A", "0000010");
        compTab.put("D-A", "0010011");
        compTab.put("A-D", "0000111");
        compTab.put("D&A", "0000000");
        compTab.put("D|A", "0010101");
        compTab.put("", "xxxxxxx");
        compTab.put("", "xxxxxxx");
        compTab.put("", "xxxxxxx");
        compTab.put("", "xxxxxxx");
        compTab.put("M", "1110000");
        compTab.put("", "xxxxxxx");
        compTab.put("!M", "1110001");
        compTab.put("", "xxxxxxx");
        compTab.put("-M", "1110011");
        compTab.put("", "xxxxxxx");
        compTab.put("M+1", "1110111");
        compTab.put("", "xxxxxxx");
        compTab.put("M-1", "1110010");
        compTab.put("D+M", "1000010");
        compTab.put("D-M", "1010011");
        compTab.put("M-D", "1000111");
        compTab.put("D&M", "1000000");
        compTab.put("D|M", "1010101");

        destTab.put("null", "000");
        destTab.put("M", "001");
        destTab.put("D", "010");
        destTab.put("MD", "011");
        destTab.put("A", "100");
        destTab.put("AM", "101");
        destTab.put("AD", "110");
        destTab.put("AMD", "111");

        jumpTab.put("null", "000");
        // >0
        jumpTab.put("JGT", "001");
        // =0
        jumpTab.put("JEQ", "010");
        // >=0
        jumpTab.put("JGE", "011");
        // <0
        jumpTab.put("JLT", "100");
        // !=0
        jumpTab.put("JNE", "101");
        // <=0
        jumpTab.put("JLE", "110");
        // jump
        jumpTab.put("JMP", "111");

    }

    public String getDest(String key) {
        if(null == key){
            return "000";
        }
        return destTab.get(key);
    }

    public String getComp(String key) {
        return compTab.get(key);
    }

    public String getJump(String key) {
        if(null == key){
            return "000";
        }
        return jumpTab.get(key);
    }


}
