// Getting the packages
const express = require("express");
const router = express.Router();
const bodyParser = require("body-parser");
const admin = require("../firebase");
const Report = require("../Report");
const VerifyToken = require("./VerifyToken");
const cookieParser = require("cookie-parser"); 

router.use(bodyParser.urlencoded({ extended: false }));
router.use(bodyParser.json());
router.use(cookieParser());
const Office = require("./Office");
const Officers = admin.database().ref("Officers");
const Users = admin.database().ref("Users");
/**
 * Configure JWT
 */
var jwt = require("jsonwebtoken"); // used to create, sign, and verify tokens
var bcrypt = require("bcryptjs");
var config = require("./config"); // get config file
const verifyToken = require("../network/VerifyToken");

const messages = (registrationToken) => {
    var message = {
        data: {
            title: "Work time!",
            body: "You have been assigned a mission"
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

const messageus = (registrationToken, location, type, msg, reportID) => {
    var message = {
        data: {
            name: reportID,
            loc: location,
            type: type,
            msg: msg,
            status: "2"
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

router.post("/login", function(req, res) {
    console.log(req.body);
    Office.findOne({ officeId: req.body.officeId }, function(err, office) {
        if (err) return res.status(500).send("Error on the server.");
        if (!office) return res.status(404).send({ auth: false, token: null });

        // check if the password is valid
        var passwordIsValid = bcrypt.compareSync(req.body.password, office.password);

        if (!passwordIsValid)
            return res.status(401).send({ auth: false, token: null });

        // if Office is found and password is valid
        // create a token
        var token = jwt.sign({ id: office._id }, config.secret, {
            expiresIn: 86400 // expires in 24 hours
        });

        // return the information including token as JSON
        res.status(200).send({ auth: true, token: token });
    });
});

router.get("/logout", function(req, res) {
    res.status(200).send({ auth: false, token: null });
});

router.post("/register", function(req, res) {
    console.log("login:" + req.body.password);
    var hashedPassword = bcrypt.hashSync(req.body.password, 8);
    console.log(req.body);
    Office.create({
            officeName: req.body.name,
            password: hashedPassword,
            officeId: req.body.officeId,
            Type: req.body.type
        },
        function(err, office) {
            if (err)
                return res
                    .status(500)
                    .send("There was a problem registering the Office.");

            // if office is registered without errors
            // create a token
            var token = jwt.sign({ id: office._id }, config.secret, {
                expiresIn: 86400 // expires in 24 hours
            });

            res.status(200).send({ auth: true, token: token });
        }
    );
});

router.get("/assign/:reportId/:officeId", (req, res) => {
    const officeId = req.params.officeId;
    console.log(officeId);
    Report.findById(req.params.reportId, (err, report) => {
        if (err) {
            console.log(err);
            res.send("Something went Wrong");
        }
        Office.findOne({officeId: officeId}, (err, office)=>{
            if (err) {
                console.log(err);
                res.send("Something went Wrong");
            }
            let officerz = [];
            if(office!==null) officerz = office.officers;
            if(officerz.length==0){
                report.status = "assigned";
                report.officerID = "prateek123";
                const dummyOfficers = ["Manthan", "Prateek", "Ritik", "Mrigyen"];
                const dummyNumbers = ["7776789899", "7776744499", "9876789899", "6616789899"];
                const k = Math.floor(Math.random() * 4);
                report.officerName = dummyOfficers[k];
                report.save((err) => {
                    if (err) res.send("Something went wrong");
                    console.log("done");
                    Users.on("value", function(snapshot) {
                        let tokens = snapshot.val();
                        Object.keys(tokens).forEach(function(key) {
                            //Decide whether the current key is viable for sending the message
                            if (key==report.uid) {
                                //console.log(tokens[key]);
                                messageus(tokens[key].Token, "18 71", "general", "kuch bhi", report.officerName);
                            }
                        });
                    }, function(errorObject) {
                        console.log("The read failed: " + errorObject.code);
                    });
                    res.redirect("https://rakshak-es.herokuapp.com/home");
                });
            }
            else{
                const officer = officerz[officerz.length-1];
                console.log(officer);
                Officers.on("value", function(snapshot) {
                    let tokens = snapshot.val();
                    messages(tokens[officer].token);
                    report.status = "assigned";
                    report.officerID = officer;
                    report.officerName = tokens[officer].name;
                    report.save((err) => {
                        if (err) res.send("Something went wrong");
                        console.log("done");
                        Users.on("value", function(snapshot) {
                            let tokens = snapshot.val();
                            Object.keys(tokens).forEach(function(key) {
                                //Decide whether the current key is viable for sending the message
                                if (key==report.uid) {
                                    //console.log(tokens[key]);
                                    messageus(tokens[key].Token, "18 71", "general", "kuch bhi", report.officerName);
                                }
                            });
                        }, function(errorObject) {
                            console.log("The read failed: " + errorObject.code);
                        });
                        res.redirect("https://rakshak-es.herokuapp.com/home");
                    });
                }, function(errorObject) {
                    console.log("The read failed: " + errorObject.code);
                });
            }
        })
    })
})

router.post("/me", VerifyToken, function(req, res, next) {
    console.log(req.body.accessToken);
    Office.findById(req.officeId, { password: 0 }, function(err, office) {
        if (err)
            return res.status(500).send({ "auth": false });
        if (!office) return res.status(404).send({ "auth": false });

        var type = "General Emergency";
        if (office.Type == 1) {
            type = "Medical";
        } else if (office.Type == 2) {
            type = "General Emergency";
        } else if (office.Type == 3) {
            type = "Fire";
        } else if (office.Type == 4) {
            type = "Disaster";
        }

        Report.find({ type: type }, function(err, reports) {
            //console.log(req.params.networkId);
            res.status(200).send({ "auth": true, "office": office, "reports": reports });
        });

    });
});

module.exports = router;