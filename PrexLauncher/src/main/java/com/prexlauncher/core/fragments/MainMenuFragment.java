package com.prexlauncher.core.fragments;

import static com.prexlauncher.event.single.RefreshVersionsEvent.MODE.END;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.prexlauncher.anim.AnimPlayer;
import com.prexlauncher.anim.animations.Animations;
import com.prexlauncher.InfoCenter;
import com.prexlauncher.R;
import com.prexlauncher.databinding.FragmentLauncherBinding;
import com.prexlauncher.event.single.AccountUpdateEvent;
import com.prexlauncher.event.single.LaunchGameEvent;
import com.prexlauncher.event.single.RefreshVersionsEvent;
import com.prexlauncher.feature.accounts.AccountsManager;
import com.prexlauncher.feature.mod.ModUtils;
import com.prexlauncher.feature.version.Version;
import com.prexlauncher.feature.version.utils.VersionIconUtils;
import com.prexlauncher.feature.version.VersionInfo;
import com.prexlauncher.feature.version.VersionsManager;
import com.prexlauncher.setting.AllSettings;
import com.prexlauncher.task.TaskExecutors;
import com.prexlauncher.ui.fragment.AboutFragment;
import com.prexlauncher.ui.fragment.ControlButtonFragment;
import com.prexlauncher.ui.fragment.FilesFragment;
import com.prexlauncher.ui.fragment.FragmentWithAnim;
import com.prexlauncher.ui.fragment.ModsFragment;
import com.prexlauncher.ui.fragment.VersionManagerFragment;
import com.prexlauncher.ui.fragment.VersionsListFragment;
import com.prexlauncher.ui.subassembly.account.AccountViewWrapper;
import com.prexlauncher.utils.path.PathManager;
import com.prexlauncher.utils.ZHTools;
import com.prexlauncher.utils.anim.ViewAnimUtils;

import com.prexlauncher.core.Tools;
import com.prexlauncher.core.progresskeeper.ProgressKeeper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

public class MainMenuFragment extends FragmentWithAnim {
    public static final String TAG = "MainMenuFragment";
    private FragmentLauncherBinding binding;
    private AccountViewWrapper accountViewWrapper;

