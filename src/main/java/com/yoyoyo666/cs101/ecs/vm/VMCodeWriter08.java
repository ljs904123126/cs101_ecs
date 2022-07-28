package com.yoyoyo666.cs101.ecs.vm;

public class VMCodeWriter08 extends VMCodeWriter {

    public VMCodeWriter08(String inputPath, String outPath) {
        super(inputPath, outPath);
    }

    protected void writeInit() {
    }

    protected void writeLabel(String label) {
    }

    protected void writeGoto(String label) {
    }

    protected void writeIf(String label) {
    }

    protected void writeCall(String functionName, int numArgs) {
    }

    protected void writeReturn() {
    }

    protected void writeFunction(String functionName, int numLocals) {
    }

    @Override
    public void start() {
        while (vmParser.hasMoreCommand()) {
            vmParser.advance();
            VMCommandType currentCommandType = vmParser.getCurrentCommandType();
            if (null == currentCommandType) {
                throw new RuntimeException("unkown Command type:" + vmParser.getCurrentCommandStr());
            }
            switch (currentCommandType) {
                case C_ARITHMETIC:
                    writeArithmetic(vmParser.arg1());
                    break;
                case C_PUSH:
                    writePushPop(VMCommandType.C_PUSH, vmParser.arg1(), vmParser.arg2());
                    break;
                case C_POP:
                    writePushPop(VMCommandType.C_POP, vmParser.arg1(), vmParser.arg2());
                    break;
                case C_LABEL:
                    writeLabel(vmParser.arg1());
                    break;
                case C_GOTO:
                    writeGoto(vmParser.arg1());
                    break;
                case C_IF:
                    writeIf(vmParser.arg1());
                    break;
                case C_FUNCTION:
                    writeFunction(vmParser.arg1(), Integer.parseInt(vmParser.arg2()));
                    break;
                case C_RETURN:
                    writeReturn();
                    break;
                case C_CALL:
                    writeCall(vmParser.arg1(), Integer.parseInt(vmParser.arg2()));
                    break;
            }
        }
        close();
    }

}

