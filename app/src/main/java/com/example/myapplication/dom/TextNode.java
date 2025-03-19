package com.example.myapplication.dom;

public class TextNode extends CharacterData {
    public int nodeType = Node.TEXT_NODE;
    public TextNode(String data) {
        super("#text");
        setData(data);
    }

    public TextNode splitText(int offset) {
        if (offset < 0 || offset > getLength()) {
            throw new IndexOutOfBoundsException("Invalid offset");
        }
        String newData = substringData(offset, getLength() - offset);
        TextNode newNode = new TextNode(newData);
        newNode.parentNode = getParentNode();
        newNode.nextSibling = (getNextSibling());
        this.nextSibling = newNode;
        newNode.previousSibling = this;
        deleteData(offset, getLength() - offset);
        return newNode;
    }

    @Override
    public String getNodeName() {
        return "#text";
    }

    @Override
    public int getNodeType() {
        return Node.TEXT_NODE;
    }
}
