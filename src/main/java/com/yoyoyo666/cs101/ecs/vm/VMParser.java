package com.yoyoyo666.cs101.ecs.vm;

import com.yoyoyo666.cs101.ecs.utils.CodeUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class VMParser {

    private String filePath;
    private List<String> commands;
    private VMCommandType currentCommandType;
    private String argStr1;
    private String argStr2;
    private Queue<String> queue;
    private String currentCommandStr;


    public VMParser(String filePath) {
        this.filePath = filePath;
        commands = CodeUtils.getCommandSetTrim(filePath);
        setQueue();
    }

    public VMParser(List<String> commands) {
        this.commands = commands;
        setQueue();
    }

    private void setQueue() {
        queue = new LinkedList(commands);
    }

    private void setCurrentNull() {
        this.currentCommandType = null;
        this.currentCommandStr = null;
        this.argStr1 = null;
        this.argStr2 = null;
    }

    public boolean hasMoreCommand() {
        return !queue.isEmpty();
    }

    public void advance() {
        if (queue.isEmpty()) {
            setCurrentNull();
            return;
        }
        String cmand = queue.poll();
        this.currentCommandStr = cmand;

    }

    public VMCommandType commandType() {

        return null;
    }

    public String arg1() {

        return argStr1;
    }

    public String arg2() {

        return argStr2;
    }

    public VMCommandType getCurrentCommandType() {
        return currentCommandType;
    }

    public String getCurrentCommandStr() {
        return currentCommandStr;
    }
}
