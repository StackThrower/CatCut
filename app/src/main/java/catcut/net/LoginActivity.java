package catcut.net;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import catcut.net.helpers.NetworkExceptionUI;
import catcut.net.listener.KeyboardEnterListener;
import catcut.net.network.AuthNetworkImpl;
import catcut.net.network.entity.Token;
import catcut.net.tasks.AccountTask;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {

    private AccountTask mAuthTask = null;
    private AutoCompleteTextView mEmailView;
    private View mProgressView;
    private View mPinView;
    private TextView createLoginStatus;
    private TextView mToggleAction;
    private TextView completePinLabel;
    private Button mEmailSignInButton;
    private EditText dig1;
    private EditText dig2;
    private EditText dig3;
    private EditText dig4;
    private boolean createNewAccount;
    private short verifyPinAttempt;
    public static short MAX_ATTEMPT_COUNT = 5;

    final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mEmailView = findViewById(R.id.email);
        mEmailView.setOnKeyListener(new KeyboardEnterListener(LoginActivity.this));
        mEmailView.setOnFocusChangeListener(new KeyboardEnterListener(LoginActivity.this));

        mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        mToggleAction = findViewById(R.id.toggle_create_or_login_label);
        mToggleAction.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!createNewAccount) {
                            mToggleAction.setText(R.string.action_log_in);
                            setTitle(R.string.title_activity_login_create);
                            mEmailSignInButton.setText(R.string.action_create_new_account);
                            mEmailSignInButton.setBackgroundResource(R.color.colorAccent);
                        } else {
                            mToggleAction.setText(R.string.action_create_new_account);
                            setTitle(R.string.title_activity_login_login);
                            mEmailSignInButton.setText(R.string.action_log_in);
                            mEmailSignInButton.setBackgroundResource(R.color.colorPrimary);
                        }
                        createNewAccount = !createNewAccount;
                    }
                }
        );

        dig1 = findViewById(R.id.dig1);
        dig2 = findViewById(R.id.dig2);
        dig3 = findViewById(R.id.dig3);
        dig4 = findViewById(R.id.dig4);

        dig1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!dig1.getText().toString().isEmpty())
                    dig2.requestFocus();

                if (isReadyPinForm())
                    attemptPin();
            }
        });
        dig2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!dig2.getText().toString().isEmpty())
                    dig3.requestFocus();

                if (isReadyPinForm())
                    attemptPin();
            }
        });
        dig3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!dig3.getText().toString().isEmpty())
                    dig4.requestFocus();

                if (isReadyPinForm())
                    attemptPin();
            }
        });
        dig4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isReadyPinForm())
                    attemptPin();
            }
        });

        mProgressView = findViewById(R.id.login_progress);
        mPinView = findViewById(R.id.pin_main_form);
        createLoginStatus = findViewById(R.id.create_or_login_stats_label);
        completePinLabel = findViewById(R.id.complete_pin_label_id);

        initRemoteConfig();


    }

    private synchronized void initRemoteConfig() {

        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {

                            String versionKey = BuildConfig.VERSION_NAME.replaceAll("\\.", "_");

                            setStateForFirebase((mFirebaseRemoteConfig.getBoolean(String.format("v%s_stat_enabled", versionKey))));

                        } else {
                            Toast.makeText(LoginActivity.this, "Configuration has been not retrieved!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void setStateForFirebase(boolean enabled) {

        FirebaseAnalytics mFirebaseAnalytics =
                FirebaseAnalytics.getInstance(LoginActivity.this);

        mFirebaseAnalytics.setAnalyticsCollectionEnabled(enabled);
    }


    private boolean isReadyPinForm() {
        return
                !dig1.getText().toString().isEmpty() &&
                        !dig2.getText().toString().isEmpty() &&
                        !dig3.getText().toString().isEmpty() &&
                        !dig4.getText().toString().isEmpty();
    }


    @Override
    public void onBackPressed() {
        // Blocking of Back button
    }

    private void attemptPin() {
        verifyPinAttempt -= 1;
        showProgress(true);
        mPinView.setVisibility(View.GONE);

        UserInfo userInfo = new UserInfo();
        userInfo.code = dig1.getText().toString() +
                dig2.getText().toString() +
                dig3.getText().toString() +
                dig4.getText().toString();
        userInfo.email = mEmailView.getText().toString();

        mAuthTask = new AccountTask(
                userInfo,
                AccountTaskMode.OBTAIN_TOKEN);
        mAuthTask.execute((Void) null);
    }

    /**
     * Attempts to sign in or register the account specified by the getPin form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual getPin attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null && mEmailView != null &&
                mEmailView.getText() != null) {
            return;
        }

        mEmailView.setError(null);


        String email = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        } else {
            mEmailSignInButton.setVisibility(View.GONE);
            mEmailView.setVisibility(View.GONE);
            mToggleAction.setVisibility(View.GONE);
            createLoginStatus.setVisibility(View.GONE);
        }

        if (cancel) {
            // There was an error; don't attempt getPin and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);

            UserInfo userInfo = new UserInfo();
            userInfo.email = mEmailView.getText().toString();

            verifyPinAttempt = LoginActivity.MAX_ATTEMPT_COUNT;
            mAuthTask = new AccountTask(userInfo, createNewAccount ?
                    AccountTaskMode.CREATE_ACCOUNT : AccountTaskMode.OBTAIN_PIN);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    /**
     * Shows the progress UI and hides the getPin form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }


    enum AccountTaskMode {
        CREATE_ACCOUNT,
        OBTAIN_PIN,
        OBTAIN_TOKEN
    }

    public class UserInfo {
        public String email;
        public String code;

        UserInfo() {
            email = code = "";
        }
    }

    /**
     * Represents an asynchronous getPin/registration task used to authenticate
     * the user.
     */
    public class AccountTask extends AsyncTask<Void, Void, Boolean> {
        AccountTaskMode mode;
        private final UserInfo userInfo;

        AccountTask(UserInfo userInfo, AccountTaskMode mode) {
            this.userInfo = userInfo;
            this.mode = mode;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            AuthNetworkImpl network = new AuthNetworkImpl();
            try {
                switch (mode) {
                    case CREATE_ACCOUNT: {

                        Bundle bundle = new Bundle();
                        bundle.putString("email", userInfo.email);
                        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(LoginActivity.this);
                        mFirebaseAnalytics.logEvent("create_referal", bundle);


                        return network.createNewAccount(userInfo.email);
                    }
                    case OBTAIN_PIN: {
                        return network.getPin(userInfo.email);
                    }
                    case OBTAIN_TOKEN: {
                        Token token = network.getToken(userInfo.email, userInfo.code);

                        SharedPreferences mPrefs = getSharedPreferences("label", 0);
                        SharedPreferences.Editor mEditor = mPrefs.edit();
                        mEditor
                                .putString("token", token.token)
                                .putString("email", userInfo.email)
                                .putString("token_id", token.token_id).apply();

                        return token != null && token.token != null && !token.token.isEmpty();
                    }
                }
            } catch (Exception e) {
                NetworkExceptionUI.showMessageNoInternetConnection(LoginActivity.this, e.getMessage());
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            String verifyPinLabel = String.format(getString(R.string.title_complete_sign_up_by_verify_email), verifyPinAttempt);

            switch (mode) {
                case OBTAIN_PIN: {

                    completePinLabel.setText(verifyPinLabel);
                    mPinView.setVisibility(View.VISIBLE);
                    mEmailView.setVisibility(View.VISIBLE);
                    mEmailView.setEnabled(false);
                    break;
                }
                case OBTAIN_TOKEN: {
                    if (verifyPinAttempt > 0 && !success) {
                        mPinView.setVisibility(View.VISIBLE);
                        mEmailView.setVisibility(View.VISIBLE);
                        completePinLabel.setText(verifyPinLabel);
                        dig1.setText("");
                        dig2.setText("");
                        dig3.setText("");
                        dig4.setText("");
                    } else {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    break;
                }
                case CREATE_ACCOUNT: {

                    setTitle(R.string.title_activity_login_login);
                    mEmailSignInButton.setText(R.string.action_log_in);
                    mEmailSignInButton.setVisibility(View.VISIBLE);
                    mEmailSignInButton.setBackgroundResource(R.color.colorPrimary);
                    mEmailView.setVisibility(View.VISIBLE);

                    createLoginStatus.setVisibility(View.VISIBLE);
                    createLoginStatus.setText(getString(R.string.action_successful_new_account));
                    createNewAccount = false;
                    break;
                }
            }

        }

        @Override
        protected void onCancelled(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

