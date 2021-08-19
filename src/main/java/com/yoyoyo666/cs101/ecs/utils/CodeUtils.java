package com.yoyoyo666.cs101.ecs.utils;

import com.yoyoyo666.cs101.ecs.exception.ECSFileNotFoundException;
import com.yoyoyo666.cs101.ecs.vm.VMCommandType;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CodeUtils {


    public static List<String> getCommandSetTrim(String path) {
        List<String> lines = Arrays.asList();
        File file = new File(path);
        if (!file.exists()) {
            throw new ECSFileNotFoundException("FILE NOT FOUND IS ERROR:" + path);
        }
        try (FileInputStream inputStream = new FileInputStream(file);
             InputStreamReader is = new InputStreamReader(inputStream);
             BufferedReader bf = new BufferedReader(is)) {
            lines = bf.lines().collect(Collectors.toList());
            lines = codeTrim(lines);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }


    public static List<String> codeTrim(List<String> org) {
        return org.stream().filter(s ->
                StringUtils.isNotBlank(s) && !s.trim().startsWith("//")
        ).map(s ->
                s.indexOf("//") > 0 ? s.substring(0, s.indexOf("//")) : s
        ).collect(Collectors.toList());
    }


    public static String commandTrim(String line) {
        List<String> strings = Arrays.asList(line);
        List<String> strings1 = codeTrim(strings);
        if (strings1.size() > 0) {
            return strings.get(0);
        }
        return "";
    }


    private static Map<String, VMCommandType> cacheVMType = null;

    static {
        cacheVMType = new HashMap<>();
        for (VMCommandType value : VMCommandType.values()) {
            String symbol = value.getSymbol();
            if (StringUtils.isEmpty(symbol)) {
                continue;
            }
            for (String k : symbol.split(",")) {
                cacheVMType.put(k, value);
            }
        }
    }

    public static VMCommandType getVMCommandType(String symbol) {

        return cacheVMType.get(symbol);

    }


}
