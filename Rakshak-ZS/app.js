//Getting the packages
const express = require("express");
const bodyParser = require("body-parser");
const admin = require("./firebase");
const mongoose = require("mongoose");
const Report = require("./Report");

//Initializing the app
const app = express();

//Adding middlewares
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

//Initializing mongoDB
mongoose.connect("mongodb+srv://test:test@cluster0-vi5m5.mongodb.net/test?retryWrites=true&w=majority", {
    useUnifiedTopology: true,
    useNewUrlParser: true
});
//Adding router for network management
const NetworkController = require("./network/NetworkController");
app.use("/networks", NetworkController);

const OfficeController = require("./office/OfficeController");
app.use("/office", OfficeController);

const Network = mongoose.model("Network");
const Office = mongoose.model("Office");
//Getting the Users db
const Users = admin.database().ref("Users");

//Function for sending notifications
const message = (registrationToken, location, type, msg, reportID) => {
    var message = {
        data: {
            reportID: reportID,
            loc: location,
            type: type,
            msg: msg,
            status: "0"
        },
        token: registrationToken
    };
    console.log(message);
    admin.messaging().send(message)
        .then((response) => {
            console.log('Successfully sent message:' + registrationToken, response);
        })
        .catch((error) => {
            console.log('Error sending message:' + registrationToken, error);
        });
};

//Function for sending notifications
const messages = (registrationToken) => {
    var message = {
        data: {
            title: "Notification aaya",
            body: "Hello Ritik"
        },
        token: registrationToken
    };
    console.log(message);
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
app.get("/", (req, res) => {
    res.send("Hi!");
});

// route to view all reports
app.get("/reports", (req, res) => {
    Report.find({}, function(err, reports) {
        console.log(req.params.networkId);
        res.send(reports);
    });
});

app.get("/network", (req, res) => {
    Network.find({}, function(err, reports) {
        console.log(reports);
        res.send(reports);
    });
});

//Medical Fire Disaster General Emergency
//route to register on a network
app.post("/useoffice", (req, res) => {
    console.log(req.body);
    Office.findOne({ officeId: req.body.officeId }, (err, office) => {
        if(office==null){
            res.send("no network found");
        }
        else {
            office.officers.push(req.body.uid);
        
        office.save((err) => {
            if (err) res.send("Something went wrong");
            res.send("Updated!");
        })
        }
    })
});
app.post("/usenetwork", (req, res) => {
    console.log(req.body);
    var hopperRef = Users.child(req.body.uid);
        hopperRef.update({
            "NetworkID": req.body.networkId
        });
    Network.findOne({ networkId: req.body.networkId }, (err, network) => {
        if(network==null) res.send("no network found");
        else{
            network.users.push(req.body.uid);
        
        network.save((err) => {
            if (err) res.send("Something went wrong");
            res.send("Updated!");
        })
        }
    })
});

app.get("/getreports/:officerId", (req, res)=>{
    console.log(req.params.officerId);
    Report.find({officerID: req.params.officerId},(err, reports)=>{
        if(err){
            console.log(err);
            res.send("Id does not match");
        }
        res.send(reports);
    });
})

// Network Server Raisng an alert, sends everyone in the network a notification
app.post("/raiseAlert", function(req, res) {
    console.log(req.body);
    Network.findOne({ networkId: req.body.networkId }, (err, network) => {
        const userstoSend = network.users;
        Users.on("value", function(snapshot) {
            let tokens = snapshot.val();
            Object.keys(tokens).forEach(function(key) {
                //Decide whether the current key is viable for sending the message
                if (userstoSend.includes(key)) {
                    //console.log(tokens[key]);
                    message(tokens[key].Token, "18 71", "general", req.body.info, "");
                }
            });
        }, function(errorObject) {
            console.log("The read failed: " + errorObject.code);
        });
        res.redirect("https://rakshak-local.herokuapp.com/home?alert=true");
    });
});


// Handling requests from Users
app.post("/requests", (req, res) => {
    console.log(req.body);
    const uid = req.body.uid;
    admin.auth().getUser(uid).then((record) => {
        console.log(record);
        Users.on("value", (snapshot) => {
            console.log(snapshot.val()[uid]);
            Report.create({
                    uid: req.body.uid,
                    msg: req.body.msg,
                    type: req.body.type,
                    loc: req.body.loc,
                    networkId: snapshot.val()[uid].NetworkID,
                    name: snapshot.val()[uid].Name,
                    phone: record.phoneNumber
                },
                (err, report) => {
                    if (err) {
                        console.log(err);
                        res.status(500).send("Server Problem in Creating Report");
                    } else {
                        console.log(report);
                        //getting all the registrationTokens and sending the notification
                        Users.on("value", function(snapshot) {
                            let tokens = snapshot.val();
                            Object.keys(tokens).forEach(function(key) {
                                //Decide whether the current key is viable for sending the message
                                if (true) {
                                    console.log(tokens[key]);
                                    message(tokens[key].Token, req.body.loc, req.body.type, req.body.msg, JSON.stringify(report._id));
                                }
                            });
                        }, function(errorObject) {
                            console.log("The read failed: " + errorObject.code);
                        });
                        res.send("Yes!");
                    }
                });
        });
    });
})



module.exports = app;