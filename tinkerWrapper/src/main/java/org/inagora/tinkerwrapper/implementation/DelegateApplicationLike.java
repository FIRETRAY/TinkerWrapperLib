package org.inagora.tinkerwrapper.implementation;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

import com.tencent.tinker.entry.ApplicationLike;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 被Tinker反射调用的Application业务逻辑类
 */
@SuppressWarnings("unused")
public class DelegateApplicationLike extends ApplicationLike {
    /**
     * @param application 传入真正运行的Application类
     */
    public DelegateApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    private Application realAppLogic;

    @Override
    public void onCreate() {
        realAppLogic.onCreate();
    }

    @Override
    public void onLowMemory() {
        realAppLogic.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        realAppLogic.onTrimMemory(level);
    }

    @Override
    public void onTerminate() {
        realAppLogic.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        realAppLogic.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBaseContextAttached(Context base) {
        TinkerMgrImpl.getInstance().initVarByContext(base);
        TinkerMgrImpl.getInstance().installTinker(this);
        reflectAppLogic();
        attachApplication(base);
    }

    private void reflectAppLogic() {
        try {
            ApplicationInfo info = getApplication()
                    .getPackageManager()
                    .getApplicationInfo(getApplication().getPackageName(),
                            PackageManager.GET_META_DATA);
            String applicationName = info.metaData.getString("application_name");
            final Class<?> clz = Class.forName(applicationName);
            final Constructor<?> constructor = clz.getConstructor();
            constructor.setAccessible(true);
            realAppLogic = (Application) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void attachApplication(Context base) {
        if (realAppLogic == null) {
            return;
        }
        try {
            final Class<?> clz = Class.forName("android.app.Application");
            final Method attachMethod = clz.getDeclaredMethod("attach", Context.class);
            attachMethod.setAccessible(true);
            attachMethod.invoke(realAppLogic, base);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
