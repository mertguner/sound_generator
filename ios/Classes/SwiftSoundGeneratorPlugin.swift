import Flutter
import UIKit
import AudioKit

public class SwiftSoundGeneratorPlugin: NSObject, FlutterPlugin {
  var onChangeIsPlaying: BetterEventChannel?;
  var onOneCycleDataHandler: BetterEventChannel?;
  // This is not used yet.
  var sampleRate: Int = 48000;
  var isPlaying: Bool = false;
  var oscillator: AKOscillator = AKOscillator();
  var mixer: AKMixer?;

  public static func register(with registrar: FlutterPluginRegistrar) {
    /*let instance =*/ _ = SwiftSoundGeneratorPlugin(registrar: registrar)
  }

  public init(registrar: FlutterPluginRegistrar) {
    super.init()
    self.mixer = AKMixer(self.oscillator)
    self.mixer!.volume = 1.0
    AKSettings.disableAVAudioSessionCategoryManagement = true
    AKSettings.disableAudioSessionDeactivationOnStop = true
    AKManager.output = self.mixer!
    let methodChannel = FlutterMethodChannel(name: "sound_generator", binaryMessenger: registrar.messenger())
    self.onChangeIsPlaying = BetterEventChannel(name: "io.github.mertguner.sound_generator/onChangeIsPlaying", messenger: registrar.messenger())
    self.onOneCycleDataHandler = BetterEventChannel(name: "io.github.mertguner.sound_generator/onOneCycleDataHandler", messenger: registrar.messenger())
    registrar.addMethodCallDelegate(self, channel: methodChannel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
      case "init":
        //let args = call.arguments as! [String: Any]
        //let sampleRate = args["sampleRate"] as Int
        self.oscillator.frequency = 400
        do {
            try AKManager.start()
            result(true);
        } catch {
            result(FlutterError(
                code: "init_error",
                message: "Unable to start AKManager",
                details: ""))
        }
        break
      case "release":
        result(nil);
        break;
      case "play":
        self.oscillator.start()
        onChangeIsPlaying!.sendEvent(event: true)
        result(nil);
        break;
      case "stop":
        self.oscillator.stop();
        onChangeIsPlaying!.sendEvent(event: false)
        result(nil);
        break;
      case "isPlaying":
        result(self.isPlaying);
        break;
      case "setAutoUpdateOneCycleSample":
        result(nil);
        break;
      case "setFrequency":
        let args = call.arguments as! [String: Any]
        self.oscillator.frequency = args["frequency"] as! Double
        result(nil);
        break;
      case "setWaveform":
        result(nil);
        break;
      case "setBalance":
        result(nil);
        break;
      case "setVolume":
        let args = call.arguments as! [String: Any]
        self.mixer!.volume = args["volume"] as! Double
        result(nil);
        break;
      case "getSampleRate":
        result(self.sampleRate);
        break;
      case "refreshOneCycleData":
        result(nil);
        break;
      default:
        result(FlutterMethodNotImplemented);
    }
  }
}
