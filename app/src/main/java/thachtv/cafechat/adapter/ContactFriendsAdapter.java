package thachtv.cafechat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import thachtv.cafechat.R;
import thachtv.cafechat.application.CafeChat;
import thachtv.cafechat.holder.ContactFriendsHolder;
import thachtv.cafechat.interfaces.NextChooseListener;
import thachtv.cafechat.model.ContactFriends;

public class ContactFriendsAdapter extends RecyclerView.Adapter<ContactFriendsHolder> {

    private Context context;
    private ArrayList<ContactFriends> friendsArrayList = new ArrayList<>();
    private LayoutInflater inflater;

    private CafeChat cafeChat = CafeChat.getInstance();

    private NextChooseListener listener;

    public void setListener(NextChooseListener listener) {
        this.listener = listener;
    }

    public ContactFriendsAdapter(Context context, ArrayList<ContactFriends> friendsArrayList) {
        this.context = context;
        this.friendsArrayList = friendsArrayList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ContactFriendsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = inflater.inflate(R.layout.layout_contact_friends, parent, false);
        return new ContactFriendsHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ContactFriendsHolder holder, int position) {
        final ContactFriends contactFriends = friendsArrayList.get(position);
        holder.tvUsernameContact.setText(contactFriends.getUserNameContact());
        holder.tvDateTimeContact.setText(contactFriends.getDateTimeContact());
        if (contactFriends.getDotOnline().equals("true")) {
            holder.ivOnlineContact.setVisibility(View.VISIBLE);
        }else {
            holder.ivOnlineContact.setVisibility(View.INVISIBLE);
        }
        cafeChat.loadImages(context, contactFriends.getImageAvatarContact(), holder.civImageAvatarContact);

        holder.rlContactFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.nextChoose(contactFriends.getUidContact());
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsArrayList.size();
    }
}
