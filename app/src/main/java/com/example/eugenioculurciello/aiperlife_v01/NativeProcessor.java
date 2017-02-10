package com.example.eugenioculurciello.aiperlife_v01;

import android.content.res.AssetManager;

public class NativeProcessor {
    public native void init(AssetManager assetManager);
    public native float[] processImage(int[] r, int[] g, int[] b);
}