import 'package:firebase_database/firebase_database.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:modal_progress_hud/modal_progress_hud.dart';
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
  bool isSaving = false;
  int _currentIndex = 0;
  PageController _pageController = PageController();
  String fullName;

  saveDataInFirebase(){
    //Save data to firebase
    final dbRef = FirebaseDatabase.instance.reference().child("Officers");
    dbRef.push().set({
      "name": nameText.text,
      "officeId": idText.text ,
      "phoneNo": widget.phoneNumber
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
  }

  saveProfile() {
    if(nameText.text.isEmpty || idText.text.isEmpty){
      Fluttertoast.showToast(msg: 'Please enter details');
      return;
    }

    setState(() {
      isSaving=true;
    });

    saveDataInFirebase();

    //TODO: Send a post request to server

    saveDataLocally();

    setState(() {
      isSaving=false;
    });

    Navigator.pushReplacement(context, MaterialPageRoute(
      builder: (context)=>HomeScreen(
        name: nameText.text,
        officerId: idText.text,
        phoneNo: widget.phoneNumber,)
    ));
  }

  @override
  Widget build(BuildContext context) {
    return ModalProgressHUD(
      inAsyncCall: isSaving,
      child: Scaffold(
          body: PageView(
            controller: _pageController,
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
                  fullName = nameText.text;
                  _pageController.animateToPage(
                      1, duration: Duration(milliseconds: 500),
                      curve: Curves.linear);
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
          )
      ),
    );
  }
}
