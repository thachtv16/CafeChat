package thachtv.cafechat.fragments.contact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

/**
 * Created by Thinkpad on 10/17/2017.
 */

public class ProfileFragment extends BaseFragment implements View.OnClickListener {

    public static final String ACTION_PRIVACY = "thachtv.cafechat.action_privacy";

    private DatabaseReference databaseReference;
    private DatabaseReference friendRequestDatabase;
    private DatabaseReference friendsDatabase;
    private FirebaseUser currentUser;

    private TextView tvTitleProfile;
    private ImageView ivLeftBackProfile;

    private CircleImageView civImageProfile;
    private TextView tvUserNameProfile;
    private TextView tvEmailProfile;
    private TextView tvPhoneProfile;
    private TextView tvGenderProfile;
    private Button btnSendRequestFriend;
    private Button btnDeclineFriendRequest;

    private String uid;
    private String current_state;

    private CafeChat cafeChat = CafeChat.getInstance();

    public static ProfileFragment newInstance() {
        ProfileFragment profileFragment = new ProfileFragment();
        return profileFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        tvTitleProfile = (TextView) rootView.findViewById(R.id.tv_title_extra);
        tvTitleProfile.setText("Profile");
        ivLeftBackProfile = (ImageView) rootView.findViewById(R.id.iv_left_extra);
        ivLeftBackProfile.setOnClickListener(this);
        civImageProfile = (CircleImageView) rootView.findViewById(R.id.civ_image_avatar_profile);
        tvUserNameProfile = (TextView) rootView.findViewById(R.id.tv_user_name_profile);
        tvEmailProfile = (TextView) rootView.findViewById(R.id.tv_email_profile);
        tvPhoneProfile = (TextView) rootView.findViewById(R.id.tv_phone_profile);
        tvGenderProfile = (TextView) rootView.findViewById(R.id.tv_gender_profile);
        btnSendRequestFriend = (Button) rootView.findViewById(R.id.btn_send_friend_request);
        btnSendRequestFriend.setOnClickListener(this);
        btnDeclineFriendRequest = (Button) rootView.findViewById(R.id.btn_decline_friend_request);
        btnDeclineFriendRequest.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getUidFromSearchUserFragment();

        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constant.FirebaseDatabase.USERS).child(uid);
        friendRequestDatabase = FirebaseDatabase.getInstance().getReference().child(Constant.FirebaseDatabase.FRIEND_REQUEST);
        friendsDatabase = FirebaseDatabase.getInstance().getReference().child(Constant.FirebaseDatabase.FRIENDS);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        current_state = "not_friend";
        showProfile();
    }

    private void getUidFromSearchUserFragment() {
        Bundle bundle = getArguments();
        uid = bundle.getString("uid");
        Log.d("ProfileFragment", uid);
    }

    private void showProfile() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("user_name").getValue().toString();
                String linkAvatar = dataSnapshot.child("link_avatar").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String phone = dataSnapshot.child("phone").getValue().toString();
                String gender = dataSnapshot.child("gender").getValue().toString();

                tvEmailProfile.setText(email);
                tvPhoneProfile.setText(phone);
                tvGenderProfile.setText(gender);
                tvUserNameProfile.setText(userName);
                if (isAdded() && getActivity() !=  null) {
                    cafeChat.loadImages(getActivity(), linkAvatar, civImageProfile);
                }

                // --------------- FRIEND LIST ---------------
                friendRequestDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(uid)) {
                            String request_type = dataSnapshot.child(uid).child("request_type").getValue().toString();
                            if (request_type.equals("received")) {
                                current_state = "request_received";
                                btnSendRequestFriend.setText("Accept Friend Request");

                                btnDeclineFriendRequest.setVisibility(View.VISIBLE);
                                btnDeclineFriendRequest.setEnabled(true);
                            }else if (request_type.equals("sent")){
                                current_state = "request_sent";
                                btnSendRequestFriend.setText("Cancel Friend Request");

                                btnDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                btnDeclineFriendRequest.setEnabled(false);
                            }
                        } else {
                            friendsDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(uid)) {
                                        current_state = "friends";
                                        btnSendRequestFriend.setText("Unfriend this person");

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
        if (current_state.equals("not_friend")) {
            btnSendRequestFriend.setEnabled(false);
            friendRequestDatabase.child(currentUser.getUid()).child(uid).child("request_type")
                    .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        friendRequestDatabase.child(uid).child(currentUser.getUid()).child("request_type")
                                .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                current_state = "request_sent";
                                btnSendRequestFriend.setText("Cancel Friend Request");
                                Toast.makeText(getActivity(), "Request Sent Successful", Toast.LENGTH_SHORT).show();

                                btnDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                btnDeclineFriendRequest.setEnabled(false);
                            }
                        });
                    }else {
                        Toast.makeText(getActivity(), "Fail Sending Request", Toast.LENGTH_SHORT).show();
                    }

                    btnSendRequestFriend.setEnabled(true);
                }
            });
        }

        // --------------- CANCEL FRIEND REQUEST ---------------
        if (current_state.equals("request_sent")) {
            friendRequestDatabase.child(currentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    friendRequestDatabase.child(uid).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            btnSendRequestFriend.setEnabled(true);
                            current_state = "not_friend";
                            btnSendRequestFriend.setText("Send Friend Request");
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
            Map<String, String> friendMapCurrentId = new HashMap<String, String>();
            friendMapCurrentId.put("date_time", currentDate);
            friendMapCurrentId.put("uid", uid);
            friendsDatabase.child(currentUser.getUid()).child(uid).setValue(friendMapCurrentId).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Map<String, String> friendMapUid = new HashMap<String, String>();
                    friendMapUid.put("date_time", currentDate);
                    friendMapUid.put("uid", currentUser.getUid());
                    friendsDatabase.child(uid).child(currentUser.getUid()).setValue(friendMapUid).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendRequestDatabase.child(currentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendRequestDatabase.child(uid).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            btnSendRequestFriend.setEnabled(true);
                                            current_state = "friends";
                                            btnSendRequestFriend.setText("Unfriend this person");

                                            btnDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                            btnDeclineFriendRequest.setEnabled(false);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }

        // --------------- UNFRIEND ---------------
        if (current_state.equals("friends")) {
            friendsDatabase.child(Constant.FirebaseDatabase.FRIENDS).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    friendsDatabase.child(currentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendsDatabase.child(uid).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    btnSendRequestFriend.setEnabled(true);
                                    current_state = "not_friend";
                                    btnSendRequestFriend.setText("Send Friend Request");

                                    btnDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                    btnDeclineFriendRequest.setEnabled(false);
                                }
                            });
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void showDeclineFriendRequest() {
        if (current_state.equals("request_received")) {
            friendRequestDatabase.child(Constant.FirebaseDatabase.FRIEND_REQUEST).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    friendRequestDatabase.child(currentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendRequestDatabase.child(uid).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    btnSendRequestFriend.setEnabled(true);
                                    current_state = "not_friend";
                                    btnSendRequestFriend.setText("Send Friend Request");

                                    btnDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                    btnDeclineFriendRequest.setEnabled(false);
                                }
                            });
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
