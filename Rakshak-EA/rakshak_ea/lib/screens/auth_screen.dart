import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:rakshak_ea/components/input_box.dart';
import 'package:rakshak_ea/screens/profile_setup.dart';
import 'package:rakshak_ea/components/template_column.dart';

class AuthScreen extends StatefulWidget {
  @override
  _AuthScreenState createState() => _AuthScreenState();
}

class _AuthScreenState extends State<AuthScreen> {
  String phoneNo;
  String smsCode;
  String verificationId;
  String initialText;
  TextEditingController phoneText = TextEditingController(text: '+91 ');


  @override
  void initState() {
    super.initState();
  }

  Future<void> verifyNumber() async {
    final PhoneCodeAutoRetrievalTimeout autoRetrieve = (String verID) {
      this.verificationId = verID;
      smsCodeDialog(context);
    };

    final PhoneVerificationCompleted verificationSuccess =
        (AuthCredential credential) {
      print("Verified");
      Navigator.push(context,
          MaterialPageRoute(builder: (context) => ProfileSetup(phoneNo)));
    };

    final PhoneCodeSent smsCodeSent = (String verID, [int forceCodeResend]) {
      this.verificationId = verID;
      Navigator.pop(context);
      Navigator.push(context,
          MaterialPageRoute(builder: (context) => ProfileSetup(phoneNo)));
    };

    final PhoneVerificationFailed verificationFailed =
        (AuthException exception) {
      print('$exception.message');
    };

    await FirebaseAuth.instance.verifyPhoneNumber(
        phoneNumber: this.phoneNo,
        codeAutoRetrievalTimeout: autoRetrieve,
        codeSent: smsCodeSent,
        timeout: const Duration(seconds: 5),
        verificationCompleted: verificationSuccess,
        verificationFailed: verificationFailed);
  }

  Future<bool> smsCodeDialog(BuildContext context) {
    return showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) => AlertDialog(
        title: Text("Enter SMS code"),
        content: TextField(onChanged: (value) {
          this.smsCode = value;
        }),
        actions: <Widget>[
          RaisedButton(
            color: Colors.teal,
            child: Text(
              "Done",
              style: TextStyle(color: Colors.white),
            ),
            onPressed: () {
              FirebaseAuth.instance.currentUser().then((user) {
                if (user != null) {
                  Navigator.pop(context);
                  Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (context) => ProfileSetup(phoneNo)));
                } else {
                  Navigator.pop(context);
                  signIn();
                }
              });
            },
          )
        ],
      ),
    );
  }

  signIn() async {
    final AuthCredential credential = PhoneAuthProvider.getCredential(
      verificationId: verificationId,
      smsCode: smsCode,
    );
    await FirebaseAuth.instance.signInWithCredential(credential).then((user) {
      Navigator.push(context,
          MaterialPageRoute(builder: (context) => ProfileSetup(phoneNo)));
    }).catchError((e) => print(e));
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        body: TemplateColumn(
          titleText: 'SIGNUP',
          inputBox: InputBox(
            textEditingController: phoneText,
            hintText: 'Enter Phone Number',
            descriptionText: 'Centre will reach out to you on this active number',
          ),
          bottomButtonText: 'NEXT',
          onBottomButtonPressed: (){
            if(phoneText.text.isNotEmpty) {
              this.phoneNo = phoneText.text;
              verifyNumber();
            }
            else{
              Fluttertoast.showToast(msg: 'Please Enter your phone number');
            }
          },
        ),
      ),
    );
  }
}


