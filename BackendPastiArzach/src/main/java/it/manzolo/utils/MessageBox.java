package it.manzolo.utils;

import android.app.Activity;
import android.app.AlertDialog;

public class MessageBox {

    /**
     * @param activity, title, message
     */
    public MessageBox(Activity activity, String title, String message) {

        AlertDialog.Builder miaAlert = new AlertDialog.Builder(activity);
        miaAlert.setTitle(title);
        miaAlert.setMessage(message);
        AlertDialog alert = miaAlert.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();

    }

}

/*
//public boolean MessageBox(Activity activity, String title, String message) {
	new AlertDialog.Builder(activity)
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							return true;
						}
					})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					return false;
				}
			}).show();

//}
*/
