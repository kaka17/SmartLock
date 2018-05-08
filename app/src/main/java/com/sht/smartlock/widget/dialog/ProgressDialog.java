package com.sht.smartlock.widget.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.sht.smartlock.R;


/**
 * 加载ProgressBar
 *
 */
@SuppressLint("CutPasteId")
public class ProgressDialog {

	public static Dialog dialog = null;
	
	public static void show(Context context,String msg) {
		showDialog(context, msg);
	}
	
	public static void show(Context context,int resId) {
		showDialog(context, context.getResources().getString(resId));
	}
	
	private static void showDialog(Context context,String msg){
		if(dialog != null && dialog.isShowing()) {
		    dialog.dismiss();
        }
		LayoutInflater inflater = LayoutInflater.from(context);
		View loadingView = inflater.inflate(R.layout.view_progress, null);
		TextView msgTextView = (TextView)loadingView.findViewById(R.id.loadingMsg);
		if(msg != null && !"".equals(msg)) {
			msgTextView.setText(msg);
		}
	    TextView loadingMsg = (TextView)loadingView.findViewById(R.id.loadingMsg);// 提示文字
        if(msg != null && ! "".equals(msg)) {
            loadingMsg.setText(msg);
        }
		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.setCancelable(true);
		loadingDialog.setContentView(loadingView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		loadingDialog.show();
		dialog = loadingDialog;
	}

	public static void disMiss() {
		if(dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}
}

