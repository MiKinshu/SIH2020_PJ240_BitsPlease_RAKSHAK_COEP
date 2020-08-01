//Getting the app
const app = require("./app");

//Setting the port
const port = process.env.PORT || 3001;

//Starting the server
const server = app.listen(3001, ()=>{
    console.log("Rakshak-ZS listening on port " + port);
});

