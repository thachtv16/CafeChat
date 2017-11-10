package thachtv.cafechat.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import thachtv.cafechat.R;

/**
 * Created by Thinkpad on 10/19/2017.
 */

public class LoadingHolder extends RecyclerView.ViewHolder {

    public ProgressBar progressBar;

    public LoadingHolder(View itemView) {
        super(itemView);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar_all_user);
    }
}
