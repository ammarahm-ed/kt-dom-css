package com.example.myapplication.style;

import com.example.myapplication.dom.DOMTokenList;
import com.example.myapplication.dom.Element;
import com.example.myapplication.style.css.RuleSet;
import com.example.myapplication.style.selectors.Selector;
import com.example.myapplication.style.selectors.SimpleSelector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

interface LookupSorter {
    void sortSelector(Selector selector);
}

public class SelectorsMap implements LookupSorter {
    private final Map<String, HashSet<Selector>> id = new HashMap<>();
    private final Map<String, HashSet<Selector>> classMap = new HashMap<>();
    private final Map<String, HashSet<Selector>> nodeType = new HashMap<>();
    private final HashSet<Selector> universal = new HashSet<>();
    private final HashSet<Selector> attributes = new HashSet<>();
    private int pos = 0;
    public List<RuleSet> ruleSets;


    @Override
    public void sortSelector(Selector selector) {
        if (selector.isSimpleSelector()) {
            SimpleSelector simpleSelector = (SimpleSelector) selector;
            if (simpleSelector.type.equals("id")) {
                addToMap(id, simpleSelector.id, selector);
            } else if (simpleSelector.type.equals("class")) {
                addToMap(classMap, simpleSelector.className, selector);
            } else if (simpleSelector.type.equals("tag")) {
                addToMap(nodeType, simpleSelector.tag, selector);
            } else if (simpleSelector.universal) {
                universal.add(selector);
            } else if (selector.type.equals("attribute") && selector.parent == null) {
                attributes.add(selector);

            }
        }
    }

    private void addToMap(Map<String, HashSet<Selector>> map, String key, Selector selector) {
        selector.position = pos++;
        map.computeIfAbsent(key, k -> new HashSet<>()).add(selector);
    }

    private Selector getRootSelector(Selector selector) {
        if (selector.parent == null) return selector;
        Selector sel = selector;
        while (sel.parent != null) {
            sel = sel.parent;
        }

        return sel;
    }

    public SelectorsMatch query(Element node) {
        SelectorsMatch selectorsMatch = new SelectorsMatch();

        String id = node.getId();
        String tagName = node.getNodeName();
        DOMTokenList classList = node.getClassList();

        List<Selector> selectorList = new ArrayList<>(universal);

        for (Selector attribute: attributes) {
            if (attribute.parent == null) {
                selectorList.addAll(attributes);
            }
        }


        if (id != null && this.id.containsKey(id)) {
            for (Selector selector : this.id.get(id)) {
                selectorList.add(getRootSelector(selector));
            }
        }

        if (tagName != null && this.nodeType.containsKey(tagName)) {
            for (Selector selector : this.nodeType.get(tagName)) {
                selectorList.add(getRootSelector(selector));
            }
        }
        if (classList.getLength() != 0) {
            for (int i = 0; i < classList.getLength(); i++) {
                String cls = classList.item(i);
                if (this.classMap.containsKey(cls)) {
                    for (Selector selector : this.classMap.get(cls)) {
                        selectorList.add(getRootSelector(selector));
                    }
                }
            }
        }

        for (Selector selector : selectorList) {
            if (selector.accumlateChanges(node, selectorsMatch)) {
                selectorsMatch.selectors.add(selector);
            }
        }

        selectorsMatch.selectors.sort(Comparator.comparingInt((Selector a) -> a.specificity).thenComparingInt(a -> a.position));

        return selectorsMatch;
    }
}
