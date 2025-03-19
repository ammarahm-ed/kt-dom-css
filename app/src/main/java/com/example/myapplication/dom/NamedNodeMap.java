package com.example.myapplication.dom;

import java.util.ArrayList;
import java.util.List;

public class NamedNodeMap {
    private List<Attr> attributes;

    public NamedNodeMap() {
        this.attributes = new ArrayList<>();
    }

    public Attr getNamedItem(String name) {
        for (Attr attr : attributes) {
            if (attr.getName().equalsIgnoreCase(name)) {
                return attr;
            }
        }
        return null;
    }

    public Attr getNamedItemNS(String namespace, String name) {
        for (Attr attr : attributes) {
            if (attr.getNamespace().equalsIgnoreCase(namespace) && attr.getName().equalsIgnoreCase(name)) {
                return attr;
            }
        }
        return null;
    }

    public void setNamedItem(Attr attr) {
        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).getName().equalsIgnoreCase(attr.getName())) {
                attributes.set(i, attr);
                return;
            }
        }
        attributes.add(attr);
    }

    public void setNamedItemNS(Attr attr) {
        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).getNamespace().equalsIgnoreCase(attr.getNamespace()) && attributes.get(i).getName().equalsIgnoreCase(attr.getName())) {
                attributes.set(i, attr);
                return;
            }
        }
        attributes.add(attr);
    }

    public void removeNamedItem(String name) {
        attributes.removeIf(attr -> attr.getName().equalsIgnoreCase(name));
    }

    public void removeNamedItemNS(String namespace, String name) {
        attributes.removeIf(attr -> attr.getNamespace().equalsIgnoreCase(namespace) && attr.getName().equalsIgnoreCase(name));
    }

    public int getLength() {
        return attributes.size();
    }

    public Attr item(int index) {
        if (index < 0 || index >= attributes.size()) {
            return null;
        }
        return attributes.get(index);
    }
}
