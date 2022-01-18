package com.inappstory.sdk.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;


import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

public class UriFromBase64 implements Callable<Uri> {
    Context context;
    JSShareFile shareFile;


    public UriFromBase64(Context context,
                         JSShareFile shareFile) {
        this.context = context;
        this.shareFile = shareFile;
    }


    public Bitmap getBitmapFromBase64String(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private Uri saveImage(Context context, Bitmap image, String name, String type) {
        //TODO - Should be processed in another thread
        File imagesFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        if (image == null) return null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, name);

            FileOutputStream stream = new FileOutputStream(file);
            Bitmap.CompressFormat compressFormat;
            switch (type) {
                case "image/jpg":
                case "image/jpeg":
                    compressFormat = Bitmap.CompressFormat.JPEG;
                    break;
                case "image/png":
                    compressFormat = Bitmap.CompressFormat.PNG;
                    break;
                default:
                    compressFormat = Bitmap.CompressFormat.WEBP;
                    break;
            }
            image.compress(compressFormat, 100, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context, "com.inappstory.fileprovider", file);
        } catch (IOException e) {
        }
        return uri;
    }

    @Override
    public Uri call() throws Exception {
        return saveImage(context,
                getBitmapFromBase64String(shareFile.getFile()),
                shareFile.getName(),
                shareFile.getType());
    }
}
