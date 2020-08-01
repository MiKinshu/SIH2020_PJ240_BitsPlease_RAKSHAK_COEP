//Getting the packages
const express = require("express");

//Initializing the app
const app = express();

//Routes
app.get("/", (req, res)=>{
    res.send("Hi!");
});

module.exports = app;