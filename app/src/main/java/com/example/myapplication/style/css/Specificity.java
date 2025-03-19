package com.example.myapplication.style.css;

public enum Specificity {
    Inline(1000),
    Id(100),
    Attribute(10),
    Class(10),
    PseudoClass(10),
    Type(1),
    Universal(0),
    Invalid(0);

    private final int value;

    Specificity(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
