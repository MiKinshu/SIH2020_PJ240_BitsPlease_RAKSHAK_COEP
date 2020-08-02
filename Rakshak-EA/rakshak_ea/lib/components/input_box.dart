import 'package:flutter/material.dart';

class InputBox extends StatelessWidget {
  final String descriptionText;
  final String hintText;
  final TextEditingController textEditingController;
  final FocusNode focusNode;

  InputBox({this.textEditingController,this.descriptionText,this.hintText,this.focusNode});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: <Widget>[
        TextField(
          style: TextStyle(fontSize: 18),
          cursorColor: Colors.black,
          focusNode: focusNode,
          controller: textEditingController,
          decoration: InputDecoration(
              focusedBorder: OutlineInputBorder(
                borderSide: BorderSide(width: 2.0),
              ),
              enabledBorder: OutlineInputBorder(
                borderSide: BorderSide(width: 2.0),
              ),
              labelText: hintText,
              labelStyle: TextStyle(color: Colors.black)),
        ),
        SizedBox(height: 10,),
        Text(descriptionText,style: TextStyle(fontSize: 12),),
      ],
    );
  }
}
