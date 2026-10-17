package com.newterm.backend

import android.content.Context
import com.newterm.core.Session
import com.newterm.db.Seeds

/** Plain Android shell, no emuroot. */
class BlankBackend : Backend {
    override val kindId = Seeds.BLANK
    override fun command(ctx: Context, s: Session) = listOf("/system/bin/sh")
}
