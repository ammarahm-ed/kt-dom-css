package com.example.myapplication.dom;

public class CustomEvent extends Event {
    private final Object detail;

    public CustomEvent(String type, CustomEventInit eventInitDict) {
        super(type, eventInitDict);
        this.detail = eventInitDict.detail;
    }

    public Object getDetail() {
        return detail;
    }
}
