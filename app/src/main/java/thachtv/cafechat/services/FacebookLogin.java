package thachtv.cafechat.services;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;
import java.util.HashMap;

import thachtv.cafechat.base.BaseFireBase;
import thachtv.cafechat.define.Constant;
import thachtv.cafechat.interfaces.LoginFacebookListener;

/**
 * Created by Thinkpad on 10/04/2017.
 */

public class FacebookLogin extends BaseFireBase {

    private static String TAG = FacebookLogin.class.getSimpleName();

    private DatabaseReference mDatabaseReference;
    private FirebaseUser firebaseUser;
    private Activity activity;

    String email;

    public FacebookLogin(Activity activity) {
        this.activity = activity;
        mDatabaseReference = getDatabaseReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void loginFacebook(CallbackManager callbackManager, final Activity activity, final FirebaseAuth mAuth, final LoginFacebookListener listener) {
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(mAuth, loginResult.getAccessToken(), activity);

                if (null != firebaseUser) {
                    HashMap<String, String> userMap = new HashMap<String, String>();
                    userMap.put("user_name", firebaseUser.getDisplayName());
                    userMap.put("image", String.valueOf(firebaseUser.getPhotoUrl()));
                    userMap.put("email", firebaseUser.getEmail());
                    createUsers(userMap);
                }

                /*GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("JSON", response.getJSONObject().toString());
                        try {
                            email = response.getJSONObject().get("email").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();*/

                listener.loginFacebookSuccess();
            }

            @Override
            public void onCancel() {
                Toast.makeText(activity, "Login Cancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(activity, "Login Error", Toast.LENGTH_LONG).show();
                Log.d(TAG, error.toString());
                listener.loginFacebookFailure();
            }
        });
    }

    private void handleFacebookAccessToken(FirebaseAuth mAuth, AccessToken accessToken, final Activity activity) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithCredential:success");
                }else {
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(activity, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createUsers(HashMap userMap) {
        mDatabaseReference.child(Constant.FirebaseDatabase.USERS).child(firebaseUser.getUid()).setValue(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccsess: user create");
            }
        });
    }
}
