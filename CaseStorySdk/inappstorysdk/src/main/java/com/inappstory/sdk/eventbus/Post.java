package com.inappstory.sdk.eventbus;

import java.util.ArrayList;
import java.util.List;

final class Post {
    private final static List<Post> pendingPostPool = new ArrayList<Post>();

    Object event;
    Subscription subscription;
    Post next;

    private Post(Object event, Subscription subscription) {
        this.event = event;
        this.subscription = subscription;
    }

    static Post obtainPendingPost(Subscription subscription, Object event) {
        synchronized (pendingPostPool) {
            int size = pendingPostPool.size();
            if (size > 0) {
                Post pendingPost = pendingPostPool.remove(size - 1);
                pendingPost.event = event;
                pendingPost.subscription = subscription;
                pendingPost.next = null;
                return pendingPost;
            }
        }
        return new Post(event, subscription);
    }

    static void releasePendingPost(Post pendingPost) {
        pendingPost.event = null;
        pendingPost.subscription = null;
        pendingPost.next = null;
        synchronized (pendingPostPool) {
            // Don't let the pool grow indefinitely
            if (pendingPostPool.size() < 10000) {
                pendingPostPool.add(pendingPost);
            }
        }
    }

}
