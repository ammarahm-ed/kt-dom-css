package com.example.myapplication.style.css;

import com.example.myapplication.dom.Element;
import com.example.myapplication.style.selectors.Selector;

import java.util.List;

public class RuleSet {
    public List<Selector> selectors;
    public List<Declaration> declarations;

    public RuleSet() {}

    public boolean matches(Element element) {
        for (Selector selector : selectors) {
            if (selector.matches(element)) {
                return true;
            }
        }
        return false;
    }
}