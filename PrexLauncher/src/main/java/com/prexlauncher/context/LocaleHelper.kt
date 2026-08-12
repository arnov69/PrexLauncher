package com.prexlauncher.context

import android.content.Context
import android.content.ContextWrapper
import com.prexlauncher.setting.Settings
import com.prexlauncher.utils.path.PathManager
import com.prexlauncher.core.prefs.LauncherPreferences

class LocaleHelper(context: Context) : ContextWrapper(context) {
    companion object {
        fun setLocale(context: Context): ContextWrapper {
            //初始化路径
            PathManager.initContextConstants(context)
            //刷新启动器设置
            Settings.refreshSettings()

            LauncherPreferences.loadPreferences()
            return LocaleHelper(context)
        }
    }
}