package com.newterm.core

import android.content.Context
import com.newterm.db.SessionDao
import com.newterm.db.TerminalDao

/** Owns live [Terminal]s (process-wide) and the persisted tab list. */
object SessionManager {
    private val live = HashMap<Long, Terminal>()

    fun terminals(ctx: Context, s: Session): List<Terminal> {
        val dao = TerminalDao(ctx)
        if (dao.ids(s.id).isEmpty()) dao.insert(s.id, "shell")
        return dao.ids(s.id).map { (id, _) -> live.getOrPut(id) { Terminal(id, s, ctx.applicationContext).also { it.start() } } }
    }

    fun addTerminal(ctx: Context, s: Session): Terminal {
        val n = TerminalDao(ctx).ids(s.id).size + 1
        val id = TerminalDao(ctx).insert(s.id, "shell $n")
        return live.getOrPut(id) { Terminal(id, s, ctx.applicationContext).also { it.start() } }
    }

    fun closeTerminal(ctx: Context, t: Terminal) {
        live.remove(t.id)?.kill(); TerminalDao(ctx).delete(t.id)
    }

    fun deleteSession(ctx: Context, s: Session) {
        TerminalDao(ctx).ids(s.id).forEach { live.remove(it.first)?.kill() }
        SessionDao(ctx).delete(s.id)
        s.dir(ctx).deleteRecursively()
    }
}
