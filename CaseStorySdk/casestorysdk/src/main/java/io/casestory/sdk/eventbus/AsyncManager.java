package io.casestory.sdk.eventbus;

public class AsyncManager implements Runnable {
    private final EventBus eventBus;
    private final PostQueue queue;

    AsyncManager(EventBus eventBus) {
        this.eventBus = eventBus;
        queue = new PostQueue();
    }

    public void enqueue(Subscription subscription, Object event) {
        Post pendingPost = Post.obtainPendingPost(subscription, event);
        queue.enqueue(pendingPost);
        eventBus.getExecutorService().execute(this);
    }

    @Override
    public void run() {
        Post pendingPost = queue.poll();
        if(pendingPost == null) {
            throw new IllegalStateException("No pending post available");
        }
        eventBus.invokeSubscriber(pendingPost);
    }
}
