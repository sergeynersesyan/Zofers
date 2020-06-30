package com.zofers.zofers.ui.edit_password

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.firestore.FirebaseFirestore
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.model.Offer
import com.zofers.zofers.model.Profile
import com.zofers.zofers.staff.States
import java.util.*

class EditPasswordViewModel : AppViewModel() {

	val passwordsNotMatchError = MutableLiveData<Boolean>()
	val passwordShortError = MutableLiveData<Boolean>()

	fun init() {

	}

	fun save(passWord1: String, password2: String) {

		if (passWord1 != password2) {
			passwordsNotMatchError.value = true
			return
		} else if (passWord1.length < 6) {
			passwordShortError.value = true
			return
		}

		if (passWord1.isNotEmpty() && passWord1 == password2) {
			state.value = States.LOADING
			firebaseService.updatePassword(passWord1) { task ->
				state.value = if (task.isSuccessful) {
					States.FINISH
				} else if ((task.exception as FirebaseAuthRecentLoginRequiredException).errorCode == "ERROR_REQUIRES_RECENT_LOGIN") {
					States.UNAUTHORIZED
				} else {
					States.ERROR
				}
			}
		}
	}


}
