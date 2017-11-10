package thachtv.cafechat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

import thachtv.cafechat.R;
import thachtv.cafechat.application.CafeChat;
import thachtv.cafechat.interfaces.LoginFacebookListener;
import thachtv.cafechat.interfaces.LoginGoogleListener;
import thachtv.cafechat.interfaces.LoginListener;
import thachtv.cafechat.services.FacebookLogin;
import thachtv.cafechat.services.GoogleLogin;
import thachtv.cafechat.services.LoginServices;
import thachtv.cafechat.ultility.GetKeyHashFacebook;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = LogInActivity.class.getName();
    private static final int RC_SIGN_IN = 2;

    private FirebaseAuth mAuth;
    private LoginServices loginServices;
    private FacebookLogin facebookLogin;
    private GoogleLogin googleLogin;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;

    private Button btnLogInFacebook;
    private Button btnLoginGoogle;
    private Button btnLogInEmail;
    private TextView tvSignInEmail;
    private TextView tvForgotPassword;
    private ProgressBar pbContent;

    private MaterialEditText metEmail;
    private MaterialEditText metPassword;

    private String email;
    private String password;

    private CafeChat cafeChat = CafeChat.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mAuth = FirebaseAuth.getInstance();

        GetKeyHashFacebook.getKeyHash(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LogInActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        initUI();
    }

    private void initUI() {
        callbackManager = CallbackManager.Factory.create();
        facebookLogin = new FacebookLogin(this);
        googleLogin = new GoogleLogin(this);
        loginServices = new LoginServices(this);
        btnLogInFacebook = (Button) findViewById(R.id.btn_log_in_facebook);
        btnLogInFacebook.setOnClickListener(this);
        btnLoginGoogle = (Button) findViewById(R.id.btn_log_in_google);
        btnLoginGoogle.setOnClickListener(this);
        btnLogInEmail = (Button) findViewById(R.id.btn_log_in_email);
        btnLogInEmail.setOnClickListener(this);
        tvSignInEmail = (TextView) findViewById(R.id.tv_sign_in_email);
        tvSignInEmail.setOnClickListener(this);
        tvForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);
        pbContent = (ProgressBar) findViewById(R.id.pb_content);

        metEmail = (MaterialEditText) findViewById(R.id.met_log_in_email);
        metPassword = (MaterialEditText) findViewById(R.id.met_log_in_password);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_log_in_facebook:
                facebookLogin.loginFacebook(callbackManager, this, mAuth, new LoginFacebookListener() {
                    @Override
                    public void loginFacebookSuccess() {
                        startActivity(new Intent(LogInActivity.this, MainActivity.class));
                    }

                    @Override
                    public void loginFacebookFailure() {
                        Toast.makeText(LogInActivity.this, "Login Fail", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.btn_log_in_google:
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(intent, RC_SIGN_IN);
                break;
            case R.id.btn_log_in_email:
                pbContent.setVisibility(View.VISIBLE);
                if (checkInputData()) {
                    loginServices.loginAccountEmail(email, password, new LoginListener() {
                        @Override
                        public void loginSuccess() {
                            pbContent.setVisibility(View.INVISIBLE);
                            Intent loginIntent = new Intent(LogInActivity.this, MainActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginIntent);
                            finish();
                            Toast.makeText(LogInActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void loginFailure() {
                            pbContent.setVisibility(View.INVISIBLE);
                            Toast.makeText(LogInActivity.this, "Login Fail", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case R.id.tv_sign_in_email:
                startActivity(new Intent(LogInActivity.this, RegisterActivity.class));
                break;
            case R.id.tv_forgot_password:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                Log.d(TAG, "email=>" +account.getEmail());
                googleLogin.firebaseAuthWithGoogle(account, this, new LoginGoogleListener() {
                    @Override
                    public void loginGoogleSuccess() {
                        startActivity(new Intent(LogInActivity.this, MainActivity.class));
                    }
                });
            } else {
                Toast.makeText(this, "Auth went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkInputData() {
        if (cafeChat.isEmpty(metEmail) && cafeChat.isEmpty(metPassword)) {
            email = metEmail.getText().toString().trim();
            password = metPassword.getText().toString().trim();
            if (!cafeChat.isEmailValid(email)) {
                metEmail.requestFocus();
                metEmail.setError(getResources().getString(R.string.email_error));
                return false;
            } else {
                if (password.length() < 6) {
                    metPassword.requestFocus();
                    metPassword.setError(getResources().getString(R.string.pass_error));
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
