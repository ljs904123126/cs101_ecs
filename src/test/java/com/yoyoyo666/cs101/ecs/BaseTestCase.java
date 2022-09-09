package com.yoyoyo666.cs101.ecs;

import com.yoyoyo666.cs101.ecs.vm.VMCodeWriter08Test;
import junit.framework.TestCase;

import java.io.File;
import java.net.URL;

public class BaseTestCase extends TestCase {
    protected File getFile(String path) {
        URL resource = VMCodeWriter08Test.class.getClassLoader().getResource(path);
        String input = resource.getFile();
        return new File(input);
    }

}
