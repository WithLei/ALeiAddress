package cn.renly.aleiaddress.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class IntentUtils {
    public static void openBroswer(Context activity, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        activity.startActivity(intent);
    }

    public static void sharePhone(Context context, String data) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, data);
        shareIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(shareIntent, "分享名片"));
    }

    public static void callPhone(Context context, String phoneNum){
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }
}
