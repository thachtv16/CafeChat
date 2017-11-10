package thachtv.cafechat.services;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import thachtv.cafechat.base.BaseFireBase;
import thachtv.cafechat.define.Constant;
import thachtv.cafechat.interfaces.RegisterListener;

/**
 * Created by Thinkpad on 10/04/2017.
 */

public class RegisterServices extends BaseFireBase {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private Activity activity;

    public RegisterServices(Activity activity) {
        this.activity = activity;
        mAuth = getFirebaseAuth();
        mDatabase = getDatabaseReference();
        firebaseUser = mAuth.getCurrentUser();
    }

    public void registerAcount(final String userName, final String email, final String password, final RegisterListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Them thong tin user vao db
                    final String uid = task.getResult().getUser().getUid();
                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put(Constant.FirebaseDatabase.ONLINE, String.valueOf(false));
                    userMap.put(Constant.FirebaseDatabase.GENDER, "Other");
                    userMap.put(Constant.FirebaseDatabase.PHONE, "Nothing");
                    userMap.put(Constant.FirebaseDatabase.USER_NAME, userName);
                    userMap.put(Constant.FirebaseDatabase.LINK_AVATAR, "Default");
                    userMap.put(Constant.FirebaseDatabase.PASSWORD, password);
                    userMap.put(Constant.FirebaseDatabase.EMAIL, email);
                    createAcountInDatabase(userMap, uid, new RegisterListener() {
                        @Override
                        public void registerSuccess() {
//                                            mAuth.signOut();// Dang xuat
                            Toast.makeText(activity, "Register Successful", Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPreferences = activity.getSharedPreferences(Constant.SharePref.CAFE_CHAT_PREF, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constant.SharePref.UID, uid);
                            editor.putBoolean(Constant.SharePref.IS_LOGGED_BEFORE, true);
                            editor.commit();
                            listener.registerSuccess();
                        }

                        @Override
                        public void registerFailure() {
                            listener.registerFailure();
                        }
                    });
                }
            }
        });
    }

    // Luu thong tin users
    private void createAcountInDatabase(HashMap userMap, final String uId, final RegisterListener listener) {
        mDatabase.child(Constant.FirebaseDatabase.USERS)
                .child(uId)
                .setValue(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.registerSuccess();
                        Log.d("RegisterService", "onSuccess=>" + uId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("RegisterService", "onFailure=>" + e.getMessage());
                        listener.registerFailure();
                    }
                });
    }
}
