package com.sht.smartlock.widget.dialog;

import android.content.Context;

import com.sht.smartlock.R;
public class DialogHelper {
	
	public static CommonDialog getPinterestDialog(Context context) {
		return new CommonDialog(context, R.style.dialog_common);
	}

	public static CommonDialog getPinterestDialogCancelable(Context context) {
		CommonDialog dialog = new CommonDialog(context,
				R.style.dialog_common);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}
}
