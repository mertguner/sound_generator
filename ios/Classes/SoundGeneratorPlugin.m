#import "SoundGeneratorPlugin.h"
#if __has_include(<sound_generator/sound_generator-Swift.h>)
#import <sound_generator/sound_generator-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "sound_generator-Swift.h"
#endif

@implementation SoundGeneratorPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftSoundGeneratorPlugin registerWithRegistrar:registrar];
}
@end
