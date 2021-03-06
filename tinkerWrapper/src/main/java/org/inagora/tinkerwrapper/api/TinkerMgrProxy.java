package org.inagora.tinkerwrapper.api;

import android.app.Application;

import org.inagora.tinkerwrapper.implementation.TinkerMgrImpl;

/**
 * 与外界通信的TinkerSubmodule唯一接口
 */
public class TinkerMgrProxy implements ITinkerMgr {
    public static ITinkerMgr INSTANCE = new TinkerMgrProxy();

    @Override
    public void startCheckAsync() {
        TinkerMgrImpl.getInstance().startCheckAsync();

    }

    @Override
    public String getInstalledPatchVersion() {
        return TinkerMgrImpl.getInstance().getInstalledPatchVersion();
    }

    @Override
    public void setPatchInfo(PatchInfo patchInfo) {
        TinkerMgrImpl.getInstance().setPatchInfo(patchInfo);

    }

    @Override
    public void setOnPatchInstalledListener(OnPatchInstalledListener onPatchInstalledListener) {
        TinkerMgrImpl.getInstance().setOnPatchInstalledListener(onPatchInstalledListener);
    }

    @Override
    public Application getRuntimeApplication() {
        return TinkerMgrImpl.getInstance().getRuntimeApplication();
    }
}