package com.example.myapplication.style.selectors;

import com.example.myapplication.dom.Element;
import com.example.myapplication.style.css.Rarity;
import com.example.myapplication.style.css.RuleSet;
import com.example.myapplication.style.SelectorsMatch;
import com.example.myapplication.style.css.Specificity;

// Define the Selector interface
public abstract class Selector {
    public String type;
    public RuleSet ruleSet;
    public int specificity = Specificity.Universal.getValue();
    public int rarity = Rarity.Universal.getValue();
    public int position = 0;
    public Selector parent;
    public boolean dynamic;

    public boolean accumlateChanges(Element element, SelectorsMatch match) {
        if (!this.dynamic) return this.matches(element);

        if (this.matches(element)) {
            this.trackChanges(element, match);
            return true;
        }
        return false;
    }


    public boolean matches(Element element) {
        return false;
    }

    public void trackChanges(Element element, SelectorsMatch match) {
        // Implemented by dynamic selectors
    }

    public  boolean isSimpleSelector() {
        return !this.type.equals("compound") && !this.type.equals("relative") && this.type.equals("complex");
    }


}
