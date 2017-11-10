package thachtv.cafechat.services;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import thachtv.cafechat.base.BaseFireBase;
import thachtv.cafechat.define.Constant;
import thachtv.cafechat.interfaces.LoginGoogleListener;

/**
 * Created by Thinkpad on 10/04/2017.
 */

public class GoogleLogin extends BaseFireBase {

    public static final String TAG = GoogleLogin.class.getSimpleName();

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser firebaseUser;
    private Activity activity;

    public GoogleLogin(Activity activity){
        this.activity = activity;
        mAuth = getFirebaseAuth();
        mDatabaseReference = getDatabaseReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount account, final Activity activity, final LoginGoogleListener listener) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            /*String email = firebaseUser.getEmail();
                            Log.d(TAG,"email=>" + email);*/
                            Log.d(TAG, "signInWithCredential:success");
                            HashMap<String, String> userMap = new HashMap<String, String>();
                            userMap.put("user_name", firebaseUser.getDisplayName());
                            userMap.put("image", String.valueOf(firebaseUser.getPhotoUrl()));
                            userMap.put("email", firebaseUser.getEmail());
                            createUsers(userMap);
                            listener.loginGoogleSuccess();
                        } else {
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
