package com.newterm.storage

import android.content.Context
import com.newterm.db.BindDao

/** Mount/unmount [Volume]s for a session; takes effect on next terminal start. */
object SessionStorage {
    fun isMounted(ctx: Context, session: Long, v: Volume) =
        BindDao(ctx).all(session).any { it.second == v.guest }

    fun toggle(ctx: Context, session: Long, v: Volume) {
        val dao = BindDao(ctx)
        if (isMounted(ctx, session, v)) dao.remove(session, v.guest) else dao.add(session, v.host, v.guest)
    }
}
