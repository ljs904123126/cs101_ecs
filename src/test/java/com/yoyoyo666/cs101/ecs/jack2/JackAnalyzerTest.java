package com.yoyoyo666.cs101.ecs.jack2;

import com.yoyoyo666.cs101.ecs.BaseTestCase;

import java.io.File;

public class JackAnalyzerTest extends BaseTestCase {

    public void testStart() {
        File file = getFile("11/Seven/Main.jack");
        JackAnalyzer.start(file);
    }

    public void testStartTobin() {
        File file = getFile("11/ConvertToBin/Main.jack");

        JackAnalyzer.start(file.getParentFile());
    }

//    public void testComplexArrays() {
//        File file = getFile("11/ComplexArrays/Main.jack");
//
//        JackAnalyzer.start(file.getParentFile());
//    }

    public void testSquare() {
        File file = getFile("11/Square/Main.jack");

        JackAnalyzer.start(file.getParentFile());
    }

    public void testAverage() {
        File file = getFile("11/Average/Main.jack");
        JackAnalyzer.start(file.getParentFile());
    }

    public void testPong() {
        File file = getFile("11/Pong/Main.jack");
        JackAnalyzer.start(file.getParentFile());
    }

    public void testComplexArrays() {
        File file = getFile("11/ComplexArrays/Main.jack");
        JackAnalyzer.start(file.getParentFile());
    }
}