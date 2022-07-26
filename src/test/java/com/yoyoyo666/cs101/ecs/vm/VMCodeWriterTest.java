package com.yoyoyo666.cs101.ecs.vm;

import org.junit.Test;

import java.net.URL;

public class VMCodeWriterTest {

    @Test
    public void testStart() {
        compile("07/StackArithmetic/SimpleAdd/SimpleAdd.vm");
        compile("07/StackArithmetic/StackTest/StackTest.vm");
        compile("07/MemoryAccess/BasicTest/BasicTest.vm");
        compile("07/MemoryAccess/PointerTest/PointerTest.vm");
        compile("07/MemoryAccess/StaticTest/StaticTest.vm");
    }
    private void compile(String path){
        URL resource = VMCodeWriterTest.class.getClassLoader().getResource(path);
        String input = resource.getFile();
        String output = input.replaceAll("\\.vm$",".asm");
        VMCodeWriter vmCodeWriter = new VMCodeWriter(input,output);
        vmCodeWriter.start();
    }
}