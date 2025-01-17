package io.github.mertguner.sound_generator;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.github.mertguner.sound_generator.handlers.getOneCycleDataHandler;
import io.github.mertguner.sound_generator.handlers.isPlayingStreamHandler;
import io.github.mertguner.sound_generator.models.WaveTypes;
/** SoundGeneratorPlugin */
public class SoundGeneratorPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private SoundGenerator soundGenerator = new SoundGenerator();
  private MethodChannel channel;
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {

    channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "sound_generator");
    channel.setMethodCallHandler(this);
    final EventChannel onChangeIsPlaying = new EventChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), isPlayingStreamHandler.NATIVE_CHANNEL_EVENT);
    onChangeIsPlaying.setStreamHandler(new isPlayingStreamHandler());
    final EventChannel onOneCycleDataHandler = new EventChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), getOneCycleDataHandler.NATIVE_CHANNEL_EVENT);
    onOneCycleDataHandler.setStreamHandler(new getOneCycleDataHandler());
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "sound_generator");
    channel.setMethodCallHandler(new SoundGeneratorPlugin());
    final EventChannel onChangeIsPlaying = new EventChannel(registrar.messenger(), isPlayingStreamHandler.NATIVE_CHANNEL_EVENT);
    onChangeIsPlaying.setStreamHandler(new isPlayingStreamHandler());
    final EventChannel onOneCycleDataHandler = new EventChannel(registrar.messenger(), getOneCycleDataHandler.NATIVE_CHANNEL_EVENT);
    onOneCycleDataHandler.setStreamHandler(new getOneCycleDataHandler());
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("init")) {
      int sampleRate = call.argument("sampleRate");
      result.success(soundGenerator.init(sampleRate));
    }else if (call.method.equals("release")) {
      soundGenerator.release();
    }else if (call.method.equals("play")) {
      soundGenerator.startPlayback();
    }else if (call.method.equals("stop")) {
      soundGenerator.stopPlayback();
    }else if (call.method.equals("isPlaying")) {
      result.success(soundGenerator.isPlaying());
    }else if (call.method.equals("dB")) {
      result.success(soundGenerator.getDecibel());
    }else if (call.method.equals("volume")) {
      result.success(soundGenerator.getVolume());
    }else if (call.method.equals("setAutoUpdateOneCycleSample")) {
      boolean autoUpdateOneCycleSample = call.argument("autoUpdateOneCycleSample");
      soundGenerator.setAutoUpdateOneCycleSample(autoUpdateOneCycleSample);
    }else if (call.method.equals("setFrequency")) {
      double frequency = call.argument("frequency");
      soundGenerator.setFrequency((float)frequency);
    }else if (call.method.equals("setWaveform")) {
      String waveType = call.argument("waveType");
      soundGenerator.setWaveform(WaveTypes.valueOf(waveType));
    }else if (call.method.equals("setBalance")) {
      double balance = call.argument("balance");
      soundGenerator.setBalance((float)balance);
    }else if (call.method.equals("setVolume")) {
      double volume = call.argument("volume");
      soundGenerator.setVolume((float)volume, true);
    }else if (call.method.equals("setDecibel")) {
      double dB = call.argument("dB");
      soundGenerator.setDecibel((float)dB);
    }else if (call.method.equals("getSampleRate")) {
      result.success(soundGenerator.getSampleRate());
    }else if (call.method.equals("refreshOneCycleData")) {
      soundGenerator.refreshOneCycleData();
    }else if (call.method.equals("setCleanStart")) {
      boolean cleanStart = call.argument("cleanStart");
      soundGenerator.setCleanStart(cleanStart);
    }else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}