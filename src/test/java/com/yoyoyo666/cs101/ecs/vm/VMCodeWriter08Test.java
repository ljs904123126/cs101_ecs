package com.yoyoyo666.cs101.ecs.vm;

import org.junit.Test;

import java.io.File;
import java.net.URL;

public class VMCodeWriter08Test {

    @Test
    public void testStart() {
        compile("08/ProgramFlow/BasicLoop/BasicLoop.vm");
        compile("08/ProgramFlow/FibonacciSeries/FibonacciSeries.vm");
        compile("08/FunctionCalls/SimpleFunction/SimpleFunction.vm");
        compile("08/FunctionCalls/FibonacciElement");
    }
    private void compile(String path){
        URL resource = VMCodeWriter08Test.class.getClassLoader().getResource(path);
        String input = resource.getFile();
        File f = new File(input);
        String output = input.replaceAll("\\.vm$",".asm");
        if(f.isDirectory()){
            output = output + File.separator + f.getName() + ".asm";
        }
        VMCodeWriter08 vmCodeWriter = new VMCodeWriter08(input,output);
        vmCodeWriter.start();
    }
}