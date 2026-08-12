package com.prexlauncher.utils.file

interface FileSearchProgress {
    fun getCurrentFileCount(): Long = 0
    fun getTotalSize(): Long = 0
    fun getPendingSize(): Long = 0
}