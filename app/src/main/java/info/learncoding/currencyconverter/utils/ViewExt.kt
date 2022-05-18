package info.learncoding.currencyconverter.utils

import android.view.View

fun View.gone() {
    this.visibility = View.GONE
}

fun View.hidden() {
    this.visibility = View.INVISIBLE
}

fun View.show() {
    this.visibility = View.VISIBLE
}
