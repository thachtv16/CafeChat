package thachtv.cafechat.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import thachtv.cafechat.R;

/**
 * Created by Thinkpad on 10/28/2017.
 */

public class MessageHolder extends RecyclerView.ViewHolder {

    public CircleImageView civImageMessage;
    public TextView tvTextMessage;
    public TextView tvTimeMessage;
    public TextView tvTextMainMessage;
    public TextView tvTimeMainMessage;

    public MessageHolder(View itemView) {
        super(itemView);

        civImageMessage = (CircleImageView) itemView.findViewById(R.id.civ_image_message);
        tvTextMessage = (TextView) itemView.findViewById(R.id.tv_text_message);
        tvTimeMessage = (TextView) itemView.findViewById(R.id.tv_time_message);
        tvTextMainMessage = (TextView) itemView.findViewById(R.id.tv_text_main_message);
        tvTimeMainMessage = (TextView) itemView.findViewById(R.id.tv_time_main_message);
    }
}
