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

    public static final String TAG_SEARCH_USER_FRAGMENT = SearchUserFragment.class.getSimpleName();

    private DatabaseReference userReference;

    private RecyclerView rvAllUsers;
    private TextView tvTitleAllUser;
    private ImageView ivLeftAllUser;

    private AllUserAdapter allUserAdapter;
    private ArrayList<Users> usersArrayList;

    public static SearchUserFragment newInstance() {
        SearchUserFragment searchUserFragment = new SearchUserFragment();
        return searchUserFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_user, container, false);
        rvAllUsers = rootView.findViewById(R.id.rv_all_user);
        usersArrayList = new ArrayList<>();
        allUserAdapter = new AllUserAdapter(getContext(), usersArrayList);
        rvAllUsers.setAdapter(allUserAdapter);
        allUserAdapter.setListener(this);
        tvTitleAllUser = rootView.findViewById(R.id.tv_title_extra);
        ivLeftAllUser = rootView.findViewById(R.id.iv_left_extra);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userReference = FirebaseDatabase.getInstance().getReference();

        showActionBar();
        getDataFromFirebaseAndLoadMore();
    }

    private void showActionBar() {
        tvTitleAllUser.setText(R.string.find_friend);
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

        userReference.child(Constant.FirebaseDatabase.USERS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String userName = dataSnapshot.child(Constant.FirebaseDatabase.USER_NAME).getValue().toString();
                String linkImageAvatar = dataSnapshot.child(Constant.FirebaseDatabase.LINK_AVATAR).getValue().toString();
                String uid = dataSnapshot.getKey();
                Log.d(TAG_SEARCH_USER_FRAGMENT, uid);
                Users users = new Users(userName, linkImageAvatar, uid);
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
        bundle.putString(Constant.FirebaseDatabase.UID, uid);
        profileFragment.setArguments(bundle);
        replaceFragmentFromFragment(profileFragment, getActivity().getSupportFragmentManager());
    }

}
