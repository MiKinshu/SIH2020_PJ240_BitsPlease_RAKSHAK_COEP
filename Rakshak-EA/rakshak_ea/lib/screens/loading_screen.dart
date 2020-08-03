import 'package:flutter/material.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';
import 'package:rakshak_ea/constants.dart';
import 'package:rakshak_ea/screens/auth_screen.dart';
import 'package:rakshak_ea/screens/home_screen.dart';
import 'package:shared_preferences/shared_preferences.dart';

class LoadingScreen extends StatefulWidget {
  @override
  _LoadingScreenState createState() => _LoadingScreenState();
}

class _LoadingScreenState extends State<LoadingScreen> {

  checkLogin() async{
    SharedPreferences prefs = await SharedPreferences.getInstance();
    bool loggedIn = prefs.getBool(kPrefLoggedIn)??false;

    if(loggedIn){
      String name = prefs.getString(kPrefName);
      String id = prefs.getString(kPrefOfficeId);
      String phoneNumber = prefs.getString(kPrefPhoneNo);
      String uid = prefs.getString(kPrefUID);
      String token = prefs.getString(kPrefToken);

      Navigator.pushReplacement(context,
          MaterialPageRoute(
              builder: (context)=> HomeScreen(
                name: name,
                officerId: id,
                phoneNo: phoneNumber,
                uid: uid,
                token: token,
              )
          ));
    }
    else{
      Navigator.pushReplacement(context,
          MaterialPageRoute(
              builder: (context)=>AuthScreen()
          ));
    }
  }
  @override
  void initState() {
    super.initState();
    checkLogin();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: SpinKitPulse(
          color: kThemeLight,
          size: 100,
        ),
      ),
    );
  }
}
