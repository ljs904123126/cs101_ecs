package com.yoyoyo666.cs101.ecs.jack2;

import com.yoyoyo666.cs101.ecs.vm.VMSegmentType;

public enum FieldType {
    STATIC("static", VMSegmentType.S_STATIC),
    FIELD("field", VMSegmentType.S_THIS),
    ARG("arg", VMSegmentType.S_ARG),
    VAR("var", VMSegmentType.S_LCL);

    private String typeName;
    private VMSegmentType vmSegmentType;

    FieldType(String typeName, VMSegmentType vmSegmentType) {
        this.typeName = typeName;
        this.vmSegmentType = vmSegmentType;
    }

    public VMSegmentType getVmSegmentType() {
        return vmSegmentType;
    }

    public String getTypeName() {
        return typeName;
    }
}
