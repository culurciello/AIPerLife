package com.fwdnxt.ar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import java.io.ByteArrayOutputStream;

public class LiveMode{
	
	protected static CreateGame context;
	int imageSideNN = 128; // input image size for neural network
	Bitmap bitmap;
    YuvImage yuv;
	float[][] protos = new float[5][];
	int saveproto = -1;

	/**
	 *
	 * @param context
	 */
	public LiveMode(Context context) {
		this.context = (CreateGame) context;
	}

	/**
	 *
	 * @param image
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	static public Bitmap getResizedBitmap(Bitmap image, int newWidth, int newHeight) {
		int width = image.getWidth();
		int height = image.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);
		matrix.postRotate(90);
		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height, matrix, false);
		return resizedBitmap;
	}

	float distance(float[] a, float b[])
	{
		// calculate the cosine distance:
		float num = 0, den1 = 0, den2 = 0;
		for(int i = 0; i < a.length; i++)
		{
			num += a[i] * b[i];
			den1 += a[i] * a[i];
			den2 += b[i] * b[i];
		}
		den1 = (float)Math.sqrt(den1 * den2);
		if(den1 > 0)
			return 1 - num / den1;
		else return 2;
	}

	/**
	 *
	 * @param data
	 * @param mCamera
	 */
	public void startProcess(byte[] data, Camera mCamera, NativeProcessor nativeAPI) {

		Camera.Parameters parameters = mCamera.getParameters();

	    int width = parameters.getPreviewSize().width;
	    int height = parameters.getPreviewSize().height;

	    yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

	    byte[] bytes = out.toByteArray();
	   	bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		bitmap = getResizedBitmap(bitmap, imageSideNN, imageSideNN);

	    // We dump the rotated Bitmap to the stream 
	    //bitmap.compress(CompressFormat.JPEG, 20, rotatedStream);

		int pixels[] = new int[imageSideNN * imageSideNN];
		bitmap.getPixels(pixels, 0, imageSideNN, 0, 0, imageSideNN, imageSideNN);
		float percentages[] = nativeAPI.processImage(pixels);
		if (saveproto >= 0) {
			protos[saveproto] = percentages;
			context.SetProtoImage(saveproto, bitmap);
			saveproto = -1;
		}
		float min = 2;
		float max = 0;
		int best = -1;
		for (int i = 0; i < 5; i++) {
			if (protos[i] != null) {
				float d = distance(protos[i], percentages);
				if (d > max) max = d;
				if (d < min) {
					best = i;
					min = d;
				}
			}
		}
		context.ProtoFound(best, min, max);
	}

}