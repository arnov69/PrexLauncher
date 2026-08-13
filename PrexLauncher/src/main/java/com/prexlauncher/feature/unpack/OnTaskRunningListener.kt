package com.prexlauncher.feature.unpack

interface OnTaskRunningListener {
    fun onTaskStart()
    fun onTaskEnd()
    fun onTaskLog(message: String) {}
}