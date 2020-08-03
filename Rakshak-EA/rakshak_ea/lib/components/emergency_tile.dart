import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:url_launcher/url_launcher.dart';
import '../constants.dart';

class EmergencyTile extends StatelessWidget {
  final String emergencyType;
  final String reportedBy;
  final String reportedAt;
  final String phoneNo;
  final String location;

  EmergencyTile({this.phoneNo,this.emergencyType,this.location,this.reportedAt,this.reportedBy});

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 360,
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Card(
          elevation: 2,
          child: Column(
            children: <Widget>[
              Container(
                padding: EdgeInsets.all(16),
                color: Color(0XFFFEF6E6),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: <Widget>[
                    Icon(
                      Icons.assignment,
                      color: kThemeDark,
                      size: 35,),
                    GestureDetector(
                        onTap: (){
                          Fluttertoast.showToast(msg: 'Done');
                        },
                        child: Text('MARK AS COMPLETE',
                          style: TextStyle(
                              fontSize: 16,
                              color: kThemeDark,fontWeight: FontWeight.bold
                          ),)
                    ),
                  ],
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: <Widget>[
                    Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Text(emergencyType,style: TextStyle(fontSize: 24),),
                    ),
                    Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Row(
                        children: <Widget>[
                          Text('Reported by:'),
                          SizedBox(width: 10,),
                          Text(reportedBy,style: TextStyle(fontWeight: FontWeight.bold,color: Colors.grey[700]),),
                        ],
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Row(
                        children: <Widget>[
                          Text('Reported at:'),
                          SizedBox(width: 10,),
                          Text(reportedAt,style: TextStyle(fontWeight: FontWeight.bold,color: Colors.grey[700]),),
                        ],
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Row(
                        children: <Widget>[
                          Text('Contact Number:'),
                          SizedBox(width: 10,),
                          Text('+91 9598227422',style: TextStyle(fontWeight: FontWeight.bold,color: Colors.grey[700]),),
                        ],
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Row(
                        children: <Widget>[
                          Text('Location:'),
                          SizedBox(width: 10,),
                          Text(location,style: TextStyle(fontWeight: FontWeight.bold,color: Colors.grey[700]),),
                        ],
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Row(
                        children: <Widget>[
                          Expanded(
                            child: Container(
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(10),
                              ),
                              height: 60,
                              child: RaisedButton(
                                shape: RoundedRectangleBorder(
                                  borderRadius: BorderRadius.circular(5.0),
                                ),
                                onPressed: (){
                                  _launchMaps('25.4358', '81.8463');
                                },
                                color: Color(0xFFD93C33),
                                child: Row(
                                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                  children: <Widget>[
                                    Text('LOCATE',style: TextStyle(color: Colors.white,fontSize: 16),),
                                    Icon(Icons.place,color: Colors.white,size: 30,),
                                  ],
                                ),
                              ),
                            ),
                          ),
                          SizedBox(width: 10,),
                          Expanded(
                            child: Container(
                              height: 60,
                              child: RaisedButton(
                                shape: RoundedRectangleBorder(
                                  borderRadius: BorderRadius.circular(5.0),
                                ),
                                onPressed: (){
                                  launch("tel: $phoneNo");
                                },
                                color: Color(0xFF5FD855),
                                child: Row(
                                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                  children: <Widget>[
                                    Text('CALL',style: TextStyle(color: Colors.white,fontSize: 16 ),),
                                    Icon(Icons.call,color: Colors.white,size: 30,),
                                  ],
                                ),
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
  _launchMaps(String lat,String lon) async {
    String url = 'https://www.google.com/maps/search/?api=1&query=$lat,$lon';
    if (await canLaunch(url)) {
      await launch(url);
    } else {
      throw 'Could not launch $url';
    }
  }
}
