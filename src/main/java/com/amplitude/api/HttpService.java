package com.amplitude.api;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

import android.os.HandlerThread;
import android.os.Looper;

public class HttpService {

    HandlerThread httpThread;
    MessageHandler messageHandler;

    public HttpService(String apiKey, String url, String bearerToken, RequestListener requestListener,
                        boolean secure) {
        this.httpThread = new HandlerThread("httpThread", THREAD_PRIORITY_BACKGROUND);
        httpThread.start();
        this.messageHandler = new MessageHandler(httpThread.getLooper(), secure, apiKey, url, bearerToken, requestListener);
    }

    public void submitSendEvents(String events, long maxEventId, long maxIdentifyId) {
        SendEventsData data = new SendEventsData(events, maxEventId, maxIdentifyId);
        messageHandler.sendMessage(messageHandler.obtainMessage(MessageHandler.REQUEST_FLUSH, data));
    }

    public interface RequestListener {
        void onSuccess(long maxEventId, long maxIdentifyId);
        void onError(long maxEventId, long maxIdentifyId, boolean needsRetry);
    }

    public Looper getHttpThreadLooper() {
        return httpThread.getLooper();
    }

    public void shutdown() {
        messageHandler.removeCallbacks(this.httpThread);
        this.httpThread.quit();
    }

}
