package com.newterm.backend

import com.newterm.core.Session

object BackendRegistry {
    /** Add new backends here. */
    val all: List<Backend> = listOf(BlankBackend(), EmurootBackend(), DistroBackend())

    fun of(s: Session): Backend = all.first { it.kindId == s.kindId }
}
