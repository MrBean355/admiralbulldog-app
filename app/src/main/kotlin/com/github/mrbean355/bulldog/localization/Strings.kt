package com.github.mrbean355.bulldog.localization

import java.util.ResourceBundle

private val strings: ResourceBundle = ResourceBundle.getBundle("strings")

fun getString(key: String): String {
    return if (strings.containsKey(key)) {
        strings.getString(key)
    } else {
        key
    }
}

fun getString(key: String, vararg formatArgs: Any?): String {
    return getString(key).format(*formatArgs)
}