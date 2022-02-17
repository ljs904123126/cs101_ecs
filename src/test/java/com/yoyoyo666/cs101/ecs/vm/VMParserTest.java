package com.yoyoyo666.cs101.ecs.vm;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class VMParserTest {

    @Test
    public void testAdvance() {

        List<String> commands = Arrays.asList("push constant 10",
                "pop local 0",
                "add",
                "sub"
        );
        VMParser p = new VMParser(commands);
        Assert.assertTrue(p.hasMoreCommand());
        p.advance();
        Assert.assertEquals(p.commandType(), VMCommandType.C_PUSH);
        Assert.assertEquals(p.arg1(), "constant");
        Assert.assertEquals(p.arg2(), "10");
        p.advance();
        Assert.assertEquals(p.commandType(), VMCommandType.C_POP);
        Assert.assertEquals(p.arg1(), "local");
        Assert.assertEquals(p.arg2(), "0");
        p.advance();
        Assert.assertEquals(p.commandType(), VMCommandType.C_ARITHMETIC);
        Assert.assertEquals(p.arg1(), "add");
        Assert.assertNull(p.arg2());
        p.advance();
        Assert.assertEquals(p.commandType(), VMCommandType.C_ARITHMETIC);
        Assert.assertEquals(p.arg1(), "sub");
        Assert.assertNull(p.arg2());
    }


}