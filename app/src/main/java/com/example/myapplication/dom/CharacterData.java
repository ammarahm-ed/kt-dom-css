package com.example.myapplication.dom;

public class CharacterData extends Node {
    private String data;
    public CharacterData(String nodeName) {
        super(nodeName);
        this.data = "";
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String getNodeValue() {
        return this.data;
    }

    @Override
    public void setNodeValue(String nodeValue) {
        this.data = nodeValue;
    }

    public int getLength() {
        return data.length();
    }

    public String substringData(int offset, int count) {
        if (offset < 0 || offset >= data.length() || count < 0) {
            throw new IndexOutOfBoundsException("Invalid offset or count");
        }
        return data.substring(offset, Math.min(offset + count, data.length()));
    }

    public void appendData(String arg) {
        data += arg;
    }

    public void insertData(int offset, String arg) {
        if (offset < 0 || offset > data.length()) {
            throw new IndexOutOfBoundsException("Invalid offset");
        }
        data = data.substring(0, offset) + arg + data.substring(offset);
    }

    public void deleteData(int offset, int count) {
        if (offset < 0 || offset >= data.length() || count < 0) {
            throw new IndexOutOfBoundsException("Invalid offset or count");
        }
        data = data.substring(0, offset) + data.substring(Math.min(offset + count, data.length()));
    }

    public void replaceData(int offset, int count, String arg) {
        if (offset < 0 || offset >= data.length() || count < 0) {
            throw new IndexOutOfBoundsException("Invalid offset or count");
        }
        data = data.substring(0, offset) + arg + data.substring(Math.min(offset + count, data.length()));
    }
}