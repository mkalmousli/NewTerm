package com.newterm.perm

/** App-level permissions the UI can ask for. */
enum class Permission(val title: String, val why: String) {
    STORAGE("Storage", "Read and write shared storage"),
    ALL_FILES("All files access", "Unrestricted access to every file (Android 11+)"),
}
