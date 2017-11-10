package thachtv.cafechat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import thachtv.cafechat.R;
import thachtv.cafechat.application.CafeChat;
import thachtv.cafechat.define.Constant;
import thachtv.cafechat.holder.MessageHolder;
import thachtv.cafechat.model.Message;

/**
 * Created by Thinkpad on 10/28/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {

    private CafeChat cafeChat = CafeChat.getInstance();

    private Context context;
    private ArrayList<Message> messageArrayList = new ArrayList<>();
    private LayoutInflater inflater;

    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;

    public MessageAdapter(Context context, ArrayList<Message> messageArrayList) {
        this.context = context;
        this.messageArrayList = messageArrayList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = inflater.inflate(R.layout.layout_message_single_item, parent, false);
        MessageHolder messageHolder = new MessageHolder(itemLayoutView);
        return messageHolder;
    }

    @Override
    public void onBindViewHolder(final MessageHolder holder, int position) {
        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        String currentUid = mAuth.getCurrentUser().getUid();

        Message message = messageArrayList.get(position);

        String fromUser = message.getFrom();
        if (fromUser.equals(currentUid)) {
            holder.tvTextMessage.setVisibility(View.INVISIBLE);
            holder.civImageMessage.setVisibility(View.INVISIBLE);
            holder.tvTimeMessage.setVisibility(View.INVISIBLE);
            if (message.getTime() > 0) {
                holder.tvTimeMainMessage.setVisibility(View.VISIBLE);
                holder.tvTimeMainMessage.setText(message.getTime() + "");
            } else {
                holder.tvTimeMainMessage.setVisibility(View.GONE);
            }
            holder.tvTextMainMessage.setVisibility(View.VISIBLE);
            holder.tvTextMessage.setTextColor(Color.WHITE);
            holder.tvTextMainMessage.setText(message.getMessage());
        }else {
            holder.tvTextMainMessage.setVisibility(View.INVISIBLE);
            holder.tvTimeMainMessage.setVisibility(View.INVISIBLE);
            holder.tvTextMessage.setVisibility(View.VISIBLE);
            holder.tvTimeMessage.setVisibility(View.VISIBLE);
            holder.civImageMessage.setVisibility(View.VISIBLE);
            holder.tvTextMessage.setTextColor(Color.BLACK);
            holder.tvTextMessage.setText(message.getMessage());

            rootRef.child(Constant.FirebaseDatabase.USERS).child(fromUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String linkAvatar = dataSnapshot.child("link_avatar").getValue().toString();
                    if (null != context) {
                        cafeChat.loadImages(context, linkAvatar, holder.civImageMessage);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }
}
