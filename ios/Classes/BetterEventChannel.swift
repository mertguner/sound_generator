import Flutter

public class BetterEventChannel: NSObject, FlutterStreamHandler {
  var eventChannel: FlutterEventChannel?;
  var eventSink: FlutterEventSink?;

  public init(name: String, messenger: FlutterBinaryMessenger) {
    super.init()
    eventChannel = FlutterEventChannel(name: name, binaryMessenger: messenger)
    eventChannel!.setStreamHandler(self)
  }

  public func onListen(
    withArguments _: Any?,
    eventSink events: @escaping FlutterEventSink
  ) -> FlutterError? {
    self.eventSink = events
    return nil
  }

  public func onCancel(
    withArguments arguments: Any?
  ) -> FlutterError? {                                                           
    self.eventSink = nil
    return nil                                                                                       
  }                                                                                            

  public func sendEvent(event: Any?) {
    self.eventSink?(event)
  }

  public func dispose() {
    eventChannel?.setStreamHandler(nil)
  }
}
