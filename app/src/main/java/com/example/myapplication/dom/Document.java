package com.example.myapplication.dom;
import java.util.List;
import java.util.ArrayList;
public class Document extends ParentNode {
    public int nodeType = Node.DOCUMENT_NODE;

    private Element documentElement;
    public Document() {
        super("#document");
    }

    public Element getDocumentElement() {
        return documentElement;
    }

    public Element createElement(String tagName) {
        Element element = new Element(tagName);
        element.ownerDocument = this;
        return element;
    }

    public TextNode createTextNode(String data) {
        return new TextNode(data);
    }

    public Element getElementById(String id) {
        return getElementByIdRecursive(documentElement, id);
    }

    private Element getElementByIdRecursive(Element element, String id) {
        if (element == null) {
            return null;
        }
        if (id.equals(element.getId())) {
            return element;
        }
        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element) {
                Element result = getElementByIdRecursive((Element) child, id);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public List<Element> getElementsByTagName(String tagName) {
        List<Element> result = new ArrayList<>();
        getElementsByTagNameRecursive(documentElement, tagName, result);
        return result;
    }

    private void getElementsByTagNameRecursive(Node node, String tagName, List<Element> result) {
        if (node == null) {
            return;
        }
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element && ((Element) child).getTagName().equalsIgnoreCase(tagName)) {
                result.add((Element) child);
            }
            if (child instanceof ParentNode) {
                getElementsByTagNameRecursive(child, tagName, result);
            }
        }
    }

    @Override
    public <T extends Node> T appendChild(T node) {
        if (node instanceof Element) {
            if (documentElement != null) {
                throw new IllegalStateException("Document already has a documentElement.");
            }
            documentElement = (Element) node;
        }
        return super.appendChild(node);
    }
}
