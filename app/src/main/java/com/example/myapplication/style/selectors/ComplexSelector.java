package com.example.myapplication.style.selectors;

import com.example.myapplication.dom.Element;

import java.util.List;

/**
 * A complex selector is a sequence of one or more simple and/or compound selectors
 * that are separated by combinators, including the white space descendant combinator.
 */
public class ComplexSelector extends Selector {
    public final List<Selector> selectors;
    public final List<Combinator> combinators;

    public ComplexSelector(List<Selector> selectors, List<Combinator> combinators) {
        this.selectors = selectors;
        this.combinators = combinators;

        this.selectors.forEach(sel -> sel.parent = this);

        this.specificity = SpecificityCalculator.calculateSpecificity(this.selectors);
        this.type = "complex";

        this.position = this.selectors.get(0).position;
    }

    @Override
    public boolean matches(Element element) {
        // Start with the last selector and work backwards
        Selector currentSelector = selectors.get(selectors.size() - 1);
        if (!currentSelector.matches(element)) {
            return false;
        }

        Element currentElement = element;
        // We skip the right most selector's combinator here as we are only
        // concerned with combinators that come in between two selectors.
        // a > b will require us to loop once to check if an element `b` has a parent `a` to
        // apply the relevant ruleset.
        for (int i = combinators.size() - 2; i >= 0; i--) {
            Combinator combinator = combinators.get(i);
            currentSelector = selectors.get(i);

            switch (combinator.getSymbol()) {
                case " ":
                    // Descendant combinator
                    currentElement = findAncestor(currentElement, currentSelector);
                    break;
                case ">":
                    // Child combinator
                    currentElement = findParent(currentElement, currentSelector);
                    break;
                case "+":
                    // Adjacent sibling combinator
                    currentElement = findPreviousSibling(currentElement, currentSelector);
                    break;
                case "~":
                    // General sibling combinator
                    currentElement = findAnyPreviousSibling(currentElement, currentSelector);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown combinator: " + combinator);
            }

            if (currentElement == null) {
                return false;
            }
        }

        return true;
    }

    private Element findAncestor(Element element, Selector selector) {
        Element parent = element.getParentElement();
        while (parent != null) {
            if (selector.matches(parent)) {
                return parent;
            }
            parent = parent.getParentElement();
        }
        return null;
    }

    private Element findParent(Element element, Selector selector) {
        Element parent = element.getParentElement();
        return (parent != null && selector.matches(parent)) ? parent : null;
    }

    private Element findPreviousSibling(Element element, Selector selector) {
        Element previousSibling = (Element) element.previousSibling;
        return (previousSibling != null && selector.matches(previousSibling)) ? previousSibling : null;
    }

    private Element findAnyPreviousSibling(Element element, Selector selector) {
        Element sibling = (Element) element.getPreviousSibling();
        while (sibling != null) {
            if (selector.matches(sibling)) {
                return sibling;
            }
            sibling = (Element) sibling.getPreviousSibling();
        }
        return null;
    }
}
