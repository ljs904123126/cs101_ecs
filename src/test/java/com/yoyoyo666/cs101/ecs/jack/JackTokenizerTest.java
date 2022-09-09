package com.yoyoyo666.cs101.ecs.jack;

import com.yoyoyo666.cs101.ecs.BaseTestCase;
import com.yoyoyo666.cs101.ecs.vm.VMCodeWriter08Test;
import junit.framework.TestCase;

import java.io.File;
import java.net.URL;

public class JackTokenizerTest extends BaseTestCase {

    public void testConstructor() {
        File file = getFile("10/ExpressionLessSquare/SquareGame.jack");
        JackTokenizer jackTokenizer = new JackTokenizer();
        jackTokenizer.constructor(file);
        System.out.println(jackTokenizer.getResult());
    }


}