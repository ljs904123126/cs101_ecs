package com.yoyoyo666.cs101.ecs.jack;

import com.yoyoyo666.cs101.ecs.BaseTestCase;

import java.io.File;

public class CompilationEngineTest extends BaseTestCase {

    public void testConstructor() {
        String path = "10/ExpressionLessSquare/SquareGame.jack";
        File file = getFile(path);
        CompilationEngine compilationEngine = new CompilationEngine();
        compilationEngine.constructor(file, new File("D:/test.xml"));
        System.out.println(path);
        System.out.println(compilationEngine.getResult());

        path = "10/ExpressionLessSquare/Square.jack";
        file = getFile(path);
        compilationEngine = new CompilationEngine();
        compilationEngine.constructor(file, new File("D:/test.xml"));
        System.out.println(path);
        System.out.println(compilationEngine.getResult());

        path = "10/ExpressionLessSquare/Main.jack";
        file = getFile(path);
        compilationEngine = new CompilationEngine();
        compilationEngine.constructor(file, new File("D:/test.xml"));
        System.out.println(path);
        System.out.println(compilationEngine.getResult());

        path = "10/ArrayTest/Main.jack";
        file = getFile(path);
        compilationEngine = new CompilationEngine();
        compilationEngine.constructor(file, new File("D:/test.xml"));
        System.out.println(path);
        System.out.println(compilationEngine.getResult());

        path = "10/Square/Main.jack";
        file = getFile(path);
        compilationEngine = new CompilationEngine();
        compilationEngine.constructor(file, new File("D:/test.xml"));
        System.out.println(path);
        System.out.println(compilationEngine.getResult());

        path = "10/Square/Square.jack";
        file = getFile(path);
        compilationEngine = new CompilationEngine();
        compilationEngine.constructor(file, new File("D:/test.xml"));
        System.out.println(path);
        System.out.println(compilationEngine.getResult());

        path = "10/Square/SquareGame.jack";
        file = getFile(path);
        compilationEngine = new CompilationEngine();
        compilationEngine.constructor(file, new File("D:/test.xml"));
        System.out.println(path);
        System.out.println(compilationEngine.getResult());

    }
}