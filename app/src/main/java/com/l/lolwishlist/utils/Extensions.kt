package com.l.lolwishlist.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.l.lolwishlist.model.ChampionBase

fun Collection<ChampionBase>.removeThrash() = filterNot {
        it.id.equals("qiyana", true) ||
                it.id.equals("lulu", true) ||
                it.id.equals("lilia", true) ||
                it.id.equals("senna", true) ||
                it.id.equals("akshan", true) ||
                it.id.equals("yuumi", true) ||
                it.id.equals("vex", true) ||
                it.id.equals("seraphine", true)
    }
    .toList()


fun EditText.showKeyboard(context: Context) {
    isFocusable = true
    isFocusableInTouchMode = true
    requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}

fun EditText.hideKeyboard(context: Context) {
    clearFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View?.setSafeOnClickListener(waitInterval: Long = 850L, onSafeClick: (View) -> Unit) {
    this ?: return
    setOnClickListener(
        SafeClickListener(waitInterval) {
            onSafeClick(it)
        }
    )
}