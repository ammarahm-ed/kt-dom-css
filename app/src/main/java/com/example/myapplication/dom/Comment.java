package com.example.myapplication.dom;

class Comment extends CharacterData {
    public int nodeType = Node.COMMENT_NODE;
    public Comment(String data) {
        super("#comment");
        setData(data);
    }
}
