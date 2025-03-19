package com.example.myapplication.style.selectors;

import com.example.myapplication.dom.Element;

import java.util.List;

/**
 * A compound selector is a sequence of simple selectors that are not separated by a combinator.
 * A compound selector represents a set of simultaneous conditions on a single element.
 * A given element is said to match a compound selector when the element matches all
 * the simple selectors in the compound selector.
 *
 * e.g a#selected
 */
public class CompoundSelector extends Selector {
    public List<SimpleSelector> simpleSelectors;

    public CompoundSelector(List<SimpleSelector> simpleSelectors) {
        this.simpleSelectors = simpleSelectors;
        this.simpleSelectors.forEach(sel -> sel.parent = this);
        this.type = "compound";
        this.position = this.simpleSelectors.get(0).position;
        this.specificity = SpecificityCalculator.calculateSpecificitySimpleSelectors( this.simpleSelectors);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean matches(Element element) {
        for (int i = simpleSelectors.size() - 1; i >= 0; i--) {
            SimpleSelector simpleSelector = (SimpleSelector) simpleSelectors.get(i);
            if (!simpleSelector.matches(element)) {
                return false;
            }
        }
        return true;
    }
}
