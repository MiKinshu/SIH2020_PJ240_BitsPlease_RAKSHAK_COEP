// Getting the packages
const express = require("express");
const router = express.Router();
const bodyParser = require("body-parser");
const report = require("../Report");
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

router.post("/me", VerifyToken, function(req, res, next) {
    console.log(req.officeId);
    Office.findById(req.officeId, { password: 0 }, function(err, office) {
        if (err)
            return res.status(500).send({ "auth": false });
        if (!office) return res.status(404).send({ "auth": false });
        res.status(200).send({ "auth": true, "office": office });
    });
});

module.exports = router;