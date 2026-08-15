package com.prexlauncher.renderer

import android.content.Context
import com.prexlauncher.feature.log.Logging
import com.prexlauncher.renderer.renderers.FreedrenoRenderer
import com.prexlauncher.renderer.renderers.GL4ESRenderer
import com.prexlauncher.renderer.renderers.KryptonRenderer
import com.prexlauncher.renderer.renderers.MobileGluesRenderer
import com.prexlauncher.renderer.renderers.PanfrostRenderer
import com.prexlauncher.renderer.renderers.VirGLRenderer
import com.prexlauncher.renderer.renderers.VulkanZinkRenderer
import com.prexlauncher.core.Architecture
import com.prexlauncher.core.Tools

/**
 * 启动器所有渲染器总管理者，启动器内置的渲染器与渲染器插件加载的渲染器，都会加载到这里
 */
object Renderers {
    private val renderers: MutableList<RendererInterface> = mutableListOf()
    private var compatibleRenderers: Pair<RenderersList, MutableList<RendererInterface>>? = null
    private var currentRenderer: RendererInterface? = null
    private var isInitialized: Boolean = false

    fun init(reset: Boolean = false) {
        if (isInitialized && !reset) return
        isInitialized = true

        if (reset) {
            renderers.clear()
            compatibleRenderers = null
            currentRenderer = null
        }

        addRenderers(
            GL4ESRenderer(),
            MobileGluesRenderer(),
            KryptonRenderer(),
            VulkanZinkRenderer(),
            VirGLRenderer(),
            FreedrenoRenderer(),
            PanfrostRenderer()
        )
    }

    /**
     * 获取兼容当前设备的所有渲染器
     */
    fun getCompatibleRenderers(context: Context): Pair<RenderersList, List<RendererInterface>> = compatibleRenderers ?: run {
        val deviceHasVulkan = Tools.checkVulkanSupport(context.packageManager)
        // Currently, only 32-bit x86 does not have the Zink binary
        val deviceHasZinkBinary = !(Architecture.is32BitsDevice() && Architecture.isx86Device())

        val compatibleRenderers1: MutableList<RendererInterface> = mutableListOf()
        renderers.forEach { renderer ->
            if (renderer.getRendererId().contains("vulkan") && !deviceHasVulkan) return@forEach
            if (renderer.getRendererId().contains("zink") && !deviceHasZinkBinary) return@forEach
            compatibleRenderers1.add(renderer)
        }

        val rendererIdentifiers: MutableList<String> = mutableListOf()
        val rendererNames: MutableList<String> = mutableListOf()
        compatibleRenderers1.forEach { renderer ->
            rendererIdentifiers.add(renderer.getUniqueIdentifier())
            rendererNames.add(renderer.getRendererName())
        }

        val rendererPair = Pair(RenderersList(rendererIdentifiers, rendererNames), compatibleRenderers1)
        compatibleRenderers = rendererPair
        rendererPair
    }

    /**
     * 加入一些渲染器
     */
    @JvmStatic
    fun addRenderers(vararg renderers: RendererInterface) {
        renderers.forEach { renderer ->
            addRenderer(renderer)
        }
    }

    /**
     * 加入单个渲染器
     */
    @JvmStatic
    fun addRenderer(renderer: RendererInterface): Boolean {
        return if (this.renderers.any { it.getUniqueIdentifier() == renderer.getUniqueIdentifier() }) {
            Logging.w("Renderers", "The unique identifier of this renderer (${renderer.getRendererName()} - ${renderer.getUniqueIdentifier()}) conflicts with an already loaded renderer. " +
                    "Normally, this shouldn't happen. You deliberately caused this conflict, didn't you, user?")
            false
        } else {
            this.renderers.add(renderer)
            Logging.i("Renderers", "Renderer loaded: ${renderer.getRendererName()} (${renderer.getRendererId()} - ${renderer.getUniqueIdentifier()})")
            true
        }
    }

    /**
     * 设置当前的渲染器
     * @param context 用于初始化适配当前设备的渲染器
     * @param uniqueIdentifier 渲染器的唯一标识符，用于找到当前想要设置的渲染器
     * @param retryToFirstOnFailure 如果未找到匹配的渲染器，是否跳回渲染器列表的首个渲染器
     */
    fun setCurrentRenderer(context: Context, uniqueIdentifier: String, retryToFirstOnFailure: Boolean = true) {
        if (!isInitialized) throw IllegalStateException("Uninitialized renderer!")
        val compatibleRenderers = getCompatibleRenderers(context).second
        currentRenderer = compatibleRenderers.find { it.getUniqueIdentifier() == uniqueIdentifier } ?: run {
            if (retryToFirstOnFailure) {
                val renderer = compatibleRenderers[0]
                Logging.w("Renderers", "Incompatible renderer $uniqueIdentifier will be replaced with ${renderer.getUniqueIdentifier()} (${renderer.getRendererName()})")
                renderer
            } else null
        }
    }

    /**
     * 获取当前的渲染器
     */
    fun getCurrentRenderer(): RendererInterface {
        if (!isInitialized) throw IllegalStateException("Uninitialized renderer!")
        return currentRenderer ?: throw IllegalStateException("Current renderer not set")
    }

    /**
     * 当前是否设置了渲染器
     */
    fun isCurrentRendererValid(): Boolean = isInitialized && this.currentRenderer != null

    /**
     * 全局渲染器设置的旧出厂默认值 ("opengles2")。它从未匹配过任何渲染器的唯一标识符，
     * 因此当全局设置仍停留在该值时，视为“自动”模式：按 Minecraft 版本自动选择渲染器。
     */
    @JvmField
    val LEGACY_DEFAULT_RENDERER: String = "opengles2"

    private val YEAR_VERSION_REGEX = Regex("""\b(2\d)\.\d+""")
    private val OLD_VERSION_REGEX = Regex("""\b1\.(\d+)(\.\d+)?""")
    private val SNAPSHOT_REGEX = Regex("""\b\d{2}w\d{2}[a-z]""")

    /**
     * 按版本号判断是否需要现代 OpenGL 渲染器 (MobileGlues)：
     * - 1.21+、26.x 年号版本、现代快照 (2XwXXx) → MobileGlues (opengles3)
     * - 1.20 及更早版本 → GL4ES (opengles2)
     */
    @JvmStatic
    fun isModernVersion(versionName: String): Boolean {
        val name = versionName.lowercase()
        if (SNAPSHOT_REGEX.containsMatchIn(name)) return true
        if (YEAR_VERSION_REGEX.containsMatchIn(name)) return true
        OLD_VERSION_REGEX.find(name)?.let { m ->
            val minor = m.groupValues[1].toIntOrNull()
            return minor != null && minor >= 21
        }
        return false
    }

    /**
     * @return 该版本应自动使用的推荐渲染器唯一标识符
     * @param extraInfo 版本的真实 Minecraft 版本信息（例如模组包 "Fabulously Optimized" 的名称中
     * 不含版本号，需要靠 "26.2, Fabric - 0.19.3" 这类信息来判断）
     */
    @JvmStatic
    fun getRecommendedRendererIdentifier(versionName: String, extraInfo: String = ""): String {
        val combined = if (extraInfo.isBlank()) versionName else "$versionName $extraInfo"
        val targetId = if (isModernVersion(combined)) "opengles3" else "opengles2"
        val renderer = renderers.firstOrNull { it.getRendererId() == targetId }
        return renderer?.getUniqueIdentifier() ?: targetId
    }
}