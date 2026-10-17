package com.newterm.ui

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ScrollView
import com.newterm.theme.Theme
import com.newterm.theme.ThemeStore
import com.newterm.ui.dsl.MATCH
import com.newterm.ui.dsl.vbox

/** Code-built screens: implement [build]; the UI is rebuilt on every resume (re-theme). */
abstract class BaseActivity : Activity() {
    protected lateinit var theme: Theme

    abstract fun build(t: Theme): View

    override fun onCreate(b: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(b)
    }

    override fun onResume() {
        super.onResume()
        theme = ThemeStore.current(this)
        window.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(theme.bg))
        if (Build.VERSION.SDK_INT >= 21) window.statusBarColor = theme.bg
        setContentView(build(theme))
    }

    /** Wrap [content] in a scrolling page with padding. */
    protected fun page(content: android.widget.LinearLayout) = ScrollView(this).apply {
        addView(vbox(16) { addView(content) }, MATCH, MATCH)
    }
}
