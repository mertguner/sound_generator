import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:sound_generator/sound_generator.dart';
import 'package:sound_generator/waveTypes.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
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
    for (var _i = 0, _len = oneCycleData.length; _i < _len; _i++) {
      maxPoints.add(Offset(
          t * i,
          size.height / 2 -
              oneCycleData[_i].toDouble() / 32767.0 * size.height / 2));
      i++;
    }

    final paint = Paint()
      ..color = Colors.black
      ..strokeWidth = 1
      ..strokeCap = StrokeCap.round;
    canvas.drawPoints(PointMode.polygon, maxPoints, paint);
  }

  @override
  bool shouldRepaint(MyPainter old) {
    if (oneCycleData != old.oneCycleData) {
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
  waveTypes waveType = waveTypes.SINUSOIDAL;
  int sampleRate = 96000;
  List<int>? oneCycleData;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        home: Scaffold(
            appBar: AppBar(
              title: const Text('Sound Generator Example'),
            ),
            body: SingleChildScrollView(
                physics: AlwaysScrollableScrollPhysics(),
                padding: EdgeInsets.symmetric(
                  horizontal: 20.0,
                  vertical: 20,
                ),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Text("A Cycle's Snapshot With Real Data"),
                      SizedBox(height: 2),
                      Container(
                          height: 100,
                          width: double.infinity,
                          color: Colors.white54,
                          padding: EdgeInsets.symmetric(
                            horizontal: 5,
                            vertical: 0,
                          ),
                          child: oneCycleData != null
                              ? CustomPaint(
                                  painter: MyPainter(oneCycleData!),
                                )
                              : Container()),
                      SizedBox(height: 2),
                      Text("A Cycle Data Length is " +
                          (sampleRate / this.frequency).round().toString() +
                          " on sample rate " +
                          sampleRate.toString()),
                      SizedBox(height: 5),
                      Divider(
                        color: Colors.red,
                      ),
                      SizedBox(height: 5),
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
                      SizedBox(height: 5),
                      Divider(
                        color: Colors.red,
                      ),
                      SizedBox(height: 5),
                      Text("Wave Form"),
                      Center(
                          child: DropdownButton<waveTypes>(
                              value: this.waveType,
                              onChanged: (waveTypes? newValue) {
                                setState(() {
                                  this.waveType = newValue!;
                                  SoundGenerator.setWaveType(this.waveType);
                                });
                              },
                              items:
                                  waveTypes.values.map((waveTypes classType) {
                                return DropdownMenuItem<waveTypes>(
                                    value: classType,
                                    child: Text(
                                        classType.toString().split('.').last));
                              }).toList())),
                      SizedBox(height: 5),
                      Divider(
                        color: Colors.red,
                      ),
                      SizedBox(height: 5),
                      Text("Frequency"),
                      Container(
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
                                          this.frequency.toStringAsFixed(2) +
                                              " Hz")),
                                ),
                                Expanded(
                                  flex: 8, // 60%
                                  child: Slider(
                                      min: 20,
                                      max: 10000,
                                      value: this.frequency,
                                      onChanged: (_value) {
                                        setState(() {
                                          this.frequency = _value.toDouble();
                                          SoundGenerator.setFrequency(
                                              this.frequency);
                                        });
                                      }),
                                )
                              ])),
                      SizedBox(height: 5),
                      Text("Balance"),
                      Container(
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
                                          this.balance.toStringAsFixed(2))),
                                ),
                                Expanded(
                                  flex: 8, // 60%
                                  child: Slider(
                                      min: -1,
                                      max: 1,
                                      value: this.balance,
                                      onChanged: (_value) {
                                        setState(() {
                                          this.balance = _value.toDouble();
                                          SoundGenerator.setBalance(
                                              this.balance);
                                        });
                                      }),
                                )
                              ])),
                      SizedBox(height: 5),
                      Text("Volume"),
                      Container(
                          width: double.infinity,
                          height: 40,
                          child: Row(
                              mainAxisAlignment: MainAxisAlignment.center,
                              crossAxisAlignment: CrossAxisAlignment.stretch,
                              children: <Widget>[
                                Expanded(
                                  flex: 2,
                                  child: Center(
                                      child:
                                          Text(this.volume.toStringAsFixed(2))),
                                ),
                                Expanded(
                                  flex: 8, // 60%
                                  child: Slider(
                                      min: 0,
                                      max: 1,
                                      value: this.volume,
                                      onChanged: (_value) {
                                        setState(() {
                                          this.volume = _value.toDouble();
                                          SoundGenerator.setVolume(this.volume);
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
