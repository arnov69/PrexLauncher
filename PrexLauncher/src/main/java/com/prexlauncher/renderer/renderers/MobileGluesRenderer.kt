package com.prexlauncher.renderer.renderers

import com.prexlauncher.renderer.RendererInterface

/**
 * MobileGlues renderer, bundled directly into the launcher.
 * Identical values to the official `com.fcl.plugin.mobileglues` FCL renderer plugin
 * (renderer=MobileGlues:libmobileglues.so:libmobileglues.so, pojavEnv as below), so it
 * behaves exactly like the plugin — but requires no separate APK installation.
 */
class MobileGluesRenderer : RendererInterface {
    override fun getRendererId(): String = "opengles3"

    // Same unique identifier as the official plugin, so saved per-version renderer
    // settings keep working and the plugin can't be registered twice.
    override fun getUniqueIdentifier(): String = "com.fcl.plugin.mobileglues"

    override fun getRendererName(): String = "MobileGlues"

    override fun getRendererEnv(): Lazy<Map<String, String>> = lazy {
        mapOf(
            "LIBGL_ES" to "3",
            "LIBGL_EGL" to "libmobileglues.so",
            "MG_COUNT_LAUNCH" to "1"
        )
    }

    override fun getDlopenLibrary(): Lazy<List<String>> = lazy { emptyList() }

    override fun getRendererLibrary(): String = "libmobileglues.so"

    override fun getRendererEGL(): String = "libmobileglues.so"
}
