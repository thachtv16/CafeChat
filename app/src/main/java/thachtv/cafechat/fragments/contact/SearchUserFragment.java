package thachtv.cafechat.fragments.contact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import thachtv.cafechat.R;
import thachtv.cafechat.adapter.AllUserAdapter;
import thachtv.cafechat.base.BaseFragment;
import thachtv.cafechat.define.Constant;
import thachtv.cafechat.interfaces.NextProfileFragmentListener;
import thachtv.cafechat.model.Users;

/**
 * Created by Thinkpad on 10/16/2017.
 */

public class SearchUserFragment extends BaseFragment implements NextProfileFragmentListener {

    private FirebaseUser firebaseUser;
    private DatabaseReference mDataAllUser;

    private RecyclerView rvAllUsers;
    private TextView tvTitleAllUser;
    private ImageView ivLeftAllUser;

    private AllUserAdapter allUserAdapter;
    private ArrayList<Users> usersArrayList;

    public static SearchUserFragment newInstance() {
        SearchUserFragment searchUserFragment = new SearchUserFragment();
        return searchUserFragment;
    }

    /*@Override
    public int getLayoutResId() {
        return R.layout.fragment_search_user;
    }

    @Override
    public void inOnCreateView(View rootView, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDataAllUser = FirebaseDatabase.getInstance().getReference();

        initUI(rootView);
        showActionBar();
        getDataFromFirebaseAndLoadMore();
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_user, container, false);
        rvAllUsers = (RecyclerView) rootView.findViewById(R.id.rv_all_user);
        usersArrayList = new ArrayList<Users>();
        allUserAdapter = new AllUserAdapter(getContext(), usersArrayList);
        rvAllUsers.setAdapter(allUserAdapter);
        allUserAdapter.setListener(this);
        tvTitleAllUser = (TextView) rootView.findViewById(R.id.tv_title_extra);
        ivLeftAllUser = (ImageView) rootView.findViewById(R.id.iv_left_extra);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDataAllUser = FirebaseDatabase.getInstance().getReference();

        showActionBar();
        getDataFromFirebaseAndLoadMore();
    }

    private void showActionBar() {
        tvTitleAllUser.setText("All User");
        ivLeftAllUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void getDataFromFirebaseAndLoadMore() {
        /*mDataAllUser.child(Constant.FirebaseDatabase.USERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postData : dataSnapshot.getChildren()) {
                    *//*String uid = postData.getValue().toString();
                    Log.d("SearchUserFragment", uid);*//*
                    String userName = (String) postData.child("user_name").getValue();
                    String linkImageAvatar = (String) postData.child("link_avatar").getValue();
                    Users users = new Users(userName, linkImageAvatar);
                    Log.d("SearchUserFragment", users.getUserName());
                    Log.d("SearchUserFragment", users.getImageAvatar());
                    usersArrayList.add(users);
                    uid = (String) postData.child("uid").getValue();
                    Log.d("ProfileFragment", uid);
                }
                allUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        mDataAllUser.child(Constant.FirebaseDatabase.USERS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String userName = dataSnapshot.child("user_name").getValue().toString();
                String linkImageAvatar = dataSnapshot.child("link_avatar").getValue().toString();
                String uid = dataSnapshot.child("uid").getValue().toString();
                Log.d("ProfileFragment", uid);
                Log.d("ProfileFragment", userName);
                Users users = new Users(userName, linkImageAvatar);
                users.setUid(uid);
                usersArrayList.add(users);
                allUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void nextAction(String uid) {
        ProfileFragment profileFragment = ProfileFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);
        profileFragment.setArguments(bundle);
        replaceFragmentFromFragment(profileFragment, getActivity().getSupportFragmentManager());
    }

}
