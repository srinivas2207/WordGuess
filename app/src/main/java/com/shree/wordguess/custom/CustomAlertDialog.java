package com.shree.wordguess.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

/**
 * App level alert dialog
 */
public class CustomAlertDialog {

	public interface DialogListener {
		void onPositiveBtnClick(int dialogType);
		void onNegativeBtnClick(int dialogType);
	}

	public static final int NORMAL_DIALOG = 0;
	public static final int HALF_REVEAL_DIALOG = 1;
	public static final int EXIT_APP_DIALOG = 2;
	public static final int HINT_DIALOG = 3;
	public static final int GAME_REFRESH_DIALOG = 4;

	private Context context;
	private String possitiveBtn;
	private String negativeBtn;
	private String title;
	private String dialogBody;
	private DialogListener listener;
	private AlertDialog alertDialog;
	private int dialogType;

	public CustomAlertDialog(Context context, int dialogType) {
		this.context = context;
		this.dialogType = dialogType;
	}

	public CustomAlertDialog setDialogListener(DialogListener listener) {
		this.listener = listener;
		return this;
	}

	public CustomAlertDialog setTitle(String title) {
		this.title = title;
		return this;
	}

	public CustomAlertDialog setBody(String body) {
		this.dialogBody = body;
		return this;
	}

	public CustomAlertDialog setButtons(String possitiveBtn, String negativeBtn) {
		this.possitiveBtn = possitiveBtn;
		this.negativeBtn = negativeBtn;
		return this;
	}

	public CustomAlertDialog build() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setMessage(dialogBody);
		if (!TextUtils.isEmpty(title)) {
			alertDialogBuilder.setTitle(title);
		}

		alertDialogBuilder.setPositiveButton( possitiveBtn,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (listener != null) {
							listener.onPositiveBtnClick(dialogType);
						}
					}
				});

		alertDialogBuilder.setNegativeButton( negativeBtn,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.onNegativeBtnClick(dialogType);
					}
				});

		alertDialog = alertDialogBuilder.create();
		return this;
	}

	public void show() {
		if (alertDialog!=null) {
			alertDialog.show();
		}
	}
}
