package thachtv.cafechat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import thachtv.cafechat.R;
import thachtv.cafechat.application.CafeChat;
import thachtv.cafechat.holder.AllUserHolder;
import thachtv.cafechat.interfaces.NextProfileFragmentListener;
import thachtv.cafechat.interfaces.OnLoadMoreListener;
import thachtv.cafechat.model.Users;

/**
 * Created by Thinkpad on 10/16/2017.
 */

public class AllUserAdapter extends RecyclerView.Adapter<AllUserHolder> {

    private Context context;
    private ArrayList<Users> usersArrayList = new ArrayList<>();
    private LayoutInflater inflater;

    private CafeChat cafeChat = CafeChat.getInstance();

    private NextProfileFragmentListener listener;
    private OnLoadMoreListener loadMoreListener;

    public void setListener(NextProfileFragmentListener listener) {
        this.listener = listener;
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public AllUserAdapter(Context context, ArrayList<Users> usersArrayList) {
        this.context = context;
        this.usersArrayList = usersArrayList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public AllUserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = inflater.inflate(R.layout.layout_user_single, parent, false);
        AllUserHolder allUserHolder = new AllUserHolder(itemLayoutView);
        return allUserHolder;
    }

    @Override
    public void onBindViewHolder(AllUserHolder holder, int position) {
        final Users users = usersArrayList.get(position);
        holder.tvUserName.setText(users.getUserName());
        cafeChat.loadImages(context, users.getImageAvatar(), holder.ivImageAvatar);

        holder.rlUserSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.nextAction(users.getUid());
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }
}
