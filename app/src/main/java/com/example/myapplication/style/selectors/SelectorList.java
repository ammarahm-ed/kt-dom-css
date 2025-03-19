package com.example.myapplication.style.selectors;

import com.example.myapplication.dom.Element;
import java.util.List;

public class SelectorList extends Selector {
    private final List<Selector> selectors;

    public SelectorList(List<Selector> selectors) {
        this.selectors = selectors;
        this.selectors.forEach(sel -> sel.parent = this);
    }

    @Override
    public boolean matches(Element element) {
        for (Selector selector : selectors) {
            if (selector.matches(element)) {
                return true;
            }
        }
        return false;
    }
}
