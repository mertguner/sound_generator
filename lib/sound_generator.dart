import 'dart:async';

import 'package:flutter/services.dart';
import 'package:sound_generator/waveTypes.dart';

class SoundGenerator {
  static const MethodChannel _channel = const MethodChannel('sound_generator');
  static const EventChannel _onChangeIsPlaying = const EventChannel('io.github.mertguner.sound_generator/onChangeIsPlaying');
  static const EventChannel _onOneCycleDataHandler = const EventChannel('io.github.mertguner.sound_generator/onOneCycleDataHandler');

  static Stream<bool> _onIsPlayingChanged;
  static Stream<bool> get onIsPlayingChanged {
    if(_onIsPlayingChanged == null)
      _onIsPlayingChanged = _onChangeIsPlaying.receiveBroadcastStream().map<bool>((value) => value);
    return _onIsPlayingChanged;
  }

  static Stream<List<int>> _onGetOneCycleDataHandler;
  static Stream<List<int>> get onOneCycleDataHandler {
    if(_onGetOneCycleDataHandler == null)
      _onGetOneCycleDataHandler = _onOneCycleDataHandler.receiveBroadcastStream().map<List<int>>((value) => new List<int>.from(value));
    return _onGetOneCycleDataHandler;
  }

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static void play() async {
    await _channel.invokeMethod('play');
  }

  static void stop() async {
    await _channel.invokeMethod('stop');
  }

  static void release() async {
    await _channel.invokeMethod('release');
  }

  static void refreshOneCycleData() async {
    await _channel.invokeMethod('refreshOneCycleData');
  }

  static Future<bool> get isPlaying async {
    final bool playing = await _channel.invokeMethod('isPlaying');
    return playing;
  }

  static Future<int> get getSampleRate async {
    final int sampleRate = await _channel.invokeMethod('getSampleRate');
    return sampleRate;
  }


  static void setFrequency(double frequency) async {
    await _channel.invokeMethod("setFrequency", <String, dynamic>{"frequency": frequency});
  }

  static void setCenter(double center) async {
    await _channel.invokeMethod("setCenter", <String, dynamic>{"center": center});
  }

  static void setBalance(double balance) async {
    await _channel.invokeMethod("setBalance", <String, dynamic>{"balance": balance});
  }

  static void setWaveType(waveTypes waveType) async {
    await _channel.invokeMethod("setWaveform", <String, dynamic>{"waveType": waveType.toString().replaceAll("waveTypes.", "")});
  }
}
