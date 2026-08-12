package com.prexlauncher.ui.fragment.settings

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.prexlauncher.anim.AnimPlayer
import com.prexlauncher.anim.animations.Animations
import com.prexlauncher.R
import com.prexlauncher.databinding.SettingsFragmentGameBinding
import com.prexlauncher.setting.AllSettings
import com.prexlauncher.task.TaskExecutors
import com.prexlauncher.ui.fragment.settings.wrapper.BaseSettingsWrapper
import com.prexlauncher.ui.fragment.settings.wrapper.EditTextSettingsWrapper
import com.prexlauncher.ui.fragment.settings.wrapper.ListSettingsWrapper
import com.prexlauncher.ui.fragment.settings.wrapper.SeekBarSettingsWrapper
import com.prexlauncher.ui.fragment.settings.wrapper.SwitchSettingsWrapper
import com.prexlauncher.utils.file.FileTools.Companion.formatFileSize
import com.prexlauncher.utils.platform.MemoryUtils.Companion.getFreeDeviceMemory
import com.prexlauncher.utils.platform.MemoryUtils.Companion.getTotalDeviceMemory
import com.prexlauncher.utils.platform.MemoryUtils.Companion.getUsedDeviceMemory
import com.prexlauncher.utils.stringutils.StringUtils
import com.prexlauncher.core.Architecture
import com.prexlauncher.core.Tools
import com.prexlauncher.core.contracts.OpenDocumentWithExtension
import com.prexlauncher.core.multirt.MultiRTConfigDialog
import kotlin.math.min

