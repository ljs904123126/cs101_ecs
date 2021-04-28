package com.yoyoyo666.cs101.ecs.assembler;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

/*
 * A 指令
 *      @value
 */
/*
C 指令
dest=comp;jump
1 1 1 a  c1 c2 c3 c4  c5 c6 d1 d2  d3 j1 j2 j3

comp : a + c
jump : j
des : d



 */
public class AssParser {

    private CommandType commandType;
    private String symbol;
    private String dest;
    private String comp;
    private String jump;
    private Queue<String> lines;
    private List<String> orgLines;
    private String orgCommand;
    private int index;


    public AssParser(File assFile) {

        try (FileInputStream inputStream = new FileInputStream(assFile);
             InputStreamReader is = new InputStreamReader(inputStream);
             BufferedReader bf = new BufferedReader(is)) {
            orgLines = bf.lines().collect(Collectors.toList());
            lines = new LinkedList<>(orgLines);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(lines);
    }

    public boolean hasMoreCommands() {
        return lines.size() > 0;
    }

    private void initNull() {
        symbol = null;
        dest = null;
        comp = null;
        jump = null;
        orgCommand = null;
        commandType = null;
    }

    public AssParser advance() {

        initNull();

        if (lines.size() == 0) {
            return null;
        }


        String poll;

        do {
            poll = getNext();
        } while (poll != null && poll.startsWith("//"));

        if (Objects.isNull(poll)) {
            return null;
        }

        orgCommand = poll;

        index++;
        parse(orgCommand);

        return this;
    }


    private void parse(String command) {

        if (command.startsWith("@")) {
            String value = command.replace("@", "");
//            if (StringUtils.isNumeric(value)) {
//                commandType = CommandType.A_COMMAND;
//            } else {
//                commandType = CommandType.L_COMMAND;
//            }
            commandType = CommandType.A_COMMAND;
            symbol = value;

        } else if (command.startsWith("(")) {
            commandType = CommandType.L_COMMAND;
            symbol = command.replace("(", "").replace(")", "");
        } else {
            commandType = CommandType.C_COMMAND;
            if (command.indexOf("=") > 0) {
                String[] split = command.split("=");
                dest = split[0];
                command = split[1];
            }
            if (command.indexOf(";") > 0) {
                String[] split = command.split(";");
                jump = split[1];
                command = split[0];
            }
            comp = command;
        }

    }

    private String getNext() {

        if (lines.size() == 0) {
            return null;
        }
        String poll = lines.poll();
        if (poll == null || poll.trim().length() == 0) {
            poll = "//";
        }
        poll = poll.replaceAll(" ", "").replaceAll("\t", "");
        if(poll.indexOf("//") > 0){
            poll = poll.substring(0,poll.indexOf("//")).trim();
        }
        return poll;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDest() {
        return dest;
    }

    public String getComp() {
        return comp;
    }

    public String getJump() {
        return jump;
    }

    public String getOrgCommand() {
        return orgCommand;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getIndex());
        sb.append(" org='");
        sb.append(this.getOrgCommand());
        sb.append("' type=");
        sb.append(this.getCommandType().getName());
        sb.append(" dest=");
        sb.append(this.getDest());
        sb.append(" comp=");
        sb.append(this.getComp());
        sb.append(" jump=");
        sb.append(this.getJump());
        sb.append(" symbol=");
        sb.append(this.getSymbol());
        return sb.toString();
    }

    public void reset(){
        lines.clear();
        lines.addAll(orgLines);
    }


    public static void main(String[] args) {
        AssParser assParser = new AssParser(new File("C:/Users/ljs/Desktop/Nand2Tetris-master/06/Assembler/Pong.asm"));
        while (assParser.hasMoreCommands()) {
            assParser.advance();
            System.out.print(assParser.toString());
            System.out.println("");
        }
    }
}
