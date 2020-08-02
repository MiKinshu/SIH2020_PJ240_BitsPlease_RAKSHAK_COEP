import 'package:flutter/material.dart';
import 'package:rakshak_ea/components/input_box.dart';

import '../constants.dart';

class TemplateColumn extends StatelessWidget {
  final InputBox inputBox;
  final String titleText;
  final Function onBottomButtonPressed;
  final String bottomButtonText;

  TemplateColumn({this.inputBox,this.titleText,this.onBottomButtonPressed,this.bottomButtonText});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.symmetric(horizontal: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: <Widget>[
          Image(
            height: 54,
            image: AssetImage('images/rakshak_hero_black.png'),
          ),
          Text(
            titleText,
            style: TextStyle(
                fontSize: 25, letterSpacing: 8, color: kThemeDark),
          ),
          inputBox,
          Container(
            height: 60,
            width: double.infinity,
            child: RaisedButton(
              color: kBlack,
              textColor: Colors.white,
              onPressed: onBottomButtonPressed,
              child: Text(bottomButtonText),
            ),
          ),
        ],
      ),
    );
  }
}
