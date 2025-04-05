package io.github.mertguner.sound_generator;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import io.github.mertguner.sound_generator.handlers.getOneCycleDataHandler;
import io.github.mertguner.sound_generator.handlers.isPlayingStreamHandler;
import io.github.mertguner.sound_generator.models.WaveTypes;

/** SoundGeneratorPlugin */
public class SoundGeneratorPlugin implements FlutterPlugin, MethodCallHandler {
  private SoundGenerator soundGenerator = new SoundGenerator();
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "sound_generator");
    channel.setMethodCallHandler(this);

    final EventChannel onChangeIsPlaying = new EventChannel(
            flutterPluginBinding.getBinaryMessenger(),
            isPlayingStreamHandler.NATIVE_CHANNEL_EVENT
    );
    onChangeIsPlaying.setStreamHandler(new isPlayingStreamHandler());

    final EventChannel onOneCycleDataHandler = new EventChannel(
            flutterPluginBinding.getBinaryMessenger(),
            getOneCycleDataHandler.NATIVE_CHANNEL_EVENT
    );
    onOneCycleDataHandler.setStreamHandler(new getOneCycleDataHandler());
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method) {
      case "getPlatformVersion":
        result.success("Android " + android.os.Build.VERSION.RELEASE);
        break;
      case "init":
        int sampleRate = call.argument("sampleRate");
        result.success(soundGenerator.init(sampleRate));
        break;
      case "release":
        soundGenerator.release();
        break;
      case "play":
        soundGenerator.startPlayback();
        break;
      case "stop":
        soundGenerator.stopPlayback();
        break;
      case "isPlaying":
        result.success(soundGenerator.isPlaying());
        break;
      case "dB":
        result.success(soundGenerator.getDecibel());
        break;
      case "volume":
        result.success(soundGenerator.getVolume());
        break;
      case "setAutoUpdateOneCycleSample":
        boolean autoUpdateOneCycleSample = call.argument("autoUpdateOneCycleSample");
        soundGenerator.setAutoUpdateOneCycleSample(autoUpdateOneCycleSample);
        break;
      case "setFrequency":
        double frequency = call.argument("frequency");
        soundGenerator.setFrequency((float) frequency);
        break;
      case "setWaveform":
        String waveType = call.argument("waveType");
        soundGenerator.setWaveform(WaveTypes.valueOf(waveType));
        break;
      case "setBalance":
        double balance = call.argument("balance");
        soundGenerator.setBalance((float) balance);
        break;
      case "setVolume":
        double volume = call.argument("volume");
        soundGenerator.setVolume((float) volume, true);
        break;
      case "setDecibel":
        double dB = call.argument("dB");
        soundGenerator.setDecibel((float) dB);
        break;
      case "getSampleRate":
        result.success(soundGenerator.getSampleRate());
        break;
      case "refreshOneCycleData":
        soundGenerator.refreshOneCycleData();
        break;
      case "setCleanStart":
        boolean cleanStart = call.argument("cleanStart");
        soundGenerator.setCleanStart(cleanStart);
        break;
      default:
        result.notImplemented();
        break;
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
