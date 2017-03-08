package com.fwdnxt.ar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.AsyncTask;

public class LiveMode{
	
	protected static CreateGame context;
	int imageSideNN = 128; // input image size for neural network
	int[] crop;
	float[][] protos = new float[5][];
	float fps = 0;
	int saveproto = -1;
	CStartProcess csp;

	/**
	 *
	 * @param context
	 */
	public LiveMode(Context context) {
		this.context = (CreateGame) context;
		crop = new int[imageSideNN*imageSideNN];
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
		//matrix.postRotate(90);
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

	private static class MyTaskParams {
		byte[] data;
		Camera mCamera;
		NativeProcessor nativeAPI;

		MyTaskParams(byte[] data, Camera mCamera, NativeProcessor nativeAPI) {
			this.data = data;
			this.mCamera = mCamera;
			this.nativeAPI = nativeAPI;
		}
	}

	private class CStartProcess extends AsyncTask<MyTaskParams, Void, float[]> {

		@Override
		protected float[] doInBackground(MyTaskParams... params) {
			byte[] data = params[0].data;
			Camera mCamera = params[0].mCamera;
			NativeProcessor nativeAPI = params[0].nativeAPI;

			Camera.Parameters parameters = mCamera.getParameters();
			int width = parameters.getPreviewSize().width;
			int height = parameters.getPreviewSize().height;
			long tm = android.os.SystemClock.uptimeMillis();
			float percentages[] = nativeAPI.processImage(data, width, height, crop);
			fps = (1000f / (android.os.SystemClock.uptimeMillis() - tm));
			return percentages;
		}

		protected void onPostExecute(float[] percentages) {
			if (saveproto >= 0) {
				protos[saveproto] = percentages;
				Bitmap bmp = Bitmap.createBitmap(crop, imageSideNN, imageSideNN, Bitmap.Config.ARGB_8888);
				context.SetProtoImage(saveproto, bmp);
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
			context.ProtoFound(best, min, max, fps);
			csp = null;
		}
	}


	/**
	 *
	 * @param data
	 * @param mCamera
	 */
	public void startProcess(byte[] data, Camera mCamera, NativeProcessor nativeAPI) {

		if(csp != null)
			return;
		MyTaskParams params = new MyTaskParams(data, mCamera, nativeAPI);
		csp = new CStartProcess();
		csp.execute(params);
	}

}