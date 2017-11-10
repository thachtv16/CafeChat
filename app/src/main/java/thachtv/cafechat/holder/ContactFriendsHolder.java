package thachtv.cafechat.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import thachtv.cafechat.R;

/**
 * Created by Thinkpad on 10/21/2017.
 */

public class ContactFriendsHolder extends RecyclerView.ViewHolder {

    public RelativeLayout rlContactFriends;
    public TextView tvUsernameContact;
    public TextView tvDateTimeContact;
    public ImageView ivOnlineContact;
    public CircleImageView civImageAvatarContact;


    public ContactFriendsHolder(View itemView) {
        super(itemView);

        rlContactFriends = (RelativeLayout) itemView.findViewById(R.id.rl_contact_friends);
        tvUsernameContact = (TextView) itemView.findViewById(R.id.tv_contact_friends);
        tvDateTimeContact = (TextView) itemView.findViewById(R.id.tv_date_time_friend);
        ivOnlineContact = (ImageView) itemView.findViewById(R.id.iv_dot_online_contact_friends);
        civImageAvatarContact = (CircleImageView) itemView.findViewById(R.id.civ_image_contact_friends);
    }
}
