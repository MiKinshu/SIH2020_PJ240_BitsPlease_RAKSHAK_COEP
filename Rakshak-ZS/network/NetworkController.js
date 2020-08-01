// Getting the packages
const express = require("express");
const router = express.Router();
const bodyParser = require("body-parser");
const report = require("../Report");
const VerifyToken = require("./VerifyToken");

router.use(bodyParser.urlencoded({ extended: false }));
router.use(bodyParser.json());
const Network = require("./Network");

/**
 * Configure JWT
 */
var jwt = require("jsonwebtoken"); // used to create, sign, and verify tokens
var bcrypt = require("bcryptjs");
var config = require("./config"); // get config file

router.post("/login", function(req, res) {
  console.log("login:" + req.body);
  Network.findOne({ networkID: req.body.networkID }, function(err, network) {
    if (err) return res.status(500).send("Error on the server.");
    if (!network) return res.status(404).send({ auth: false, token: null });

    // check if the password is valid
    var passwordIsValid = bcrypt.compareSync(req.body.password, network.password);

    if (!passwordIsValid)
      return res.status(401).send({ auth: false, token: null });

    // if network is found and password is valid
    // create a token
    var token = jwt.sign({ id: network._id }, config.secret, {
      expiresIn: 86400 // expires in 24 hours
    });

    // return the information including token as JSON
    res.status(200).send({ auth: true, token: token });
  });
});

router.post("/setUrl",  function(req, res, next){
  console.log("andar" + " " + req.body.networkID);
  const url = req.body.url;
  Network.findByIdAndUpdate( req.body.networkID , {"networkId":req.body.url}, (err, network)=>{
    console.log("mila" + " " + network.networkID);
    console.log(network);
  });
});

router.post("/:networkId/requests", VerifyToken, function(req, res, next){
  report.find({networkId : req.params.networkId}, function(err, reports){
    console.log(req.params.networkId);
    let response  = [];
    reports.forEach(element => {
          let temp = {
            networkID: element.networkID,
            msg: element.msg,
            type: element.type,
            date: element.date,
            name: element.name
          };
          response.push(temp);
          console.log(temp);
    });
    response.reverse();
    res.send(response);
  });
});

router.get("/logout", function(req, res) {
  res.status(200).send({ auth: false, token: null });
});

router.post("/register", function(req, res) {
  var hashedPassword = bcrypt.hashSync(req.body.password, 8);
  console.log(req.body);
  Network.create(
    {
      name: req.body.name,
      networkID: req.body.networkID,
      password: hashedPassword
    },
    function(err, network) {
      if (err)
        return res
          .status(500)
          .send("There was a problem registering the network`.");

      // if network is registered without errors
      // create a token
      var token = jwt.sign({ id: network._id }, config.secret, {
        expiresIn: 86400 // expires in 24 hours
      });

      res.status(200).send({ auth: true, token: token });
    }
  );
});

router.post("/me", VerifyToken, function(req, res, next) {
  Network.findById(req.networkId, { password: 0 }, function(err, network) {
    if (err)
      return res.status(500).send({"auth": false});
    if (!network) return res.status(404).send({"auth": false});
    res.status(200).send({"auth": true, "network" : network});
  });
});

module.exports = router;
