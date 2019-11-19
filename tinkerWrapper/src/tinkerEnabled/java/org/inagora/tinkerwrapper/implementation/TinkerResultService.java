package org.inagora.tinkerwrapper.implementation;

import android.util.Log;

import com.tencent.tinker.lib.service.DefaultTinkerResultService;
import com.tencent.tinker.lib.service.PatchResult;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.lib.util.TinkerServiceInternals;

import org.inagora.tinkerwrapper.implementation.utils.TinkerUtils;

import java.io.File;

public class TinkerResultService extends DefaultTinkerResultService {

    private static final String TAG = "TinkerResultService";

    @Override
    public void onPatchResult(PatchResult result) {
        if (result == null) {
            Log.e(TAG, "TinkerResultService received null result!!!!");
            return;
        }
        Log.i(TAG, "TinkerResultService received a result:%s " + result.toString());

        // first, we want to kill the recover process
        TinkerServiceInternals.killTinkerPatchServiceProcess(getApplicationContext());

        // if success and newPatch, it is nice to delete the raw file, and restart at once
        // only main process can load an upgrade patch!
        if (result.isSuccess) {
            // 删除安装过的*.patch文件
            deleteRawPatchFile(new File(result.rawPatchFilePath));
            // 清除记录的信息
            if (TinkerUtils.removePatchCorrectMD5()) {
                final boolean isForced = TinkerMgrImpl.getInstance().isForced();
                if (checkIfNeedKill(result)) {
                    TinkerMgrImpl.getInstance().getOnPatchInstalledListener()
                            .onPatchInstalledSuccess(isForced);
                } else {
                    TinkerLog.i(TAG, "I have already install the newly patch version!");
                }
            }

        }
    }
}
