package com.yoyoyo666.cs101.ecs.jack2;

import com.yoyoyo666.cs101.ecs.vm.VMArithmeticType;
import com.yoyoyo666.cs101.ecs.vm.VMSegmentType;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VMWriter {

    private List<String> vmCode;
    private File outFile;


    private void init() {
        vmCode = new ArrayList<>();
    }

    public VMWriter(File outfile) {
        this.vmCode = vmCode;
        this.constructor(outfile);
        init();
    }

    public void constructor(File outfile) {
        this.outFile = outfile;
    }

    public void writePush(VMSegmentType vmSegmentType, int index) {
        vmCode.add("push " + vmSegmentType.getVmCode() + " " + index);
    }

    public void writePop(VMSegmentType vmSegmentType, int index) {
        vmCode.add("pop " + vmSegmentType.getVmCode() + " " + index);
    }


    public void writeArithmetic(VMArithmeticType arithmeticType) {
        vmCode.add(arithmeticType.getVmcode());
    }

    public void writeLabel(String label) {
        vmCode.add("label " + label);
    }

    public void writeGoto(String label) {
        vmCode.add("goto " + label);
    }

    public void writeIf(String label) {
        vmCode.add("if-goto " + label);
    }

    public void writeCall(String funName, int nArgs) {
        vmCode.add("call " + funName + " " + nArgs);
    }

    public void writeFunction(String funName, int nLocals) {
        vmCode.add("function " + funName + " " + nLocals);
    }

    public void writeReturn() {
        vmCode.add("return");
    }

    public void close() {
        try {
            FileUtils.writeLines(outFile, vmCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
