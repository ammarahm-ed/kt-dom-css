package com.example.myapplication.style.css;

import com.google.gson.Gson

val removeCommentsRegex = Regex("\\/\\*.*?\\*\\/", RegexOption.DOT_MATCHES_ALL)
val tokenizeRegex = Regex("\n+|(?=[{}:;])|(?<=[{}:;])");

// Function to remove comments from CSS
fun removeComments(css: String): String {
    return css.replace(removeCommentsRegex, "")
}
// Tokenizer function to split CSS input into tokens
fun tokenize(css: String): List<String> {
    return css.split(tokenizeRegex)
}


// Parser function to convert tokens into CSS components
fun parse(css: String): SyntaxTree {
    val tokens = tokenize(removeComments(css))
    val rules = mutableListOf<Rule>()
    var i = 0

    var firstRule = false;

    fun parseRule(ruleList: MutableList<Rule>, parentSelectors: List<String> = emptyList()): Rule {
        val selectors = mutableListOf<String>()
        val declarations = mutableListOf<Declaration>()
        var hasOpened = false;

        if (!firstRule) {
            i = 0;
            firstRule = true;
        }

        if (tokens[i + 1].equals(":")) {
            for (index in i..tokens.size) {
                if (tokens[index].equals("{")) {
                    break;
                } else if (tokens[index].equals(";")) {
                    hasOpened = true;
                }
            }
        }

        val rule = Rule(
            selectors,
            declarations,
            Position(LineColumn(i, i), LineColumn(0, 0)),
            null,
            null,
            null,
            null
        )
        ruleList.add(rule);

        while (i < tokens.size && tokens[i] != "}") {
            when (tokens[i]) {
                "" -> {}
                " " -> {}
                "{" -> {
                    if (hasOpened) {
                        while (!tokens[--i].equals((";"))) {
                            if (i == 0) break;
                        }
                        i++;
                        parseRule(ruleList, selectors);
                    } else {
                        hasOpened = true;
                    }

                }

                ":" -> {
                    if (!hasOpened) {
                        val sel = tokens[i].split(",");
                        selectors.addAll(sel);
                    } else {
                        val property = tokens[i - 1]
                        val value = tokens[++i];
                        declarations.add(Declaration(property, value))
                    }
                }

                ";" -> {}
                else -> {
                    if (!hasOpened) {
                        val sel = tokens[i].split(",");
                        selectors.addAll(sel);
                    }
                }
            }
            i++
        }
        val combinedSelectors = if (parentSelectors.isNotEmpty()) {
            parentSelectors.flatMap { parent ->
                selectors.map { selector ->
                    if (selector.trim().equals("&")) {
                        parent + " " + selector.replace("&", "*")
                    } else {
                        parent + " " + selector.replace("&", "")
                    }
                }
            }
        } else {
            selectors
        }

        rule.selectors = combinedSelectors.toMutableList();
        rule.declarations = declarations;
        rule.pos.end.line = i
        rule.pos.end.column = i

        return rule;
    }

    while (i < tokens.size) {
        when (tokens[i]) {
            "" -> {}
            "@import" -> {
                val start = i;
                val url = tokens[++i]
                val rule = Rule(
                    null,
                    null,
                    Position(LineColumn(start, start), LineColumn(i, i)),
                    null, null, null, url
                );
                rule.type = "import"
                rules.add(
                    rule
                )

            }

            "@media" -> {
                val start = i;
                val query = tokens[++i]
                val mediaRules = mutableListOf<Rule>()
                while (tokens[i] != "}") {
                    if (tokens[i] == "{") {
                        i++
                        mediaRules.add(parseRule(mediaRules))
                    }
                    i++
                }
                val rule = Rule(
                    null,
                    null,
                    Position(LineColumn(start, start), LineColumn(i, i)),
                    null, mediaRules, query, null
                );
                rule.type = "import"
                rules.add(
                    rule
                )
            }

            "@keyframes" -> {
                val start = i;
                val name = tokens[++i]
                val keyFrameRules = mutableListOf<Rule>()
                while (tokens[i] != "}") {
                    if (tokens[i] == "{") {
                        i++
                        parseRule(keyFrameRules);
                    }
                    i++
                }

                val rule = Rule(
                    null,
                    null,
                    Position(LineColumn(start, start), LineColumn(i, i)),
                    name, keyFrameRules, null, null
                );
                rule.type = "keyframes"
                rules.add(
                    rule
                )
            }

            "@font-face" -> {
                ++i
                val rule = parseRule( mutableListOf<Rule>())
                rule.selectors = null;
                rule.type = "font-face"
                rules.add(rule)
            }

            "{" -> {
                if (i != 1) {
                    while (!tokens[--i].equals("}")) {
                        if (i == 0) break;
                    }
                }
                i++
                parseRule(rules)
            }

            ":" -> {
                if (i != 1) {
                    while (!tokens[--i].equals("}")) {
                        if (i == 0) break;
                    }
                }
                i++
                parseRule(rules)
            }
            else -> {
//                val start = i;
//                val name = tokens[i];
//                val atRuleValue = tokens[++i]
//                val rule = Rule(
//                    mutableListOf("@$name"),
//                    mutableListOf(Declaration("value", atRuleValue)),
//                    Position(LineColumn(start, start), LineColumn(i, i)),
//                    null, null, null, null
//                );
//                rule.type = name;
//                rules.add(
//                    rule
//                )
            }
        }
        i++
    }

    return SyntaxTree(StyleSheet(rules))
}

fun parseFromJSON(jsonString: String?): SyntaxTree {
    val gson = Gson()
    return gson.fromJson(jsonString, SyntaxTree::class.java)
}


// Example usage
fun main() {

    val css =
        """parent{color: red;}"""

    val start = System.nanoTime();
    for (i in 1..100) {
       parse(css)
    }
    val end = System.nanoTime();

    println("${(end - start) / 100}ns")
}