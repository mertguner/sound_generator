import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:sound_generator/sound_generator.dart';
import 'package:sound_generator/waveTypes.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class MyPainter extends CustomPainter {
  //         <-- CustomPainter class
  final List<int> oneCycleData;

  MyPainter(this.oneCycleData);

  @override
  void paint(Canvas canvas, Size size) {
    var i = 0;
    List<Offset> maxPoints = [];

    final t = size.width / (oneCycleData.length - 1);
    for (var i0 = 0, len = oneCycleData.length; i0 < len; i0++) {
      maxPoints.add(Offset(
          t * i,
          size.height / 2 -
              oneCycleData[i0].toDouble() / 32767.0 * size.height / 2));
      i++;
    }

    final paint = Paint()
      ..color = Colors.black
      ..strokeWidth = 1
      ..strokeCap = StrokeCap.round;
    canvas.drawPoints(PointMode.polygon, maxPoints, paint);
  }

  @override
  bool shouldRepaint(MyPainter oldDelegate) {
    if (oneCycleData != oldDelegate.oneCycleData) {
      return true;
    }
    return false;
  }
}

class _MyAppState extends State<MyApp> {
  bool isPlaying = false;
  double frequency = 20;
  double balance = 0;
  double volume = 1;
  double dB = 0;
  waveTypes waveType = waveTypes.SINUSOIDAL;
  int sampleRate = 192000;
  List<int>? oneCycleData;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        home: Scaffold(
            appBar: AppBar(
              title: const Text('Sound Generator Example'),
            ),
            body: SingleChildScrollView(
                physics: const AlwaysScrollableScrollPhysics(),
                padding: const EdgeInsets.symmetric(
                  horizontal: 20.0,
                  vertical: 20,
                ),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      const Text("A Cycle's Snapshot With Real Data"),
                      const SizedBox(height: 2),
                      Container(
                          height: 100,
                          width: double.infinity,
                          color: Colors.white54,
                          padding: const EdgeInsets.symmetric(
                            horizontal: 5,
                            vertical: 0,
                          ),
                          child: oneCycleData != null
                              ? CustomPaint(
                                  painter: MyPainter(oneCycleData!),
                                )
                              : Container()),
                      const SizedBox(height: 2),
                      Text(
                          "A Cycle Data Length is ${(sampleRate / frequency).round()} on sample rate $sampleRate"),
                      const SizedBox(height: 5),
                      const Divider(
                        color: Colors.red,
                      ),
                      const SizedBox(height: 5),
                      CircleAvatar(
                          radius: 30,
                          backgroundColor: Colors.lightBlueAccent,
                          child: IconButton(
                              icon: Icon(
                                  isPlaying ? Icons.stop : Icons.play_arrow),
                              onPressed: () {
                                isPlaying
                                    ? SoundGenerator.stop()
                                    : SoundGenerator.play();
                              })),
                      const SizedBox(height: 5),
                      const Divider(
                        color: Colors.red,
                      ),
                      const SizedBox(height: 5),
                      const Text("Wave Form"),
                      Center(
                          child: DropdownButton<waveTypes>(
                              value: waveType,
                              onChanged: (waveTypes? newValue) {
                                setState(() {
                                  waveType = newValue!;
                                  SoundGenerator.setWaveType(waveType);
                                });
                              },
                              items:
                                  waveTypes.values.map((waveTypes classType) {
                                return DropdownMenuItem<waveTypes>(
                                    value: classType,
                                    child: Text(
                                        classType.toString().split('.').last));
                              }).toList())),
                      const SizedBox(height: 5),
                      const Divider(
                        color: Colors.red,
                      ),
                      const SizedBox(height: 5),
                      const Text("Frequency"),
                      SizedBox(
                          width: double.infinity,
                          height: 40,
                          child: Row(
                              mainAxisAlignment: MainAxisAlignment.center,
                              crossAxisAlignment: CrossAxisAlignment.stretch,
                              children: <Widget>[
                                Expanded(
                                  flex: 2,
                                  child: Center(
                                      child: Text(
                                          "${frequency.toStringAsFixed(2)} Hz")),
                                ),
                                Expanded(
                                  flex: 8, // 60%
                                  child: Slider(
                                      min: 20,
                                      max: 10000,
                                      value: frequency,
                                      onChanged: (value) {
                                        setState(() {
                                          frequency = value.toDouble();
                                          SoundGenerator.setFrequency(
                                              frequency);
                                        });
                                      }),
                                )
                              ])),
                      const SizedBox(height: 5),
                      const Text("Balance"),
                      SizedBox(
                          width: double.infinity,
                          height: 40,
                          child: Row(
                              mainAxisAlignment: MainAxisAlignment.center,
                              crossAxisAlignment: CrossAxisAlignment.stretch,
                              children: <Widget>[
                                Expanded(
                                  flex: 2,
                                  child: Center(
                                      child: Text(balance.toStringAsFixed(2))),
                                ),
                                Expanded(
                                  flex: 8, // 60%
                                  child: Slider(
                                      min: -1,
                                      max: 1,
                                      value: balance,
                                      onChanged: (value) {
                                        setState(() {
                                          balance = value.toDouble();
                                          SoundGenerator.setBalance(balance);
                                        });
                                      }),
                                )
                              ])),
                      const SizedBox(height: 5),
                      const Text("Volume"),
                      SizedBox(
                          width: double.infinity,
                          height: 40,
                          child: Row(
                              mainAxisAlignment: MainAxisAlignment.center,
                              crossAxisAlignment: CrossAxisAlignment.stretch,
                              children: <Widget>[
                                Expanded(
                                  flex: 2,
                                  child: Center(
                                      child: Text(volume.toStringAsFixed(6))),
                                ),
                                Expanded(
                                  flex: 8, // 60%
                                  child: Slider(
                                      min: 0,
                                      max: 1,
                                      value: volume,
                                      onChanged: (value) async {
                                        SoundGenerator.setVolume(volume);
                                        double newDB =
                                            await SoundGenerator.getDecibel;
                                        setState(() {
                                          volume = value.toDouble();
                                          dB = newDB;
                                        });
                                      }),
                                )
                              ])),
                      const SizedBox(height: 5),
                      const Text("Decibel"),
                      SizedBox(
                          width: double.infinity,
                          height: 40,
                          child: Row(
                              mainAxisAlignment: MainAxisAlignment.center,
                              crossAxisAlignment: CrossAxisAlignment.stretch,
                              children: <Widget>[
                                Expanded(
                                  flex: 2,
                                  child: Center(
                                      child: Text(dB.toStringAsFixed(2))),
                                ),
                                Expanded(
                                  flex: 8, // 60%
                                  child: Slider(
                                      min: -120,
                                      max: 0,
                                      value: dB,
                                      onChanged: (value) async {
                                        SoundGenerator.setDecibel(
                                            value.toDouble());
                                        double newVolume =
                                            await SoundGenerator.getVolume;
                                        setState(() {
                                          dB = value.toDouble();
                                          volume = newVolume;
                                        });
                                      }),
                                )
                              ]))
                    ]))));
  }

  @override
  void dispose() {
    super.dispose();
    SoundGenerator.release();
  }

  @override
  void initState() {
    super.initState();
    isPlaying = false;

    SoundGenerator.init(sampleRate);

    SoundGenerator.onIsPlayingChanged.listen((value) {
      setState(() {
        isPlaying = value;
      });
    });

    SoundGenerator.onOneCycleDataHandler.listen((value) {
      setState(() {
        oneCycleData = value;
      });
    });

    SoundGenerator.setAutoUpdateOneCycleSample(true);
    //Force update for one time
    SoundGenerator.refreshOneCycleData();
  }
}
