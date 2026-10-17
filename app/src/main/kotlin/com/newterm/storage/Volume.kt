package com.newterm.storage

/** A host path a session may mount at [guest]. */
data class Volume(val label: String, val host: String, val guest: String)
