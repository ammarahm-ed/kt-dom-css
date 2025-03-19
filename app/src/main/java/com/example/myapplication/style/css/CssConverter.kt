package com.example.myapplication.style.css

import com.example.myapplication.style.SelectorsMap
import com.example.myapplication.style.selectors.AttributeSelector
import com.example.myapplication.style.selectors.AttributeSelectorNode
import com.example.myapplication.style.selectors.ClassSelectorNode
import com.example.myapplication.style.selectors.Combinator
import com.example.myapplication.style.selectors.ComplexSelector
import com.example.myapplication.style.selectors.CompoundSelector
import com.example.myapplication.style.selectors.IdSelectorNode
import com.example.myapplication.style.selectors.Parsed
import com.example.myapplication.style.selectors.PseudoClassSelectorNode
import com.example.myapplication.style.selectors.RelativeSelector
import com.example.myapplication.style.selectors.Selector
import com.example.myapplication.style.selectors.SelectorParser.parseSelector
import com.example.myapplication.style.selectors.SimpleSelector
import com.example.myapplication.style.selectors.SimpleSelectorNode
import com.example.myapplication.style.selectors.TypeSelectorNode
import java.util.stream.Collectors

object CssConverter {
    fun fromAstNodes(syntaxTree: SyntaxTree): SelectorsMap {
        val ruleSets: MutableList<RuleSet> = ArrayList();
        val selectorsMap = SelectorsMap()

        for (rule in syntaxTree.stylesheet.rules) {
            if (rule.type.equals("comment")) continue;

            val ruleSet = RuleSet()

            val declarations = rule.declarations?.stream()
                ?.map { decl: Declaration -> Declaration(decl.property, decl.property) }
                ?.collect(Collectors.toList())

            val selectors = rule.selectors?.stream()
                ?.map { selectorText: String? ->
                    val parsedSelector = parseSelector(selectorText!!, 0)
                    convertParsedSelectorToSelector(parsedSelector, selectorsMap, ruleSet);
                }
                ?.collect(Collectors.toList())

            ruleSet.selectors = selectors;
            if (declarations != null) {
                ruleSet.declarations = declarations;
            }

            ruleSets.add(ruleSet)
        }

        selectorsMap.ruleSets = ruleSets;

        return selectorsMap
    }


