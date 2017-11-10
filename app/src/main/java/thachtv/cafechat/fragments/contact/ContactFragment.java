package thachtv.cafechat.fragments.contact;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import thachtv.cafechat.R;
import thachtv.cafechat.adapter.ContactFriendsAdapter;
import thachtv.cafechat.base.BaseFragment;
import thachtv.cafechat.define.Constant;
import thachtv.cafechat.fragments.chat.ChatDetailFragment;
import thachtv.cafechat.interfaces.NextChooseListener;
import thachtv.cafechat.model.ContactFriends;

/**
 * Created by Thinkpad on 09/30/2017.
 */

public class ContactFragment extends BaseFragment implements NextChooseListener {

    private DatabaseReference friendDatabase;
    private DatabaseReference userDatabase;
    private FirebaseAuth mAuth;

    private RecyclerView rvContactFriend;
    private ContactFriendsAdapter friendsAdapter;
    private ArrayList<ContactFriends> friendsArrayList;

    private String uid;

    public static ContactFragment newInstance() {
        ContactFragment contactFragment = new ContactFragment();
        return contactFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        rvContactFriend = (RecyclerView) rootView.findViewById(R.id.rv_contact_friend);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        friendDatabase = FirebaseDatabase.getInstance().getReference();
        userDatabase = FirebaseDatabase.getInstance().getReference().child(Constant.FirebaseDatabase.USERS);

        friendsArrayList = new ArrayList<ContactFriends>();
        friendsAdapter = new ContactFriendsAdapter(getContext(), friendsArrayList);
        rvContactFriend.setAdapter(friendsAdapter);
        friendsAdapter.setListener(this);

        getDataFromFirebase();
    }

    private void getDataFromFirebase() {
        friendDatabase.child(Constant.FirebaseDatabase.FRIENDS).child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String dateTime = dataSnapshot.child("date_time").getValue().toString();
                uid = dataSnapshot.child("uid").getValue().toString();
                Log.d("ContactFragment", uid);
                final ContactFriends contactFriends = new ContactFriends();
                contactFriends.setUidContact(uid);
                userDatabase.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.child("user_name").getValue().toString();
                        String linkAvatar = dataSnapshot.child("link_avatar").getValue().toString();
                        String dotOnline = dataSnapshot.child("online").getValue().toString();
                        //ContactFriends contactFriends = new ContactFriends(userName, linkAvatar, dateTime, dotOnline);
                        contactFriends.setUserNameContact(userName);
                        contactFriends.setDateTimeContact(dateTime);
                        contactFriends.setImageAvatarContact(linkAvatar);
                        contactFriends.setDotOnline(dotOnline);
                        Log.d("ContactFragment===", contactFriends.getUidContact());
                        friendsArrayList.add(contactFriends);
                        friendsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
    public void nextChoose(final String uid) {
        CharSequence options[] = new CharSequence[]{"Open Profile", "Send Message"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        ProfileFragment profileFragment = ProfileFragment.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("uid", uid);
                        Log.d("ContactFragment", uid);
                        profileFragment.setArguments(bundle);
                        replaceFragmentFromFragment(profileFragment, getActivity().getSupportFragmentManager());
                        break;
                    case 1:
                        ChatDetailFragment chatDetailFragment = ChatDetailFragment.newInstance();
                        Bundle chatDetailBundle = new Bundle();
                        chatDetailBundle.putString("uid", uid);
                        chatDetailFragment.setArguments(chatDetailBundle);
                        replaceFragmentFromFragment(chatDetailFragment, getActivity().getSupportFragmentManager());
                        break;
                }
            }
        });
        builder.create().show();
    }

}
