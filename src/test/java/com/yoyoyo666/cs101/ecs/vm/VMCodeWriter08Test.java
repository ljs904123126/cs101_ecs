package com.yoyoyo666.cs101.ecs.vm;

import org.junit.Test;

import java.net.URL;

public class VMCodeWriter08Test {

    @Test
    public void testStart() {
        compile("08/ProgramFlow/BasicLoop/BasicLoop.vm");
        compile("08/ProgramFlow/FibonacciSeries/FibonacciSeries.vm");
        compile("08/FunctionCalls/SimpleFunction/SimpleFunction.vm");
        compile("08/FunctionCalls/StaticsTest/Class1.vm");
        compile("08/FunctionCalls/StaticsTest/Class2.vm");
        compile("08/FunctionCalls/StaticsTest/Sys.vm");
        compile("08/FunctionCalls/NestedCall/Sys.vm");
        compile("08/FunctionCalls/FibonacciElement/Main.vm");
        compile("08/FunctionCalls/FibonacciElement/Sys.vm");
    }
    private void compile(String path){
        URL resource = VMCodeWriter08Test.class.getClassLoader().getResource(path);
        String input = resource.getFile();
        String output = input.replaceAll("\\.vm$",".asm");
        VMCodeWriter08 vmCodeWriter = new VMCodeWriter08(input,output);
        vmCodeWriter.start();
    }
}