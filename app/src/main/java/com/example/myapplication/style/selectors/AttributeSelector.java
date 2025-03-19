package com.example.myapplication.style.selectors;

import com.example.myapplication.dom.Element;
import com.example.myapplication.style.css.Specificity;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class AttributeSelector extends SimpleSelector {
    public String attribute;
    public String operator;
    public String attributeValue;
    public boolean caseSensitive;

    public AttributeSelector(String attribute, @Nullable  String operator, String value, boolean caseSensitive) {
        this.type = "attribute";
        this.attribute = attribute;
        this.operator = operator;
        this.attributeValue = value;
        this.caseSensitive = caseSensitive;
        this.specificity = Specificity.Attribute.getValue();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean matches(Element element) {
        String attrValue = element.getAttribute(attribute);
        if (attrValue == null) {
            return false;
        }

        switch (operator) {
            case "=":
                return caseSensitive ? attrValue.equals(attributeValue) : attrValue.equalsIgnoreCase(attributeValue);
            case "~=":
                return Arrays.asList(attrValue.split("\\s+")).contains(attributeValue);
            case "|=":
                return attrValue.equals(attributeValue) || attrValue.startsWith(attributeValue + "-");
            case "^=":
                return attrValue.startsWith(attributeValue);
            case "$=":
                return attrValue.endsWith(attributeValue);
            case "*=":
                return attrValue.contains(attributeValue);
            default:
                return false;
        }
    }


}
