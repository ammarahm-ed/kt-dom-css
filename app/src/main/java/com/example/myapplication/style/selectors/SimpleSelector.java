package com.example.myapplication.style.selectors;

import com.example.myapplication.dom.Element;

public class SimpleSelector extends Selector {
    public String id;
    public String className;
    public String attribute;
    public String attributeValue;
    public String pseudoClass;
    public String type;
    public String tag;
    public Boolean universal = false;

    public SimpleSelector() {}

    @Override
    public boolean matches(Element element) {
        if (universal) {
            return true;
        }
        if (tag != null && (tag.equals("*") || tag.equals(element.getNodeName()))) {
            return true;
        }
        if (id != null && id.equals(element.getId())) {
            return true;
        }
        if (className != null && element.getClassList().contains(className)) {
            return true;
        }
        if (attribute != null) {
            String attrValue = element.getAttribute(attribute);
            if (attributeValue == null || attributeValue.isEmpty() || attributeValue.equals(attrValue)) {
                return true;
            }
        }
        if (pseudoClass != null && element.hasPseudoClass(pseudoClass)) {
            return true;
        }
        return false;
    }
}
