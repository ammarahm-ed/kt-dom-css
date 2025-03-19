package com.example.myapplication.dom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DOMTokenList {
    private List<String> tokens;

    public DOMTokenList() {
        this.tokens = new ArrayList<>();
    }

    public int getLength() {
        return tokens.size();
    }

    public String item(int index) {
        if (index < 0 || index >= tokens.size()) {
            return null;
        }
        return tokens.get(index);
    }

    public boolean contains(String token) {
        return tokens.contains(token);
    }

    public void add(String... tokensToAdd) {
        for (String token : tokensToAdd) {
            if (!tokens.contains(token)) {
                tokens.add(token);
            }
        }
    }

    public void remove(String... tokensToRemove) {
        tokens.removeAll(Arrays.asList(tokensToRemove));
    }

    public boolean toggle(String token) {
        if (tokens.contains(token)) {
            tokens.remove(token);
            return false;
        } else {
            tokens.add(token);
            return true;
        }
    }

    public boolean toggle(String token, boolean force) {
        if (force) {
            if (!tokens.contains(token)) {
                tokens.add(token);
            }
            return true;
        } else {
            if (tokens.contains(token)) {
                tokens.remove(token);
                return false;
            } else {
                tokens.add(token);
                return true;
            }
        }
    }

    public String toString() {
        return String.join(" ", tokens);
    }
}
