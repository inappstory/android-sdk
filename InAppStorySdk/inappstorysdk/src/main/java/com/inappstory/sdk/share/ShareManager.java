package com.inappstory.sdk.share;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.utils.StoryShareBroadcastReceiver;
import com.inappstory.sdk.stories.utils.TaskRunner;

import java.util.ArrayList;

public class ShareManager {

    public static final int SHARE_EVENT = 909;

    private void sendIntent(Activity context, Intent sendIntent) {
        PendingIntent pi = PendingIntent.getBroadcast(context, SHARE_EVENT,
                new Intent(context, StoryShareBroadcastReceiver.class),
                FLAG_UPDATE_CURRENT);
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

    public void shareDefault(final Activity context, JSShareModel shareObject) {
        final Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, shareObject.getTitle());

        if (shareObject.getText() != null)
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareObject.getText());
        if (shareObject.getFiles().isEmpty()) {
            sendIntent.setType("text/plain");
            sendIntent(context, sendIntent);
        } else {
            sendIntent.setType(shareObject.getFiles().get(0).getType());
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            new TaskRunner().executeAsync(new UriFromBase64(context, shareObject.getFiles()),
                    new TaskRunner.Callback<ArrayList<Uri>>() {
                        @Override
                        public void onComplete(ArrayList<Uri> result) {
                            if (result.size() > 1) {
                                sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                                sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, result);
                            } else if (!result.isEmpty()) {
                                sendIntent.putExtra(Intent.EXTRA_STREAM, result.get(0));
                            }
                            sendIntent(context, sendIntent);
                        }
                    });
        }

    }
}
