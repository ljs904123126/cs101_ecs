package com.yoyoyo666.cs101.ecs.assembler;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Assembler {


    public static void main(String[] args) throws IOException {

        List<String> hackList = new ArrayList<>();
        List<String> mList = new ArrayList<>();

        System.out.println(toBinary(5, 16));

        int ramAddress = 16;

        File assFile = new File("C:/Users/ljs/Desktop/100.asm");

        String parent = assFile.getParent();
        String fileRname = assFile.getName().substring(0, assFile.getName().indexOf("."));

        File hackFile = new File(parent + File.separator + fileRname + ".hack");

        if (hackFile.exists()) {
            hackFile.delete();
        }
        hackFile.createNewFile();


        File mfile = new File(parent + File.separator + fileRname + ".m");

        if (mfile.exists()) {
            mfile.delete();
        }
        mfile.createNewFile();

        AssParser assParser = new AssParser(assFile);

        AssSymbol assSymbol = new AssSymbol();

        AssCode assCode = new AssCode();

        int pc = -1;
        while (assParser.hasMoreCommands()) {
            assParser.advance();
            String symbol = assParser.getSymbol();
            if (assParser.getCommandType() == CommandType.L_COMMAND) {
                assSymbol.addEntry(symbol, pc + 1);
            } else {
//                if (assParser.getCommandType() == CommandType.A_COMMAND) {
//                    if (!StringUtils.isNumeric(symbol)) {
//                        if (!assSymbol.contains(symbol)) {
//                            assSymbol.addEntry(symbol, ramAddress++);
//                        }
//                    }
//                }
                pc++;
            }
        }


        assParser.reset();
        while (assParser.hasMoreCommands()) {
            assParser.advance();
            String symbol = assParser.getSymbol();
            if (assParser.getCommandType() != CommandType.L_COMMAND) {
                if (assParser.getCommandType() == CommandType.A_COMMAND) {
                    //A指令 如果后面跟着的是数字 那么就将数据载入A寄存器
                    // 如果后面是符号，则将符号分配地址载入A寄存器
                    if (!StringUtils.isNumeric(symbol)) {
                        //不是数字 则是符号 那么载入地址
                        if (!assSymbol.contains(symbol)) {
                            System.out.println(symbol +  "--" + ramAddress);
                            // allocate memory address
                            assSymbol.addEntry(symbol, ramAddress++);
                        }
                        //get address
                        Integer address = assSymbol.getAddress(symbol);
                        String mc = assParser.getOrgCommand()
                                .replace(symbol, address + "");
//                        System.out.println(mc);
                        //instruction
                        String hc = toBinary(address.intValue(), 16);
//                        System.out.println(hc);
                        mList.add(mc);
                        hackList.add(hc);
                    } else {
                        //数字
                        String mc = assParser.getOrgCommand();
//                        System.out.println(mc);
                        String hc = toBinary(Integer.valueOf(assParser.getSymbol()), 16);
//                        System.out.println(hc);
                        mList.add(mc);
                        hackList.add(hc);
                    }
                } else {
                    //C指令
                    String b = "111"
                            + assCode.getComp(assParser.getComp())
                            + assCode.getDest(assParser.getDest())
                            + assCode.getJump(assParser.getJump());
                    String mc = assParser.getOrgCommand();
//                    System.out.println(mc);
//                    System.out.println(b);
                    mList.add(mc);
                    hackList.add(b);
                }
            }
        }
        try (FileWriter fileWriter = new FileWriter(hackFile);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        ) {
            hackList.forEach(s -> {
                try {
                    bufferedWriter.write(s);
                    bufferedWriter.write("\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            bufferedWriter.flush();
        }
        try (FileWriter fileWriter = new FileWriter(mfile);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        ) {
            mList.forEach(s -> {
                try {
                    bufferedWriter.write(s);
                    bufferedWriter.write("\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            bufferedWriter.flush();
        }

    }

    public static String toBinary(int num, int digits) {
        String cover = Integer.toBinaryString(1 << digits).substring(1);
        String s = Integer.toBinaryString(num);
        return s.length() < digits ? cover.substring(s.length()) + s : s;
    }

}
