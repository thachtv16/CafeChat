package thachtv.cafechat.application;

import android.app.Application;
import android.content.Context;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import thachtv.cafechat.R;
import thachtv.cafechat.define.Constant;
import thachtv.cafechat.ultility.GlideApp;

/**
 * Created by Thinkpad on 10/21/2017.
 */

public class CafeChat extends Application {

    private DatabaseReference usersDatabase;
    private FirebaseAuth mAuth;
    private static CafeChat mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mAuth = FirebaseAuth.getInstance();
        if (null != usersDatabase) {
            usersDatabase = FirebaseDatabase.getInstance().getReference().child(Constant.FirebaseDatabase.USERS).child(mAuth.getCurrentUser().getUid());
            usersDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        usersDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public static CafeChat getInstance() {
        return mInstance;
    }

    public void loadImages(Context context, String linkAvatar, ImageView ivAvatar) {
        GlideApp.with(context).load(linkAvatar).placeholder(R.drawable.person_image).into(ivAvatar);
    }

    public boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return true;
        } else {
            etText.requestFocus();
            etText.setError("Vui lòng điền thông tin!");
            return false;
        }
    }

    public boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "[a-zA-Z0-9._-]+@[a-z]+(\\.+[a-z]+)+";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}
