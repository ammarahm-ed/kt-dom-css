package com.example.myapplication.style.selectors

import com.example.myapplication.dom.Element
import com.example.myapplication.style.css.CssConverter
import com.example.myapplication.style.css.parseFromJSON
import java.util.regex.Pattern

enum class Combinator(val symbol: String) {
    PLUS("+"), TILDE("~"), GREATER(">"), SPACE(" ");

    companion object {
        fun fromString(symbol: String?): Combinator {
            return values().firstOrNull { it.symbol == symbol } ?: SPACE
        }
    }
}

public interface SimpleSelectorNode {
    val type: String;
}

data class UniversalSelectorNode(override val type: String = "*") : SimpleSelectorNode

data class TypeSelectorNode(override val type: String = "", val identifier: String) :
    SimpleSelectorNode

data class ClassSelectorNode(override val type: String = ".", val identifier: String) :
    SimpleSelectorNode

data class IdSelectorNode(override val type: String = "#", val identifier: String) :
    SimpleSelectorNode

data class PseudoClassSelectorNode(
    override val type: String = ":",
    val identifier: String,
    val nestedSelector: List<Pair<List<SimpleSelectorNode>, Combinator?>>? = null
) : SimpleSelectorNode

enum class AttributeSelectorTest(val symbol: String) {
    EQUALS("="), STARTS_WITH("^="), ENDS_WITH("$="), CONTAINS("*="), INCLUDES("~="), DASH_MATCH("|=");

    companion object {
        fun fromString(symbol: String): AttributeSelectorTest? {
            return values().firstOrNull { it.symbol == symbol }
        }
    }
}

data class AttributeSelectorNode(
    override val type: String = "[]",
    val property: String,
    val test: AttributeSelectorTest? = null,
    val value: String? = null
) : SimpleSelectorNode

data class Parsed<T>(val start: Int, val end: Int, val value: T)

object SelectorParser {
    private val universalSelectorRegEx = Pattern.compile("\\*", Pattern.DOTALL)
    private val simpleIdentifierSelectorRegEx = Pattern.compile("(#|\\.|:|\\b)((?:[\\w_-]|\\\\.)(?:[\\w\\d_-]|\\\\.)*)", Pattern.DOTALL)
    private val unicodeEscapeRegEx = Pattern.compile("\\\\([0-9a-fA-F]{1,5}\\s|[0-9a-fA-F]{6})", Pattern.DOTALL)
    private val attributeSelectorRegEx = Pattern.compile("\\[\\s*([_\\-\\w][_\\-\\w\\d]*)\\s*(?:(=|\\^=|\\$=|\\*=|~=|\\|=)\\s*(?:([_\\-\\w][_\\-\\w\\d]*)|\"((?:[^\\\\\"]|\\\\(?:\"|n|r|f|\\\\|0-9a-f))*)\"|'((?:[^\\\\']|\\\\(?:'|n|r|f|\\\\|0-9a-f))*)')\\s*)?\\]", Pattern.DOTALL)
    private val combinatorRegEx = Pattern.compile("\\s*([+~>])?\\s*", Pattern.DOTALL)
    private val whiteSpaceRegEx = Pattern.compile("\\s*", Pattern.DOTALL)
    private val nestedSelectorRegEx = Pattern.compile(":(has|not|matches|is|where)\\(([^()]*(?:\\([^()]*\\)[^()]*)*)\\)", Pattern.DOTALL)

    fun parseUniversalSelector(text: String, start: Int = 0): Parsed<UniversalSelectorNode>? {
        val matcher = universalSelectorRegEx.matcher(text)
        matcher.region(start, text.length)
        if (!matcher.lookingAt()) {
            return null
        }
        val end = matcher.end()
        return Parsed(start, end, UniversalSelectorNode())
    }

    fun parseSimpleIdentifierSelector(text: String, start: Int = 0): Parsed<SimpleSelectorNode>? {
        val matcher = simpleIdentifierSelectorRegEx.matcher(text.replace(unicodeEscapeRegEx.toRegex()) { matchResult ->
            "\\" + String(Character.toChars(Integer.parseInt(matchResult.groupValues[1].trim(), 16)))
        })
        matcher.region(start, text.length)
        if (!matcher.lookingAt()) {
            return null
        }
        val end = matcher.end()
        val type = matcher.group(1)
        val identifier = matcher.group(2).replace("\\", "")
        val value: SimpleSelectorNode = when (type) {
            "#" -> IdSelectorNode("#", identifier)
            "." -> ClassSelectorNode(".", identifier)
            ":" -> {
                val nestedSelector = parseNestedSelector(text, start)
                PseudoClassSelectorNode(":", identifier, nestedSelector?.value)
            }
            else -> TypeSelectorNode(identifier = identifier)
        }
        return Parsed(start, end, value)
    }

