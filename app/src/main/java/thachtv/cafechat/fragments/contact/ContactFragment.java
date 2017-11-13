package thachtv.cafechat.fragments.contact;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class ContactFragment extends BaseFragment implements NextChooseListener {

    public static final String TAG_CONTACT_FRAGMENT = ContactFriends.class.getSimpleName();

    private DatabaseReference rootReference;

    private RecyclerView rvContactFriend;
    private ContactFriendsAdapter friendsAdapter;
    private ArrayList<ContactFriends> friendsArrayList;

    private String firstUid;
    private String secondUid;

    @NonNull
    public static ContactFragment newInstance() {
        return new ContactFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        rvContactFriend = rootView.findViewById(R.id.rv_contact_friend);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showFirebase();
        friendsArrayList = new ArrayList<>();
        friendsAdapter = new ContactFriendsAdapter(getContext(), friendsArrayList);
        rvContactFriend.setAdapter(friendsAdapter);
        friendsAdapter.setListener(this);

        getDataFromFirebase();
    }

    private void showFirebase() {
        rootReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (null != currentUser) {
            firstUid = currentUser.getUid();
        }
    }

    private void getDataFromFirebase() {
        rootReference.child(Constant.FirebaseDatabase.FRIENDS).child(firstUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                secondUid = dataSnapshot.getKey();
                final String dateTime = dataSnapshot.child(Constant.FirebaseDatabase.DATE_TIME).getValue().toString();
                Log.d(TAG_CONTACT_FRAGMENT, secondUid);
                rootReference.child(Constant.FirebaseDatabase.USERS).child(secondUid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.child(Constant.FirebaseDatabase.USER_NAME).getValue().toString();
                        String linkAvatar = dataSnapshot.child(Constant.FirebaseDatabase.LINK_AVATAR).getValue().toString();
                        String dotOnline = dataSnapshot.child(Constant.FirebaseDatabase.ONLINE).getValue().toString();
                        ContactFriends contactFriends = new ContactFriends(userName, linkAvatar, secondUid,dateTime, dotOnline);
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
                        Log.d(TAG_CONTACT_FRAGMENT, uid);
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
