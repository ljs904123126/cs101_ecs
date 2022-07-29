package com.yoyoyo666.cs101.ecs.vm;

import com.yoyoyo666.cs101.ecs.utils.CodeUtils;

import java.io.File;
import java.io.FileNotFoundException;
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
    private String fileName;


    public VMParser(String filePath) {
        this.filePath = filePath;
        File f = new File(filePath);
        if (!f.exists()) {
            throw new RuntimeException(new FileNotFoundException(filePath));
        }
        this.fileName = f.getName().replace(".vm", "");
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
        setCurrentNull();
        if (queue.isEmpty()) {
            return;
        }
        this.currentCommandStr = queue.poll();
        String[] s = this.currentCommandStr.split(" ");
        this.currentCommandType = VMCommandType.getCommandType(s[0]);
        if (this.currentCommandType != VMCommandType.C_RETURN) {
            if (this.currentCommandType == VMCommandType.C_ARITHMETIC) {
                this.argStr1 = s[0];
            } else {
                this.argStr1 = s[1];
            }
        }


        if (this.currentCommandType == VMCommandType.C_PUSH
                || this.currentCommandType == VMCommandType.C_POP
                || this.currentCommandType == VMCommandType.C_FUNCTION
                || this.currentCommandType == VMCommandType.C_CALL) {
            this.argStr2 = s[2];
        }
    }

    public VMCommandType commandType() {

        return this.currentCommandType;
    }

    /**
     * 返回当前命令的第一个参数，如果当前命令类型为C_ARITHMETIC，则返回名命令本身
     * 当前命令为C_RETURN 则不应该调用本程序
     *
     * @return
     */
    public String arg1() {

        return argStr1 == null ? null : argStr1.trim();
    }

    /**
     * 返回命令的第二个参数，仅 命令类型为 C_PUSH C_POP C_FUNCTION C_CALL 可用
     *
     * @return
     */
    public String arg2() {

        return argStr2 == null ? null : argStr2.trim();
    }

    public VMCommandType getCurrentCommandType() {
        return currentCommandType;
    }

    public String getCurrentCommandStr() {
        return currentCommandStr;
    }


    public String getFileName() {
        return fileName;
    }
}



