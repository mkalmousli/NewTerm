package com.newterm.db

/** DDL, one statement per table. Order matters (FKs). Seeds are in [Seeds]. */
object Schema {
    val TABLES = listOf(
        "CREATE TABLE backend_kind(id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE)",
        """CREATE TABLE distro(id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE,
            url_arm TEXT, url_arm64 TEXT, url_x86 TEXT, url_x86_64 TEXT)""",
        """CREATE TABLE session(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL,
            kind_id INTEGER NOT NULL REFERENCES backend_kind(id),
            distro_id INTEGER REFERENCES distro(id), created_at INTEGER NOT NULL)""",
        """CREATE TABLE terminal(id INTEGER PRIMARY KEY AUTOINCREMENT,
            session_id INTEGER NOT NULL REFERENCES session(id) ON DELETE CASCADE,
            title TEXT NOT NULL, position INTEGER NOT NULL)""",
        """CREATE TABLE bind_mount(id INTEGER PRIMARY KEY AUTOINCREMENT,
            session_id INTEGER NOT NULL REFERENCES session(id) ON DELETE CASCADE,
            host_path TEXT NOT NULL, guest_path TEXT NOT NULL, UNIQUE(session_id, guest_path))""",
        "CREATE TABLE setting_def(key TEXT PRIMARY KEY, default_value TEXT NOT NULL)",
        """CREATE TABLE setting(key TEXT PRIMARY KEY REFERENCES setting_def(key),
            value TEXT NOT NULL)""",
    )
}
