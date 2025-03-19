package com.example.myapplication.dom;

public class CustomEventInit extends EventInit {
    public Object detail;

    public static CustomEventInit create(boolean bubbles, boolean cancelable, boolean composed, Object detail, boolean directEvent) {
        CustomEventInit eventInit = new CustomEventInit();
        eventInit.bubbles = bubbles;
        eventInit.cancelable = cancelable;
        eventInit.composed = composed;
        eventInit.directEvent = directEvent;
        eventInit.detail = detail;
        return eventInit;
    }

    public CustomEventInit() {
        super();
        this.detail = null;
    }
}
