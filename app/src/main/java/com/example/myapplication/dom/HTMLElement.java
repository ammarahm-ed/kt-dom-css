package com.example.myapplication.dom;

public class HTMLElement extends Element {


    public HTMLElement(String tagName) {
        super(tagName);
    }



    public void click() {
        // Dispatch a click event
        Event clickEvent = new Event("click", new EventInit());
        dispatchEvent(clickEvent);
    }

    // Additional properties and methods can be added as needed
}
