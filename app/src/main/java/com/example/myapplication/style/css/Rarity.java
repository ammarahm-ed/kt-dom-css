package com.example.myapplication.style.css;

public enum Rarity {
    Invalid(4),
    Id(3),
    Class(2),
    Type(1),
    PseudoClass(0),
    Attribute(0),
    Universal(0),
    Inline(0);

    private final int value;

    Rarity(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}