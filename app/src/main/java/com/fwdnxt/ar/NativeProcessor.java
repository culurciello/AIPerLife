package com.fwdnxt.ar;

import android.content.res.AssetManager;

public class NativeProcessor {
    // If assetManager is null, the native API will take it from a file
    // side is the side length of the image that the network expectes
    public native int init(AssetManager assetManager, String path, int side, boolean dropclassifer);
    // The native API will take the wxh NV21 image, crop and rescale it to side x side inside crop
    public native float[] processImage(byte[] nv21, int w, int h, int[] crop);
}