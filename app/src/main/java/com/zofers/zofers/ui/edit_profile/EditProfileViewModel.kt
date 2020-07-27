package com.zofers.zofers.ui.edit_profile

import androidx.lifecycle.MutableLiveData
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.model.Profile
import com.zofers.zofers.staff.States

class EditProfileViewModel : AppViewModel() {

	val emptyUserNameError = MutableLiveData<Boolean>()
	val emptyEmailError = MutableLiveData<Boolean>()
	val invalidEmailError = MutableLiveData<Boolean>()
	val profile = MutableLiveData<Profile>()

	fun init() {
		firebaseService.getProfile(currentUser!!.id) {
			profile.value = it
		}
	}

	fun save(userName: String, email: String, description: String) {
		if (userName.isEmpty()) {
			emptyUserNameError.value = true
			return
		}
		if (email.isEmpty()) {
			emptyEmailError.value = true
			return
		}
		if (!email.contains('@')) {
			invalidEmailError.value = true
			return
		}

		state.value = States.LOADING
		val editFieldsMap = mutableMapOf<String, String>()

		if (email != auth.currentUser?.email) {
			firebaseService.updateEmail(email) {}
			//todo await
		}
		if (userName != currentUser?.name) {
			editFieldsMap["name"] = userName
			firebaseService.updateUserName(userName) {}
			firebaseService.updateNameInConversations(userName, currentUser!!.id)

			//todo await
		}
		if (description != currentUser?.description) {
			editFieldsMap["description"] = description
		}

		if (editFieldsMap.isNotEmpty()) {
			firebaseService.updateDocument("profile", currentUser!!.id, editFieldsMap) { task ->
				if (!task.isSuccessful) {
					state.value = States.ERROR
					return@updateDocument
				}
				firebaseService.getProfile(currentUser!!.id) {
					currentUser = it
					state.value = States.FINISH
				}
			}
		} else {
            state.value = States.FINISH
        }

	}
}
