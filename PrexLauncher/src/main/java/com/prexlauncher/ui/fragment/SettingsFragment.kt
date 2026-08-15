package com.prexlauncher.ui.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.prexlauncher.R
import com.prexlauncher.anim.AnimPlayer
import com.prexlauncher.anim.animations.Animations
import com.prexlauncher.databinding.FragmentSettingsBinding
import com.prexlauncher.event.value.SettingsPageSwapEvent
import com.prexlauncher.setting.Settings
import com.prexlauncher.ui.fragment.settings.ControlSettingsFragment
import com.prexlauncher.ui.fragment.settings.ExperimentalSettingsFragment
import com.prexlauncher.ui.fragment.settings.GameSettingsFragment
import com.prexlauncher.ui.fragment.settings.LauncherSettingsFragment
import com.prexlauncher.ui.fragment.settings.VideoSettingsFragment
import org.greenrobot.eventbus.EventBus

class SettingsFragment : FragmentWithAnim(R.layout.fragment_settings) {
    companion object {
        const val TAG: String = "SettingsFragment"
    }

    private lateinit var binding: FragmentSettingsBinding

    private class Tab(val root: View, val icon: ImageView, val label: TextView)

    private val tabs = mutableListOf<Tab>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tabs += Tab(binding.chipVideo, binding.chipVideoIcon, binding.chipVideoLabel)
        tabs += Tab(binding.chipControl, binding.chipControlIcon, binding.chipControlLabel)
        tabs += Tab(binding.chipGame, binding.chipGameIcon, binding.chipGameLabel)
        tabs += Tab(binding.chipLauncher, binding.chipLauncherIcon, binding.chipLauncherLabel)
        tabs += Tab(binding.chipExperimental, binding.chipExperimentalIcon, binding.chipExperimentalLabel)

        initViewPager()

        tabs.forEachIndexed { index, tab ->
            tab.root.setOnClickListener {
                if (binding.settingsViewpager.currentItem != index) {
                    binding.settingsViewpager.setCurrentItem(index, false)
                }
            }
        }

        updateTabState(0)
    }

    override fun onResume() {
        super.onResume()
        Settings.refreshSettings()
    }

    private fun initViewPager() {
        binding.settingsViewpager.apply {
            adapter = ViewPagerAdapter(this@SettingsFragment)
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 1
            isUserInputEnabled = false
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateTabState(position)
                    EventBus.getDefault().post(SettingsPageSwapEvent(position))
                }
            })
        }
    }

    private fun updateTabState(selected: Int) {
        val accentColor = ContextCompat.getColor(requireContext(), R.color.prex_accent_bright)
        val textColor = ContextCompat.getColor(requireContext(), R.color.settings_text_primary)
        val secondaryColor = ContextCompat.getColor(requireContext(), R.color.settings_text_secondary)

        tabs.forEachIndexed { index, tab ->
            val isSelected = index == selected
            tab.root.setBackgroundResource(
                if (isSelected) R.drawable.bg_settings_chip_selected else R.drawable.bg_settings_chip
            )
            ImageViewCompat.setImageTintList(
                tab.icon,
                ColorStateList.valueOf(if (isSelected) accentColor else secondaryColor)
            )
            tab.label.setTextColor(if (isSelected) textColor else secondaryColor)

            // Lightweight, smooth selection animation
            val targetScale = if (isSelected) 1.0f else 0.94f
            tab.root.animate().scaleX(targetScale).scaleY(targetScale)
                .setDuration(160)
                .setInterpolator(android.view.animation.DecelerateInterpolator())
                .start()
        }
    }

    override fun slideIn(animPlayer: AnimPlayer) {
        animPlayer.apply(AnimPlayer.Entry(binding.settingsHeader, Animations.FadeInDown))
            .apply(AnimPlayer.Entry(binding.settingsChipScroll, Animations.FadeInDown))
            .apply(AnimPlayer.Entry(binding.settingsViewpager, Animations.FadeIn))
    }

    override fun slideOut(animPlayer: AnimPlayer) {
        animPlayer.apply(AnimPlayer.Entry(binding.settingsHeader, Animations.FadeOutUp))
            .apply(AnimPlayer.Entry(binding.settingsChipScroll, Animations.FadeOutUp))
            .apply(AnimPlayer.Entry(binding.settingsViewpager, Animations.FadeOut))
    }

    private class ViewPagerAdapter(val fragment: FragmentWithAnim) : FragmentStateAdapter(fragment.requireActivity()) {
        override fun getItemCount(): Int = 5
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                1 -> ControlSettingsFragment(fragment)
                2 -> GameSettingsFragment()
                3 -> LauncherSettingsFragment(fragment)
                4 -> ExperimentalSettingsFragment()
                else -> VideoSettingsFragment()
            }
        }
    }
}