class GameSettingsFragment : AbstractSettingsFragment(R.layout.settings_fragment_game, SettingCategory.GAME) {
    private lateinit var binding: SettingsFragmentGameBinding
    private val mVmInstallLauncher = registerForActivityResult(
        OpenDocumentWithExtension("xz")
    ) { uris: List<Uri>? ->
        uris?.let { uriList ->
            uriList[0].let { data ->
                Tools.installRuntimeFromUri(context, data)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingsFragmentGameBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context = requireContext()

        SwitchSettingsWrapper(
            context,
            AllSettings.versionIsolation,
            binding.versionIsolationLayout,
            binding.versionIsolation
        )

        EditTextSettingsWrapper(
            AllSettings.versionCustomInfo,
            binding.versionCustomInfoLayout,
            binding.versionCustomInfoEdittext
        )

        SwitchSettingsWrapper(
            context,
            AllSettings.autoSetGameLanguage,
            binding.autoSetGameLanguageLayout,
            binding.autoSetGameLanguage
        )

        SwitchSettingsWrapper(
            context,
            AllSettings.gameLanguageOverridden,
            binding.gameLanguageOverriddenLayout,
            binding.gameLanguageOverridden
        )

        ListSettingsWrapper(
            context,
            AllSettings.setGameLanguage,
            binding.setGameLanguageLayout,
            binding.setGameLanguageTitle,
            binding.setGameLanguageValue,
            R.array.all_game_language, R.array.all_game_language_value
        )

        BaseSettingsWrapper(
            context,
            binding.installJreLayout
        ) {
            MultiRTConfigDialog().apply {
                prepare(context, mVmInstallLauncher)
            }.show()
        }

        ListSettingsWrapper(
            context,
            AllSettings.selectRuntimeMode,
            binding.selectRuntimeModeLayout,
            binding.selectRuntimeModeTitle,
            binding.selectRuntimeModeValue,
            R.array.select_java_runtime_names, R.array.select_java_runtime_values
        )

        EditTextSettingsWrapper(
            AllSettings.javaArgs,
            binding.javaArgsLayout,
            binding.javaArgsEdittext
        )

        val deviceRam = Tools.getTotalDeviceMemory(context)
        val maxRAM = if (Architecture.is32BitsDevice() || deviceRam < 2048) min(
            1024.0,
            deviceRam.toDouble()
        ).toInt()
        else deviceRam - (if (deviceRam < 3064) 800 else 1024) //To have a minimum for the device to breathe

        SeekBarSettingsWrapper(
            context,
            AllSettings.ramAllocation.value,
            binding.allocationLayout,
            binding.allocationTitle,
            binding.allocationSummary,
            binding.allocationValue,
            binding.allocation,
            "MB"
        ) { wrapper ->
            wrapper.seekbarView.max = maxRAM
            wrapper.seekbarView.progress = AllSettings.ramAllocation.value.getValue()
            wrapper.setSeekBarValueTextView()

            updateMemoryInfo(context, wrapper.seekbarView.progress.toLong())
        }.apply {
            setOnSeekBarProgressChangeListener {
                updateMemoryInfo(
                    requireContext(),
                    seekbarView.progress.toLong()
                )
            }
        }

        SwitchSettingsWrapper(
            context,
            AllSettings.javaSandbox,
            binding.javaSandboxLayout,
            binding.javaSandbox
        )

        SwitchSettingsWrapper(
            context,
            AllSettings.gameMenuShowMemory,
            binding.gameMenuShowMemoryLayout,
            binding.gameMenuShowMemory
        ).setOnCheckedChangeListener { _, _, listener ->
            listener.onSave()
            openGameMenuMemory()
        }

        SwitchSettingsWrapper(
            context,
            AllSettings.gameMenuShowFPS,
            binding.gameMenuShowFPSLayout,
            binding.gameMenuShowFPS
        ).setOnCheckedChangeListener { _, _, listener ->
            listener.onSave()
            openGameMenuFPS()
        }

        EditTextSettingsWrapper(
            AllSettings.gameMenuMemoryText,
            binding.gameMenuMemoryTextLayout,
            binding.gameMenuMemoryText
        ).setOnTextChangedListener {
            updateGameMenuMemoryText()
        }.setMaxLength(40)

        ListSettingsWrapper(
            context,
            AllSettings.gameMenuLocation,
            binding.gameMenuLocationLayout,
            binding.gameMenuLocationTitle,
            binding.gameMenuLocationValue,
            R.array.game_menu_location_names, R.array.game_menu_location_values
        )

        SeekBarSettingsWrapper(
            context,
            AllSettings.gameMenuInfoRefreshRate,
            binding.gameMenuInfoRefreshRateLayout,
            binding.gameMenuInfoRefreshRateTitle,
            binding.gameMenuInfoRefreshRateSummary,
            binding.gameMenuInfoRefreshRateValue,
            binding.gameMenuInfoRefreshRate,
            "ms"
        )

        SeekBarSettingsWrapper(
            context,
            AllSettings.gameMenuAlpha,
            binding.gameMenuAlphaLayout,
            binding.gameMenuAlphaTitle,
            binding.gameMenuAlphaSummary,
            binding.gameMenuAlphaValue,
            binding.gameMenuAlpha,
            "%"
        ).setOnSeekBarProgressChangeListener { progress ->
            setGameMenuAlpha(progress.toFloat() / 100F)
        }

        openGameMenuMemory()
        openGameMenuFPS()
        updateGameMenuMemoryText()
        setGameMenuAlpha(AllSettings.gameMenuAlpha.getValue().toFloat() / 100F)
    }

    override fun slideIn(animPlayer: AnimPlayer) {
        animPlayer.apply(AnimPlayer.Entry(binding.root, Animations.BounceInDown))
    }

    private fun updateMemoryInfo(context: Context, seekValue: Long) {
        val value = seekValue * 1024 * 1024
        val freeDeviceMemory = getFreeDeviceMemory(context)

        val isMemorySizeExceeded = value > freeDeviceMemory

        var summary = getMemoryInfoText(context, freeDeviceMemory)
        if (isMemorySizeExceeded) summary =
            StringUtils.insertNewline(summary, getString(R.string.setting_java_memory_exceeded))

        TaskExecutors.runInUIThread { binding.allocationMemory.text = summary }
    }

    private fun getMemoryInfoText(context: Context, freeDeviceMemory: Long): String {
        return getString(
            R.string.setting_java_memory_info,
            formatFileSize(getUsedDeviceMemory(context)),
            formatFileSize(getTotalDeviceMemory(context)),
            formatFileSize(freeDeviceMemory)
        )
    }

    private fun openGameMenuMemory() {
        binding.gameMenuPreview.memoryText.visibility = if (AllSettings.gameMenuShowMemory.getValue()) View.VISIBLE else View.GONE
    }

    private fun openGameMenuFPS() {
        binding.gameMenuPreview.fpsText.visibility = if (AllSettings.gameMenuShowFPS.getValue()) View.VISIBLE else View.GONE
    }

    private fun setGameMenuAlpha(alpha: Float) {
        binding.gameMenuPreview.root.alpha = alpha
    }

    private fun updateGameMenuMemoryText() {
        val text = "${AllSettings.gameMenuMemoryText.getValue()} 0MB/0MB"
        binding.gameMenuPreview.memoryText.text = text.trim()
    }
}