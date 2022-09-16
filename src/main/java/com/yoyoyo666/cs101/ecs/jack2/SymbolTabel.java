package com.yoyoyo666.cs101.ecs.jack2;

import de.vandermeer.asciitable.AsciiTable;

import java.util.*;

public class SymbolTabel {

    private int staticCnt;
    private int fildCnt;
    private int argCnt;
    private int varCnt;

    private List<SymbolEntity> symbolEntities;
    private Map<String, SymbolEntity> symbolEntityMap;

    public static SymbolTabel getInstance() {
        return new SymbolTabel();
    }

    public void print() {
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow("name", "typeName", "fieldType", "index");
        at.setPaddingLeft(2);
        at.addRule();
        symbolEntities.forEach(entity -> {
            at.addRow(entity.getName(), entity.getTypeName(), entity.getFieldType().getTypeName(), entity.getIndex());
            at.setPaddingLeft(2);
            at.addRule();
        });
        System.out.println(at.render());
    }

    public SymbolTabel() {
        init();
    }

    private void init() {
        staticCnt = 0;
        fildCnt = 0;
        argCnt = 0;
        varCnt = 0;
        symbolEntities = new ArrayList<>();
        symbolEntityMap = new HashMap<>();
    }

    public void startSubroutine() {
        init();
    }

    public boolean has(String name) {
        return symbolEntityMap.containsKey(name);
    }

    public SymbolEntity get(String name) {
        return symbolEntityMap.get(name);
    }

    /**
     * 定义符号
     *
     * @param name 变量名称
     * @param type 变量的类型 int | char | boolean | className
     * @param kind type
     */
    public void define(String name, String type, FieldType kind) {

        SymbolEntity symbolEntity = null;

        switch (kind) {
            case STATIC:
                symbolEntity = new SymbolEntity(type, name, staticCnt, kind);
                staticCnt++;
                break;
            case FIELD:
                symbolEntity = new SymbolEntity(type, name, fildCnt, kind);
                fildCnt++;
                break;
            case ARG:
                symbolEntity = new SymbolEntity(type, name, argCnt, kind);
                argCnt++;
                break;
            case VAR:
                symbolEntity = new SymbolEntity(type, name, varCnt, kind);
                varCnt++;
                break;
            default:
                throw new RuntimeException("don't suport field type");
        }
        symbolEntities.add(symbolEntity);
        symbolEntityMap.put(name, symbolEntity);
    }

    public int varCount(FieldType kind) {
        switch (kind) {
            case STATIC:
                return staticCnt;
            case FIELD:
                return fildCnt;
            case ARG:
                return argCnt;
            case VAR:
                return varCnt;
            default:
                throw new RuntimeException("don't suport field type");
        }
    }

    public FieldType kindOf(String name) {
        SymbolEntity symbolEntity = symbolEntityMap.get(name);
        if (Objects.isNull(symbolEntity)) {
            return null;
        }
        return symbolEntity.getFieldType();
    }

    public String typeOf(String name) {
        SymbolEntity symbolEntity = symbolEntityMap.get(name);
        if (Objects.isNull(symbolEntity)) {
            return null;
        }
        return symbolEntity.getTypeName();
    }

    public Integer indexOf(String name) {
        SymbolEntity symbolEntity = symbolEntityMap.get(name);
        if (Objects.isNull(symbolEntity)) {
            return null;
        }
        return symbolEntity.getIndex();
    }

    public static class SymbolEntity {

        private String typeName;
        private String name;
        private int index;
        private FieldType fieldType;

        public SymbolEntity(String typeName, String name, int index, FieldType fieldType) {
            this.typeName = typeName;
            this.name = name;
            this.index = index;
            this.fieldType = fieldType;
        }

        public String getTypeName() {
            return typeName;
        }

        public String getName() {
            return name;
        }

        public int getIndex() {
            return index;
        }

        public FieldType getFieldType() {
            return fieldType;
        }
    }

    public static void main(String[] args) {
        SymbolTabel instance = getInstance();
        instance.define("test1", "int", FieldType.ARG);
        instance.define("test2", "char", FieldType.STATIC);
        instance.define("test3", "int", FieldType.ARG);
        instance.define("test4", "Hell", FieldType.ARG);
        instance.define("test5", "boolean", FieldType.FIELD);
        instance.define("test6", "int", FieldType.ARG);
        instance.define("test7", "Test", FieldType.ARG);
        instance.define("test9", "int", FieldType.VAR);
        instance.print();
    }
}
