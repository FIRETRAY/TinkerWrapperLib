package org.inagora.tinkerwrapper.implementation.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.inagora.tinkerwrapper.implementation.TinkerMgrImpl;

import java.util.HashSet;
import java.util.Set;

class TinkerSPUtils {

    private static final String SP_TINKER_OPTION = "tinker_option";

    static boolean getBoolean(String key, Boolean defaultValue) {
        Context context = TinkerMgrImpl.getInstance().getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences(SP_TINKER_OPTION, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    static void putBoolean(String key, boolean value) {
        Context context = TinkerMgrImpl.getInstance().getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences(SP_TINKER_OPTION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    static String getString(String key, String defaultValue) {
        Context context = TinkerMgrImpl.getInstance().getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences(SP_TINKER_OPTION, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    static void putString(String key, String value) {
        Context context = TinkerMgrImpl.getInstance().getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences(SP_TINKER_OPTION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    static boolean putStringByCommit(String key, String value) {
        Context context = TinkerMgrImpl.getInstance().getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences(SP_TINKER_OPTION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    static int getInt(String key, int defaultValue) {
        Context context = TinkerMgrImpl.getInstance().getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences(SP_TINKER_OPTION, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }

    static void putInt(String key, int value) {
        Context context = TinkerMgrImpl.getInstance().getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences(SP_TINKER_OPTION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    static long getLong(String key, long defaultValue) {
        Context context = TinkerMgrImpl.getInstance().getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences(SP_TINKER_OPTION, Context.MODE_PRIVATE);
        return sp.getLong(key, defaultValue);
    }

    static void putLong(String key, long value) {
        Context context = TinkerMgrImpl.getInstance().getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences(SP_TINKER_OPTION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    boolean putLongByCommit(String key, long value) {
        Context context = TinkerMgrImpl.getInstance().getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences(SP_TINKER_OPTION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    static void putStringSet(String key, Set<String> value) {
        Context context = TinkerMgrImpl.getInstance().getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences(SP_TINKER_OPTION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    static Set<String> getStringSet(String key) {
        Context context = TinkerMgrImpl.getInstance().getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences(SP_TINKER_OPTION, Context.MODE_PRIVATE);
        return sp.getStringSet(key, new HashSet<String>());
    }
}