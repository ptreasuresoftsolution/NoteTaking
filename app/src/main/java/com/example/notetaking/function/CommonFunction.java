package com.example.notetaking.function;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CommonFunction {
    static ProgressDialog progressDialog;

    public static void PleaseWaitShow(Context context) {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                CommonFunction.dismissDialog();
            }
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    public static void PleaseWaitShowMessage(String message) {
        try {
            if (progressDialog != null)
                progressDialog.setMessage(message);
        } catch (Exception e) {
            Log.e("progressDialog EXCEPTION", "Update Progress Alert", e);
        }
    }

    public static void dismissDialog() {
        try {
            if (progressDialog != null)
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
        } catch (Exception e) {
        }
    }


    public static AlertDialog _LoadAlert(Context context, String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        return alertDialog;
    }

    public static boolean isNetworkConnected(Context context) {
        boolean connected = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            } else {
                connected = false;
                Toast.makeText(context.getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }
        return connected;
    }

    public static void _LoadFirstFragment(AppCompatActivity appCompatActivity, int resId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(resId, fragment);
        fragmentTransaction.commit();
    }

    public static void loadFragmentWithStack(FragmentActivity fragmentActivity, int layout, Fragment fragment, String fragmentName) {
        FragmentTransaction fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
        //   fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(layout, fragment);
        fragmentTransaction.addToBackStack(fragmentName);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public static String timestampToString(Timestamp timestamp) {
        if (timestamp == null)
            return new SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate());
        return new SimpleDateFormat("MM/dd/yyyy").format(timestamp.toDate());
    }

    public static Spanned getContentFromJson(String formattedContent) {
        try {
            JSONArray jsonArray = new JSONArray(formattedContent);
            List<JSONObject> jsonObjectList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObjectList.add(jsonArray.getJSONObject(i));
            }

            Spannable spannable = Spannable.Factory.getInstance().newSpannable("");
            for (JSONObject jsonObject : jsonObjectList) {
                String character = jsonObject.getString("ch");
                int style = jsonObject.optInt("st", -1);
                spannable = appendSpannable(spannable, character, style);
            }
            return spannable;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Spannable appendSpannable(Spannable spannable, String character, int style) {
        SpannableStringBuilder spannableBuilder;
        if (spannable instanceof SpannableStringBuilder) {
            spannableBuilder = (SpannableStringBuilder) spannable;
        } else {
            spannableBuilder = new SpannableStringBuilder(spannable);
        }

        int start = spannableBuilder.length();
        spannableBuilder.append(character);
        int end = spannableBuilder.length();
        if (style != -1) {
            spannableBuilder.setSpan(new StyleSpan(style), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableBuilder;
    }

    public static String getContentAsJson(Spanned content) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < content.length(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("ch", String.valueOf(content.charAt(i)));
                StyleSpan[] styleSpans = content.getSpans(i, i + 1, StyleSpan.class);
                if (styleSpans.length > 0) {
                    int style = styleSpans[styleSpans.length - 1].getStyle();
                    jsonObject.put("st", style);
                }
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray.toString();
    }
    public static DocumentReference getCollectionReferenceForFolder() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance().collection("notes")
                .document(currentUser.getUid());
    }

}
