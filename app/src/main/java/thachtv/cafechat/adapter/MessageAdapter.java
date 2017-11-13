package thachtv.cafechat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import thachtv.cafechat.R;
import thachtv.cafechat.application.CafeChat;
import thachtv.cafechat.holder.MessageHolder;
import thachtv.cafechat.model.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {

    private CafeChat cafeChat = CafeChat.getInstance();

    private Context context;
    private ArrayList<Message> messageArrayList = new ArrayList<>();
    private LayoutInflater inflater;

    private String firstUid;

    public MessageAdapter(Context context, ArrayList<Message> messageArrayList) {
        this.context = context;
        this.messageArrayList = messageArrayList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = inflater.inflate(R.layout.layout_message_single_item, parent, false);
        return new MessageHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final MessageHolder holder, int position) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (null != currentUser) {
            firstUid = currentUser.getUid();
        }
        Message message = messageArrayList.get(position);

        String fromUser = message.getFrom();
        if (fromUser.equals(firstUid)) {
            holder.tvTextMessage.setVisibility(View.INVISIBLE);
            holder.civImageMessage.setVisibility(View.INVISIBLE);
            holder.tvTimeMessage.setVisibility(View.INVISIBLE);
            holder.tvTextMainMessage.setVisibility(View.VISIBLE);
            holder.tvTimeMainMessage.setVisibility(View.VISIBLE);
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

            if (null != context) {
                cafeChat.loadImages(context, message.getLinkAvatar(), holder.civImageMessage);
            }

        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }
}