    public MainMenuFragment() {
        super(R.layout.fragment_launcher);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLauncherBinding.inflate(getLayoutInflater());
        accountViewWrapper = new AccountViewWrapper(this, binding.viewAccount);
        accountViewWrapper.refreshAccountInfo();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.aboutText.setText(InfoCenter.replaceName(requireActivity(), R.string.about_tab));
        refreshWelcomeSubtitle();
        binding.aboutButton.setOnClickListener(v -> ZHTools.swapFragmentWithAnim(this, AboutFragment.class, AboutFragment.TAG, null));
        binding.customControlButton.setOnClickListener(v -> ZHTools.swapFragmentWithAnim(this, ControlButtonFragment.class, ControlButtonFragment.TAG, null));
        binding.openMainDirButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(FilesFragment.BUNDLE_LIST_PATH, PathManager.DIR_GAME_HOME);
            ZHTools.swapFragmentWithAnim(this, FilesFragment.class, FilesFragment.TAG, bundle);
        });
        binding.installJarButton.setOnClickListener(v -> runInstallerWithConfirmation(false));
        binding.installJarButton.setOnLongClickListener(v -> {
            runInstallerWithConfirmation(true);
            return true;
        });
        binding.shareLogsButton.setOnClickListener(v -> ZHTools.shareLogs(requireActivity()));

        binding.modsShortcut.setOnClickListener(v -> openModsSection());
        binding.resourcesShortcut.setOnClickListener(v -> openFilesSection("resourcepacks"));
        binding.shadersShortcut.setOnClickListener(v -> openFilesSection("shaderpacks"));

        binding.version.setOnClickListener(v -> {
            if (!isTaskRunning()) {
                ZHTools.swapFragmentWithAnim(this, VersionsListFragment.class, VersionsListFragment.TAG, null);
            } else {
                ViewAnimUtils.setViewAnim(binding.version, Animations.Shake);
                TaskExecutors.runInUIThread(() -> Toast.makeText(requireContext(), R.string.version_manager_task_in_progress, Toast.LENGTH_SHORT).show());
            }
        });
        binding.managerProfileButton.setOnClickListener(v -> {
            if (!isTaskRunning()) {
                ViewAnimUtils.setViewAnim(binding.managerProfileButton, Animations.Pulse);
                ZHTools.swapFragmentWithAnim(this, VersionManagerFragment.class, VersionManagerFragment.TAG, null);
            } else {
                ViewAnimUtils.setViewAnim(binding.managerProfileButton, Animations.Shake);
                TaskExecutors.runInUIThread(() -> Toast.makeText(requireContext(), R.string.version_manager_task_in_progress, Toast.LENGTH_SHORT).show());
            }
        });

        binding.playButton.setOnClickListener(v -> EventBus.getDefault().post(new LaunchGameEvent()));

        binding.versionName.setSelected(true);
        binding.versionInfo.setSelected(true);

        refreshCurrentVersion();
        refreshStats();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshStats();
    }

    private void refreshStats() {
        Version version = VersionsManager.INSTANCE.getCurrentVersion();
        File gameDir = version != null ? version.getGameDir() : new File(PathManager.DIR_GAME_HOME);
        File modsDir = new File(gameDir, "mods");
        int mods = 0;
        int enabled = 0;
        File[] modFiles = modsDir.listFiles();
        if (modFiles != null) {
            for (File file : modFiles) {
                String name = file.getName();
                if (name.endsWith(ModUtils.JAR_FILE_SUFFIX)) {
                    mods++;
                    enabled++;
                } else if (name.endsWith(ModUtils.DISABLE_JAR_FILE_SUFFIX)) {
                    mods++;
                }
            }
        }
        binding.statsModsValue.setText(String.valueOf(mods));
        binding.statsEnabledValue.setText(String.valueOf(enabled));
        binding.statsRamValue.setText(AllSettings.getRamAllocation().getValue().getValue() + " MB");
        binding.statsVersionsValue.setText(String.valueOf(VersionsManager.INSTANCE.getVersions().size()));
    }

    private void openModsSection() {
        Bundle bundle = new Bundle();
        File modsDir = new File(getCurrentGameDir(), "mods");
        if (!modsDir.exists()) modsDir.mkdirs();
        bundle.putString(ModsFragment.BUNDLE_ROOT_PATH, modsDir.getAbsolutePath());
        ZHTools.swapFragmentWithAnim(this, ModsFragment.class, ModsFragment.TAG, bundle);
    }

    private void openFilesSection(String subDir) {
        Bundle bundle = new Bundle();
        File dir = new File(getCurrentGameDir(), subDir);
        if (!dir.exists()) dir.mkdirs();
        bundle.putString(FilesFragment.BUNDLE_LIST_PATH, dir.getAbsolutePath());
        ZHTools.swapFragmentWithAnim(this, FilesFragment.class, FilesFragment.TAG, bundle);
    }

    private File getCurrentGameDir() {
        Version version = VersionsManager.INSTANCE.getCurrentVersion();
        if (version != null) {
            File gameDir = version.getGameDir();
            if (gameDir != null) return gameDir;
        }
        return new File(PathManager.DIR_GAME_HOME);
    }

    private void refreshCurrentVersion() {
        Version version = VersionsManager.INSTANCE.getCurrentVersion();

        int versionInfoVisibility;
        if (version != null) {
            binding.versionName.setText(version.getVersionName());
            VersionInfo versionInfo = version.getVersionInfo();
            if (versionInfo != null) {
                binding.versionInfo.setText(versionInfo.getInfoString());
                versionInfoVisibility = View.VISIBLE;
            } else versionInfoVisibility = View.GONE;

            new VersionIconUtils(version).start(binding.versionIcon);
            binding.managerProfileButton.setVisibility(View.VISIBLE);
        } else {
            binding.versionName.setText(R.string.version_no_versions);
            binding.managerProfileButton.setVisibility(View.GONE);
            versionInfoVisibility = View.GONE;
        }
        binding.versionInfo.setVisibility(versionInfoVisibility);
    }

    @Subscribe()
    public void event(RefreshVersionsEvent event) {
        if (event.getMode() == END) {
            TaskExecutors.runInUIThread(() -> {
                refreshCurrentVersion();
                refreshStats();
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(AccountUpdateEvent event) {
        if (accountViewWrapper != null) accountViewWrapper.refreshAccountInfo();
        refreshWelcomeSubtitle();
    }

    private void refreshWelcomeSubtitle() {
        if (binding.welcomeUserText == null) return;
        String playerName = AccountsManager.INSTANCE.getCurrentAccount() != null ? AccountsManager.INSTANCE.getCurrentAccount().username : null;
        binding.welcomeUserText.setText(
                (playerName == null || playerName.isEmpty()) ? getString(R.string.home_welcome_subtitle) : playerName);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void runInstallerWithConfirmation(boolean isCustomArgs) {
        if (ProgressKeeper.getTaskCount() == 0)
            Tools.installMod(requireActivity(), isCustomArgs);
        else
            Toast.makeText(requireContext(), R.string.tasks_ongoing, Toast.LENGTH_LONG).show();
    }

    @Override
    public void slideIn(AnimPlayer animPlayer) {
        animPlayer.apply(new AnimPlayer.Entry(binding.launcherMenu, Animations.BounceInDown))
                .apply(new AnimPlayer.Entry(binding.playLayout, Animations.BounceInLeft))
                .apply(new AnimPlayer.Entry(binding.playButtonsLayout, Animations.BounceEnlarge));
    }

    @Override
    public void slideOut(AnimPlayer animPlayer) {
        animPlayer.apply(new AnimPlayer.Entry(binding.launcherMenu, Animations.FadeOutUp))
                .apply(new AnimPlayer.Entry(binding.playLayout, Animations.FadeOutRight))
                .apply(new AnimPlayer.Entry(binding.playButtonsLayout, Animations.BounceShrink));
    }
}
