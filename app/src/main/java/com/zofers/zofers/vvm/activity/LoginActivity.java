package com.zofers.zofers.vvm.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zofers.zofers.R;
import com.zofers.zofers.staff.MessageHelper;
import com.zofers.zofers.vvm.viewmodel.LoginViewModel;
import com.zofers.zofers.vvm.viewmodel.States;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements OnClickListener {

    private static final int REQUEST_READ_CONTACTS = 0;

    // UI references.
    private AutoCompleteTextView emailEditText;
    private EditText passwordEditText;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout emailInputLayout;
    private View mProgressView;
    private View mLoginFormView;
    private Button logInButton;
    private Button toggleButton;

    private boolean isRegisterMode = false;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //todo before release
        if (getApp().getUserManager().hasAuthorized() || true) {
            openApp();
        }
        // Set up the login form.
        emailEditText = findViewById(R.id.email);
//        populateAutoComplete();

        passwordEditText = findViewById(R.id.password);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        passwordInputLayout = findViewById(R.id.password_input_layout);
        emailInputLayout = findViewById(R.id.email_input_layout);

        logInButton = findViewById(R.id.email_sign_in_button);
        logInButton.setOnClickListener(this);
        toggleButton = findViewById(R.id.toggle_button);
        toggleButton.setOnClickListener(this);
        buttonTextsSetup();

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        viewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        viewModel.getState().observe(this, state -> {
            switch (state) {
                case States.NONE:
                    showProgress(false);
                    break;
                case States.LOADING:
                    showProgress(true);
                    break;
                case States.ERROR:
                    if (isRegisterMode) {
                        emailInputLayout.setError(getString(R.string.error_busy_email));
                        emailEditText.requestFocus();
                    } else {
                        passwordInputLayout.setError(getString(R.string.error_incorrect_credentials));
                        passwordEditText.requestFocus();
                    }
                    showProgress(false);

                    break;
                case States.FAIL:
                    showProgress(false);
                    MessageHelper.showNoConnectionToast(LoginActivity.this);
                    break;
                case States.FINISH:
                    showProgress(false);
                    openApp();
            }
        });
    }

    private void attemptLogin() {
        // Reset errors.
        emailInputLayout.setError(null);
        passwordInputLayout.setError(null);

        // Store values at the time of the login attempt.
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !viewModel.isPasswordValid(password)) {
            passwordInputLayout.setError(getString(R.string.error_invalid_password));
            focusView = passwordEditText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError(getString(R.string.error_field_required));
            focusView = emailEditText;
            cancel = true;
        } else if (!viewModel.isEmailValid(email)) {
            emailInputLayout.setError(getString(R.string.error_invalid_email));
            focusView = emailEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else if (isRegisterMode) {
            viewModel.register(email, password);
        } else {
            viewModel.login(email, password);
        }
    }

    private void openApp() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle_button:
                isRegisterMode = !isRegisterMode;
                buttonTextsSetup();
                break;
            case R.id.email_sign_in_button:
                attemptLogin();
                break;
        }
    }

    private void buttonTextsSetup() {
        toggleButton.setText(isRegisterMode ? R.string.action_sign_in : R.string.action_register);
        logInButton.setText(isRegisterMode ? R.string.action_register : R.string.action_sign_in);
    }

    //    private void populateAutoComplete() {
//        if (!mayRequestContacts()) {
//            return;
//        }
//
//        getLoaderManager().initLoader(0, null, this);
//    }
//
//    private boolean mayRequestContacts() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
//            Snackbar.make(emailEditText, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
//                    .setAction(android.R.string.ok, new View.OnClickListener() {
//                        @Override
//                        @TargetApi(Build.VERSION_CODES.M)
//                        public void onClick(View v) {
//                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//                        }
//                    });
//        } else {
//            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//        }
//        return false;
//    }

    /**
     * Callback received when a permissions request has been completed.
     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_READ_CONTACTS) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                populateAutoComplete();
//            }
//        }
//    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        return new CursorLoader(this,
//                // Retrieve data rows for the device user's 'profile' contact.
//                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
//                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
//
//                // Select only email addresses.
//                ContactsContract.Contacts.Data.MIMETYPE +
//                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
//                .CONTENT_ITEM_TYPE},
//
//                // Show primary email addresses first. Note that there won't be
//                // a primary email address if the user hasn't specified one.
//                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//        List<String> emails = new ArrayList<>();
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            emails.add(cursor.getString(ProfileQuery.ADDRESS));
//            cursor.moveToNext();
//        }
//
//        addEmailsToAutoComplete(emails);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> cursorLoader) {
//
//    }
//
//    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
//        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
//        ArrayAdapter<String> adapter =
//                new ArrayAdapter<>(LoginActivity.this,
//                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
//
//        emailEditText.setAdapter(adapter);
//    }
//
//
//    private interface ProfileQuery {
//        String[] PROJECTION = {
//                ContactsContract.CommonDataKinds.Email.ADDRESS,
//                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
//        };
//
//        int ADDRESS = 0;
//        int IS_PRIMARY = 1;
//    }
}

