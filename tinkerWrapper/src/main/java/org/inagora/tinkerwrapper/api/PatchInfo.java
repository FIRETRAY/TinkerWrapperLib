package org.inagora.tinkerwrapper.api;

import org.json.JSONObject;

/**
 * 补丁信息数据结构
 */
public class PatchInfo {
    /**
     * 安装Patch成功后是否显示弹窗，提示重新安装
     */
    public boolean is_forced;
    public String patch_version;
    public String patch_url;
    public String patch_md5;

    public static PatchInfo fromJson(JSONObject jsonObject) {
        if (jsonObject == null || jsonObject.length() == 0) {
            return null;
        }
        PatchInfo patchInfo = new PatchInfo();
        patchInfo.is_forced = jsonObject.optInt("is_forced") == 1;
        patchInfo.patch_version = jsonObject.optString("patch_version");
        patchInfo.patch_url = jsonObject.optString("patch_url");
        patchInfo.patch_md5 = jsonObject.optString("patch_md5");
        return patchInfo;
    }
}
