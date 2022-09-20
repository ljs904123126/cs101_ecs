package com.yoyoyo666.cs101.ecs.jack2;

import java.io.File;
import java.util.Arrays;

public class JackAnalyzer {

    public static void main(String[] args) {

    }

    public static void start(File inputFile) {
        if (inputFile.isDirectory()) {

            Arrays.stream(inputFile.list((dir, name) -> name.endsWith(".jack"))).forEach(fileName -> {
                File _in = new File(inputFile.getPath() + File.separator + fileName);
                String _o = inputFile.getPath() + File.separator + fileName.replace(".jack", ".vm");
                new CompilationEngine().constructor(_in, new File(_o));
            });
            return;
        }
        String out = inputFile.getParent() + File.separator + inputFile.getName().replace(".jack", ".vm");
        new CompilationEngine().constructor(inputFile, new File(out));

    }

}
