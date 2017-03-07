#include <string.h>
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <android/log.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include "thnets.h"
#include "libswscale/swscale.h"
#include "android_fopen.h"
#define  D(...)  __android_log_print(ANDROID_LOG_INFO, "thnets", __VA_ARGS__)

static THNETWORK *net;
static struct SwsContext *sws_context;
static int prevw, prevh, side, prevside;
static int *bgra;

/**
 * Takes the rgb values of an image, runs it through the network, and returns an array of percentages.
 * The percentages can be correlated to the categories in the categories.txt file in the network folder.
 */
jfloatArray Java_com_fwdnxt_ar_NativeProcessor_processImage(JNIEnv *env, jobject thiz, jbyteArray image, int w, int h, jintArray crop)
{
    float *result;
    int outwidth, outheight;

    if(!net)
        return (*env)->NewFloatArray(env, 0);
    uint8_t *pixels = (uint8_t *)(*env)->GetByteArrayElements(env, image, NULL);
    int *croppixels = (*env)->GetIntArrayElements(env, crop, NULL);
    if(prevw != w || prevh != h || side != prevside || !sws_context || !bgra)
    {
        if(sws_context)
        {
            sws_freeContext(sws_context);
            sws_context = 0;
        }
        if(bgra)
        {
            free(bgra);
            bgra = 0;
        }
        if(w > h)
            sws_context = sws_getContext(h, h, AV_PIX_FMT_NV21, side, side, AV_PIX_FMT_BGRA, SWS_FAST_BILINEAR, 0, 0, 0);
        else sws_context = sws_getContext(w, w, AV_PIX_FMT_NV21, side, side, AV_PIX_FMT_BGRA, SWS_FAST_BILINEAR, 0, 0, 0);
        if(!sws_context)
        {
            D("Error creating swsContext for %dx%d NV21 -> %dx%d BGRA", w, h, side, side);
            return (*env)->NewFloatArray(env, 0);
        }
        prevh = h;
        prevw = w;
        prevside = side;
        bgra = malloc(4 * side * side);
    }
    const uint8_t *srcSlice[3];
    int srcStride[3];
    srcStride[0] = srcStride[1] = srcStride[2] = w;
    int dstStride = 4*side;
    if(w > h)
    {
        srcSlice[0] = pixels + (w-h)/2;
        srcSlice[1] = srcSlice[2] = pixels + w*h + (w-h)/2;
        sws_scale(sws_context, srcSlice, srcStride, 0, h, (int8_t **)&bgra, &dstStride);
    } else {
        srcSlice[0] = pixels + w * (h-w)/2;
        srcSlice[1] = srcSlice[2] = pixels + w*h + w * (h-w)/4;
        sws_scale(sws_context, srcSlice, srcStride, 0, w, (uint8_t **)&bgra, &dstStride);
    }
    int i, j;
#pragma omp parallel for private(i,j)
    for(i = 0; i < side; i++)
        for(j = 0; j < side; j++)
            croppixels[i*side+side-1-j] = bgra[j*side+i];

    int size = THProcessImages(net, &croppixels, 1, side, side, 4*side, &result, &outwidth, &outheight, 1);
    jfloatArray percentages = (*env)->NewFloatArray(env, size);
    (*env)->SetFloatArrayRegion(env, percentages, 0, size, result);
    return percentages;
}

/**
 * Set up the system to allow native processing.
 */
int Java_com_fwdnxt_ar_NativeProcessor_init(JNIEnv* env, jobject thiz, jobject assetManager, jstring netpath, int side1, jboolean dropclassifier)
{
    // get native asset manager. This allows access to files stored in the assets folder
    if(assetManager)
        android_fopen_set_asset_manager((AAssetManager *)AAssetManager_fromJava(env, assetManager));
    else android_fopen_set_asset_manager(0);
    if(side1 < 8 || side1 > 2048)
    {
        D("Wrong side given to NativeProcessor_init()");
        return -1;
    }

    THInit();

    if(net)
        THFreeNetwork(net);

    const char *path = (*env)->GetStringUTFChars(env, netpath, 0);
    net = THLoadNetwork(path);
    (*env)->ReleaseStringUTFChars(env, netpath, path);
    if(net) {
        THUseSpatialConvolutionMM(net, 2);
        if(dropclassifier)
        {
            if(net->net->modules[net->net->nelem-1].type == MT_SoftMax)
                net->net->nelem--;
            if(net->net->modules[net->net->nelem-1].type == MT_Linear)
                net->net->nelem--;
            if(net->net->modules[net->net->nelem-1].type == MT_View)
                net->net->nelem--;
        }
        side = side1;
        return 0;

    } else {
        D("Error loading network");
        return -1;
    }
}
