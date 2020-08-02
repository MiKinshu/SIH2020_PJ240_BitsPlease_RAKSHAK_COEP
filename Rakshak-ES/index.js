//calling express
var express = require("express");
app = express();

//handle bars with section
var handlebars = require("express-handlebars").create({
    defaultLayout: "main",
    helpers: {
        section: function(name, options) {
            if (!this._sections) this._sections = {};
            this._sections[name] = options.fn(this);
            return null;
        }
    }
});

app.engine("handlebars", handlebars.engine);
app.set("view engine", "handlebars");

// add body parser
var cookieParser = require("cookie-parser");
app.use(cookieParser());

const bodyParser = require("body-parser");

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

// mongoose connect

const mongoose = require("mongoose");

// mongoose.connect(
//   "mongodb+srv://admin:admin_password@cluster0-vi5m5.mongodb.net/test?retryWrites=true&w=majority",
//   () => {}
// );

//add port to listen-default 3000
app.set("port", process.env.PORT || 3000);

// add public direct
app.use(express.static(__dirname + "/public"));

//MAIN code goes here

app.get("/login", (req, res) => {
    res.render("login");
});


app.get("/home", (req, res) => {
    res.render("home", {
        officeName: "office Here",
        type: "Hospitaa",
        officerCount: 10,
        pendingRequests: 5,
        totalRequests: 100,
        resolvedRequests: 95
    });
});

app.get("/register", (req, res) => {
    res.render("register");
});

app.post("/register", (req, res) => {
    var request = require("request");
    console.log(req.body);
    request.post(
        "https://rakshak-zs.herokuapp.com/office/register", {
            json: {
                name: req.body.name,
                password: req.body.password,
                officeId: req.body.officeId,
                Type: req.body.type
            }
        },
        function(error, response, body) {
            if (!error && response != null & response.body.auth == true) {
                res.cookie("auth", response.body.token);
                res.redirect("home");
            } else {
                console.log("ere");
                res.redirect("login");
            }
        }
    );
});

app.get("/logout", (req, res) => {
    res.clearCookie("auth");
    res.redirect("login");
});

app.post("/login", (req, res) => {
    var request = require("request");
    console.log(req.body);
    request.post(
        "https://rakshak-zs.herokuapp.com/office/login", {
            json: {

                password: req.body.password,
                officeId: req.body.officeId,

            }
        },
        function(error, response, body) {
            //  console.log(response);
            if (!error && response != null && response.body.auth == true) {
                res.cookie("auth", response.body.token);
                res.redirect("home");
            } else {
                res.redirect("login");
            }
        }
    );
});

app.get("/", (req, res) => {
    res.render("login");
});

// Middlewares and errors
// 404 page
app.use(function(req, res) {
    res.status(404);

    res.send("404");
});

// 500 page
app.use(function(err, req, res, next) {
    console.error(err.stack);
    res.status(500);
    res.send("500");
});

//Listen port
app.listen(app.get("port"), function() {
    console.log("express started " + app.get("port"));
});