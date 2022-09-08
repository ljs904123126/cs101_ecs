package com.yoyoyo666.cs101.ecs.jack;

import java.util.Arrays;
import java.util.List;

public class JackConstant {
    //    public static final String[] KEYWORKS = "class,constructor,function,method,field,static,var,int,char,boolean,void,true,false,null,this,let,do,if,else,while,return".split(",");
    public static final List<String> SYMBOL = Arrays.asList(
            "{", "}", "(", ")", "[", "]", ".", ",", ";", "+", "-", "*", "/", "&", "|", "<", ">", "=", "~"
    );


    public static final List<String> OP_SYMBOL = Arrays.asList(
            "+", "-", "*", "/", "&", "|", "<", ">", "="
    );

    public static final List<String> UNARYOP_SYMBOL = Arrays.asList(
            "-", "~"
    );

    public static final List<String> KEYWORD_CONSTANT = Arrays.asList(
            "true", "false", "null", "this"
    );

}
