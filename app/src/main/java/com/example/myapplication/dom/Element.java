package com.example.myapplication.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Element extends ParentNode {
    public String tagName;
    public String localName;
    public NamedNodeMap attributes;
    private String id;
    private String className;
    private DOMTokenList classList = new DOMTokenList();
    public HashMap<String, Boolean> pseudoClasses = new HashMap();

    public boolean hasPseudoClass(String pseudoClassName) {
        return pseudoClasses.containsKey(pseudoClassName);
    }

    public void addPseudoClass(String pseudoClass, Boolean initialValue) {
        pseudoClasses.put(pseudoClass, initialValue);
    }

    public void togglePseudoClass(String pseudoClass) {
        pseudoClasses.put(pseudoClass, !Boolean.TRUE.equals(pseudoClasses.get(pseudoClass)));
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        setAttribute("id", id);
    }


    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.classList = new DOMTokenList();
        if (className != null && !className.isEmpty()) {
            String[] classes = className.split("\\s+");
            for (String cls : classes) {
                classList.add(cls);
            }
        }
        this.className = className;
        setAttribute("class", className);
    }

    public DOMTokenList getClassList() {
        return classList;
    }

    public Element(String nodeName) {
        super(nodeName);
        this.nodeType = Node.ELEMENT_NODE;
        this.tagName = nodeName.toUpperCase();
        this.localName = nodeName.toUpperCase();
        this.attributes = new NamedNodeMap();
    }

    public String getTagName() {
        return tagName;
    }

    public String getAttribute(String name) {
        Attr attr = attributes.getNamedItem(name);
        return attr != null ? attr.getValue() : null;
    }

    public void setAttribute(String name, String value) {
        Attr attr = new Attr(null, name, value);
        attributes.setNamedItem(attr);
    }

    public void removeAttribute(String name) {
        attributes.removeNamedItem(name);
    }

    public String getAttributeNS(String namespace, String name) {
        Attr attr = attributes.getNamedItemNS(namespace, name);
        return attr != null ? attr.getValue() : null;
    }

    public void setAttributeNS(String namespace, String name, String value) {
        Attr attr = new Attr(namespace, name, value);
        attributes.setNamedItemNS(attr);
    }

    public void removeAttributeNS(String namespace, String name) {
        attributes.removeNamedItemNS(namespace, name);
    }

    public String getTextContent() {
        StringBuilder textContent = new StringBuilder();
        Node currentNode = getFirstChild();
        while (currentNode != null) {
            if (currentNode.getNodeType() != Node.COMMENT_NODE) {
                String nodeTextContent = currentNode.getTextContent();
                if (nodeTextContent != null) {
                    textContent.append(nodeTextContent);
                }
            }
            currentNode = currentNode.getNextSibling();
        }
        return textContent.toString();
    }

    public void setTextContent(String textContent) {
        while (getFirstChild() != null) {
            removeChild(getFirstChild());
        }
        appendChild(new TextNode(textContent));
    }

    public Element getParentElement() {
        if (this.parentNode != null && this.parentNode.nodeType == Node.ELEMENT_NODE) return (Element) this.parentNode;

        return null;
    }

    public Element getFirstElementChild() {
        if (this.firstChild != null && this.firstChild.nodeType == Node.ELEMENT_NODE) return (Element) this.firstChild;
        return null;
    }

    public Element getLastElementChild() {
        if (this.lastChild != null && this.lastChild.nodeType == Node.ELEMENT_NODE) return (Element) this.lastChild;
        return null;
    }

    public Element getPreviousElementSibling() {
        if (this.previousSibling != null && this.previousSibling.nodeType == Node.ELEMENT_NODE)
            return (Element) this.previousSibling;

        return null;
    }

    public Element getNextElementSibling() {
        if (this.nextSibling != null && this.nextSibling.nodeType == Node.ELEMENT_NODE) return (Element) this.nextSibling;

        return null;
    }


}
