package com.example.myapplication.dom;

public class Window {
private Document document;

public Window() {
    DOMImplementation domImplementation = new DOMImplementation();
    this.document = domImplementation.createDocument();
}

public Document getDocument() {
    return document;
}
}