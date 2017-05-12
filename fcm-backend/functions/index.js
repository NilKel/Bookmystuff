var functions = require('firebase-functions');
var admin = require("firebase-admin");
var serviceAccount = require('./bookmystuff-79c2e-firebase-adminsdk-4wglh-9054d93f93.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://bookmystuff-79c2e.firebaseio.com"
});

// functions.on

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

exports.sendMessage = functions.database.ref('/lobbies/{lobby_id}/messages')
                            .onWrite(event => {
                               const original = event.data.val();
                               console.log(event.params.lobby_id);
                               var topic = event.params.lobby_id;
                               var payload = {
                                   data: original
                               };
                               return admin.messaging().sendToTopic(topic, payload);
                            });
