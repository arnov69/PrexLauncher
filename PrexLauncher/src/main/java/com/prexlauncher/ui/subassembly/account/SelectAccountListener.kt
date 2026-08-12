package com.prexlauncher.ui.subassembly.account

import com.prexlauncher.core.value.MinecraftAccount

interface SelectAccountListener {
    fun onSelect(account: MinecraftAccount)
}
