package com.example.myapplication.dom;

public class EventListenerOptions {
    public boolean capture;
    public AbortSignal signal;
    public EventListenerOptions() {
        this.capture = false;
    }
}
