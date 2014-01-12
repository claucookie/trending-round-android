package com.claucookie.trendingroundapp.lazylist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.app.Activity;
import android.app.PendingIntent.OnFinished;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ImageLoader {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	final int stub_id;
	int mPixels = 0;

	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;

	public ImageLoader(Context context, String cacheDirName, int stub) {

		cacheDirName = "VoiceplateCache/" + cacheDirName;
		fileCache = new FileCache(context, cacheDirName);
		executorService = Executors.newFixedThreadPool(5);
		stub_id = stub;
	}

	public void DisplayImage(String url, ImageView imageView) {

		try {

			imageViews.put(imageView, url);
			Bitmap bitmap = memoryCache.get(url);
			if (bitmap != null)
				imageView.setImageBitmap(bitmap);
			else {
				if (!url.equals("null") && !url.equals("") && url != null) {
					queuePhoto(url, imageView);
					imageView.setImageResource(stub_id);
				} else {
					imageView.setImageResource(stub_id);
				}
			}

		} catch (Exception e) {
			
		}

	}

	public void DisplayRoundedImage(String url, ImageView imageView, int pixels) {

		try {

			mPixels = pixels;
			imageViews.put(imageView, url);
			Bitmap bitmap = memoryCache.get(url);
			if (bitmap != null)
				imageView
						.setImageBitmap(getRoundedCornerBitmap(bitmap, pixels));
			else {
				if (!url.equals("null") && !url.equals("") && url != null) {
					queuePhoto(url, imageView);
					imageView.setImageResource(stub_id);
				} else {
					imageView.setImageResource(stub_id);
				}
			}

		} catch (Exception e) {
			//Log.v("ImageLoader", e.getMessage());
		}

	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {

		Bitmap cropedBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);

		// Hacemos el avatar cuadrado
		if (bitmap.getWidth() >= bitmap.getHeight()) {

			cropedBitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 2
					- bitmap.getHeight() / 2, 0, bitmap.getHeight(),
					bitmap.getHeight());

		} else {

			cropedBitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight()
					/ 2 - bitmap.getWidth() / 2, bitmap.getWidth(),
					bitmap.getWidth());
		}

		// Sobreescribimos el bitmap fuente y creamos el destino
		bitmap = Bitmap.createScaledBitmap(cropedBitmap, pixels + 40,
				pixels + 40, false);
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);

		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		if (executorService.isShutdown())
			executorService = Executors.newFixedThreadPool(5);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url);

		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;

		// from web
		try {

			Log.v("ImageLoader", "ImageLoader " + url);
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();

			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1");

			// conn.addRequestProperty("REFERER", "http://www.theseen.tv");

			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);

			Log.v("ImageLoader", "ImageLoader " + url + " got !!!");

			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 256;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			// Activity a = (Activity) photoToLoad.imageView.getContext();
			// a.runOnUiThread(bd);
			photoToLoad.imageView.post(bd);

		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null) {
				Animation a = AnimationUtils.loadAnimation(
						photoToLoad.imageView.getContext(),
						android.R.anim.fade_in);
				photoToLoad.imageView.startAnimation(a);
				if (mPixels > 0)
					photoToLoad.imageView
							.setImageBitmap(getRoundedCornerBitmap(bitmap,
									mPixels));
				else
					photoToLoad.imageView.setImageBitmap(bitmap);
			} else
				photoToLoad.imageView.setImageResource(stub_id);

			executorService.shutdownNow();

		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

}
