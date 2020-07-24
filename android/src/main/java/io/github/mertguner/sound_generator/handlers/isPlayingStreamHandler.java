package io.github.mertguner.sound_generator.handlers;

import io.flutter.plugin.common.EventChannel.EventSink;
import io.flutter.plugin.common.EventChannel.StreamHandler;

public class isPlayingStreamHandler implements StreamHandler {
    public static final String NATIVE_CHANNEL_EVENT = "io.github.mertguner.sound_generator/onChangeIsPlaying";
    private volatile static isPlayingStreamHandler mEventManager;
    EventSink eventSink;

    public isPlayingStreamHandler()
    {
        if(mEventManager == null)
            mEventManager = this;
    }

    @Override
    public void onListen(Object o, EventSink eventSink) {
        this.eventSink = eventSink;
    }

    public static void change(boolean value) {
        if(mEventManager != null)
        if (mEventManager.eventSink != null)
            mEventManager.eventSink.success(Boolean.valueOf(value));
    }

    @Override
    public void onCancel(Object o) {
        if (this.eventSink != null) {
            this.eventSink.endOfStream();
            this.eventSink = null;
        }
    }
}