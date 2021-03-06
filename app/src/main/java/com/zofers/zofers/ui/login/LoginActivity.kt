package com.zofers.zofers.ui.login

import android.R.attr.left
import android.R.attr.right
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.setPadding
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.zofers.zofers.BaseActivity
import com.zofers.zofers.R
import com.zofers.zofers.databinding.ActivityLoginBinding
import com.zofers.zofers.staff.MessageHelper
import com.zofers.zofers.staff.States
import com.zofers.zofers.ui.home.HomeActivity
import java.util.*


open class LoginActivity : BaseActivity(), OnClickListener {

	private var isRegisterMode = true
	private lateinit var binding: ActivityLoginBinding
	private lateinit var viewModel: LoginViewModel

	companion object {
		const val REQUEST_CODE_LAZY_LOGIN: Int = 1973
		fun start(context: Context) {
			context.startActivity(
					Intent(
							context,
							LoginActivity::class.java
					)
			)
		}

		fun startForResult(activity: Activity) {
			activity.startActivityForResult(
					Intent(
							activity,
							LoginActivity::class.java
					),
					REQUEST_CODE_LAZY_LOGIN
			)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
		initViewModel()
//		if (viewModel.currentUser != null && FirebaseAuth.getInstance().currentUser != null) {
//			openApp()
//		}
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
		binding.forgotPasswordButton.setOnClickListener(this)
		binding.facebookSignInButton.setPermissions(listOf("email"));
		binding.confirmPassword.doOnTextChanged { text, start, count, after ->
			binding.confirmPasswordInputLayout.error = null
		}
		binding.email.doOnTextChanged { text, start, count, after ->
			binding.emailInputLayout.error = null
		}
		binding.password.doOnTextChanged { text, start, count, after ->
			binding.passwordInputLayout.error = null
		}
		binding.name.doOnTextChanged { text, start, count, after ->
			binding.nameInputLayout.error = null
		}
		viewSetup()
	}

	public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		viewModel.onActivityResult(this, requestCode, resultCode, data)
	}

	private fun initViewModel() {
		viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
		viewModel.state.observe(this, Observer { state ->
			when (state) {
				States.NONE -> showProgress(false)
				States.LOADING -> showProgress(true)
				States.ERROR -> {
					if (viewModel.errorMessage.isNotEmpty()) {
						MessageHelper.showSnackBar(binding.root, viewModel.errorMessage, Snackbar.LENGTH_LONG)
						viewModel.errorMessage = ""
					} else if (isRegisterMode) {
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
					if (callingActivity == null) {
						openApp()
					} else {
						setResult(Activity.RESULT_OK)
						finish()
					}
				}
			}
		})
		viewModel.messageEvent.observe(this, Observer { message ->
			Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
		})
		viewModel.init(this)
	}

	private fun attemptLogin() {
		// Reset errors.
		binding.emailInputLayout.error = null
		binding.passwordInputLayout.error = null

		// Store values at the time of the login attempt.
		val name = binding.name.text.trim().toString()
		val email = binding.email.text.trim().toString()
		val password = binding.password.text.toString()

		var cancel = false
		var focusView: View? = null

		if (isRegisterMode && name.isEmpty()) {
			binding.nameInputLayout.error = getString(R.string.error_field_required)
			focusView = binding.name
			cancel = true
		}

		// Check for a valid password, if the user entered one.
		if (password.isEmpty()) {
			binding.passwordInputLayout.error = getString(R.string.error_field_required)
			focusView = binding.password
			cancel = true
		} else if (!viewModel.isPasswordValid(password)) {
			binding.passwordInputLayout.error = getString(R.string.error_short_password)
			focusView = binding.password
			cancel = true
		} else if (isRegisterMode && password != binding.confirmPassword.text.toString()) {
			binding.confirmPasswordInputLayout.error = getString(R.string.error_passwords_not_match)
			focusView = binding.confirmPassword
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

		when {
			cancel -> {
				focusView!!.requestFocus()
			}
			isRegisterMode -> {
				viewModel.register(email, password, name)
			}
			else -> {
				viewModel.login(email, password)
			}
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
				viewSetup()
			}
			R.id.email_sign_in_button -> attemptLogin()
			R.id.google_sign_in_button -> viewModel.onGoogleSignIn(this)
			R.id.forgot_password_button -> {
				val editText = EditText(this)
				editText.hint = getString(R.string.enter_email_hint)
				val container = FrameLayout(this)
				container.setPadding(resources.getDimension(R.dimen.activity_horizontal_margin).toInt())
				container.addView(editText)

				AlertDialog.Builder(this)
						.setTitle(getString(R.string.reset_password))
						.setPositiveButton(R.string.send_email) { _, _ ->
							viewModel.sendForgotPasswordEmail(editText.text.trim().toString())
						}
						.setNegativeButton(android.R.string.cancel) { _, _ -> }
						.setView(container)
						.show()
			}
		}
	}

	private fun viewSetup() {
		if (isRegisterMode) {
			binding.toggleButton.setText(R.string.action_sign_in)
			binding.emailSignInButton.setText(R.string.action_register)
			binding.nameInputLayout.visibility = View.VISIBLE
			binding.confirmPasswordInputLayout.visibility = View.VISIBLE
			//animate input fields
			binding.nameInputLayout.alpha = 0f
			binding.confirmPasswordInputLayout.alpha = 0f
			binding.nameInputLayout.animate().alpha(1f).start()
			binding.confirmPasswordInputLayout.animate().alpha(1f).start()
			//animate buttons
			binding.emailSignInButton.translationY = binding.confirmPasswordInputLayout.height * -1f
			binding.emailSignInButton.animate().translationY(0f).start()
			binding.toggleButton.translationY = binding.confirmPasswordInputLayout.height * -1f
			binding.toggleButton.animate().translationY(0f).start()
		} else {
			binding.toggleButton.setText(R.string.action_register)
			binding.emailSignInButton.setText(R.string.action_sign_in)
			//animate buttons
			binding.emailSignInButton.animate().translationY(binding.confirmPasswordInputLayout.height * -1f).start()
			binding.toggleButton.animate().translationY(binding.confirmPasswordInputLayout.height * -1f).start()
			//animate input fields
			binding.nameInputLayout.animate().alpha(0f).start()
			binding.confirmPasswordInputLayout.animate().alpha(0f).withEndAction {
				binding.nameInputLayout.visibility = View.GONE
				binding.confirmPasswordInputLayout.visibility = View.GONE
				binding.emailSignInButton.translationY = 0f
				binding.toggleButton.translationY = 0f
			}.start()
		}

	}
}

