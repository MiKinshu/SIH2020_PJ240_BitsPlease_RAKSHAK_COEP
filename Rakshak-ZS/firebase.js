const admin = require('firebase-admin');
const serviceAccount = require("./serviceaccount.json");

//Intitializing firebase
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://rakshak-53755.firebaseio.com"
});

module.exports = admin;

