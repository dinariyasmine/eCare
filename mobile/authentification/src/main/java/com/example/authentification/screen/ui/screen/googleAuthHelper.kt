package com.example.authentification.screen.ui.screen

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleAuthHelper(private val activity: Activity) {
    val googleSignInClient: GoogleSignInClient

    companion object {
        // Make sure this matches your Django GOOGLE_OAUTH2_CLIENT_ID
        // Must be the Web client ID, not the Android client ID
        const val WEB_CLIENT_ID = "453325312850-rr9fsgb9b85tq6trvmgj903okutdc48j.apps.googleusercontent.com"
    }

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()  // Request profile information
            .requestIdToken(WEB_CLIENT_ID)  // Request ID token using Web client ID
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }
}