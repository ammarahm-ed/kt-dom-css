package com.example.myapplication.dom;

class DocumentFragment extends Node {
    public int nodeType = Node.DOCUMENT_FRAGMENT_NODE;

    public DocumentFragment() {
        super("#document-fragment");
    }

    @Override
    public String getNodeName() {
        return "#document-fragment";
    }

    @Override
    public int getNodeType() {
        return this.nodeType;
    }

    // Additional methods can be added as needed
}
