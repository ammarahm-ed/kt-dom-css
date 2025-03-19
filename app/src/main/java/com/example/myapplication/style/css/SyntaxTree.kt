package com.example.myapplication.style.css

data class Position(val start: LineColumn, val end: LineColumn)
data class LineColumn(var line: Int, var column: Int)

sealed class Node(var type: String, val position: Position)
data class Declaration(val property: String, val value: String) :
    Node("declaration", Position(LineColumn(0, 0), LineColumn(0, 0)))

data class Rule(
    var selectors: MutableList<String>?,
    var declarations: MutableList<Declaration>?,
    var pos: Position,
    var name: String?,
    var rules: MutableList<Rule>?,
    var media: String?,
    var import: String?
) : Node("rule", pos)

data class StyleSheet(val rules: List<Rule>)
data class SyntaxTree(val stylesheet: StyleSheet)
