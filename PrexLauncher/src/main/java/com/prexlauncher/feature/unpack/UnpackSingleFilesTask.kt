package com.prexlauncher.feature.unpack

import android.content.Context
import com.prexlauncher.feature.log.Logging.e
import com.prexlauncher.utils.CopyDefaultFromAssets.Companion.copyFromAssets
import com.prexlauncher.utils.path.PathManager
import com.prexlauncher.core.Tools

class UnpackSingleFilesTask(val context: Context) : AbstractUnpackTask() {
    override fun isNeedUnpack(): Boolean = true

    override fun run() {
        log("> Unpacking critical single files ...")
        runCatching {
            copyFromAssets(context)
            Tools.copyAssetFile(context, "resolv.conf", PathManager.DIR_DATA, false)
            log("> Critical files installed")
        }.getOrElse { e("AsyncAssetManager", "Failed to unpack critical components !") }
    }
}