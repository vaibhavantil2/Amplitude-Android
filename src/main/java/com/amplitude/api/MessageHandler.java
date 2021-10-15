package com.amplitude.api;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class MessageHandler extends Handler {

    private static final String TAG = MessageHandler.class.getName();
    private static final AmplitudeLog logger = AmplitudeLog.getLogger();

    static final int REQUEST_FLUSH = 1;
    private final HttpClient httpClient;
    private final HttpService.RequestListener requestListener;

    public MessageHandler(Looper looper, HttpClient httpClient, HttpService.RequestListener requestListener) {
        super(looper);
        this.httpClient = httpClient;
        this.requestListener = requestListener;
    }

    @Override
    public void handleMessage(final Message msg) {
        switch (msg.what) {
            case REQUEST_FLUSH:
                SendEventsData data = (SendEventsData) msg.obj;
                flushEvents(data);
                break;
            default:
                throw new AssertionError("Unknown dispatcher message: " + msg.what);
        }
    }

    private void flushEvents(SendEventsData data) {
        try {
            HttpResponse response = httpClient.getSyncHttpResponse(data.events);
            if (response.responseCode == 200 && response.responseMessage.equals("success")) {
                requestListener.onSuccess(data.maxEventId, data.maxIdentifyId);
            } else {
                String stringResponse = response.responseMessage;
                if (stringResponse.equals("invalid_api_key")) {
                    logger.e(TAG, "Invalid API key, make sure your API key is correct in initialize()");
                } else if (stringResponse.equals("bad_checksum")) {
                    logger.w(TAG,
                            "Bad checksum, post request was mangled in transit, will attempt to reupload later");
                } else if (stringResponse.equals("request_db_write_failed")) {
                    logger.w(TAG,
                            "Couldn't write to request database on server, will attempt to reupload later");
                } else if (response.responseCode == 413) {
                    requestListener.onError(data.maxEventId, data.maxIdentifyId);
                } else {
                    logger.w(TAG, "Upload failed, " + stringResponse
                            + ", will attempt to reupload later");
                }
            }
        } catch (Exception e) {
            logger.e(TAG, e.toString());
        }
    }

}