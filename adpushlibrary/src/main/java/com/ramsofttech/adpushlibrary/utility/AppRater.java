package com.ramsofttech.adpushlibrary.utility;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ramsofttech.adpushlibrary.R;


public class AppRater {
    private static long DAY_TIME = 24 * 60 * 60 * 1000;

    private static String APP_NAME = "";
    private static String APP_PACKAGE_NAME = "";

    private static String LAST_LAUNCH_PREF = "last_launch_pref";

    public static void setTimePref(Context context, long value) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(LAST_LAUNCH_PREF, value).commit();
    }

    public static long getTimePref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getLong(LAST_LAUNCH_PREF, 0);
    }

    private static String DONT_SHOW_PREF = "dont_show_pref";

    public static void setDontShowPref(Context context, boolean value) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(DONT_SHOW_PREF, value).commit();
    }

    public static boolean getDontShowPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(DONT_SHOW_PREF, false);
    }

    private static String FIRST_LAUNCH_PREF = "first_launch_pref";

    public static void setFirstLaunchPref(Context context, boolean value) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(FIRST_LAUNCH_PREF, value).commit();
    }

    public static boolean getFirstLaunchPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(FIRST_LAUNCH_PREF, true);
    }

    public static void rateNow(Context mContext) {

        APP_NAME = mContext.getResources().getString(R.string.app_name);
        APP_PACKAGE_NAME = mContext.getPackageName();
        if (getFirstLaunchPref(mContext)) {
            setFirstLaunchPref(mContext, false);
            return;
        }

        if (getDontShowPref(mContext)) {
            return;
        }

        if (System.currentTimeMillis() >= getTimePref(mContext) + DAY_TIME) {
            setTimePref(mContext, System.currentTimeMillis());
            showRateDialog(mContext);
        }

    }

    public static void showRateDialog(final Context mContext) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Rate " + APP_NAME);

        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(8, 8, 8, 8);

        TextView tv = new TextView(mContext);
        tv.setText("If you enjoy using " + APP_NAME
                + ", please take a moment to rate it. Thanks for your support!");
        tv.setWidth(320);
        tv.setPadding(8, 8, 8, 8);
        ll.addView(tv);

        Button b1 = new Button(mContext);
        b1.setText("Rate Now");
        b1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                setDontShowPref(mContext, true);
                try {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse("market://details?id=" + APP_PACKAGE_NAME)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    mContext.startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id="
                                    + APP_PACKAGE_NAME)));
                }

                dialog.dismiss();
            }
        });
        ll.addView(b1);

        Button b2 = new Button(mContext);
        b2.setText("Remind me later");
        b2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ll.addView(b2);

        Button b3 = new Button(mContext);
        b3.setText("No, thanks");
        b3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                setDontShowPref(mContext, true);
                dialog.dismiss();
            }
        });
        ll.addView(b3);

        dialog.setContentView(ll);
        dialog.show();
    }
}