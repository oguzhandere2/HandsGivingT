const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.helloWorld = functions.https.onRequest((request, response) => {
  functions.logger.info("Hello logs!", {structuredData: true});
  response.send("Hello from Firebase!");
});

exports.getCurrentUserInfo = functions.https.onCall((data, context) => {
  return new Promise((resolve, reject) => {
    const stuff = {};
    const db = admin.firestore();
    db.collection("User")
        .where("Email", "==", data.email)
        .get()
        .then((snapshot) => {
          snapshot.forEach((doc) => {
            const email = doc.data().Email;
            const stuff2 = doc.data();
            stuff[email] = stuff2;
          });
          resolve(stuff);
        })
        .catch((reason) => {
          console.log("db.collection(\"User\").get gets err, reason: " +
          reason);
          reject(reason);
        });
  });
});

exports.addNewRequest = functions.https.onCall((data, context) => {
  const db = admin.firestore();
  let data2 = {};
  let count = 0;
  db.collection("Request").get()
      .then((snapshot) =>{
        count = snapshot.size;
        data2 = {
          Description: data.RequestDesc,
          Location: new admin.firestore.GeoPoint(data.Latitude, data.Longitude),
          RequestID: count + 1 + data.email,
          LocationDesc: data.LocationDesc,
          RequestType: data.RequestType,
          userEmail: data.email,
        };

        db.collection("Request").add(data2);
      }
      );
});
