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
  TextEditingController phoneText = TextEditingController();
  TextEditingController otpText = TextEditingController();
  PageController _pageController = PageController();

  @override
  void initState() {
    super.initState();
  }

  Future<void> verifyNumber() async {
    final PhoneCodeAutoRetrievalTimeout autoRetrieve = (String verID) {
      verificationId = verID;
    };

    final PhoneVerificationCompleted verificationSuccess =
        (AuthCredential credential) {
      print('Auto-Verification OFF');
//      Fluttertoast.showToast(msg: 'Verification Successful');
//      Navigator.push(context,
//          MaterialPageRoute(builder: (context) => ProfileSetup(phoneNo)));
    };

    final PhoneCodeSent smsCodeSent = (String verID, [int forceCodeResend]) {
      this.verificationId = verID;
      Fluttertoast.showToast(msg: 'SMS Code Sent');
    };

    final PhoneVerificationFailed verificationFailed =
        (AuthException exception) {
      print('$exception.message');
      Fluttertoast.showToast(msg: 'Phone Number Error');
      _pageController.animateToPage(0, duration: Duration(milliseconds: 500), curve: Curves.linear);
    };

    await FirebaseAuth.instance.verifyPhoneNumber(
        phoneNumber: this.phoneNo,
        codeAutoRetrievalTimeout: autoRetrieve,
        codeSent: smsCodeSent,
        timeout: const Duration(seconds: 5),
        verificationCompleted: verificationSuccess,
        verificationFailed: verificationFailed);
  }

  signIn() async {
    final AuthCredential credential = PhoneAuthProvider.getCredential(
      verificationId: verificationId,
      smsCode: smsCode,
    );
    await FirebaseAuth.instance.signInWithCredential(credential).then((user) {
      print(user.additionalUserInfo);
      Navigator.push(context,
          MaterialPageRoute(builder: (context) => ProfileSetup(phoneNo)));
      Fluttertoast.showToast(msg: 'Verification Successful');
    }).catchError((e) {
      print(e);
      Fluttertoast.showToast(msg: 'Code Invalid');
    });
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        body: PageView(
          controller: _pageController,
          children: <Widget>[
            TemplateColumn(
              titleText: 'SIGNUP',
              inputBox: InputBox(
                textEditingController: phoneText,
                hintText: 'Enter Phone Number',
                descriptionText: 'Centre will reach out to you on this active number',
              ),
              bottomButtonText: 'NEXT',
              onBottomButtonPressed: (){
                if(phoneText.text.isNotEmpty) {
                  phoneNo = '+91' + phoneText.text;
                  _pageController.animateToPage(1, duration: Duration(milliseconds: 500), curve: Curves.linear);
                  verifyNumber();
                }
                else{
                  Fluttertoast.showToast(msg: 'Please Enter your phone number');
                }
              },
            ),
            TemplateColumn(
              titleText: 'SIGNUP',
              inputBox: InputBox(
                textEditingController: otpText,
                hintText: 'Enter OTP',
                descriptionText: 'Please enter 6-digit otp sent to your phone number',
              ),
              requiredBackButton: true,
              onBackPressed: (){
                _pageController.animateToPage(0, duration: Duration(milliseconds: 500), curve: Curves.linear);
              },
              bottomButtonText: 'VERIFY',
              onBottomButtonPressed: (){
                smsCode = otpText.text;
                FirebaseAuth.instance.currentUser().then((user) {
                  if (user != null) {
                    Navigator.pop(context);
                    print('User already exits: Redirecting to setup profile');
                    print(user.uid);
                    Navigator.push(
                        context,
                        MaterialPageRoute(
                            builder: (context) => ProfileSetup(phoneNo)));
                  } else {
                    signIn();
                  }
                });
              },
            ),
          ],
        ),
      ),
    );
  }
}


