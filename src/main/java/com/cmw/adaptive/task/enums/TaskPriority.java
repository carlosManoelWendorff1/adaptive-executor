package com.cmw.adaptive.task.enums;

public enum TaskPriority {

    HIGH(0),
    NORMAL(1),
    LOW(2);

    private final int value;

    TaskPriority(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}