package com.example.myapplication.style

import com.example.myapplication.style.css.CSSSource

class StyleScope(
    private var _selectorsMap: SelectorsMap?,
    private var _css: String?,
    private var _localSelectorsMap: SelectorsMap?
) {

    var css: String?
        get() = _css
        set(css: String?) = setCss(css)

    fun setCss(css: String?) {
        if (css != null) {
            val cssSource = CSSSource.fromSource(css)
            _localSelectorsMap = cssSource.selectors;
        }

    }

    fun ensureSelectors() {

    }



}
