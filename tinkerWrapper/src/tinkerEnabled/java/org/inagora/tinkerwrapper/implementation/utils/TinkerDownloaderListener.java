package org.inagora.tinkerwrapper.implementation.utils;

import java.io.File;
import java.io.IOException;

public abstract class TinkerDownloaderListener {
    public abstract void onSuccess(File file);

    void onFail(IOException e) {
        e.printStackTrace();
    }
}