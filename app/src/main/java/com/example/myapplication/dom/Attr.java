package com.example.myapplication.dom;

public class Attr {
    private String namespace;
    private String name;
    private String value;

    public Attr(String namespace, String name, String value) {
        this.namespace = namespace;
        this.name = name;
        this.value = value;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