    private fun convertParsedSelectorToSelector(parsedSelector: Parsed<List<Pair<List<SimpleSelectorNode>, Combinator?>>>?, sorter: SelectorsMap, ruleSet: RuleSet): Selector? {
        val pairs = parsedSelector?.value ?: return null
        var finalizedSelector: Selector?

        val pseudoNode = if (pairs[0].first.last().type == ":") {
            pairs[0].first.last() as PseudoClassSelectorNode?;
        } else {
            null;
        }

        return if (pairs.size == 1 && (pairs[0].second == null || pairs[0].second == Combinator.SPACE) && pseudoNode?.nestedSelector == null) {
            val nodes: List<SimpleSelectorNode> = pairs[0].first

            if (nodes.size == 1) {
                finalizedSelector = convertNodeToSimpleSelector(nodes[0])
                finalizedSelector.ruleSet = ruleSet;
                sorter.sortSelector(finalizedSelector)

                finalizedSelector
            } else {
                finalizedSelector =
                    CompoundSelector(
                        nodes.stream().map {
                            val sel = convertNodeToSimpleSelector(it)
                            sel.ruleSet = ruleSet;
                            sorter.sortSelector(sel)
                            sel
                        }.collect(Collectors.toList())
                    )

                finalizedSelector
            }
        } else {
            val selectors: MutableList<Selector> = ArrayList()
            val combinators: MutableList<Combinator?> = ArrayList()
            for (pair in pairs) {
                val nodes: List<SimpleSelectorNode> = pair.first
                val selector = if (nodes.any { it is PseudoClassSelectorNode }) {
                    val pseudoClassNode = nodes.first { it is PseudoClassSelectorNode } as PseudoClassSelectorNode

                    val nestedSelector = pseudoClassNode.nestedSelector

                    if (nestedSelector != null) {
                        val relativeSelector = convertParsedSelectorToSelector(Parsed(0, 1, nestedSelector), sorter, ruleSet);

                        val anchorSelector = if (nodes.size > 2) {
                            val anchor =
                                CompoundSelector(
                                    nodes.stream().filter { it !is PseudoClassSelectorNode }.map {
                                        val sel = convertNodeToSimpleSelector(it)
                                        sel.ruleSet = ruleSet;
                                        sorter.sortSelector(sel)
                                        sel
                                    }.collect(Collectors.toList())
                                )
                            anchor.ruleSet = ruleSet;
                            sorter.sortSelector(anchor)
                            anchor

                        } else {
                            val anchor = convertNodeToSimpleSelector(nodes.get(0));
                            anchor.ruleSet = ruleSet;
                            sorter.sortSelector(anchor);
                            anchor
                        }
                        relativeSelector?.ruleSet = ruleSet;
                        sorter.sortSelector(relativeSelector);
                        finalizedSelector =
                            RelativeSelector(
                                pseudoClassNode.identifier,
                                anchorSelector,
                                relativeSelector
                            )
                        finalizedSelector.ruleSet = ruleSet;
                        finalizedSelector;

                    } else {
                        null
                    }
                } else {
                    if (nodes.size > 1) {
                        finalizedSelector =
                            CompoundSelector(
                                nodes.stream().map {
                                    val sel = convertNodeToSimpleSelector(it)
                                    sel.ruleSet = ruleSet;
                                    sorter.sortSelector(sel)
                                    sel
                                }.collect(Collectors.toList())
                            )
                    } else {
                        finalizedSelector = convertNodeToSimpleSelector(nodes.get(0));
                        sorter.sortSelector(finalizedSelector)
                    }
                    finalizedSelector.ruleSet = ruleSet;
                    finalizedSelector
                }
                if (selector != null) {
                    selectors.add(selector)
                    combinators.add(pair.second)
                }
            }
            if (combinators.size > 1 && combinators.any { it != null }) {
                finalizedSelector=
                    ComplexSelector(
                        selectors,
                        combinators
                    )
                finalizedSelector.ruleSet = ruleSet;
                finalizedSelector
            } else {
                finalizedSelector = selectors.firstOrNull();
                finalizedSelector
            }

        }
    }

    private fun convertNodeToSimpleSelector(node: SimpleSelectorNode): SimpleSelector {
        val simpleSelector = SimpleSelector()
        var attributeSelector: AttributeSelector? = null;
        when (node) {
            is TypeSelectorNode -> {
                simpleSelector.type = "tag";
                simpleSelector.tag = node.identifier;
                simpleSelector.specificity = Specificity.Type.value
                simpleSelector.rarity = Rarity.Type.value
            }
            is ClassSelectorNode -> {
                simpleSelector.type = "class"
                simpleSelector.className = node.identifier
                simpleSelector.specificity = Specificity.Class.value
                simpleSelector.rarity = Rarity.Class.value
            }
            is IdSelectorNode -> {
                simpleSelector.type = "id"
                simpleSelector.id = node.identifier
                simpleSelector.specificity = Specificity.Id.value
                simpleSelector.rarity = Rarity.Id.value
            }
            is AttributeSelectorNode -> {
                attributeSelector = AttributeSelector(node.property, node.test?.symbol, node.value, false)
                simpleSelector.specificity = Specificity.Attribute.value
                simpleSelector.rarity = Rarity.Attribute.value
            }
            is PseudoClassSelectorNode -> {
                simpleSelector.type = "pseudo"
                simpleSelector.pseudoClass = node.identifier
                simpleSelector.specificity = Specificity.PseudoClass.value
                simpleSelector.rarity = Rarity.PseudoClass.value
            }
            else -> {
                simpleSelector.universal = true;
                simpleSelector.type = "universal"
                simpleSelector.specificity = Specificity.Universal.value
                simpleSelector.rarity = Rarity.Universal.value
            }
        }

        if (attributeSelector != null) {
            return attributeSelector
        } else {
            return simpleSelector
        }
    }
}