import 'package:flutter_test/flutter_test.dart';
import 'package:sound_generator/sound_generator_platform_interface.dart';
import 'package:sound_generator/sound_generator_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockSoundGeneratorPlatform
    with MockPlatformInterfaceMixin
    implements SoundGeneratorPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final SoundGeneratorPlatform initialPlatform = SoundGeneratorPlatform.instance;

  test('$MethodChannelSoundGenerator is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelSoundGenerator>());
  });

  test('getPlatformVersion', () async {
    MockSoundGeneratorPlatform fakePlatform = MockSoundGeneratorPlatform();
    SoundGeneratorPlatform.instance = fakePlatform;

    expect(await initialPlatform.getPlatformVersion(), '42');
  });
}
