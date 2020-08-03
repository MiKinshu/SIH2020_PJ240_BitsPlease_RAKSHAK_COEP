import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:http/http.dart';
import 'package:rakshak_ea/components/input_box.dart';
import 'package:rakshak_ea/components/template_column.dart';
import 'package:rakshak_ea/constants.dart';
import 'package:rakshak_ea/screens/home_screen.dart';
import 'package:shared_preferences/shared_preferences.dart';

class ProfileSetup extends StatefulWidget {
  final String phoneNumber;
  ProfileSetup(this.phoneNumber);
  @override
  _ProfileSetupState createState() => _ProfileSetupState();
}

class _ProfileSetupState extends State<ProfileSetup> {

  TextEditingController nameText = TextEditingController();
  TextEditingController idText = TextEditingController();
  int _currentIndex = 0;
  PageController _pageController = PageController();
  String fullName;
  String token;
  String uid;

  saveDataInFirebase(){
    //Save data to firebase
    final dbRef = FirebaseDatabase.instance.reference().child("Officers");
    dbRef.child(uid).set({
      "name": nameText.text,
      "officeId": idText.text ,
      "phoneNo": widget.phoneNumber,
      "token": token,
    }).then((_) {
      Fluttertoast.showToast(msg: 'Data saved successfully');
    }).catchError((onError) {
      print(onError);
    });
  }

  saveDataLocally() async{
    //Cache officer locally
    SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.setBool(kPrefLoggedIn, true);
    prefs.setString(kPrefName,nameText.text);
    prefs.setString(kPrefOfficeId, idText.text);
    prefs.setString(kPrefPhoneNo, widget.phoneNumber);
    prefs.setString(kPrefUID, uid);
    prefs.setString(kPrefToken, token);
  }

  Future<String> registerUser() async{
    try {
      var response = await post('https://rakshak-zs.herokuapp.com/useoffice',
          body: {
            "officeId": idText.text,
            "uid": uid,
          });
      return response.body;
    }
    catch(e){
      print('Unable to register officer');
    }
  }

  saveProfile() async{

    final FirebaseMessaging _firebaseMessaging = FirebaseMessaging();
    await _firebaseMessaging.getToken().then((t){
      print(t);
      token = t;
    });

    await FirebaseAuth.instance.currentUser().then((value){
      uid = value.uid;
      print(uid);
    });


    if(nameText.text.isEmpty || idText.text.isEmpty){
      Fluttertoast.showToast(msg: 'Please enter details');
      return;
    }

    saveDataInFirebase();

    //Send a GET request to server to register officer
    String responseBody = await registerUser();
    if(responseBody!='Updated!'){
      Fluttertoast.showToast(msg: 'Office Id Invalid');
      return;
    }

    saveDataLocally();

    Navigator.pushReplacement(context, MaterialPageRoute(
      builder: (context)=>HomeScreen(
        name: nameText.text,
        officerId: idText.text,
        phoneNo: widget.phoneNumber,
        token: token,
        uid: uid,
      )
    ));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: Column(
          children: <Widget>[
            Expanded(
              child: PageView(
                physics: NeverScrollableScrollPhysics(),
                controller: _pageController,
                onPageChanged: (index) {
                  setState(() => _currentIndex = index);
                },
                children: <Widget>[
                  TemplateColumn(
                    titleText: 'About You',
                    inputBox: InputBox(
                      textEditingController: nameText,
                      descriptionText: 'Your name registered at office',
                      hintText: 'Your Full Name',
                    ),
                    bottomButtonText: 'NEXT',
                    onBottomButtonPressed: (){
                      if(nameText.text.isNotEmpty) {
                        fullName = nameText.text;
                        _pageController.animateToPage(
                            1, duration: Duration(milliseconds: 500),
                            curve: Curves.linear);
                      }
                      else{
                        Fluttertoast.showToast(msg: 'Name is a required Field');
                      }
                    },
                  ),
                  TemplateColumn(
                    titleText: 'About You',
                    inputBox: InputBox(
                      textEditingController: idText,
                      descriptionText: 'The Id registered for your department',
                      hintText: 'Office ID',
                    ),
                    requiredBackButton: true,
                    onBackPressed: (){
                      _pageController.animateToPage(0, duration: Duration(milliseconds: 500), curve: Curves.linear);
                    },
                    bottomButtonText: 'DONE',
                    onBottomButtonPressed: (){
                      saveProfile();
                    },
                  ),
                ],
              ),
            ),
            LinearProgressIndicator(
              value: _currentIndex==0?0.5:1.0,
              backgroundColor: Colors.grey,
              valueColor: AlwaysStoppedAnimation<Color>(kBlack),
            ),
          ],
        )
    );
  }
}
