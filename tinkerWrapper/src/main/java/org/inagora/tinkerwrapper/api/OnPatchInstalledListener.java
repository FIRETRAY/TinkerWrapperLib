package org.inagora.tinkerwrapper.api;

public interface OnPatchInstalledListener {
    /**
     * 补丁安装成功回调
     *
     * @param isPatchForced 是否是强制补丁，也就是返回PatchInfo.is_forced字段
     */
    void onPatchInstalledSuccess(boolean isPatchForced);
}