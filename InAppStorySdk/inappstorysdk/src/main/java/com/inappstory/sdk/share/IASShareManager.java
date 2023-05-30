package com.inappstory.sdk.share;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.inner.share.UriFromBase64;
import com.inappstory.sdk.stories.utils.TaskRunner;

import java.util.ArrayList;
import java.util.List;

public class IASShareManager {

    public static final int SHARE_EVENT = 909;

    private <T extends BroadcastReceiver> void sendIntent(Context context, Intent sendIntent, Class<T> receiver) {
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

    public <T extends BroadcastReceiver> void shareDefault(
            final Class<T> receiver,
            final Context context,
            final IASShareData shareObject) {
        final Intent sendingIntent = new Intent();
        sendingIntent.setAction(Intent.ACTION_SEND);

        if (shareObject.url != null)
            sendingIntent.putExtra(Intent.EXTRA_TEXT, shareObject.url);
        List<Uri> files = shareObject.getFiles();
        if (files.isEmpty()) {
            sendingIntent.setType("text/plain");
        } else {
            sendingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sendingIntent.setType("image/*");
            if (files.size() > 1) {
                sendingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                sendingIntent.putParcelableArrayListExtra(
                        Intent.EXTRA_STREAM,
                        new ArrayList(files)
                );
            } else {
                sendingIntent.putExtra(Intent.EXTRA_STREAM, files.get(0));
            }
        }
        sendIntent(context, sendingIntent, receiver);
    }

    public <T extends BroadcastReceiver> void shareToSpecificApp(
            final Class<T> receiver,
            final Context context,
            final IASShareData shareObject,
            String packageName) {
        final Intent sendingIntent = new Intent();
        sendingIntent.setAction(Intent.ACTION_SEND);

        if (shareObject.url != null)
            sendingIntent.putExtra(Intent.EXTRA_TEXT, shareObject.url);
        if (packageName != null) {
            sendingIntent.setPackage(packageName);
        }
        List<Uri> files = shareObject.getFiles();
        if (files.isEmpty()) {
            sendingIntent.setType("text/plain");
        } else {
            sendingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sendingIntent.setType("image/*");
            if (files.size() > 1) {
                sendingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                sendingIntent.putParcelableArrayListExtra(
                        Intent.EXTRA_STREAM,
                        new ArrayList(files)
                );
            } else {
                sendingIntent.putExtra(Intent.EXTRA_STREAM, files.get(0));
            }
        }
        sendIntent(context, sendingIntent, receiver);
    }
}
