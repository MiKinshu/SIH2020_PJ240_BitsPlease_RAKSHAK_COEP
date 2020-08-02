import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:rakshak_ea/constants.dart';
import 'package:rakshak_ea/screens/auth_screen.dart';
import 'package:shared_preferences/shared_preferences.dart';

class HomeScreen extends StatefulWidget {
  final String name;
  final String officerId;
  final String phoneNo;

  HomeScreen({this.name,this.officerId,this.phoneNo});

  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        floatingActionButton: FloatingActionButton(
          onPressed: () async{
            //TODO: Sign Out
            SharedPreferences prefs = await SharedPreferences.getInstance();
            prefs.remove(kPrefLoggedIn);
            prefs.remove(kPrefName);
            prefs.remove(kPrefOfficeId);
            prefs.remove(kPrefPhoneNo);
            Navigator.pushReplacement(context, MaterialPageRoute(
              builder: (context)=>AuthScreen(),
            ));
            FirebaseAuth.instance.signOut();
          },
        ),
        body: Center(child: Text('Welcome to Home Page')),
    );
  }
}
