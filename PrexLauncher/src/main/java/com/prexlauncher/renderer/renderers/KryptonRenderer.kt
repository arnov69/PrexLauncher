package com.prexlauncher.renderer.renderers

import com.prexlauncher.renderer.RendererInterface

/**
 * Krypton Wrapper renderer, bundled directly into the launcher.
 * Identical values to the official `com.bzlzhh.plugin.ngg` FCL renderer plugin
 * (renderer=NGGL4ES:libng_gl4es.so:libEGL.so, pojavEnv as below), so it behaves
 * exactly like the plugin — but requires no separate APK installation.
 * Note: the official plugin only ships arm64-v8a binaries, so this renderer
 * only works on arm64 devices (same as the plugin itself).
 */
class KryptonRenderer : RendererInterface {
    override fun getRendererId(): String = "opengles3"

    // Same unique identifier as the official plugin, so saved per-version renderer
    // settings keep working and the plugin can't be registered twice.
    override fun getUniqueIdentifier(): String = "com.bzlzhh.plugin.ngg"

    override fun getRendererName(): String = "Krypton Wrapper"

    override fun getRendererEnv(): Lazy<Map<String, String>> = lazy {
        mapOf(
            "LIBGL_USE_MC_COLOR" to "1",
            "LIBGL_GL" to "31",
            "LIBGL_ES" to "3",
            "LIBGL_NORMALIZE" to "1",
            "LIBGL_NOERROR" to "1"
        )
    }

    override fun getDlopenLibrary(): Lazy<List<String>> = lazy { emptyList() }

    override fun getRendererLibrary(): String = "libng_gl4es.so"

    override fun getRendererEGL(): String = "libEGL.so"
}
