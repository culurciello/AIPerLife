package com.fwdnxt.ar;

import android.content.res.AssetManager;

public class NativeProcessor {
    public native int init(AssetManager assetManager, boolean dropclassifer);
    public native float[] processImage(int[] argb);
}