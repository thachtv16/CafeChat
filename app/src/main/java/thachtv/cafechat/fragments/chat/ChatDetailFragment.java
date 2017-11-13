package thachtv.cafechat.fragments.chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import thachtv.cafechat.R;
import thachtv.cafechat.adapter.MessageAdapter;
import thachtv.cafechat.application.GetTimeAgo;
import thachtv.cafechat.base.BaseFragment;
import thachtv.cafechat.define.Constant;
import thachtv.cafechat.model.Message;

public class ChatDetailFragment extends BaseFragment {

    public static final String TAG_CHAT_DETAIL_FRAGMENT = ChatDetailFragment.class.getSimpleName();

    private TextView tvUserNameToolBar;
    private TextView tvLastSeenToolBar;
    private ImageView ivLeftBackToolBar;

    private EditText etTextMessage;
    private ImageView ivPlusMessage;
    private ImageView ivSendMessage;
    private RecyclerView rvMessage;
    private SwipeRefreshLayout srlLayout;

    private MessageAdapter messageAdapter;
    private ArrayList<Message> messageArrayList;

    private String linkAvatar;
    private String toolBarUserName;
    private String statusOnlineToolBar;

    private String secondUid;
    private String firstUid;

    private DatabaseReference rootRef;

    private static final int TOTAL_ITEM_TO_LOAD = 10;

    @NonNull
    public static ChatDetailFragment newInstance() {
        return new ChatDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        tvUserNameToolBar = rootView.findViewById(R.id.tv_user_message);
        tvLastSeenToolBar = rootView.findViewById(R.id.tv_last_seen_message);
        ivLeftBackToolBar = rootView.findViewById(R.id.iv_left_message);

        etTextMessage = rootView.findViewById(R.id.et_text_message);
        ivPlusMessage = rootView.findViewById(R.id.iv_plus_message);
        ivSendMessage = rootView.findViewById(R.id.iv_send_message);
        rvMessage = rootView.findViewById(R.id.rv_message);
        srlLayout = rootView.findViewById(R.id.srl_layout);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showFirebase();

        messageArrayList = new ArrayList<>();
        messageAdapter = new MessageAdapter(getContext(), messageArrayList);
        rvMessage.setAdapter(messageAdapter);

        getDataAndShowToolBar();
        sendMessage();
        loadMessage();
    }

    private void showFirebase() {
        rootRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (null != currentUser) {
            firstUid = currentUser.getUid();
        }
    }

    private void getDataAndShowToolBar() {
        Bundle bundle = getArguments();
        secondUid = bundle.getString("uid");
        rootRef.child(Constant.FirebaseDatabase.USERS).child(secondUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toolBarUserName = dataSnapshot.child(Constant.FirebaseDatabase.USER_NAME).getValue().toString();
                statusOnlineToolBar = dataSnapshot.child(Constant.FirebaseDatabase.ONLINE).getValue().toString();
                linkAvatar = dataSnapshot.child(Constant.FirebaseDatabase.LINK_AVATAR).getValue().toString();
                tvUserNameToolBar.setText(toolBarUserName);
                if (statusOnlineToolBar.equals("true")) {
                    tvLastSeenToolBar.setText(R.string.online);
                }else {
                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long lastTime = Long.parseLong(statusOnlineToolBar);
                    String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getActivity().getApplicationContext());
                    tvLastSeenToolBar.setText(lastSeenTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ivLeftBackToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void sendMessage() {
        ivSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etTextMessage.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    String currentUserRef = Constant.FirebaseDatabase.MESSAGES + "/" + firstUid + "/" + secondUid;
                    String chatUserRef = Constant.FirebaseDatabase.MESSAGES + "/" + secondUid + "/" + firstUid;

                    DatabaseReference userMessagePush = rootRef.child(Constant.FirebaseDatabase.MESSAGES).child(firstUid).child(secondUid).push();
                    String pushUid = userMessagePush.getKey();

                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put(Constant.FirebaseDatabase.MESSAGE, message);
                    messageMap.put(Constant.FirebaseDatabase.SEEN, false);
                    messageMap.put(Constant.FirebaseDatabase.TYPE, "text");
                    messageMap.put(Constant.FirebaseDatabase.TIME, ServerValue.TIMESTAMP);
                    messageMap.put(Constant.FirebaseDatabase.LINK_AVATAR_MESSAGE, linkAvatar);
                    Log.d(TAG_CHAT_DETAIL_FRAGMENT, linkAvatar);
                    messageMap.put(Constant.FirebaseDatabase.FROM, firstUid);
                    messageMap.put(Constant.FirebaseDatabase.TO, secondUid);

                    Map<String, Object> messageUserMap = new HashMap<>();
                    messageUserMap.put(currentUserRef + "/" + pushUid, messageMap);
                    messageUserMap.put(chatUserRef + "/" + pushUid, messageMap);

                    etTextMessage.setText("");

                    rootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d(TAG_CHAT_DETAIL_FRAGMENT, databaseError.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    private void loadMessage() {
        DatabaseReference messageRef = rootRef.child(Constant.FirebaseDatabase.MESSAGES).child(firstUid).child(secondUid);
        int currentPage = 1;
        Query messageQuery = messageRef.limitToLast(currentPage * TOTAL_ITEM_TO_LOAD);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);

                messageArrayList.add(message);
                messageAdapter.notifyDataSetChanged();

                rvMessage.scrollToPosition(messageArrayList.size() - 1);
                srlLayout.setRefreshing(false);
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
