package thachtv.cafechat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import thachtv.cafechat.R;
import thachtv.cafechat.application.CafeChat;
import thachtv.cafechat.model.ChatFriend;

/**
 * Created by Thinkpad on 11/09/2017.
 */

public class ChatFriendAdapter extends RecyclerView.Adapter<ChatFriendAdapter.ChatFriendHolder> {

    private CafeChat cafeChat = CafeChat.getInstance();

    private Context context;
    private ArrayList<ChatFriend> chatFriendArrayList = new ArrayList<>();
    private LayoutInflater inflater;

    public ChatFriendAdapter(Context context, ArrayList<ChatFriend> chatFriendArrayList) {
        this.context = context;
        this.chatFriendArrayList = chatFriendArrayList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ChatFriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = inflater.inflate(R.layout.layout_item_chat_friend, parent, false);
        ChatFriendHolder chatFriendHolder = new ChatFriendHolder(itemLayoutView);
        return chatFriendHolder;
    }

    @Override
    public void onBindViewHolder(ChatFriendHolder holder, int position) {
        ChatFriend chatFriend = chatFriendArrayList.get(position);
        holder.tvUserNameChatFriend.setText(chatFriend.getUserNameChatFriend());
        holder.tvMessageChatFriend.setText(chatFriend.getMessageChatFriend());
        if (null != context) {
            cafeChat.loadImages(context, chatFriend.getLinkAvatarChatFriend(), holder.civChatFriend);
        }
    }

    @Override
    public int getItemCount() {
        return chatFriendArrayList.size();
    }

    public class ChatFriendHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rlChatFriend;
        public CircleImageView civChatFriend;
        public TextView tvUserNameChatFriend;
        public TextView tvMessageChatFriend;

        public ChatFriendHolder(View itemView) {
            super(itemView);

            rlChatFriend = (RelativeLayout) itemView.findViewById(R.id.rl_chat_friend);
            civChatFriend = (CircleImageView) itemView.findViewById(R.id.civ_image_chat_friends);
            tvUserNameChatFriend = (TextView) itemView.findViewById(R.id.tv_user_name_chat_friends);
            tvMessageChatFriend = (TextView) itemView.findViewById(R.id.tv_message_chat);
        }
    }
}
