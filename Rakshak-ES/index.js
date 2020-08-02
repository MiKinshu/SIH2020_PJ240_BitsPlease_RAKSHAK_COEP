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
        },
        ifequal: function(a, b, opts) {
            if (a == b) {
                return opts.fn(this);
            } else {
                return opts.inverse(this);
            }
        },
        ifnequal: function(a, b, opts) {
            if (a != b) {
                return "Officer Message: <b>" + b + "</b>";

            } else {
                return opts.inverse(this);
            }
        },
        ifOnequal: function(a, b, opts) {
            if (a != b) {
                return "Officer Name: <b>" + b + "</b>";

            } else {
                return opts.inverse(this);
            }
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

    var request = require("request");
    var token = req.cookies.auth;
    var alert = false;
    if (req.query.alert) {
        alert = true;
    }
    request.post(
        "https://rakshak-zs.herokuapp.com/office/me", {
            json: { accessToken: token }
        },
        function(error, response, body) {
            if (error || (response.body.auth !== true)) {
                console.log("haa");
                res.redirect("login");
            } else {
                // console.log(response.body.reports);
                //response.body.reports[0].status = "completed";
                //    response.body.reports[response.body.reports.length - 1].status = "completed";
                // response.body.reports[response.body.reports.length - 1].msg = "completeda";
                response.body.reports = response.body.reports.reverse();
                var x = 0;
                for (var i = 0; i < response.body.reports.length; i++) {
                    //        response.body.reports[i].loc = response.body.reports[i].replace(" ", ',');
                    if (response.body.reports[i].status == "completed") {
                        x++;
                    }
                }
                res.render("home", {
                    office: response.body.office,
                    officerCount: 10,
                    reports: response.body.reports,
                    pendingRequests: response.body.reports.length - x,
                    totalRequests: response.body.reports.length,
                    resolvedRequests: x
                });
            }
        }
    );

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