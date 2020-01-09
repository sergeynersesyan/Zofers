package com.zofers.zofers.login

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.BaseActivity
import com.zofers.zofers.model.Profile
import com.zofers.zofers.staff.States

class LoginViewModel : AppViewModel() {

	private val authListener = { task: Task<AuthResult> ->
		ensureWriteProfile()
	}

	private lateinit var googleSignInClient: GoogleSignInClient

	companion object {
		private const val RC_GOOGLE_SIGN_IN = 1000
	}

	fun initGoogleSignIn(activity: Activity) {
		val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken("558848005913-t3td3ttapfp517eqbpb7kn6kb5gtni63.apps.googleusercontent.com") // for firebase
				.requestEmail()
				.build()
		googleSignInClient = GoogleSignIn.getClient(activity, gso)
	}

	fun register(email: String, password: String) {
		state.value = States.LOADING
		auth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener(authListener)
	}

	fun login(email: String, password: String) {
		state.value = States.LOADING
		auth.signInWithEmailAndPassword(email, password)
				.addOnCompleteListener(authListener)
	}

	fun isEmailValid(email: String): Boolean {
		return email.contains("@")
	}

	fun isPasswordValid(password: String): Boolean {
		return password.length > 5
	}

	fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == RC_GOOGLE_SIGN_IN) {
			val task = GoogleSignIn.getSignedInAccountFromIntent(data)
			try {
				// Google Sign In was successful, authenticate with Firebase
				val account = task.getResult(ApiException::class.java)
				firebaseAuthWithGoogle(activity, account!!)
			} catch (e: ApiException) {
				state.value = States.ERROR
			}
		}
	}

	fun onGoogleSignIn(activity: BaseActivity) {
		activity.startActivityForResult(googleSignInClient.signInIntent, RC_GOOGLE_SIGN_IN)
	}

	private fun firebaseAuthWithGoogle(activity: Activity, acct: GoogleSignInAccount) {
		state.value = States.LOADING
		val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
		auth.signInWithCredential(credential)
				.addOnCompleteListener(activity, authListener)
	}

	private fun ensureWriteProfile() {
		FirebaseFirestore.getInstance()
				.collection("profile")
				.document(auth.currentUser!!.uid)
				.get()
				.addOnSuccessListener {
					if (it.exists() && it.toObject(Profile::class.java)!!.id == auth.currentUser!!.uid) {
						userManager.userProfile = it.toObject(Profile::class.java)
						state.value = States.FINISH
					} else {
						createProfile()
					}
				}
				.addOnFailureListener {
					createProfile()
				}
	}

	private fun createProfile() {
		val profile = Profile().apply {
			id = auth.currentUser!!.uid
			name = auth.currentUser?.displayName
		}
		val db = FirebaseFirestore.getInstance()
		db.collection("profile")
				.document(profile.id)
				.set(profile)
				.addOnCompleteListener { task ->
					if (task.isSuccessful) {
						userManager.userProfile = profile
						state.value = States.FINISH
					} else {
						state.value = States.ERROR
					}
				}
	}
}
