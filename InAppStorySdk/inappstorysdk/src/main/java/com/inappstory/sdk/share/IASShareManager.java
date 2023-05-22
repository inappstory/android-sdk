package com.inappstory.sdk.share;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.utils.StoryShareBroadcastReceiver;
import com.inappstory.sdk.stories.utils.TaskRunner;

import java.util.ArrayList;

public class IASShareManager {

    public static final int SHARE_EVENT = 909;

    private <T extends BroadcastReceiver> void sendIntent(Activity context, Intent sendIntent, Class<T> receiver) {
        int shareFlag = FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            shareFlag |= FLAG_IMMUTABLE;
        }
        PendingIntent pi = PendingIntent.getBroadcast(context, SHARE_EVENT,
                new Intent(context, receiver),
                shareFlag);
        Intent finalIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            finalIntent = Intent.createChooser(sendIntent, null, pi.getIntentSender());
            context.startActivityForResult(finalIntent, SHARE_EVENT);
        } else {
            if (InAppStoryService.isNull()) return;
            finalIntent = Intent.createChooser(sendIntent, null);
            finalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(finalIntent);
        }
    }

    public <T extends BroadcastReceiver> void shareDefault(
            final Class<T> receiver,
            final Activity context,
            final IASShareData shareObject) {
        final Intent sendingIntent = new Intent();
        sendingIntent.setAction(Intent.ACTION_SEND);
        sendingIntent.putExtra(Intent.EXTRA_SUBJECT, shareObject.getTitle());

        if (shareObject.getText() != null)
            sendingIntent.putExtra(Intent.EXTRA_TEXT, shareObject.getText());
        if (shareObject.getFiles().isEmpty()) {
            sendingIntent.setType("text/plain");
            sendIntent(context, sendingIntent, receiver);
        } else {
            sendingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sendingIntent.setType("image/*");
            new TaskRunner().executeAsync(new UriFromBase64(context, shareObject.getFiles()),
                    new TaskRunner.Callback<ArrayList<Uri>>() {
                        @Override
                        public void onComplete(ArrayList<Uri> result) {
                            if (result.size() > 1) {
                                sendingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                                sendingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, result);
                            } else if (!result.isEmpty()) {
                                sendingIntent.putExtra(Intent.EXTRA_STREAM, result.get(0));
                            }
                            sendIntent(context, sendingIntent, receiver);
                        }
                    });
        }
    }

    public <T extends BroadcastReceiver> void shareToSpecificApp(
            final Class<T> receiver,
            final Activity context,
            final IASShareData shareObject,
            String packageName) {
        final Intent sendingIntent = new Intent();
        sendingIntent.setAction(Intent.ACTION_SEND);
        sendingIntent.putExtra(Intent.EXTRA_SUBJECT, shareObject.getTitle());
        if (packageName != null) {
            sendingIntent.setPackage(packageName);
        }
        if (shareObject.getText() != null)
            sendingIntent.putExtra(Intent.EXTRA_TEXT, shareObject.getText());
        if (shareObject.getFiles().isEmpty()) {
            sendingIntent.setType("text/plain");
            sendIntent(context, sendingIntent, receiver);
        } else {
            sendingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sendingIntent.setType("image/*");
            new TaskRunner().executeAsync(new UriFromBase64(context, shareObject.getFiles()),
                    new TaskRunner.Callback<ArrayList<Uri>>() {
                        @Override
                        public void onComplete(ArrayList<Uri> result) {
                            if (result.size() > 1) {
                                sendingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                                sendingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, result);
                            } else if (!result.isEmpty()) {
                                sendingIntent.putExtra(Intent.EXTRA_STREAM, result.get(0));
                            }
                            sendIntent(context, sendingIntent, receiver);
                        }
                    });
        }
    }
}
