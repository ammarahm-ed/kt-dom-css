package com.example.myapplication.dom;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Node extends EventTarget {
    public static final int ELEMENT_NODE = 1;
    public static final int ATTRIBUTE_NODE = 2;
    public static final int TEXT_NODE = 3;
    public static final int CDATA_SECTION_NODE = 4;
    public static final int ENTITY_REFERENCE_NODE = 5;
    public static final int ENTITY_NODE = 6;
    public static final int PROCESSING_INSTRUCTION_NODE = 7;
    public static final int COMMENT_NODE = 8;
    public static final int DOCUMENT_NODE = 9;
    public static final int DOCUMENT_TYPE_NODE = 10;
    public static final int DOCUMENT_FRAGMENT_NODE = 11;
    public static final int NOTATION_NODE = 12;

    public static final int DOCUMENT_POSITION_DISCONNECTED = 0x01;
    public static final int DOCUMENT_POSITION_PRECEDING = 0x02;
    public static final int DOCUMENT_POSITION_FOLLOWING = 0x04;
    public static final int DOCUMENT_POSITION_CONTAINS = 0x08;
    public static final int DOCUMENT_POSITION_CONTAINED_BY = 0x10;
    public static final int DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC = 0x20;

    public String baseURI;
    public Node firstChild;
    public boolean isConnected;
    public Node lastChild;
    public Node nextSibling;
    public String nodeName;
    public int nodeType;
    public String nodeValue;
    public Document ownerDocument;
    public HTMLElement parentElement;
    public Node parentNode;
    public Node previousSibling;
    private String textContent;

    public Node(String nodeName) {
        this.firstChild = null;
        this.lastChild = null;
        this.nodeName = nodeName;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public List<Node> getChildNodes() {
        List<Node> children = new ArrayList<>();
        Node current = firstChild;
        while (current != null) {
            children.add(current);
            current = current.nextSibling;
        }
        return children;
    }

    public Node getFirstChild() {
        return firstChild;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public Node getLastChild() {
        return lastChild;
    }

    public Node getNextSibling() {
        return nextSibling;
    }

    public String getNodeName() {
        return nodeName;
    }

    public int getNodeType() {
        return nodeType;
    }

    public String getNodeValue() {
        return nodeValue;
    }

    public void setNodeValue(String nodeValue) {
        this.nodeValue = nodeValue;
    }

    public Document getOwnerDocument() {
        return ownerDocument;
    }
    
    public Node getParentNode() {
        return parentNode;
    }

    public Node getPreviousSibling() {
        return previousSibling;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public <T extends Node> T appendChild(T node) {
        return insertBefore(node, null);
    }


    public Node cloneNode(boolean deep) {
        Node clonedNode = null;

        if (this instanceof ParentNode) {
            if (this.getNodeType() == Node.DOCUMENT_NODE) {
                clonedNode = new Document();
            } else if (this.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE) {
                clonedNode = new DocumentFragment();
            } else if (this instanceof Element) {
                Element element = (Element) this;
                clonedNode = new Element(element.getTagName());
                NamedNodeMap sourceAttrs = element.attributes;
                for (int i = 0; i < sourceAttrs.getLength(); i++) {
                    Attr attr = sourceAttrs.item(i);
                    ((Element) clonedNode).setAttributeNS(attr.getNamespace(), attr.getName(), attr.getValue());
                }
            }

            if (deep) {
                Node currentNode = this.getFirstChild();
                while (currentNode != null) {
                    clonedNode.appendChild(currentNode.cloneNode(deep));
                    currentNode = currentNode.getNextSibling();
                }
            }
        } else if (this.getNodeType() == Node.TEXT_NODE) {
            clonedNode = new TextNode(this.getNodeValue());
        } else if (this.getNodeType() == Node.COMMENT_NODE) {
            clonedNode = new Comment(this.getNodeValue());
        }

        return clonedNode;
    }

    public int compareDocumentPosition(Node other) {
        // Simplified implementation
        if (this == other) return 0;
        return DOCUMENT_POSITION_DISCONNECTED;
    }

    public boolean contains(Node other) {
        if (other == null) return false;
        if (this == other) return true;
        Node child = this.firstChild;
        while (child != null) {
            if (child.contains(other)) return true;
            child = child.nextSibling;
        }
        return false;
    }

    public Node getRootNode() {
        Node root = this;
        while (root.parentNode != null) {
            root = root.parentNode;
        }
        return root;
    }

    public boolean hasChildNodes() {
        return firstChild != null;
    }

    public <T extends Node> T insertBefore(T node, @Nullable Node ref) {
        if (node.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE) {
            Node fragment = (Node) node;
            Node firstChild = fragment.getFirstChild();
            Node lastChild = fragment.getLastChild();
            if (firstChild != null && lastChild != null) {
                Node currentNode = firstChild;
                while (currentNode != null) {
                    Node nextSibling = currentNode.getNextSibling();
                    currentNode.parentNode = this;
                    currentNode = nextSibling;
                }

                if (ref != null) {
                    firstChild.previousSibling = ref.previousSibling;
                    lastChild.nextSibling = ref;
                    ref.previousSibling = lastChild;
                } else {
                    firstChild.previousSibling = this.lastChild;
                    lastChild.nextSibling = null;
                }

                if (firstChild.getPreviousSibling() != null) {
                    firstChild.getPreviousSibling().nextSibling = firstChild;
                } else {
                    this.firstChild = firstChild;
                }

                if (lastChild.getNextSibling() != null) {
                    lastChild.getNextSibling().previousSibling = lastChild;
                } else {
                    this.lastChild = lastChild;
                }

                fragment.firstChild = null;
                fragment.lastChild = null;
            }
            return node;
        }

        if (ref == null) {
            if (lastChild == null) {
                firstChild = node;
            } else {
                lastChild.nextSibling = node;
                node.previousSibling = lastChild;
            }
            lastChild = node;
        } else {
            if (ref.getParentNode() != this) {
                throw new IllegalArgumentException("The node before which the new node is to be inserted is not a child of this node.");
            }
            if (ref == firstChild) {
                firstChild = node;
            } else {
                ref.previousSibling.nextSibling = node;
            }
            node.previousSibling = ref.previousSibling;
            node.nextSibling = ref;
            ref.previousSibling = node;
        }
        node.parentNode = this;

        return node;
    }

    public boolean isDefaultNamespace(String namespace) {
        // Simplified implementation
        return false;
    }

    public boolean isEqualNode(Node otherNode) {
        if (otherNode == null) return false;
        return this.nodeName.equals(otherNode.nodeName) &&
                this.nodeType == otherNode.nodeType &&
                this.nodeValue.equals(otherNode.nodeValue);
    }

    public boolean isSameNode(Node otherNode) {
        return this == otherNode;
    }

    public String lookupNamespaceURI(String prefix) {
        // Simplified implementation
        return null;
    }

    public String lookupPrefix(String namespace) {
        // Simplified implementation
        return null;
    }

    public void normalize() {
        // Simplified implementation
    }

    public <T extends Node> T removeChild(T child) {
        if (child.parentNode != this) {
            throw new IllegalArgumentException("The node to be removed is not a child of this node.");
        }
        if (child == firstChild) {
            firstChild = child.nextSibling;
        } else {
            child.previousSibling.nextSibling = child.nextSibling;
        }
        if (child == lastChild) {
            lastChild = child.previousSibling;
        } else {
            child.nextSibling.previousSibling = child.previousSibling;
        }
        child.parentNode = null;
        child.previousSibling = null;
        child.nextSibling = null;
        return child;
    }

    public <T extends Node> T replaceChild(Node node, T child) {
        insertBefore(node, child);
        removeChild(child);
        return child;
    }
}