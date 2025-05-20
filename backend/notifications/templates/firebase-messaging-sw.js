// Give the service worker access to Firebase Messaging
importScripts('https://www.gstatic.com/firebasejs/8.10.0/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/8.10.0/firebase-messaging.js');

// Initialize Firebase
firebase.initializeApp({
  apiKey: "AIzaSyCpA_vO8LxkWFYZ9vG_N_OnnpJ1PTGqMQ4",
  authDomain: "ecare-f39d4.firebaseapp.com",
  projectId: "ecare-f39d4",
  storageBucket: "ecare-f39d4.firebasestorage.app",
  messagingSenderId: "733878126691",
  appId: "1:733878126691:web:e25d2e7c1ee5e51a675056"
});

const messaging = firebase.messaging();

// Handle background messages
messaging.setBackgroundMessageHandler(function(payload) {
  console.log('[firebase-messaging-sw.js] Received background message ', payload);
  
  const notificationTitle = payload.notification.title;
  const notificationOptions = {
    body: payload.notification.body,
    icon: '/static/images/logo.png',
    data: payload.data
  };

  self.addEventListener('notificationclick', function(event) {
    event.notification.close();
    
    // Handle notification click based on type
    if (payload.data && payload.data.notification_type === 'prescription_created') {
      clients.openWindow(`/prescriptions/${payload.data.prescription_id}/`);
    } else if (payload.data && payload.data.notification_type === 'medication_reminder') {
      clients.openWindow(`/prescriptions/${payload.data.prescription_id}/`);
    }
  });

  return self.registration.showNotification(notificationTitle, notificationOptions);
});
