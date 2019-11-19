package org.inagora.tinkerwrapper.api;

public interface ITinkerMgr {
    /**
     * 异步检查是否需要下载安装补丁，具体启动时机由外界业务层决定
     * attention: 需要先设定PatchInfo，然后调用该方法
     */
    void startCheckAsync();

    /**
     * 返回安装的补丁版本
     *
     * @return 返回补丁版本。若没有安装过补丁则返回空
     */
    String getInstalledPatchVersion();

    /**
     * PatchInfo设定
     */
    void setPatchInfo(PatchInfo patchInfo);

    /**
     * 设置补丁安装成功回调
     */
    void setOnPatchInstalledListener(OnPatchInstalledListener onPatchInstalledListener);
}
