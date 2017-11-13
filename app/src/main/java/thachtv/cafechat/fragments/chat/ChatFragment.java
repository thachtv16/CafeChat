package thachtv.cafechat.fragments.chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import thachtv.cafechat.R;
import thachtv.cafechat.adapter.ChatFriendAdapter;
import thachtv.cafechat.base.BaseFragment;
import thachtv.cafechat.define.Constant;
import thachtv.cafechat.model.ChatFriend;

public class ChatFragment extends BaseFragment {

    private DatabaseReference roorRef;
    private FirebaseAuth mAuth;

    private RecyclerView rvChatFriend;
    private ChatFriendAdapter chatFriendAdapter;
    private ArrayList<ChatFriend> chatFriendArrayList;

    @NonNull
    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        roorRef = FirebaseDatabase.getInstance().getReference();

        /*chatFriendArrayList = new ArrayList<ChatFriend>();
        chatFriendAdapter = new ChatFriendAdapter(getContext(), chatFriendArrayList);
        rvChatFriend.setAdapter(chatFriendAdapter);*/

        getDataFromFirebase();
    }

    private void getDataFromFirebase() {
        roorRef.child(Constant.FirebaseDatabase.MESSAGES).child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String uid = dataSnapshot.getKey();
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
}
