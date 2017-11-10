package thachtv.cafechat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.view.Window;

import thachtv.cafechat.R;

/**
 * Created by Thinkpad on 10/12/2017.
 */

public class ProgressBarDialog extends Dialog {

    private Context context;

    public ProgressBarDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_dialog_progress_bar);
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
