package org.inagora.tinkerwrapper.implementation;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Real application for tinker life cycle
 */
public class RealApplicationImpl extends TinkerApplication {

    public RealApplicationImpl() {
        // copy from auto_build
        // 7 == ShareConstants.TINKER_ENABLE_ALL
        // loadVerifyFlag = false
        super(ShareConstants.TINKER_ENABLE_ALL,
                "org.inagora.tinkerwrapper.implementation.DelegateApplicationLike",
                "com.tencent.tinker.loader.TinkerLoader",
                false);
    }
}