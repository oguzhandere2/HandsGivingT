const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

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
          Accepted: false,
          volunteerEmail: "",
          Finished: false,
          IsPointed: false,
          Feedback: "",
          Point: 0,
        };

        db.collection("Request").add(data2);
      }
      );
});

exports.getUserOngoingRequests = functions.https.onCall((data, context) => {
  return new Promise((resolve, reject) => {
    const stuff = {};
    const db = admin.firestore();
    db.collection("Request")
        .where("userEmail", "==", data.email)
        .where("Finished", "==", false)
        .get()
        .then((snapshot) => {
          snapshot.forEach((doc) => {
            const requestId = doc.data().RequestID;
            const stuff2 = doc.data();
            stuff[requestId] = stuff2;
          });
          resolve(stuff);
        })
        .catch((reason) => {
          console.log("db.collection(\"Request\").get gets err, reason: " +
          reason);
          reject(reason);
        });
  });
});

exports.getNearRequests = functions.https.onCall((data, context) => {
  return new Promise((resolve, reject) => {
    const stuff = {};
    const db = admin.firestore();

    const lat = 0.02;
    const lon = 0.02;
    const latitude = data.latitude;
    const longitude = data.longitude;

    const lowerLat = latitude - lat;
    const lowerLon = longitude - lon;

    const greaterLat = latitude + lat;
    const greaterLon = longitude + lon;

    db.collection("Request")
        .where("Accepted", "==", false)
        .where("Finished", "==", false)
        .get()
        .then((snapshot) => {
          snapshot.forEach((doc) => {
            const latO = doc.data().Location._latitude;
            const longO = doc.data().Location._longitude;
            if (latO >= lowerLat && latO <= greaterLat &&
            longO >= lowerLon && longO <= greaterLon) {
              const requestId = doc.data().RequestID;
              const stuff2 = doc.data();
              stuff[requestId] = stuff2;
            }
          });
          resolve(stuff);
        })
        .catch((reason) => {
          console.log("db.collection(\"Request\").get gets err, reason: " +
                  reason);
          reject(reason);
        });
  });
});

exports.getVolunteerOngoingRequests = functions.https.
    onCall((data, context) => {
      return new Promise((resolve, reject) => {
        const stuff = {};
        const db = admin.firestore();

        const email = data.email;
        db.collection("Request")
            .where("Accepted", "==", true)
            .where("Finished", "==", false)
            .where("volunteerEmail", "==", email)
            .get()
            .then((snapshot) => {
              snapshot.forEach((doc) => {
                const requestId = doc.data().RequestID;
                const stuff2 = doc.data();
                stuff[requestId] = stuff2;
              });
              resolve(stuff);
            })
            .catch((reason) => {
              console.log("db.collection(\"Request\").get gets err, reason: " +
                  reason);
              reject(reason);
            });
      });
    });

exports.getUserFinishedRequests = functions.https.onCall((data, context) => {
  return new Promise((resolve, reject) => {
    const stuff = {};
    const db = admin.firestore();

    const email = data.email;
    db.collection("Request")
        .where("Accepted", "==", true)
        .where("Finished", "==", true)
        .where("userEmail", "==", email)
        .get()
        .then((snapshot) => {
          snapshot.forEach((doc) => {
            const requestId = doc.data().RequestID;
            const stuff2 = doc.data();
            stuff[requestId] = stuff2;
          });
          resolve(stuff);
        })
        .catch((reason) => {
          console.log("db.collection(\"Request\").get gets err, reason: " +
                  reason);
          reject(reason);
        });
  });
});

exports.getVolunteerFinishedRequests = functions.https.
    onCall((data, context) => {
      return new Promise((resolve, reject) => {
        const stuff = {};
        const db = admin.firestore();

        const email = data.email;
        db.collection("Request")
            .where("Accepted", "==", true)
            .where("Finished", "==", true)
            .where("volunteerEmail", "==", email)
            .get()
            .then((snapshot) => {
              snapshot.forEach((doc) => {
                const requestId = doc.data().RequestID;
                const stuff2 = doc.data();
                stuff[requestId] = stuff2;
              });
              resolve(stuff);
            })
            .catch((reason) => {
              console.log("db.collection(\"Request\").get gets err, reason: " +
                  reason);
              reject(reason);
            });
      });
    });

exports.deleteRequest = functions.https.onCall((data, context) => {
  const db = admin.firestore();
  const reqID = data.requestID;

  const jobskillquery = db.collection("Request")
      .where("Finished", "==", false)
      .where("RequestID", "==", reqID);

  jobskillquery.get().then(function(querySnapshot) {
    querySnapshot.forEach(function(doc) {
      doc.ref.delete();
    });
  });
});

exports.getUserFinishedUnpointedRequests = functions.https
    .onCall((data, context) => {
      return new Promise((resolve, reject) => {
        const stuff = {};
        const db = admin.firestore();

        const email = data.email;
        db.collection("Request")
            .where("Accepted", "==", true)
            .where("Finished", "==", true)
            .where("IsPointed", "==", false)
            .where("userEmail", "==", email)
            .get()
            .then((snapshot) => {
              snapshot.forEach((doc) => {
                const requestId = doc.data().RequestID;
                const stuff2 = doc.data();
                stuff[requestId] = stuff2;
              });
              resolve(stuff);
            })
            .catch((reason) => {
              console.log("db.collection(\"Request\").get gets err, reason: " +
                  reason);
              reject(reason);
            });
      });
    });

exports.acceptRequest = functions.https.onCall((data, context) => {
  const db = admin.firestore();
  const reqID = data.requestID;
  const email = data.email;

  const jobskillquery = db.collection("Request")
      .where("Accepted", "==", false)
      .where("RequestID", "==", reqID);

  jobskillquery.get().then(function(querySnapshot) {
    querySnapshot.forEach(function(doc) {
      doc.ref.update({
        Accepted: true,
        volunteerEmail: email,
      });
    });
  });
});

exports.finishRequest = functions.https.onCall((data, context) => {
  const db = admin.firestore();
  const reqID = data.requestID;

  const jobskillquery = db.collection("Request")
      .where("Accepted", "==", true)
      .where("Finished", "==", false)
      .where("RequestID", "==", reqID);

  jobskillquery.get().then(function(querySnapshot) {
    querySnapshot.forEach(function(doc) {
      doc.ref.update({
        Finished: true,
      });
    });
  });
});
