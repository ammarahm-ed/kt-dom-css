package com.example.myapplication.dom;

public class EventInit {
    public Boolean bubbles;
    public Boolean cancelable;
    public Boolean composed;
    public Boolean directEvent;

    public static EventInit create(boolean bubbles, boolean cancelable, boolean composed, boolean directEvent) {
        EventInit eventInit = new EventInit();
        eventInit.bubbles = bubbles;
        eventInit.cancelable = cancelable;
        eventInit.composed = composed;
        eventInit.directEvent = directEvent;
        return eventInit;
    }

    public EventInit() {
        this.bubbles = false;
        this.cancelable = false;
        this.composed = false;
    }
}
