import 'package:flutter/material.dart';
import 'package:rakshak_ea/constants.dart';
import 'package:rakshak_ea/screens/loading_screen.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Rakshak-Hero',
      theme: ThemeData(
//        accentColor: kBlack,
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: LoadingScreen(),
    );
  }
}

