package com.prexlauncher.feature.download

import androidx.lifecycle.ViewModel
import com.prexlauncher.feature.download.item.InfoItem
import com.prexlauncher.feature.download.platform.AbstractPlatformHelper

class InfoViewModel : ViewModel() {
    var platformHelper: AbstractPlatformHelper? = null
    var infoItem: InfoItem? = null
}