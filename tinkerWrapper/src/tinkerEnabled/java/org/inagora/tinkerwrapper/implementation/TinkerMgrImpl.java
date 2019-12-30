package org.inagora.tinkerwrapper.implementation;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.tinker.entry.ApplicationLike;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;

import org.inagora.tinkerwrapper.api.ITinkerMgr;
import org.inagora.tinkerwrapper.api.OnPatchInstalledListener;
import org.inagora.tinkerwrapper.api.PatchInfo;
import org.inagora.tinkerwrapper.implementation.utils.TinkerDownloader;
import org.inagora.tinkerwrapper.implementation.utils.TinkerDownloaderListener;
import org.inagora.tinkerwrapper.implementation.utils.TinkerUtils;

import java.io.File;

/**
 * TinkerMgr的具体实现，仅供Submodule内部使用
 */
public class TinkerMgrImpl implements ITinkerMgr {

    private static final String TAG = "TinkerMgrImpl";

    private static TinkerMgrImpl sInstance;
    private boolean needKillProcess;
    // 安装Patch成功后是否显示弹窗，提示重新安装
    private boolean isForced;
    private Context applicationContext;
    private PatchInfo patchInfo;
    private OnPatchInstalledListener onPatchInstalledListener;
    private Application runtimeApplication;

    public TinkerMgrImpl() {
    }

    public static TinkerMgrImpl getInstance() {
        if (sInstance == null) {
            sInstance = new TinkerMgrImpl();
        }
        return sInstance;
    }

    @Override
    public Application getRuntimeApplication() {
        return runtimeApplication;
    }

    public void initVarByContext(Context applicationContext) {
        this.applicationContext = applicationContext;
        // 初始化下载路径
        TinkerWrapperConstants.patchStorageRootPath = applicationContext.getFilesDir() + File.separator + "patch";
    }

    public Context getApplicationContext() {
        return applicationContext;
    }

    public void installTinker(ApplicationLike outer) {
        Tinker tinker = new Tinker.Builder(outer.getApplication()).build();
        Tinker.create(tinker);
        tinker.install(outer.getTinkerResultIntent(), TinkerResultService.class, new UpgradePatch());
    }

    public void installPatch() {
        final String path = TinkerUtils.getNormalPatchFilePath();
        TinkerInstaller.onReceiveUpgradePatch(applicationContext, path);
    }

    public boolean isNeedKillProcess() {
        return needKillProcess;
    }

    public void setNeedKillProcess(boolean needKillProcess) {
        this.needKillProcess = needKillProcess;
    }

    private void startDownloadPatchFile(PatchInfo patchInfo) {
        File patchRootDir = new File(TinkerWrapperConstants.patchStorageRootPath);
        if (TinkerUtils.checkDirectoryIsLegalToRead(patchRootDir)) {
            Log.d(TAG, "startDownloadPatchFile, patch dir check ok");
        } else {
            Log.e(TAG, "startDownloadPatchFile, patch root cannot be read, quit patch download function.");
            return;
        }

        for (File childFile : patchRootDir.listFiles()) {
            String name = childFile.getName();
            // 老旧补丁删除，包括direct patch也被清空了，保证此时文件夹里就只会存在最新的包
            if (name.endsWith(".patch")) {
                boolean ret = childFile.delete();
                if (ret) {
                    Log.d(TAG, "startDownloadPatchFile, remove old patch [" + name + "]");
                }
            }
        }
        Log.d(TAG, "start download patch  == " + patchInfo);
        TinkerDownloader.downloadFile(patchInfo.patch_url,
                TinkerUtils.getNormalPatchFilePath(),
                new TinkerDownloaderListener() {
                    @Override
                    public void onSuccess(File file) {
                        Log.d(TAG, "download patch down , " + file.getAbsolutePath());
                        if (TinkerUtils.checkDownloadedPatchMD5(TinkerUtils.getPatchCorrectMD5())) {
                            Log.d(TAG, "start to install patch");
                            TinkerMgrImpl.getInstance().installPatch();
                        } else {
                            Log.e(TAG, "MD5 doesn't match");
                        }
                    }
                });
    }

