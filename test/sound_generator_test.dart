import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:sound_generator/sound_generator.dart';

void main() {
  const MethodChannel channel = MethodChannel('sound_generator');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      if (methodCall.method == 'isPlaying') {
        return false;
      }

      throw ArgumentError.value(methodCall.method);
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('isPlaying should return the value from the channel', () async {
    expect(await SoundGenerator.isPlaying, false);
  });
}
