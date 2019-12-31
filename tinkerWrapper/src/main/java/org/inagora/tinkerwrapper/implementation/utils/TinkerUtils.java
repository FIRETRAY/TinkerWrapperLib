package org.inagora.tinkerwrapper.implementation.utils;

import android.text.TextUtils;
import android.util.Log;

import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerLoadResult;
import com.tencent.tinker.loader.shareutil.SharePatchFileUtil;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

import org.inagora.tinkerwrapper.implementation.TinkerMgrImpl;
import org.inagora.tinkerwrapper.implementation.TinkerWrapperConstants;

import java.io.File;
import java.util.Properties;

public class TinkerUtils {
    private static final String TAG = "TinkerUtils";

    public static void savePatchIsForced(boolean isForced) {
        TinkerSPUtils.putBoolean(TinkerWrapperConstants.PATCH_SP_IS_FORCED, isForced);
    }

    /**
     * 保存从服务端获取的PatchInfo里的MD5
     */
    public static void savePatchCorrectMD5(String correctMD5FromServer) {
        TinkerSPUtils.putString(TinkerWrapperConstants.PATCH_SP_MD5, correctMD5FromServer);
    }

    public static boolean removePatchCorrectMD5() {
        return TinkerSPUtils.putStringByCommit(TinkerWrapperConstants.PATCH_SP_MD5, "");
    }

    /**
     * 返回从服务端获取的PatchInfo里的MD5
     */
    public static String getPatchCorrectMD5() {
        return TinkerSPUtils.getString(TinkerWrapperConstants.PATCH_SP_MD5, "");
    }

    public static String getNormalPatchFilePath() {
        // 正式发布的时候，最好不要以.apk结尾，防止被运营商挟持
        return TinkerWrapperConstants.patchStorageRootPath + File.separator + TinkerWrapperConstants.NORMAL_PATCH_NAME;
    }

    public static boolean checkPatchIsLegalToRead(File patchFile) {
        final String name = patchFile.getName();
        if (!patchFile.exists()) {
            Log.e(TAG, "no " + name + " file at all");
            return false;
        } else if (!patchFile.canRead()) {
            Log.e(TAG, name + " cannot be read, may be permission issue");
            return false;
        } else if (!patchFile.isFile()) {
            Log.e(TAG, name + " is not a file");
            return false;
        }
        Log.d(TAG, name + " is good to go ~~");
        return true;
    }

    public static boolean checkDirectoryIsLegalToRead(File dir) {
        final String name = dir.getName();
        if (!dir.exists()) {
            Log.e(TAG, "no " + name + " file at all");
            return false;
        } else if (!dir.canRead()) {
            Log.e(TAG, name + " cannot be read, may be permission issue");
            return false;
        } else if (!dir.isDirectory()) {
            Log.e(TAG, name + " is not a directory");
            return false;
        }
        Log.d(TAG, name + " is good to go ~~");
        return true;
    }

    public static File getPatchFile() {
        return new File(getNormalPatchFilePath());
    }

//    public static void showDialog() {
//        Context appContext = TinkerMgrImpl.getInstance().getApplicationContext();
//        Intent intent = new Intent(appContext, TinkerAlertDialogActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        appContext.startActivity(intent);
//    }

    public static String getPatchVersionFromPatchFile() {
        String ret = null;
        File patchFile = new File(getNormalPatchFilePath());
        Properties properties = ShareTinkerInternals.fastGetPatchPackageMeta(patchFile);
        if (properties != null) {
            ret = properties.getProperty(TinkerWrapperConstants.KEY_PATCH_VERSION);
        }
        Log.d(TAG, "patchFile version == " + ret);
        return ret;
    }

    public static String getInstalledPatchVersionIfPresent() {
        String ret = null;
        Tinker tinker = Tinker.with(TinkerMgrImpl.getInstance().getApplicationContext());
        if (tinker.isTinkerLoaded()) {
            TinkerLoadResult tinkerLoadResult = tinker.getTinkerLoadResultIfPresent();
            if (tinkerLoadResult != null && !tinkerLoadResult.useInterpretMode) {
                ret = tinkerLoadResult.packageConfig.get(TinkerWrapperConstants.KEY_PATCH_VERSION);
            }
        }
        Log.d(TAG, "getInstalledPatchVersionIfPresent == " + ret);
        return ret;
    }

    public static boolean checkDownloadedPatchMD5(String correctMD5) {
        File normalPatchFile = new File(getNormalPatchFilePath());
        if (!normalPatchFile.exists()) {
            return false;
        }
        final String normalPatchMD5 = SharePatchFileUtil.getMD5(normalPatchFile);
        if (TextUtils.isEmpty(normalPatchMD5)) {
            return false;
        }
        if (TextUtils.isEmpty(correctMD5)) {
            return false;
        }
        return normalPatchMD5.equals(correctMD5);
    }

    public static long parseVersionLong(String object) {
        long ret = Long.MIN_VALUE;
        try {
            ret = Long.parseLong(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static boolean isInvalidVersion(Long version) {
        return version == Long.MIN_VALUE;
    }
}
