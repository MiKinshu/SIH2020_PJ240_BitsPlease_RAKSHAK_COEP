//Getting the packages
const express = require("express");
const bodyParser = require("body-parser");
const admin = require("./firebase");
const mongoose = require("mongoose");
const Report = require("./Report");
const Network = require("./network/network")

//Initializing the app
const app = express();

//Adding middlewares
app.use(bodyParser.urlencoded({extended:false}));
app.use(bodyParser.json());

//Adding router for network management
const NetworkController = require("./network/NetworkController");
app.use("/networks", NetworkController);

//Initializing mongoDB
mongoose.connect("mongodb+srv://test:test@cluster0-vi5m5.mongodb.net/test?retryWrites=true&w=majority", {
  useUnifiedTopology: true,
  useNewUrlParser: true
});

//Getting the Users db
const Users = admin.database().ref("users");

//Function for sending notifications
const message = (registrationToken, reportID, location, type, msg) => {
    var message = {
      data: {
        reportID,
        loc : location,
        type: type,
        msg: msg
      },
      token: registrationToken
    };
    admin.messaging().send(message)
      .then((response) => {
        console.log('Successfully sent message:' + registrationToken, response);
      })
      .catch((error) => {
        console.log('Error sending message:' + registrationToken, error);
      });
  };

//--------------------Routes--------------------------//

// Trivial Route
app.get("/", (req, res)=>{
    res.send("Hi!");
});

// route to view all reports
app.get("/reports", (req, res)=>{
    Report.find({}, function(err, reports){
        console.log(req.params.networkId);
        res.send(reports);
    });
});

//route to register on a network
app.post("/usenetwork", (req, res)=>{
  console.log(req.body);
  Network.findOne({networkID: req.body.networkID}, (err, network)=>{
    network.users.push(req.body.uid);
    res.send("Updated!");
  })
});

// Handling requests from Users
app.post("/requests", (req, res)=>{
    console.log(req.body);
    const uid = req.body.uid;
    admin.auth().getUser(uid).then((record)=>{
        console.log(record);
        Report.create({
            uid: req.body.uid,
            msg: req.body.msg,
            loc: req.body.loc,
            networkID: record.networkID,
            name: record.displayName,
            phone: record.phoneNumber
        },
        (err, report)=>{
            if(err){
                console.log(err);
                res.status(500).send("Server Problem in Creating Report");
            }
            else{
                console.log(report._id);
                // //getting all the registrationTokens and sending the notification
                // Users.on("value", function(snapshot) {
                //     let tokens = snapshot.val();
                //     Object.keys(tokens).forEach(function(key) {
                //     //Decide whether the current key is viable for sending the message
                //     if (key!==uid && isViable(key)){
                //         //console.log(tokens[key]);
                //         message(tokens[key], req.body.loc, req.body.type, req.body.msg);
                //     }
                //     });
                // }, function (errorObject) {
                //     console.log("The read failed: " + errorObject.code);
                // });
                res.send("Yes!");
            }
          });
    });
})



module.exports = app;