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
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class UriFromBase64 implements Callable<ArrayList<Uri>> {
    Context context;
    ArrayList<JSShareFile> shareFiles;


    public UriFromBase64(Context context,
                         ArrayList<JSShareFile> shareFiles) {
        this.context = context;
        this.shareFiles = shareFiles;
    }


    public Bitmap getBitmapFromBase64String(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private Uri saveImage(Context context, Bitmap image, String name, String type) {
        File imagesFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        Bitmap.CompressFormat compressFormat;
        switch (type) {
            case "image/jpg":
            case "image/jpeg":
                compressFormat = Bitmap.CompressFormat.JPEG;
                break;
            case "image/png":
                compressFormat = Bitmap.CompressFormat.PNG;
                break;
            case "image/webp":
                compressFormat = Bitmap.CompressFormat.WEBP;
                break;
            default:
                return null;
        }
        if (image == null) return null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, name);
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(compressFormat, 100, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context, "com.inappstory.fileprovider", file);
        } catch (IOException e) {
        }
        return uri;
    }

    @Override
    public ArrayList<Uri> call() throws Exception {
        ArrayList<Uri> response = new ArrayList<>();
        for (JSShareFile shareFile : shareFiles) {
            Uri uri = saveImage(context,
                    getBitmapFromBase64String(shareFile.getFile()),
                    shareFile.getName(),
                    shareFile.getType());
            if (uri != null) {
                response.add(uri);
            }
        }
        return response;
    }
}