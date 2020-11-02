package io.casestory.sdk.eventbus;

import android.os.Looper;

public interface MainThreadSupport {

    boolean isMainThread();

    Poster createPoster(CsEventBus eventBus);

    class AndroidHandlerMainThreadSupport implements MainThreadSupport {

        private final Looper looper;

        public AndroidHandlerMainThreadSupport(Looper looper) {
            this.looper = looper;
        }

        @Override
        public boolean isMainThread() {
            return looper == Looper.myLooper();
        }

        @Override
        public Poster createPoster(CsEventBus eventBus) {
            return new HandlerPoster(eventBus, looper, 10);
        }
    }

}
