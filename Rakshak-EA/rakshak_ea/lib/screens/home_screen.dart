import 'dart:convert';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:http/http.dart';
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
  final String uid;
  final String token;

  HomeScreen({this.name, this.officerId, this.phoneNo,this.token,this.uid});

  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {

  List<Emergency> emergenciesAll = [];
  List<Emergency> emergenciesOngoing = [];
  List<Emergency> emergenciesCompleted = [];

  listenForNotifications(){
    final FirebaseMessaging _firebaseMessaging = FirebaseMessaging();
    _firebaseMessaging.configure(
      onMessage: (Map<String, dynamic> message) async {
        print('on message $message');
        Notifications().generate(message['data']['title'], '${message['data']['body']}');
      },
      onResume: (Map<String, dynamic> message) async {
        print('on resume $message');
      },
      onLaunch: (Map<String, dynamic> message) async {
        print('on launch $message');
      },
    );
  }

  fetchEmergencies() async{

    while(mounted){
      await Future.delayed(Duration(seconds: 2),(){
      });

      try {
        var response = await get(
            'https://rakshak-zs.herokuapp.com/getreports/${widget.uid}');
        var emergencies = jsonDecode(response.body);

        List<Emergency> emergencyList = [];
        for (var item in emergencies) {
          Emergency emergency = Emergency(
            emergencyType: item['type'],
            reportedBy: item['name'],
            reportedAt: item['date'],
            phoneNo: item['phone'],
            location: item['loc'],
          );
          emergencyList.add(emergency);
        }
        setState(() {
          emergenciesAll = emergencyList;
        });

      }
      catch(e){
        print(e);
      }
    }
  }

  @override
  void initState() {
    super.initState();
    fetchEmergencies();
    listenForNotifications();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: DefaultTabController(
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
              emergenciesAll.isEmpty?Center(child: Text('No Emergency Right Now :)'),):
              ListView.builder(
                itemCount: emergenciesAll.length,
                itemBuilder: (context, index) {
                  return EmergencyTile(
                    emergencyType: emergenciesAll[index].emergencyType,
                    reportedAt: emergenciesAll[index].reportedAt,
                    reportedBy: emergenciesAll[index].reportedBy,
                    phoneNo: emergenciesAll[index].phoneNo,
                    location: emergenciesAll[index].location,
                  );
                },
              ),
              ListView.builder(
                itemCount: emergenciesAll.length,
                itemBuilder: (context, index) {
                  return EmergencyTile(
                    emergencyType: emergenciesAll[index].emergencyType,
                    reportedAt: emergenciesAll[index].reportedAt,
                    reportedBy: emergenciesAll[index].reportedBy,
                    phoneNo: emergenciesAll[index].phoneNo,
                    location: emergenciesAll[index].location,
                  );
                },
              ),
              ListView.builder(
                itemCount: emergenciesCompleted.length,
                itemBuilder: (context, index) {
                  return EmergencyTile(
                    emergencyType: emergenciesCompleted[index].emergencyType,
                    reportedAt: emergenciesCompleted[index].reportedAt,
                    reportedBy: emergenciesCompleted[index].reportedBy,
                    phoneNo: emergenciesCompleted[index].phoneNo,
                    location: emergenciesCompleted[index].location,
                  );
                },
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<bool> _onWillPop() async => SystemChannels.platform.invokeMethod('SystemNavigator.pop');

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

