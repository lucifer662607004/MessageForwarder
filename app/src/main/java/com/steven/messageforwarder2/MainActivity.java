package com.steven.messageforwarder2;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    TextView urlInput = null;
    TextView paramInput = null;
    SharedPreferences sharedPref = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlInput = findViewById(R.id.main_url);
        paramInput = findViewById(R.id.extra_param);

        sharedPref = getSharedPreferences(
                "bark_push_api", Context.MODE_PRIVATE);
        String barkPushAPI = sharedPref.getString("bark_url", null);
        String barkPushParam = sharedPref.getString("bark_param", null);

        if (barkPushAPI != null) { urlInput.setText(barkPushAPI); }
        if (barkPushParam != null) { paramInput.setText(barkPushParam); }
    }

    public void onSave(View view) {
        grantPermission(0);

        String barkPushAPIInput = urlInput.getText().toString();
        String barkPushParamInput = paramInput.getText().toString();
        if (checkUrlFormat(barkPushAPIInput)) {
            SharedPreferences.Editor e = sharedPref.edit();
            e.putString("bark_url", barkPushAPIInput);
            if (checkParamFormat(barkPushParamInput)) {
                e.putString("bark_param", barkPushParamInput);
            } else {
                e.putString("bark_param", null);
            }
            e.apply();
            Toast toast = Toast.makeText(MainActivity.this, "Completed", Toast.LENGTH_LONG);
            toast.show();
        } else {
            Toast toast = Toast.makeText(MainActivity.this, "Illegal URL", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private boolean checkUrlFormat(String target) {
        if (target == null) { return false; }
        if (!target.startsWith("https://api.day.app/")) { return false; }
        return target.endsWith("/");
    }

    private boolean checkParamFormat(String target) {
        if (target == null || target.length() == 0) { return false; }
        boolean hasPrefix = target.startsWith("?");
        if (!hasPrefix) {
            Toast toast = Toast.makeText(MainActivity.this, "Extra Param is invalid, ignore", Toast.LENGTH_LONG);
            toast.show();
        }
        return hasPrefix;
    }

    public boolean checkPermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    public void grantPermission(int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, requestCode);
    }
}