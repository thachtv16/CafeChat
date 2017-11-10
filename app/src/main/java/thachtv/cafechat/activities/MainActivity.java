package thachtv.cafechat.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import thachtv.cafechat.R;
import thachtv.cafechat.base.BaseFragment;
import thachtv.cafechat.define.Constant;
import thachtv.cafechat.fragments.ViewPagerFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BaseFragment.getInstance().replaceFragmentFromActivity(ViewPagerFragment.newInstance(), getSupportFragmentManager());
        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference().child(Constant.FirebaseDatabase.USERS).child(mAuth.getCurrentUser().getUid());
    }

    @Override
    protected void onStart() {
        super.onStart();
        userDatabase.child(Constant.FirebaseDatabase.ONLINE).setValue(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        userDatabase.child(Constant.FirebaseDatabase.ONLINE).setValue(ServerValue.TIMESTAMP);
    }
}
