package com.example.myapplication.style.selectors;

import com.example.myapplication.dom.Element;
import com.example.myapplication.style.SelectorsMatch;

import java.util.Arrays;
import java.util.List;

// RelativeSelector class
public class RelativeSelector extends Selector {
    private final String pseudoClass;
    public final Selector anchorSelector;
    public final Selector relativeSelector;

    public RelativeSelector(String pseudoClass, Selector anchorSelector, Selector relativeSelector) {
        this.pseudoClass = pseudoClass;
        this.anchorSelector = anchorSelector;
        this.relativeSelector = relativeSelector;
        this.anchorSelector.parent = this;
        this.relativeSelector.parent = this;
        this.type = "relative";

        List<Selector> list = Arrays.asList(this.anchorSelector, this.relativeSelector);
        this.specificity = SpecificityCalculator.calculateSpecificity(list);
        this.position = this.anchorSelector.position;
    }

    @Override
    public boolean accumlateChanges(Element element, SelectorsMatch match) {
        if (!anchorSelector.matches(element)) {
            return false;
        }
        this.trackChanges(element, match);

        return this.matches(element);
    }

    @Override
    public boolean matches(Element element) {
        switch (pseudoClass) {
            case "has":
                return matchesHas(element);
            case "not":
                return matchesNot(element);
            // Add other pseudo-classes as needed
            default:
                return false;
        }
    }

    private boolean matchesHas(Element element) {
        // Check if the relativeSelector is a ComplexSelector
        if (relativeSelector instanceof ComplexSelector) {
            ComplexSelector complexSelector = (ComplexSelector) relativeSelector;
            if (complexSelector.selectors.size() == 1 && !complexSelector.combinators.isEmpty()) {
                Combinator combinator = complexSelector.combinators.get(0);
                Selector firstSelector = complexSelector.selectors.get(0);

                switch (combinator.getSymbol()) {
                    case "+":
                        return hasMatchingAdjacentSibling(element, firstSelector);
                    case ">":
                        return hasMatchingChild(element, firstSelector);
                    case "~":
                        return hasMatchingGeneralSibling(element, firstSelector);
                    default:
                        return false;
                }
            }
        }

        return hasMatchingDescendant(element, relativeSelector);
    }

    private boolean hasMatchingDescendant(Element element, Selector selector) {
        Element child = element.getFirstElementChild();
        while (child != null) {
            if (selector.matches(child) || hasMatchingDescendant(child, selector)) {
                return true;
            }
            child = child.getNextElementSibling();
        }
        return false;
    }

    private boolean hasMatchingChild(Element element, Selector selector) {
        Element child = element.getFirstElementChild();
        while (child != null) {
            if (selector.matches(child)) {
                return true;
            }
            child = child.getNextElementSibling();
        }
        return false;
    }

    private boolean hasMatchingAdjacentSibling(Element element, Selector selector) {
        Element sibling = element.getNextElementSibling();
        return sibling != null && selector.matches(sibling);
    }

    private boolean hasMatchingGeneralSibling(Element element, Selector selector) {
        Element sibling = element.getNextElementSibling();
        while (sibling != null) {
            if (selector.matches(sibling)) {
                return true;
            }
            sibling = sibling.getNextElementSibling();
        }
        return false;
    }

    private boolean matchesNot(Element element) {

        // Check if the relativeSelector is a ComplexSelector
        if (relativeSelector instanceof ComplexSelector) {
            ComplexSelector complexSelector = (ComplexSelector) relativeSelector;
            if (complexSelector.selectors.size() == 1 && !complexSelector.combinators.isEmpty()) {
                Combinator combinator = complexSelector.combinators.get(0);
                Selector firstSelector = complexSelector.selectors.get(0);

                switch (combinator.getSymbol()) {
                    case "+":
                        return !hasMatchingAdjacentSibling(element, firstSelector);
                    case ">":
                        return !hasMatchingChild(element, firstSelector);
                    case "~":
                        return !hasMatchingGeneralSibling(element, firstSelector);
                    case " ":
                        return !hasMatchingDescendant(element, firstSelector);
                    default:
                        throw new IllegalArgumentException("Unknown combinator: " + combinator);
                }
            }
        }

        return !hasMatchingDescendant(element, relativeSelector);
    }
}