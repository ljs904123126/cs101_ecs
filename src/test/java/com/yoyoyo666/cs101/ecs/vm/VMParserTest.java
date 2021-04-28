package com.yoyoyo666.cs101.ecs.vm;

import com.yoyoyo666.cs101.ecs.utils.ExmapleData;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

public class VMParserTest  {

    @Test
    public void testAdvance() {
        List<String> commands = ExmapleData.getCommands();
        VMParser p = new VMParser(commands);
//        while (p.advance();)
    }
}