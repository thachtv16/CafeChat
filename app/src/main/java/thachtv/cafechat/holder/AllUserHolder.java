package thachtv.cafechat.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import thachtv.cafechat.R;

/**
 * Created by Thinkpad on 10/16/2017.
 */

public class AllUserHolder extends RecyclerView.ViewHolder{

    public RelativeLayout rlUserSingle;
    public TextView tvUserName;
    public CircleImageView ivImageAvatar;

    public AllUserHolder(View itemView) {
        super(itemView);
        tvUserName = (TextView) itemView.findViewById(R.id.tv_user_single);
        ivImageAvatar = (CircleImageView) itemView.findViewById(R.id.civ_image_user_single);
        rlUserSingle = (RelativeLayout) itemView.findViewById(R.id.rl_user_single);
    }
}
