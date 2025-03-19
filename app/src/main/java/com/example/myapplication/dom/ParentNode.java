package com.example.myapplication.dom;

public class ParentNode extends Node {
    public ParentNode(String nodeName) {
        super(
                nodeName
        );
    }

    public void append(Node... nodes) {
        for (Node node : nodes) {
            appendChild(node);
        }
    }

    public void prepend(Node... nodes) {
        for (int i = nodes.length - 1; i >= 0; i--) {
            insertBefore(nodes[i], getFirstChild());
        }
    }
}