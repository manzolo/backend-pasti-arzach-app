package it.manzolo.utils;

import android.content.Context;
import android.widget.Toast;

public class ToolTip {
    public ToolTip(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public ToolTip(Context context, String message, boolean longMessage) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}
