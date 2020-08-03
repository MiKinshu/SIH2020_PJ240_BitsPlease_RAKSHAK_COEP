// Getting the packages
const express = require("express");
const router = express.Router();
const bodyParser = require("body-parser");
const Report = require("../Report");
const VerifyToken = require("./VerifyToken");


router.use(bodyParser.urlencoded({ extended: false }));
router.use(bodyParser.json());
const Office = require("./Office");

/**
 * Configure JWT
 */
var jwt = require("jsonwebtoken"); // used to create, sign, and verify tokens
var bcrypt = require("bcryptjs");
var config = require("./config"); // get config file

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

router.get("/assign/:reportId", (req, res) => {
    console.log(req.body);
    Report.findById(req.params.reportId, (err, report) => {
        if (err) {
            console.log(err);
            res.send("Something went Wrong");
        }
        report.status = "assigned";
        report.officerId = "prateek123";
        const dummyOfficers = ["Manthan", "Prateek", "Ritik", "Mrigyen"];
        const dummyNumbers = ["7776789899", "7776744499", "9876789899", "6616789899"];
        const k = Math.floor(Math.random() * 4);
        report.officerName = dummyOfficers[k];
        report.save((err) => {
            if (err) res.send("Something went wrong");
            console.log("done")
            res.redirect("https://rakshak-es.herokuapp.com/home");
        });
    })
})

router.post("/me", VerifyToken, function(req, res, next) {
    console.log(req.body.accessToken);
    Office.findById(req.officeId, { password: 0 }, function(err, office) {
        if (err)
            return res.status(500).send({ "auth": false });
        if (!office) return res.status(404).send({ "auth": false });

        var type = "General Emergency";
        if (office.Type == 0) {
            type = "Medical";
        } else if (office.Type == 1) {
            type = "General Emergency";
        } else if (office.Type == 2) {
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