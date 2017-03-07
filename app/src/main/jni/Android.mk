LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CFLAGS += -std=c99 -fomit-frame-pointer -pthread \
   -g -Wdeclaration-after-statement -Wall -Wdisabled-optimization -Wpointer-arith -Wredundant-decls \
   -Wwrite-strings -Wtype-limits -Wundef -Wmissing-prototypes -Wno-pointer-to-int-cast \
   -Wstrict-prototypes -Wempty-body -Wno-parentheses -Wno-switch -Wno-format-zero-length \
   -Wno-pointer-sign -O3 -fno-math-errno -fno-signed-zeros -fno-tree-vectorize \
   -Werror=format-security -Werror=implicit-function-declaration -Werror=missing-prototypes \
   -Werror=return-type -Werror=vla -Wformat -fdiagnostics-color=auto -Wno-maybe-uninitialized \
   -DHAVE_AV_CONFIG_H

LOCAL_MODULE := avutil

LOCAL_SRC_FILES := libavutil/adler32.c \
	libavutil/aes.c \
	libavutil/aes_ctr.c \
	libavutil/audio_fifo.c \
	libavutil/avstring.c \
	libavutil/base64.c \
	libavutil/blowfish.c \
	libavutil/bprint.c \
	libavutil/buffer.c \
	libavutil/cast5.c \
	libavutil/camellia.c \
	libavutil/channel_layout.c \
	libavutil/color_utils.c \
	libavutil/cpu.c \
	libavutil/crc.c \
	libavutil/des.c \
	libavutil/dict.c \
	libavutil/display.c \
	libavutil/downmix_info.c \
	libavutil/error.c \
	libavutil/eval.c \
	libavutil/fifo.c \
	libavutil/file.c \
	libavutil/file_open.c \
	libavutil/float_dsp.c \
	libavutil/frame.c \
	libavutil/hash.c \
	libavutil/hmac.c \
	libavutil/hwcontext.c \
	libavutil/imgutils.c \
	libavutil/integer.c \
	libavutil/intmath.c \
	libavutil/lfg.c \
	libavutil/lls.c \
	libavutil/log.c \
	libavutil/log2_tab.c \
	libavutil/mathematics.c \
	libavutil/mastering_display_metadata.c \
	libavutil/md5.c \
	libavutil/mem.c \
	libavutil/murmur3.c \
	libavutil/opt.c \
	libavutil/parseutils.c \
	libavutil/pixdesc.c \
	libavutil/pixelutils.c \
	libavutil/random_seed.c \
	libavutil/rational.c \
	libavutil/reverse.c \
	libavutil/rc4.c \
	libavutil/ripemd.c \
	libavutil/samplefmt.c \
	libavutil/sha.c \
	libavutil/sha512.c \
	libavutil/stereo3d.c \
	libavutil/threadmessage.c \
	libavutil/time.c \
	libavutil/timecode.c \
	libavutil/tree.c \
	libavutil/twofish.c \
	libavutil/utils.c \
	libavutil/xga_font_data.c \
	libavutil/xtea.c \
	libavutil/tea.c

ifeq ($(TARGET_ARCH),arm64)
LOCAL_SRC_FILES += libavutil/aarch64/cpu.c \
	libavutil/aarch64/float_dsp_init.c \
	libavutil/aarch64/float_dsp_neon.S
LOCAL_CFLAGS += -DAARCH64
else
LOCAL_SRC_FILES += libavutil/arm/cpu.c \
	libavutil/arm/float_dsp_init_arm.c \
	libavutil/arm/float_dsp_init_vfp.c \
	libavutil/arm/float_dsp_vfp.S \
	libavutil/arm/float_dsp_init_neon.c \
	libavutil/arm/float_dsp_neon.S
LOCAL_CFLAGS += -march=armv7-a -mthumb -D_REENTRANT
endif

LOCAL_LDLIBS := -landroid -lm -llog

include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_CFLAGS += -std=c99 -fomit-frame-pointer -pthread \
   -g -Wdeclaration-after-statement -Wall -Wdisabled-optimization -Wpointer-arith -Wredundant-decls \
   -Wwrite-strings -Wtype-limits -Wundef -Wmissing-prototypes -Wno-pointer-to-int-cast \
   -Wstrict-prototypes -Wempty-body -Wno-parentheses -Wno-switch -Wno-format-zero-length \
   -Wno-pointer-sign -O3 -fno-math-errno -fno-signed-zeros -fno-tree-vectorize \
   -Werror=format-security -Werror=implicit-function-declaration -Werror=missing-prototypes \
   -Werror=return-type -Werror=vla -Wformat -fdiagnostics-color=auto -Wno-maybe-uninitialized \
   -DHAVE_AV_CONFIG_H

