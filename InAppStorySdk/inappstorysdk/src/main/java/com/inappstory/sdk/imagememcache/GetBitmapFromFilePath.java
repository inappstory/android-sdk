package com.inappstory.sdk.imagememcache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.utils.Sizes;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Callable;

public class GetBitmapFromFilePath implements Callable<Bitmap> {
    private String filePath;

    public GetBitmapFromFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    @WorkerThread
    public Bitmap call() throws Exception {
        return decodeFile(new File(filePath));
    }

    private Bitmap decodeFile(File f) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            final int REQUIRED_SIZE = Sizes.dpToPxExt(800);
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream fileInputStream = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream, null, o2);
            fileInputStream.close();
            return bitmap;
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
        }
        return null;
    }
}