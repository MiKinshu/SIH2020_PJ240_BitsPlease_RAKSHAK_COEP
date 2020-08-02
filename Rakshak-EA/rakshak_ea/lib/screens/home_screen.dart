import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:modal_progress_hud/modal_progress_hud.dart';
import 'package:rakshak_ea/components/emergency_tile.dart';
import 'package:rakshak_ea/constants.dart';
import 'package:rakshak_ea/emergency.dart';
import 'package:rakshak_ea/notifications.dart';
import 'package:rakshak_ea/screens/auth_screen.dart';
import 'package:shared_preferences/shared_preferences.dart';

class HomeScreen extends StatefulWidget {
  final String name;
  final String officerId;
  final String phoneNo;

  HomeScreen({this.name, this.officerId, this.phoneNo});

  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {

  List<Emergency> emergencies = [
    Emergency(
        emergencyType: 'Emergency headline',
        reportedBy: 'Manthan Surkar',
        reportedAt: '11:05 am, Aug-03-2020',
        location: 'Area Details',
        phoneNo: '+916584962136'),
    Emergency(
        emergencyType: 'Emergency headline',
        reportedBy: 'Roshni Prajapati',
        reportedAt: '12:34 am, Aug-02-2020',
        location: 'Area Details',
        phoneNo: '+919598227422'),
  ];

  listenForNotifications(){
    final FirebaseMessaging _firebaseMessaging = FirebaseMessaging();
    _firebaseMessaging.configure(
      onMessage: (Map<String, dynamic> message) async {
        print('on message $message');
        Notifications().generate('Emergency', 'You have been assigned an emergency');
      },
      onResume: (Map<String, dynamic> message) async {
        print('on resume $message');
      },
      onLaunch: (Map<String, dynamic> message) async {
        print('on launch $message');
      },
    );
  }

  @override
  void initState() {
    super.initState();
    //TODO: Fetch Data via Post Requests
    listenForNotifications();
  }

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 3,
      child: Scaffold(
        appBar: AppBar(
            actions: <Widget>[
              IconButton(
                color: Colors.black,
                icon: Icon(Icons.search),
                onPressed: () {},
              ),
              IconButton(
                color: Colors.black,
                icon: Icon(Icons.clear),
                onPressed: () async {
                  //Signing Out
                  showDialog(
                    context: context,
                    builder: (context) => showExitDialog(context),
                  );
                },
              )
            ],
            backgroundColor: Colors.white,
            bottom: TabBar(
              labelColor: kThemeDark,
              indicatorColor: kThemeDark,
              tabs: [
                Tab(
                    child: Text('ALL'),
                    icon: Icon(Icons.view_list)),
                Tab(
                    child: Text('ONGOING'),
                    icon: Icon(Icons.call_missed_outgoing)),
                Tab(
                  child: Text('COMPLETED'),
                  icon: Icon(Icons.done_outline),
                ),
              ],
            ),
            title: Image(
              height: 40,
              image: AssetImage('images/rakshak_hero_black.png'),
            )),
        body: TabBarView(
          children: [
            ListView.builder(
              itemCount: emergencies.length,
              itemBuilder: (context, index) {
                return EmergencyTile(
                  emergencyType: emergencies[index].emergencyType,
                  reportedAt: emergencies[index].reportedAt,
                  reportedBy: emergencies[index].reportedBy,
                  phoneNo: emergencies[index].phoneNo,
                  location: emergencies[index].location,
                );
              },
            ),
            ListView.builder(
              itemCount: emergencies.length,
              itemBuilder: (context, index) {
                return EmergencyTile(
                  emergencyType: emergencies[index].emergencyType,
                  reportedAt: emergencies[index].reportedAt,
                  reportedBy: emergencies[index].reportedBy,
                  phoneNo: emergencies[index].phoneNo,
                  location: emergencies[index].location,
                );
              },
            ),
            ListView.builder(
              itemCount: emergencies.length,
              itemBuilder: (context, index) {
                return EmergencyTile(
                  emergencyType: emergencies[index].emergencyType,
                  reportedAt: emergencies[index].reportedAt,
                  reportedBy: emergencies[index].reportedBy,
                  phoneNo: emergencies[index].phoneNo,
                  location: emergencies[index].location,
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  signOutUser(BuildContext context) async{
    SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.remove(kPrefLoggedIn);
    prefs.remove(kPrefName);
    prefs.remove(kPrefOfficeId);
    prefs.remove(kPrefPhoneNo);
    FirebaseAuth.instance.signOut();
    Navigator.pushReplacement(context,MaterialPageRoute(
      builder: (context) => AuthScreen(),
    ));
  }

  AlertDialog showExitDialog(BuildContext context) {
    return AlertDialog(
        title: Text(
          'Sign Out',
          style: TextStyle(fontSize: 30),
        ),
        content: Text('This will take you back to login screen',style: TextStyle(fontSize: 14),),
        actions: <Widget>[
          FlatButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: Text('No'),
          ),
          FlatButton(
            onPressed: () => signOutUser(context),
            child: Text('Yes'),
          )
        ]);
  }
}

