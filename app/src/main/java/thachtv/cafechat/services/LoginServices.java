package thachtv.cafechat.services;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import thachtv.cafechat.base.BaseFireBase;
import thachtv.cafechat.define.Constant;
import thachtv.cafechat.interfaces.LoginListener;

/**
 * Created by Thinkpad on 10/04/2017.
 */

public class LoginServices extends BaseFireBase {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private Activity activity;

    public LoginServices(Activity activity) {
        this.activity = activity;
        mAuth = getFirebaseAuth();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        mDatabase = getDatabaseReference().child(Constant.FirebaseDatabase.USERS).child(firebaseUser.getUid());
    }

    public void loginAccountEmail(String email, String passWord, final LoginListener listener) {
        mAuth.signInWithEmailAndPassword(email, passWord)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.loginFailure();
                        Log.d("LoginService", e.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d("LoginService","email=" + authResult.getUser().getEmail());
                SharedPreferences sharedPreferences = activity.getSharedPreferences(Constant.SharePref.CAFE_CHAT_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constant.SharePref.UID, authResult.getUser().getUid());
                editor.putBoolean(Constant.SharePref.IS_LOGGED_BEFORE, true);
                editor.commit();
                listener.loginSuccess();
            }
        });
    }
}
