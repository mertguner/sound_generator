import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:sound_generator/sound_generator.dart';

void main() {
  const MethodChannel channel = MethodChannel('sound_generator');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await SoundGenerator.platformVersion, '42');
  });
}
