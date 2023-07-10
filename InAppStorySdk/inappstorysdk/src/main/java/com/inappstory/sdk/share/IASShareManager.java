package com.inappstory.sdk.share;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IASShareManager {

    public static final int SHARE_EVENT = 909;

    private <T extends BroadcastReceiver> void sendIntent(
            Context context,
            Intent sendIntent,
            Class<T> receiver
    ) {
        int shareFlag = FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            shareFlag |= FLAG_IMMUTABLE;
        }
        PendingIntent pi = PendingIntent.getBroadcast(context, SHARE_EVENT,
                new Intent(context, receiver),
                shareFlag);
        Intent finalIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            finalIntent = Intent.createChooser(sendIntent, null, pi.getIntentSender());
        } else {
            finalIntent = Intent.createChooser(sendIntent, null);
        }
        finalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(finalIntent);
    }

    public List<Uri> getUrisFromShareData(Context context, IASShareData shareData) {
        List<String> filePaths = shareData.getFiles();
        ArrayList<Uri> files = new ArrayList<>();
        for (String path : filePaths) {
            files.add(
                    FileProvider.getUriForFile(
                            context,
                            context.getPackageName() + ".com.inappstory.fileprovider",
                            new File(path)
                    )
            );
        }
        return files;
    }

    private Intent prepareIntent(Context context, IASShareData shareData) {
        final Intent sendingIntent = new Intent();
        sendingIntent.setAction(Intent.ACTION_SEND);

        if (shareData.getUrl() != null)
            sendingIntent.putExtra(Intent.EXTRA_TEXT, shareData.getUrl());
        List<Uri> files = getUrisFromShareData(context, shareData);
        if (files.isEmpty()) {
            sendingIntent.setType("text/plain");
        } else {
            sendingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sendingIntent.setType("image/*");
            if (files.size() > 1) {
                sendingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                sendingIntent.putParcelableArrayListExtra(
                        Intent.EXTRA_STREAM,
                        new ArrayList<>(files)
                );
            } else {
                sendingIntent.putExtra(Intent.EXTRA_STREAM, files.get(0));
            }
        }
        return sendingIntent;
    }


    public <T extends BroadcastReceiver> void shareDefault(
            Class<T> receiver,
            Context context,
            IASShareData shareObject) {
        Intent sendingIntent = prepareIntent(context, shareObject);
        sendIntent(context, sendingIntent, receiver);
    }


    public <T extends BroadcastReceiver> void shareToSpecificApp(
            final Class<T> receiver,
            final Context context,
            final IASShareData shareObject,
            String packageName) {
        Intent sendingIntent = prepareIntent(context, shareObject);
        if (packageName != null) {
            sendingIntent.setPackage(packageName);
        }
        sendIntent(context, sendingIntent, receiver);
    }
}
