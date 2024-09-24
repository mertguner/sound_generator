import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'sound_generator_method_channel.dart';

abstract class SoundGeneratorPlatform extends PlatformInterface {
  /// Constructs a SoundGeneratorPlatform.
  SoundGeneratorPlatform() : super(token: _token);

  static final Object _token = Object();

  static SoundGeneratorPlatform _instance = MethodChannelSoundGenerator();

  /// The default instance of [SoundGeneratorPlatform] to use.
  ///
  /// Defaults to [MethodChannelSoundGenerator].
  static SoundGeneratorPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [SoundGeneratorPlatform] when
  /// they register themselves.
  static set instance(SoundGeneratorPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
