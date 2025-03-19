package com.example.myapplication.style.selectors;

import java.util.List;
import java.util.Arrays;

public class SpecificityCalculator {

    public static int calculateSpecificity(List<Selector> selectors) {
        int[] specificity = {0, 0, 0};
        int totalSpecifcity = 0;

        for (Selector selector : selectors) {
            if (selector.type.equals("id")) {
                specificity[0]++;
            } else if (selector.type.equals("class")) {
                specificity[1]++;
            } else if (selector.type.equals("attribute")) {
                specificity[1]++;
            } else if (selector.type.equals("pseudo")) {
//                if (selector.type.equals("psuedoElement")) {
//                    specificity[2]++;
//                } else {
                    specificity[1]++;
//                }
            } else if (selector.type.equals("compound")) {
                totalSpecifcity += selector.specificity;

            } else if (selector.type.equals("relative")) {
                totalSpecifcity += selector.specificity;
            } else if (selector.type.equals("complex")) {
                totalSpecifcity += selector.specificity;
            } else {
                specificity[2]++;
            }
        }

        totalSpecifcity += specificityToNumber(specificity, 0);

        return totalSpecifcity;
    }

    public static int calculateSpecificitySimpleSelectors(List<SimpleSelector> selectors) {
        int[] specificity = {0, 0, 0};
        int totalSpecifcity = 0;

        for (SimpleSelector selector : selectors) {
            if (selector.type.equals("id")) {
                specificity[0]++;
            } else if (selector.type.equals("class")) {
                specificity[1]++;
            } else if (selector.type.equals("attribute")) {
                specificity[1]++;
            } else if (selector.type.equals("psuedo")) {
//                if (selector.type.equals("psuedoElement")) {
//                    specificity[2]++;
//                } else {
                specificity[1]++;
//                }
            } else {
                specificity[2]++;
            }
        }

        totalSpecifcity += specificityToNumber(specificity, 0);

        return totalSpecifcity;
    }

    public static int specificityToNumber(int[] specificity, int base) {
        base = Math.max(base, Arrays.stream(specificity).max().orElse(0) + 1);
        return specificity[0] * (base << 1) + specificity[1] * base + specificity[2];
    }
}
