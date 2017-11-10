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

/**
 * Created by Thinkpad on 10/24/2017.
 */

public class ChatDetailFragment extends BaseFragment {

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

    private String uid;
    private String currentUid;

    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;

    private static final int TOTAL_ITEM_TO_LOAD = 10;
    private int currentPage = 1;
    private int itemPosition = 0;
    private String lastKey = "";
    private String prevKey = "";

    @NonNull
    public static ChatDetailFragment newInstance() {
        return new ChatDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        tvUserNameToolBar = (TextView) rootView.findViewById(R.id.tv_user_message);
        tvLastSeenToolBar = (TextView) rootView.findViewById(R.id.tv_last_seen_message);
        ivLeftBackToolBar = (ImageView) rootView.findViewById(R.id.iv_left_message);

        etTextMessage = (EditText) rootView.findViewById(R.id.et_text_message);
        ivPlusMessage = (ImageView) rootView.findViewById(R.id.iv_plus_message);
        ivSendMessage = (ImageView) rootView.findViewById(R.id.iv_send_message);
        rvMessage = (RecyclerView) rootView.findViewById(R.id.rv_message);
        srlLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_layout);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser().getUid();

        messageArrayList = new ArrayList<Message>();
        messageAdapter = new MessageAdapter(getContext(), messageArrayList);
        rvMessage.setAdapter(messageAdapter);

        getDataAndShowToolBar();
        addDataToChat();
        sendMessage();
        loadMessage();
        scrollToTop();
    }

    private void getDataAndShowToolBar() {
        Bundle bundle = getArguments();
        uid = bundle.getString("uid");
        rootRef.child(Constant.FirebaseDatabase.USERS).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String toolBarUserName = dataSnapshot.child("user_name").getValue().toString();
                String statusOnlineToolBar = dataSnapshot.child("online").getValue().toString();
                tvUserNameToolBar.setText(toolBarUserName);
                if (statusOnlineToolBar.equals("true")) {
                    tvLastSeenToolBar.setText("online");
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

    private void addDataToChat() {
        rootRef.child(Constant.FirebaseDatabase.CHAT).child(currentUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(uid)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("time_stamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put(Constant.FirebaseDatabase.CHAT + "/" + currentUid + "/" + uid, chatAddMap);
                    chatUserMap.put(Constant.FirebaseDatabase.CHAT + "/" + uid + "/" + currentUid, chatAddMap);

                    rootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        ivSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etTextMessage.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    String currentUserRef = Constant.FirebaseDatabase.MESSAGES + "/" + currentUid + "/" + uid;
                    String chatUserRef = Constant.FirebaseDatabase.MESSAGES + "/" + uid + "/" + currentUid;

                    DatabaseReference userMessagePush = rootRef.child(Constant.FirebaseDatabase.MESSAGES).child(currentUid).child(uid).push();
                    String pushUid = userMessagePush.getKey();

                    Map messageMap = new HashMap();
                    messageMap.put("message", message);
                    messageMap.put("seen", false);
                    messageMap.put("type", "text");
                    messageMap.put("time", ServerValue.TIMESTAMP);
                    messageMap.put("from", currentUid);
                    messageMap.put("to", uid);

                    Map messageUserMap = new HashMap();
                    messageUserMap.put(currentUserRef + "/" + pushUid, messageMap);
                    messageUserMap.put(chatUserRef + "/" + pushUid, messageMap);

                    etTextMessage.setText("");

                    rootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }
        });
    }

    long oldTime = System.currentTimeMillis() / 1000;
    boolean isFirstReceiveMessage = true;

    private void loadMessage() {
        DatabaseReference messageRef = rootRef.child(Constant.FirebaseDatabase.MESSAGES).child(currentUid).child(uid);
        Query messageQuery = messageRef.limitToLast(currentPage * TOTAL_ITEM_TO_LOAD);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("abc123", dataSnapshot.toString());
                Message message = dataSnapshot.getValue(Message.class);
                if (isFirstReceiveMessage) {
                    oldTime = System.currentTimeMillis() / 1000;
                }
                isFirstReceiveMessage = false;
                long newTime = System.currentTimeMillis() / 1000;
                long delta = newTime - oldTime;
                Log.d("ABC", "delta=" + delta);

                if (delta > 1) {
                    message.setTime(0);
                }

                oldTime = newTime;

                itemPosition++;
                if (itemPosition == 1) {
                    String messageKey = dataSnapshot.getKey();
                    lastKey = messageKey;
                    prevKey = messageKey;
                }

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

    private void scrollToTop() {
        srlLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
                itemPosition = 0;
                loadMoreMessage();
            }
        });
    }

    private void loadMoreMessage() {
        DatabaseReference messageRef = rootRef.child(Constant.FirebaseDatabase.MESSAGES).child(currentUid).child(uid);
        Query messageQuery = messageRef.orderByKey().endAt(lastKey).limitToLast(10);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                String messageKey = dataSnapshot.getKey();

                if (!prevKey.equals(messageKey)) {
                    messageArrayList.add(itemPosition++, message);
                    prevKey = messageKey;
                }

                if (itemPosition == 1) {
                    lastKey = messageKey;
                }

                messageAdapter.notifyDataSetChanged();

                //rvMessage.scrollToPosition(messageArrayList.size() - 1);
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
