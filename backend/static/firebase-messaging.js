// Initialize Firebase
import { initializeApp } from "firebase/app";
import { getMessaging, getToken, onMessage } from "firebase/messaging";

const firebaseConfig = {
  apiKey: "AIzaSyCpA_vO8LxkWFYZ9vG_N_OnnpJ1PTGqMQ4",
  authDomain: "ecare-f39d4.firebaseapp.com",
  projectId: "ecare-f39d4",
  storageBucket: "ecare-f39d4.firebasestorage.app",
  messagingSenderId: "733878126691",
  appId: "1:733878126691:web:e25d2e7c1ee5e51a675056"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);

// Request permission and get token
export function requestNotificationPermission() {
  console.log('Requesting notification permission...');
  
  return Notification.requestPermission().then((permission) => {
    if (permission === 'granted') {
      console.log('Notification permission granted.');
      
      // Get token
      return getToken(messaging, { 
        vapidKey: 'YOUR_VAPID_KEY_HERE' // Replace with your VAPID key from Firebase console
      })
      .then((currentToken) => {
        if (currentToken) {
          console.log('FCM token:', currentToken);
          // Register token with backend
          return registerTokenWithBackend(currentToken);
        } else {
          console.log('No registration token available.');
          return false;
        }
      })
      .catch((err) => {
        console.error('An error occurred while retrieving token:', err);
        return false;
      });
    } else {
      console.log('Notification permission denied.');
      return false;
    }
  });
}

// Register token with backend
function registerTokenWithBackend(token) {
  return fetch('/api/core/devices/register/', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + localStorage.getItem('access_token')
    },
    body: JSON.stringify({
      registration_id: token,
      type: 'web',
      name: 'Web Browser'
    })
  })
  .then(response => response.json())
  .then(data => {
    console.log('Device registered:', data);
    return true;
  })
  .catch(error => {
    console.error('Error registering device:', error);
    return false;
  });
}

// Listen for messages when app is in foreground
export function setupMessageListener(callback) {
  onMessage(messaging, (payload) => {
    console.log('Message received in foreground:', payload);
    if (callback) {
      callback(payload);
    }
  });
}
