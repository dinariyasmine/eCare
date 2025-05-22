package com.example.authentification.screen.ui.screen

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleAuthHelper(private val activity: Activity) {
    val googleSignInClient: GoogleSignInClient

    companion object {
        // Replace this with your Android client ID from Google Cloud Console
        const val ANDROID_CLIENT_ID = "453325312850-uenml1flter962eus18bke2betc91u94.apps.googleusercontent.com"
    }

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken(ANDROID_CLIENT_ID)
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }
}