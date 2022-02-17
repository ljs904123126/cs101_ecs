package com.yoyoyo666.cs101.ecs.utils;

import com.yoyoyo666.cs101.ecs.vm.VMCommandType;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class CodeUtilsTest {

    private static List<String> data;
    private static File dataFile;

    @Test
    public void testGetCommandSetTrim() {
        String path = Objects.requireNonNull(this.getClass().getClassLoader().getResource("07/MemoryAccess/BasicTest/BasicTest.vm")).getPath();
        System.out.println(path);
        List<String> commandSetTrim = CodeUtils.getCommandSetTrim(path);
//        commandSetTrim.forEach(System.out::println);
        long count = commandSetTrim.stream().filter(s -> s.indexOf("//") > -1).count();
        Assert.assertEquals(count, 0);
        Assert.assertEquals(commandSetTrim.size(), 25);
    }

    @Test
    public void testCodeTrim() {
        List<String> strings = CodeUtils.codeTrim(data);
        long count = strings.stream().filter(s -> s.indexOf("//") > -1).count();
        Assert.assertEquals(count, 0);
        Assert.assertEquals(strings.size(), 25);

    }

    @Test
    public void testCommandTrim() {
        String normalOrg = "push local 0";
        String normal = CodeUtils.commandTrim(normalOrg);
        assertEquals(normal, normalOrg);
        String commentsOrg = "// this is test comments";
        String comments = CodeUtils.commandTrim(commentsOrg);
        assertEquals("", comments);
        normal = CodeUtils.commandTrim(normalOrg+commentsOrg);
        assertEquals(normal, normalOrg);
    }


    @Test
    public void testGetVMCommandType(){
        VMCommandType add = CodeUtils.getVMCommandType("add");
        VMCommandType sub = CodeUtils.getVMCommandType("sub");
        VMCommandType pop = CodeUtils.getVMCommandType("pop");
        VMCommandType push = CodeUtils.getVMCommandType("push");
//        ass
    }

    @BeforeClass
    public static void initData() {
        System.out.println("init data");
        data = Arrays.asList("// This file is part of www.nand2tetris.org",
                "// and the book \"The Elements of Computing Systems\"",
                "// by Nisan and Schocken, MIT Press.",
                "// File name: projects/07/MemoryAccess/BasicTest/BasicTest.vm",
                "",
                "// Executes pop and push commands using the virtual memory segments.",
                "push constant 10",
                "pop local 0",
                "push constant 21",
                "push constant 22",
                "pop argument 2 // Executes pop and push commands using ",
                "pop argument 1",
                "push constant 36",
                "pop this 6",
                "push constant 42",
                "push constant 45",
                "pop that 5",
                "pop that 2",
                "push constant 510",
                "pop temp 6",
                "push local 0",
                "push that 5",
                "add",
                "push argument 1",
                "sub",
                "push this 6",
                "push this 6",
                "add",
                "sub",
                "push temp 6",
                "add",
                "");
        String property = System.getProperty("java.io.tmpdir");
        String s = UUID.randomUUID().toString();
        String filePath = property + File.separator + s;
        dataFile = new File(filePath);
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileUtils.writeLines(dataFile, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() {
        if (dataFile != null && dataFile.exists()) {
            dataFile.delete();
        }
    }


}