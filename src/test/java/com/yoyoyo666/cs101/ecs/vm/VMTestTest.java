package com.yoyoyo666.cs101.ecs.vm;

import junit.framework.TestCase;
import org.junit.Test;

public class VMTestTest  {

    @Test
    public void enumTest() {
        for (VMCommandType value : VMCommandType.values()) {
            System.out.println(value.toString());
        }
    }

}