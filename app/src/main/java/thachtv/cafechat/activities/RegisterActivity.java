package thachtv.cafechat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rengwuxian.materialedittext.MaterialEditText;

import thachtv.cafechat.R;
import thachtv.cafechat.application.CafeChat;
import thachtv.cafechat.interfaces.RegisterListener;
import thachtv.cafechat.services.RegisterServices;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private RegisterServices registerServices;

    private MaterialEditText metRegisterUserName;
    private MaterialEditText metRegisterPassword;
    private MaterialEditText metRegisterPasswordAgain;
    private MaterialEditText metRegisterEmail;

    private Button btnRegister;

    private ImageView ivBackLeft;
    private TextView tvTitle;
    private ProgressBar pbRegister;

    private String userName;
    private String email;
    private String password;
    private String passwordAgain;

    private CafeChat cafeChat = CafeChat.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        initUI();
    }

    private void initUI() {
        registerServices = new RegisterServices(this);
        metRegisterUserName = (MaterialEditText) findViewById(R.id.met_register_user_name);
        metRegisterEmail = (MaterialEditText) findViewById(R.id.met_register_email);
        metRegisterPassword = (MaterialEditText) findViewById(R.id.met_register_password);
        metRegisterPasswordAgain = (MaterialEditText) findViewById(R.id.met_register_password_again);
        tvTitle = (TextView) findViewById(R.id.tv_title_extra);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);
        ivBackLeft = (ImageView) findViewById(R.id.iv_left_extra);
        ivBackLeft.setOnClickListener(this);
        tvTitle.setText("Sign In");
        pbRegister = (ProgressBar) findViewById(R.id.pb_content_register);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left_extra:
                finish();
                break;
            case R.id.btn_register:
                if (checkInputData()) {
                    pbRegister.setVisibility(View.VISIBLE);
                    registerServices.registerAcount(userName, email, password, new RegisterListener() {
                        @Override
                        public void registerSuccess() {
                            pbRegister.setVisibility(View.INVISIBLE);
                            Intent registerIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            registerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(registerIntent);
                        }

                        @Override
                        public void registerFailure() {
                            pbRegister.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this, "Register Fail", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
    }

    public boolean checkInputData() {
        if (cafeChat.isEmpty(metRegisterUserName) && cafeChat.isEmpty(metRegisterEmail) && cafeChat.isEmpty(metRegisterPassword) && cafeChat.isEmpty(metRegisterPasswordAgain)) {
            userName = metRegisterUserName.getText().toString().trim();
            email = metRegisterEmail.getText().toString().trim();
            password = metRegisterPassword.getText().toString().trim();
            passwordAgain = metRegisterPasswordAgain.getText().toString().trim();
            if (!cafeChat.isEmailValid(email)) {
                metRegisterEmail.requestFocus();
                metRegisterEmail.setError(getResources().getString(R.string.email_error));
                return false;
            }else if (password.length() < 6) {
                metRegisterPassword.requestFocus();
                metRegisterPassword.setError(getResources().getString(R.string.pass_error));
                return false;
            }else {
                if (!passwordAgain.equals(password)) {
                    metRegisterPasswordAgain.requestFocus();
                    metRegisterPasswordAgain.setError(getResources().getString(R.string.pass_again_error));
                    return false;
                }
            }
            return true;
        }else {
            return false;
        }
    }
}