    fun parseAttributeSelector(text: String, start: Int): Parsed<AttributeSelectorNode>? {
        val matcher = attributeSelectorRegEx.matcher(text)
        matcher.region(start, text.length)
        if (!matcher.lookingAt()) {
            return null
        }
        val end = matcher.end()
        val property = matcher.group(1)
        if (matcher.group(2) != null) {
            val test = AttributeSelectorTest.fromString(matcher.group(2))
            val value = matcher.group(3) ?: matcher.group(4) ?: matcher.group(5)
            return Parsed(start, end, AttributeSelectorNode(property = property, test = test, value = value))
        }
        return Parsed(start, end, AttributeSelectorNode(property = property))
    }

    fun parseSimpleSelector(text: String, start: Int = 0): Parsed<out SimpleSelectorNode>? {
        return parseUniversalSelector(text, start)
            ?: parseSimpleIdentifierSelector(text, start)
            ?: parseAttributeSelector(text, start)
    }

    fun parseSimpleSelectorSequence(text: String, start: Int): Parsed<List<SimpleSelectorNode>>? {
        var simpleSelector: Parsed<out SimpleSelectorNode>?;

        simpleSelector = parseSimpleSelector(text, start) ?: return null;

        var end = simpleSelector.end
        val value = mutableListOf<SimpleSelectorNode>()
        while (simpleSelector != null) {
            value.add(simpleSelector.value)
            end = simpleSelector.end
            simpleSelector = parseSimpleSelector(text, end)
        }
        return Parsed(start, end, value)
    }

    fun parseCombinator(text: String, start: Int = 0): Parsed<Combinator>? {
        val matcher = combinatorRegEx.matcher(text)
        matcher.region(start, text.length)
        if (!matcher.lookingAt()) {
            return null
        }
        val end = matcher.end()
        val value = Combinator.fromString(matcher.group(1))
        return Parsed(start, end, value)
    }

    fun parseSelector(text: String, start: Int = 0): Parsed<List<Pair<List<SimpleSelectorNode>, Combinator?>>>? {
        var end = start

        val leadingWhiteSpace = whiteSpaceRegEx.matcher(text).apply { region(end, text.length) }
        if (leadingWhiteSpace.lookingAt()) {
            end = leadingWhiteSpace.end()
        }
        val value = mutableListOf<Pair<List<SimpleSelectorNode>, Combinator?>>()
        var combinator: Parsed<Combinator>? = null
        var expectSimpleSelector = true
        var pair: Pair<List<SimpleSelectorNode>, Combinator?>? = null
        do {
            val simpleSelectorSequence = parseSimpleSelectorSequence(text, end) ?: if (expectSimpleSelector) return null else break
            end = simpleSelectorSequence.end

            pair = simpleSelectorSequence.value to null

            combinator = parseCombinator(text, end)
            if (combinator != null) {
                end = combinator.end
                pair = pair.copy(second = combinator.value)
            }
            value.add(pair);
            expectSimpleSelector = combinator != null && combinator.value != Combinator.SPACE
        } while (combinator != null)

        return Parsed(start, end, value)
    }

    fun parseNestedSelector(text: String, start: Int): Parsed<List<Pair<List<SimpleSelectorNode>, Combinator?>>>? {
        val matcher = nestedSelectorRegEx.matcher(text)
        matcher.region(start, text.length)
        if (!matcher.lookingAt()) {
            return null
        }
        val end = matcher.end()
        val nestedSelectorText = matcher.group(2)!!

        return parseSelector(nestedSelectorText, 0)?.let {
            Parsed(start, end, it.value)
        }
    }
}



fun main() {
    // Example usage

    val parsedSheetJson = "{\n" +
            "  \"type\": \"stylesheet\",\n" +
            "  \"stylesheet\": {\n" +
            "    \"rules\": [\n" +
            "      {\n" +
            "        \"type\": \"rule\",\n" +
            "        \"selectors\": [\n" +
            "          \"a:has(b)\"\n" +
            "        ],\n" +
            "        \"declarations\": [\n" +
            "          {\n" +
            "            \"type\": \"declaration\",\n" +
            "            \"property\": \"color\",\n" +
            "            \"value\": \"red\",\n" +
            "            \"position\": {\n" +
            "              \"start\": {\n" +
            "                \"line\": 2,\n" +
            "                \"column\": 3\n" +
            "              },\n" +
            "              \"end\": {\n" +
            "                \"line\": 2,\n" +
            "                \"column\": 12\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        ],\n" +
            "        \"position\": {\n" +
            "          \"start\": {\n" +
            "            \"line\": 1,\n" +
            "            \"column\": 1\n" +
            "          },\n" +
            "          \"end\": {\n" +
            "            \"line\": 3,\n" +
            "            \"column\": 2\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    ],\n" +
            "    \"parsingErrors\": []\n" +
            "  }\n" +
            "}"

    val syntaxTree = parseFromJSON(parsedSheetJson);

    val selectorsMap = CssConverter.fromAstNodes(syntaxTree);

    val element = Element("a");
    element.className = "hello";
    val start = System.nanoTime();

    val element2 = Element("b");
    element.appendChild(element2);

    val matches = selectorsMap.query(element);

    val end = System.nanoTime();

    println("Parsed successfully: ${end - start}ms ${matches.selectors.size}")

//    if (parsedSelector != null) {
//
//    } else {
//        println("Failed to parse selector.")
//    }
}