    @Override
    public void startCheckAsync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                checkAndDownloadPatch();
            }
        }).start();
    }

    private void checkAndDownloadPatch() {
        // 没初始化路径时返回
        if (TextUtils.isEmpty(TinkerWrapperConstants.patchStorageRootPath)) {
            return;
        }
        // root dir creation
        File rootDir = new File(TinkerWrapperConstants.patchStorageRootPath);
        if (!rootDir.exists()) {
            boolean ret = rootDir.mkdirs();
            if (!ret) {
                Log.e(TAG, "ERROR, cannot create patch root dir, quit patch function");
                return;
            }
        }
        final String installedPatchVersion = TinkerUtils.getInstalledPatchVersionIfPresent();
        // 网络数据合法性校验
        if (patchInfo == null) {
            return;
        }
        if (TextUtils.isEmpty(patchInfo.patch_md5)) {
            return;
        }
        if (TextUtils.isEmpty(patchInfo.patch_version)) {
            return;
        }
        // 检查patch_version是否是合法long类型
        if (TinkerUtils.isInvalidVersion(TinkerUtils.parseVersionLong(patchInfo.patch_version))) {
            return;
        }
        // 拿到Config后再检查本地下载项
        // 只有本地的和云端的版本保持一致，才会安装
        if (checkLocalPatch(patchInfo)) {
            setForced(patchInfo.is_forced);
            installPatch();
        } else {
            // 没安装过，而且安装的版本过低才下载新补丁
            if (TextUtils.isEmpty(installedPatchVersion) || TinkerUtils.parseVersionLong
                    (installedPatchVersion) < TinkerUtils.parseVersionLong(patchInfo.patch_version)) {
                // Config保存
                TinkerMgrImpl.getInstance().setForced(patchInfo.is_forced);
                TinkerUtils.savePatchIsForced(patchInfo.is_forced);
                TinkerUtils.savePatchCorrectMD5(patchInfo.patch_md5);
                startDownloadPatchFile(patchInfo);
            }
        }
    }

    private boolean checkLocalPatch(PatchInfo latestPatchInfo) {
        // 下载补丁的可读性、版本校验
        File patchFile = TinkerUtils.getPatchFile();
        // 可以读取嘛
        if (!TinkerUtils.checkPatchIsLegalToRead(patchFile)) {
            return false;
        }
        // 补丁MD5校验
        if (!TinkerUtils.checkDownloadedPatchMD5(latestPatchInfo.patch_md5)) {
            return false;
        }
        // 补丁版本校验，避免无效安装
        final String downloadedPatchVersion = TinkerUtils.getPatchVersionFromPatchFile();
        final String installedPatchVersion = TinkerUtils.getInstalledPatchVersionIfPresent();
        if (TextUtils.isEmpty(downloadedPatchVersion)) {
            // 下载的补丁不带version，那就是非法了。不安装
            return false;
        }
        long latestPatchVersionNum = TinkerUtils.parseVersionLong(latestPatchInfo.patch_version);
        long downloadedPatchVersionNum = TinkerUtils.parseVersionLong(downloadedPatchVersion);
        // 已经下载的补丁包版本号格异常，不安装
        if (TinkerUtils.isInvalidVersion(downloadedPatchVersionNum)) {
            return false;
        }
        // 下载的与服务器给的版本不同，不安装
        if (downloadedPatchVersionNum != latestPatchVersionNum) {
            return false;
        }
        // 没安装过，而且本地和云端patchVersion一致，可以
        // or
        // 安装过，但是下载的补丁版本更高
        return TextUtils.isEmpty(installedPatchVersion) || TinkerUtils.parseVersionLong(installedPatchVersion) <
                downloadedPatchVersionNum;
    }

    public boolean isForced() {
        return isForced;
    }

    private void setForced(boolean forced) {
        isForced = forced;
    }

    @Override
    public void setPatchInfo(PatchInfo patchInfo) {
        this.patchInfo = patchInfo;
    }

    @Override
    public void setOnPatchInstalledListener(OnPatchInstalledListener onPatchInstalledListener) {
        this.onPatchInstalledListener = onPatchInstalledListener;
    }

    public OnPatchInstalledListener getOnPatchInstalledListener() {
        return onPatchInstalledListener;
    }

    @Override
    public String getInstalledPatchVersion() {
        return TinkerUtils.getInstalledPatchVersionIfPresent();
    }
}
