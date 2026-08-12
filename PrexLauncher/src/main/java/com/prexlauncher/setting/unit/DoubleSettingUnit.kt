package com.prexlauncher.setting.unit

import com.prexlauncher.setting.Settings.Manager

class DoubleSettingUnit(key: String, defaultValue: Double) : AbstractSettingUnit<Double>(key, defaultValue) {
    override fun getValue() = Manager.getValue(key, defaultValue) { it.toDoubleOrNull() }
}