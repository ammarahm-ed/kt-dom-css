package com.example.myapplication.style.css

import com.example.myapplication.style.SelectorsMap

class CSSSource private constructor(
    private var _ast: SyntaxTree?,
    private var _source: String?
) {
    private var _selectors: SelectorsMap? = null

    init {
        parse()
    }

    companion object {
        fun fromDetect(cssOrAst: Any, fileName: String? = null): CSSSource {
            return when (cssOrAst) {
                is String -> fromSource(cssOrAst, null)
                is Map<*, *> -> {
                    val ast = cssOrAst as SyntaxTree;
                    fromAST(ast as SyntaxTree,  fileName)
                }
                else -> fromSource(cssOrAst.toString(), fileName)
            }
        }

        fun fromSource(source: String,  url: String? = null): CSSSource {
            return CSSSource( null, source)
        }

        fun fromAST(ast: SyntaxTree, url: String? = null): CSSSource {
            return CSSSource(ast, null)
        }
    }

    val selectors: SelectorsMap?
        get() = _selectors

    val source: String?
        get() = _source

    private fun parse() {
        try {
            if (_ast == null) {

                if (_source != null && !_source.equals("[object Object]")) {
                    _ast = com.example.myapplication.style.css.parse(_source!!)
                }

            }
            if (_ast != null) {
                createSelectors()
            }
        } catch (e: Exception) {
        }
    }

    private fun createSelectors() {
        if (_ast != null) {
            createSelectorsFromSyntaxTree()
        }
    }

    private  fun createSelectorsFromSyntaxTree() {
        if (_ast != null) {
            _selectors = CssConverter.fromAstNodes(_ast!!)
        }
    }

}
