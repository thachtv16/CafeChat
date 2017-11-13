package thachtv.cafechat.fragments.contact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import thachtv.cafechat.R;
import thachtv.cafechat.application.CafeChat;
import thachtv.cafechat.base.BaseFragment;
import thachtv.cafechat.define.Constant;

public class ProfileFragment extends BaseFragment implements View.OnClickListener {

    private DatabaseReference rootReference;

    private TextView tvTitleProfile;
    private ImageView ivLeftBackProfile;

    private CircleImageView civImageProfile;
    private TextView tvUserNameProfile;
    private TextView tvEmailProfile;
    private TextView tvPhoneProfile;
    private TextView tvGenderProfile;
    private Button btnSendRequestFriend;
    private Button btnDeclineFriendRequest;

    private String secondUid;
    private String firstUid;
    private String current_state;

    private CafeChat cafeChat = CafeChat.getInstance();

    @NonNull
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        tvTitleProfile = rootView.findViewById(R.id.tv_title_extra);
        ivLeftBackProfile = rootView.findViewById(R.id.iv_left_extra);
        civImageProfile = rootView.findViewById(R.id.civ_image_avatar_profile);
        tvUserNameProfile = rootView.findViewById(R.id.tv_user_name_profile);
        tvEmailProfile = rootView.findViewById(R.id.tv_email_profile);
        tvPhoneProfile = rootView.findViewById(R.id.tv_phone_profile);
        tvGenderProfile = rootView.findViewById(R.id.tv_gender_profile);
        btnSendRequestFriend = rootView.findViewById(R.id.btn_send_friend_request);
        btnSendRequestFriend.setOnClickListener(this);
        btnDeclineFriendRequest = rootView.findViewById(R.id.btn_decline_friend_request);
        btnDeclineFriendRequest.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showToolBar();
        getUidFromSearchUserFragment();
        showFirebase();
        showProfile();
    }

    private void showToolBar() {
        tvTitleProfile.setText(R.string.profile);
        ivLeftBackProfile.setOnClickListener(this);
    }

    private void showFirebase() {
        rootReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (null != currentUser) {
            firstUid = currentUser.getUid();
        }

        current_state = "not_friend";
    }

    private void getUidFromSearchUserFragment() {
        Bundle bundle = getArguments();
        secondUid = bundle.getString(Constant.FirebaseDatabase.UID);
    }

    private void showProfile() {
        rootReference.child(Constant.FirebaseDatabase.USERS).child(secondUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child(Constant.FirebaseDatabase.USER_NAME).getValue().toString();
                String linkAvatar = dataSnapshot.child(Constant.FirebaseDatabase.LINK_AVATAR).getValue().toString();
                String email = dataSnapshot.child(Constant.FirebaseDatabase.EMAIL).getValue().toString();
                String phone = dataSnapshot.child(Constant.FirebaseDatabase.PHONE).getValue().toString();
                String gender = dataSnapshot.child(Constant.FirebaseDatabase.GENDER).getValue().toString();

                tvEmailProfile.setText(email);
                tvPhoneProfile.setText(phone);
                tvGenderProfile.setText(gender);
                tvUserNameProfile.setText(userName);
                if (isAdded() && getActivity() !=  null) {
                    cafeChat.loadImages(getActivity(), linkAvatar, civImageProfile);
                }

                // --------------- FRIEND LIST ---------------
                rootReference.child(Constant.FirebaseDatabase.FRIEND_REQUEST).child(firstUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(secondUid)) {
                            String requestType = dataSnapshot.child(secondUid).child(Constant.FirebaseDatabase.REQUEST_TYPE).getValue().toString();
                            if (requestType.equals("received")) {
                                current_state = "request_received";
                                btnSendRequestFriend.setText(R.string.accept_friend_request);

                                btnDeclineFriendRequest.setVisibility(View.VISIBLE);
                                btnDeclineFriendRequest.setEnabled(true);
                            }else if (requestType.equals("sent")) {
                                current_state = "request_sent";
                                btnSendRequestFriend.setText(R.string.cancel_friend_request);

                                btnDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                btnDeclineFriendRequest.setEnabled(false);
                            }
                        }else {
                            rootReference.child(Constant.FirebaseDatabase.FRIENDS).child(firstUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(secondUid)) {
                                        current_state = "friends";
                                        btnSendRequestFriend.setText(R.string.unfriend_this_person);

                                        btnDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                        btnDeclineFriendRequest.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left_extra:
                getActivity().onBackPressed();
                break;
            case R.id.btn_send_friend_request:
                showFriendRequest();
                break;
            case R.id.btn_decline_friend_request:
                showDeclineFriendRequest();
                break;
        }
    }

    private void showFriendRequest() {
        // --------------- NOT FRIEND ---------------
        btnSendRequestFriend.setEnabled(false);
        if (current_state.equals("not_friend")) {
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put(Constant.FirebaseDatabase.FRIEND_REQUEST + "/" + firstUid + "/" + secondUid + "/" + Constant.FirebaseDatabase.REQUEST_TYPE, "sent");
            requestMap.put(Constant.FirebaseDatabase.FRIEND_REQUEST + "/" + secondUid + "/" + firstUid + "/" + Constant.FirebaseDatabase.REQUEST_TYPE, "received");
            rootReference.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (null != databaseError) {
                        Toast.makeText(getActivity(), "Fail Sending Request", Toast.LENGTH_SHORT).show();
                    }else {
                        current_state = "request_sent";
                        btnSendRequestFriend.setText(R.string.cancel_friend_request);
                        Toast.makeText(getActivity(), "Request Sent Successful", Toast.LENGTH_SHORT).show();

                        btnDeclineFriendRequest.setVisibility(View.INVISIBLE);
                        btnDeclineFriendRequest.setEnabled(false);
                    }
                    btnSendRequestFriend.setEnabled(true);
                }
            });
        }

        // --------------- CANCEL FRIEND REQUEST ---------------
        if (current_state.equals("request_sent")) {
            rootReference.child(Constant.FirebaseDatabase.FRIEND_REQUEST).child(firstUid).child(secondUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    rootReference.child(Constant.FirebaseDatabase.FRIEND_REQUEST).child(secondUid).child(firstUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            btnSendRequestFriend.setEnabled(true);
                            current_state = "not_friend";
                            btnSendRequestFriend.setText(R.string.send_friend_request);
                            Toast.makeText(getActivity(), "Cancel Friend Request Successful", Toast.LENGTH_SHORT).show();

                            btnDeclineFriendRequest.setVisibility(View.INVISIBLE);
                            btnDeclineFriendRequest.setEnabled(false);
                        }
                    });
                }
            });
        }

        // --------------- REQUEST RECEIVED STATE ---------------
        if (current_state.equals("request_received")) {
            final String currentDate = SimpleDateFormat.getDateTimeInstance().format(new Date());
            Map<String, Object> friendMap = new HashMap<>();
            friendMap.put(Constant.FirebaseDatabase.FRIENDS + "/" + firstUid + "/" + secondUid + "/" + Constant.FirebaseDatabase.DATE_TIME, currentDate);
            friendMap.put(Constant.FirebaseDatabase.FRIENDS + "/" + secondUid + "/" + firstUid + "/" + Constant.FirebaseDatabase.DATE_TIME, currentDate);

            friendMap.put(Constant.FirebaseDatabase.FRIEND_REQUEST + "/" + firstUid + "/" + secondUid, null);
            friendMap.put(Constant.FirebaseDatabase.FRIEND_REQUEST + "/" + secondUid + "/" + firstUid, null);

            rootReference.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (null == databaseError) {
                        btnSendRequestFriend.setEnabled(true);
                        current_state = "friends";
                        btnSendRequestFriend.setText(R.string.unfriend_this_person);

                        btnDeclineFriendRequest.setVisibility(View.INVISIBLE);
                        btnDeclineFriendRequest.setEnabled(false);
                    }else {
                        String error = databaseError.getMessage();
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // --------------- UNFRIEND ---------------
        if (current_state.equals("friends")) {
            Map<String, Object> unfriendMap = new HashMap<>();
            unfriendMap.put(Constant.FirebaseDatabase.FRIENDS + "/" + firstUid + "/" + secondUid, null);
            unfriendMap.put(Constant.FirebaseDatabase.FRIENDS + "/" + secondUid + "/" + firstUid, null);

            rootReference.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (null == databaseError) {
                        btnSendRequestFriend.setEnabled(true);
                        current_state = "not_friend";
                        btnSendRequestFriend.setText(R.string.send_friend_request);

                        btnDeclineFriendRequest.setVisibility(View.INVISIBLE);
                        btnDeclineFriendRequest.setEnabled(false);
                    }
                }
            });
        }
    }

    private void showDeclineFriendRequest() {
        if (current_state.equals("request_received")) {
            rootReference.child(Constant.FirebaseDatabase.FRIEND_REQUEST).child(firstUid).child(secondUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    rootReference.child(Constant.FirebaseDatabase.FRIEND_REQUEST).child(secondUid).child(firstUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            btnSendRequestFriend.setEnabled(true);
                            current_state = "not_friend";
                            btnSendRequestFriend.setText(R.string.send_friend_request);

                            btnDeclineFriendRequest.setVisibility(View.INVISIBLE);
                            btnDeclineFriendRequest.setEnabled(false);
                        }
                    });
                }
            });
        }
    }

}
