const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.helloWorld = functions.https.onRequest((request, response) => {
  functions.logger.info("Hello logs!", {structuredData: true});
  response.send("Hello from Firebase!");
});

exports.addMessage = functions.https.onCall((data, context) => {
  return new Promise((resolve, reject) => {
    const stuff = {};
    const db = admin.firestore();
    db.collection("User")
        .get()
        .then((snapshot) => {
          snapshot.forEach((doc) => {
            const email = doc.data().Email;
            const stuff2 = doc.data();
            stuff["email"] = email;
            stuff[email] = stuff2;
          });
          const stuffStr = JSON.stringify(stuff, null, "\t");
          console.log("colors callback result : " + stuffStr);
          resolve(stuff);
        })
        .catch((reason) => {
          console.log("db.collection(\"colors\").get gets err, reason: " +
          reason);
          reject(reason);
        });
  });
});
