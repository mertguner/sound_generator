package io.github.mertguner.sound_generator.handlers;

import java.util.List;

import io.flutter.plugin.common.EventChannel;

public class getOneCycleDataHandler implements EventChannel.StreamHandler {
    public static final String NATIVE_CHANNEL_EVENT = "io.github.mertguner.sound_generator/onOneCycleDataHandler";
    private volatile static getOneCycleDataHandler mEventManager;
    EventChannel.EventSink eventSink;

    public getOneCycleDataHandler()
    {
        if(mEventManager == null)
            mEventManager = this;
    }

    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {
        this.eventSink = eventSink;
    }

    public static void setData(List<Integer> value) {
        if (mEventManager != null)
            if (mEventManager.eventSink != null)
                    mEventManager.eventSink.success(value);
    }
    @Override
    public void onCancel(Object o) {
        if (this.eventSink != null) {
            this.eventSink.endOfStream();
            this.eventSink = null;
        }
    }
}
