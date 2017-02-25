package com.steelkiwi.cropiwa.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import com.steelkiwi.cropiwa.config.CropIwaSaveConfig;
import com.steelkiwi.cropiwa.util.CropIwaLog;
import com.steelkiwi.cropiwa.util.CropIwaUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.steelkiwi.cropiwa.util.CropIwaUtils.*;

/**
 * @author Yaroslav Polyakov https://github.com/polyak01
 *         on 25.02.2017.
 */

public class CropIwaBitmapManager {

    private static final CropIwaBitmapManager INSTANCE = new CropIwaBitmapManager();

    public static final int SIZE_UNSPECIFIED = -1;

    public static CropIwaBitmapManager get() {
        return INSTANCE;
    }

    private Map<Uri, BitmapLoadListener> requestResultListeners;
    private Map<Uri, File> localCache;

    private CropIwaBitmapManager() {
        requestResultListeners = new HashMap<>();
        localCache = Collections.synchronizedMap(new HashMap<Uri, File>());
    }

    public void load(Context context, Uri uri, int width, int height, BitmapLoadListener listener) {
        CropIwaLog.d("requesting to load " + uri);
        if (requestResultListeners.containsKey(uri)) {
            CropIwaLog.d("loading already in progress...");
            requestResultListeners.put(uri, listener);
            return;
        }
        CropIwaLog.d("loading started...");
        requestResultListeners.put(uri, listener);
        LoadImageTask task = new LoadImageTask(
                context.getApplicationContext(), uri,
                width, height);
        task.execute();
    }

    public void crop(Context context, CropArea cropArea, Uri uri, CropIwaSaveConfig saveConfig) {
        CropImageTask cropTask = new CropImageTask(
                context.getApplicationContext(),
                cropArea, uri, saveConfig);
        cropTask.execute();
    }

    public void unregisterLoadListenerFor(Uri uri) {
        if (requestResultListeners.containsKey(uri)) {
            requestResultListeners.put(uri, null);
        }
    }

    public void scheduleRemoveIfCached(Uri uri) {
        delete(localCache.remove(uri));
    }

    private Bitmap loadToMemory(Context context, Uri uri, int width, int height) throws IOException {
        Bitmap result;
        Uri localResUri = uri;
        if (isWebUri(uri)) {
            File cached = localCache.get(uri);
            if (cached == null) {
                cached = cacheLocally(context, uri);
                localCache.put(uri, cached);
            }
            localResUri = Uri.fromFile(cached);
        }

        BitmapFactory.Options options = null;
        if (width != SIZE_UNSPECIFIED && height != SIZE_UNSPECIFIED) {
            options = getOptimalSizeOptions(context, localResUri, width, height);
        }
        InputStream is = context.getContentResolver().openInputStream(localResUri);
        result = BitmapFactory.decodeStream(is, null, options);

        CropIwaLog.d("loaded image with dimensions {width=%d, height=%d}",
                result.getWidth(),
                result.getHeight());

        return result;
    }

    private File cacheLocally(Context context, Uri input) throws IOException {
        File local = new File(context.getCacheDir(), generateLocalTempFileName(input));
        URL url = new URL(input.toString());
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            int read;
            byte[] buffer = new byte[1024];
            bis = new BufferedInputStream(url.openStream());
            bos = new BufferedOutputStream(new FileOutputStream(local));
            while ((read = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
            bos.flush();
        } finally {
            closeSilently(bis);
            closeSilently(bos);
        }
        CropIwaLog.d("cached %s as %s", input.toString(), local.getAbsolutePath());
        return local;
    }

    private static BitmapFactory.Options getOptimalSizeOptions(
            Context context, Uri bitmapUri,
            int reqWidth, int reqHeight) throws FileNotFoundException {
        InputStream is = context.getContentResolver().openInputStream(bitmapUri);
        BitmapFactory.Options result = new BitmapFactory.Options();
        result.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, result);
        result.inJustDecodeBounds = false;
        result.inSampleSize = calculateInSampleSize(result, reqWidth, reqHeight);
        return result;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private boolean isWebUri(Uri uri) {
        String scheme = uri.getScheme();
        return "http".equals(scheme) || "https".equals(scheme);
    }

    private String generateLocalTempFileName(Uri uri) {
        return "temp_" + uri.getLastPathSegment() + "_" + System.currentTimeMillis();
    }

    private class LoadImageTask extends AsyncTask<Void, Void, Throwable> {

        private Context context;
        private Uri uri;
        private int desiredWidth;
        private int desiredHeight;

        private Bitmap result;

        private LoadImageTask(Context context, Uri uri, int desiredWidth, int desiredHeight) {
            this.context = context;
            this.uri = uri;
            this.desiredWidth = desiredWidth;
            this.desiredHeight = desiredHeight;
        }

        @Override
        protected Throwable doInBackground(Void... params) {
            try {
                result = loadToMemory(context, uri, desiredWidth, desiredHeight);
            } catch (Exception e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Throwable e) {
            BitmapLoadListener listener = requestResultListeners.remove(uri);
            if (listener != null) {
                if (e != null) {
                    listener.onLoadFailed(e);
                } else {
                    listener.onBitmapLoaded(uri, result);
                }
            } else {
                //There is no listener interested in this request, so nobody will take care of
                //cached image.
                scheduleRemoveIfCached(uri);
            }
        }
    }

    private class CropImageTask extends AsyncTask<Void, Void, Throwable> {

        private Context context;
        private CropArea cropArea;
        private Uri srcUri;
        private CropIwaSaveConfig saveConfig;

        public CropImageTask(Context context, CropArea cropArea, Uri srcUri, CropIwaSaveConfig saveConfig) {
            this.context = context;
            this.cropArea = cropArea;
            this.srcUri = srcUri;
            this.saveConfig = saveConfig;
        }

        @Override
        protected Throwable doInBackground(Void... params) {
            try {
                Bitmap bitmap = loadToMemory(
                        context, srcUri, saveConfig.getWidth(),
                        saveConfig.getHeight());

                Bitmap cropped = cropArea.applyCropTo(bitmap);

                Uri dst = saveConfig.getDstUri();
                OutputStream os = context.getContentResolver().openOutputStream(dst);
                cropped.compress(saveConfig.getCompressFormat(), saveConfig.getQuality(), os);
                CropIwaUtils.closeSilently(os);

                bitmap.recycle();
                cropped.recycle();
            } catch (IOException e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Throwable throwable) {
            super.onPostExecute(throwable);
        }
    }

    public interface BitmapLoadListener {
        void onBitmapLoaded(Uri uri, Bitmap bitmap);

        void onLoadFailed(Throwable e);
    }
}
