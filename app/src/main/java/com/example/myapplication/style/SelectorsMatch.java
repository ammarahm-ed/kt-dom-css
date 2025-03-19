package com.example.myapplication.style;

import com.example.myapplication.dom.Element;
import com.example.myapplication.style.selectors.Selector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectorsMatch {
    /**
     * Dynamic selectors need to subscribe to some kind of view updates like adding/removing children, attributes, pseudoClasses etc.
     * When a selector changes it's value for the relevant view, we will update the view accordingly reapplying all styles again.
     *
     */
    public HashMap<Element, List<Selector>> dynamicSelectorsMap = new HashMap<>();
    /**
     * List of selectors that match a specific element.
     * We will apply all the rulsets that these selectors link to on the view.
     */
    public List<Selector> selectors = new ArrayList<>();
    // Other methods and properties
}