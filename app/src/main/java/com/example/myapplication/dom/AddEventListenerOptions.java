package com.example.myapplication.dom;

public class AddEventListenerOptions extends EventListenerOptions {
    public boolean once;
    public boolean passive;
    public static AddEventListenerOptions create( boolean once, boolean passive, boolean capture) {
        AddEventListenerOptions options = new AddEventListenerOptions();
        options.once = once;
        options.passive = passive;
        options.capture = capture;
        return options;
    }
    public AddEventListenerOptions() {
        super();
        this.once = false;
        this.passive = false;
    }
}
