package com.steppschuh.intelliq;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by Steppschuh on 28/07/15.
 */
public class UiHelper {

    public static final int VIBRATE_DURATION_DEFAULT = 500;

    public static void vibrate(Context context, int duration) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(duration);
    }

}
