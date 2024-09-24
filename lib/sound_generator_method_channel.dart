import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'sound_generator_platform_interface.dart';

/// An implementation of [SoundGeneratorPlatform] that uses method channels.
class MethodChannelSoundGenerator extends SoundGeneratorPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('sound_generator');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
