package com.zofers.zofers.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.zofers.zofers.R
import com.zofers.zofers.staff.MessageHelper
import com.zofers.zofers.staff.States
import com.zofers.zofers.databinding.ActivityLoginBinding
import com.zofers.zofers.home.HomeActivity
import com.zofers.zofers.BaseActivity


class LoginActivity : BaseActivity(), OnClickListener {

	private var isRegisterMode = false
	private lateinit var binding: ActivityLoginBinding
	private lateinit var viewModel: LoginViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
		initViewModel()
		if (viewModel.currentUser != null && FirebaseAuth.getInstance().currentUser != null) {
			openApp()
		}
		initView()
	}

	private fun initView() {
		binding.password.setOnEditorActionListener(TextView.OnEditorActionListener { textView, id, keyEvent ->
			if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
				attemptLogin()
				return@OnEditorActionListener true
			}
			false
		})

		(binding.googleSignInButton.getChildAt(0) as TextView).text = getString(R.string.sign_in_google)
		binding.googleSignInButton.setOnClickListener(this)
		binding.emailSignInButton.setOnClickListener(this)
		binding.toggleButton.setOnClickListener(this)
		buttonTextsSetup()
	}

	public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		viewModel.onActivityResult(this, requestCode, resultCode, data)
	}

	private fun initViewModel() {
		viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
		viewModel.state.observe(this, Observer { state ->
			when (state) {
				States.NONE -> showProgress(false)
				States.LOADING -> showProgress(true)
				States.ERROR -> {
					if (isRegisterMode) {
						binding.emailInputLayout.error = getString(R.string.error_busy_email)
						binding.email.requestFocus()
					} else {
						binding.passwordInputLayout.error = getString(R.string.error_incorrect_credentials)
						binding.password.requestFocus()
					}
					showProgress(false)
				}
				States.FAIL -> {
					showProgress(false)
					MessageHelper.showNoConnectionToast(this@LoginActivity)
				}
				States.FINISH -> {
					showProgress(false)
					openApp()
				}
			}
		})
		viewModel.initGoogleSignIn(this)
	}

	private fun attemptLogin() {
		// Reset errors.
		binding.emailInputLayout.error = null
		binding.passwordInputLayout.error = null

		// Store values at the time of the login attempt.
		val email = binding.email.text.toString()
		val password = binding.password.text.toString()

		var cancel = false
		var focusView: View? = null

		// Check for a valid password, if the user entered one.
		if (password.isEmpty()) {
			binding.passwordInputLayout.error = getString(R.string.error_field_required)
			focusView = binding.password
			cancel = true
		} else if (!viewModel.isPasswordValid(password)) {
			binding.passwordInputLayout.error = getString(R.string.error_invalid_password)
			focusView = binding.password
			cancel = true
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			binding.emailInputLayout.error = getString(R.string.error_field_required)
			focusView = binding.email
			cancel = true
		} else if (!viewModel.isEmailValid(email)) {
			binding.emailInputLayout.error = getString(R.string.error_invalid_email)
			focusView = binding.email
			cancel = true
		}

		if (cancel) {
			focusView!!.requestFocus()
		} else if (isRegisterMode) {
			viewModel.register(email, password)
		} else {
			viewModel.login(email, password)
		}
	}

	private fun openApp() {
		val intent = Intent(applicationContext, HomeActivity::class.java)
		startActivity(intent)
		finish()
	}

	private fun showProgress(show: Boolean) {
		val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

		binding.loginForm.visibility = if (show) View.GONE else View.VISIBLE
		binding.loginForm.animate().setDuration(shortAnimTime.toLong()).alpha(
				(if (show) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
			override fun onAnimationEnd(animation: Animator) {
				binding.loginForm.visibility = if (show) View.GONE else View.VISIBLE
			}
		})

		binding.loginProgress.visibility = if (show) View.VISIBLE else View.GONE
		binding.loginProgress.animate().setDuration(shortAnimTime.toLong()).alpha(
				(if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
			override fun onAnimationEnd(animation: Animator) {
				binding.loginProgress.visibility = if (show) View.VISIBLE else View.GONE
			}
		})
	}

	override fun onClick(v: View) {
		when (v.id) {
			R.id.toggle_button -> {
				isRegisterMode = !isRegisterMode
				buttonTextsSetup()
			}
			R.id.email_sign_in_button -> attemptLogin()
			R.id.google_sign_in_button -> viewModel.onGoogleSignIn(this)
		}
	}

	private fun buttonTextsSetup() {
		binding.toggleButton.setText(if (isRegisterMode) R.string.action_sign_in else R.string.action_register)
		binding.emailSignInButton.setText(if (isRegisterMode) R.string.action_register else R.string.action_sign_in)
	}
}

