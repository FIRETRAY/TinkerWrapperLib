package org.inagora.tinkerwrapper.api;

/**
 * 与外界通信的TinkerSubmodule唯一接口
 */
public class TinkerMgrProxy implements ITinkerMgr {
    public static ITinkerMgr INSTANCE = new TinkerMgrProxy();

    @Override
    public void startCheckAsync() {
        // nothing
    }

    @Override
    public String getInstalledPatchVersion() {
        // nothing
        return null;
    }

    @Override
    public void setPatchInfo(PatchInfo patchInfo) {
        // nothing
    }

    @Override
    public void setOnPatchInstalledListener(OnPatchInstalledListener onPatchInstalledListener) {
        // nothing
    }
}