LOCAL_MODULE := swscale

LOCAL_SRC_FILES := libswscale/alphablend.c \
	libswscale/hscale.c \
	libswscale/hscale_fast_bilinear.c \
	libswscale/gamma.c \
	libswscale/input.c \
	libswscale/options.c \
	libswscale/output.c \
	libswscale/rgb2rgb.c \
	libswscale/slice.c \
	libswscale/swscale.c \
	libswscale/swscale_unscaled.c \
	libswscale/utils.c \
	libswscale/yuv2rgb.c \
	libswscale/vscale.c

ifeq ($(TARGET_ARCH),arm64)
LOCAL_SRC_FILES += \
	libswscale/aarch64/swscale.c \
	libswscale/aarch64/swscale_unscaled.c \
	libswscale/aarch64/hscale.S \
	libswscale/aarch64/output.S \
	libswscale/aarch64/yuv2rgb_neon.S
LOCAL_CFLAGS += -DAARCH64
else
LOCAL_SRC_FILES += \
	libswscale/arm/swscale.c \
	libswscale/arm/swscale_unscaled.c \
	libswscale/arm/rgb2yuv_neon_32.S \
	libswscale/arm/rgb2yuv_neon_16.S \
	libswscale/arm/hscale.S \
	libswscale/arm/output.S \
	libswscale/arm/yuv2rgb_neon.S
LOCAL_CFLAGS += -march=armv7-a -mthumb -D_REENTRANT
endif

LOCAL_LDLIBS := -landroid -lm -llog
LOCAL_STATIC_LIBRARIES := avutil

include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_CFLAGS += -Wall -c -fopenmp -fPIC -DARM -O3

LOCAL_MODULE := NativeProcessor

LOCAL_SRC_FILES := thload.c \
	thbasic.c \
	thapi.c \
	pytorch.c \
	nativeProcessor.c \
	android_fopen.c \
	modules/CAddTable.c \
	modules/Concat.c \
	modules/ConcatTable.c \
	modules/Dropout.c \
	modules/JoinTable.c \
	modules/Linear.c \
	modules/Normalize.c \
	modules/PReLU.c \
	modules/Reshape.c \
	modules/Sequential.c \
	modules/SoftMax.c \
	modules/SpatialAveragePooling.c \
	modules/SpatialBatchNormalization.c \
	modules/SpatialConvolution.c \
	modules/SpatialConvolutionMM.c \
	modules/SpatialFullConvolution.c \
	modules/SpatialMaxPooling.c \
	modules/SpatialMaxUnpooling.c \
	modules/SpatialZeroPadding.c \
	modules/Threshold.c \
	modules/View.c \
	OpenBLAS-stripped/sgemm.c \
	OpenBLAS-stripped/sger.c \
	OpenBLAS-stripped/sgemv.c \
	OpenBLAS-stripped/gemm_beta.c \
	OpenBLAS-stripped/gemv_t.c \
	OpenBLAS-stripped/copy.c

ifeq ($(TARGET_ARCH),arm64)
LOCAL_SRC_FILES += \
	OpenBLAS-stripped/arm64/axpy.S \
	OpenBLAS-stripped/arm64/sgemm_kernel_4x4.S \
	OpenBLAS-stripped/generic/gemm_ncopy_4.c \
	OpenBLAS-stripped/generic/gemm_tcopy_4.c
else
LOCAL_SRC_FILES += \
	OpenBLAS-stripped/arm/axpy_vfp.S \
	OpenBLAS-stripped/arm/sgemm_kernel_4x4_vfpv3.S \
	OpenBLAS-stripped/arm/sgemm_ncopy_4_vfp.S \
	OpenBLAS-stripped/arm/sgemm_tcopy_4_vfp.S
LOCAL_CFLAGS += -D__NEON__ -mcpu=cortex-a9 -mfpu=neon
endif

LOCAL_LDLIBS := -landroid -lm -llog
LOCAL_STATIC_LIBRARIES := swscale avutil

include $(BUILD_SHARED_LIBRARY)
