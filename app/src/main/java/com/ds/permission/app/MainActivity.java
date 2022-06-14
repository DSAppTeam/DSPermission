package com.ds.permission.app;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ds.permission.PermissionManager;
import com.ds.permission.PermissionConfig;
import com.ds.permission.ResultCall;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv_req).setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PermissionManager.get()
                        .inject(this)
                        .request(new ResultCall() {
                                     @Override
                                     public void granted() {
                                         Toast.makeText(MainActivity.this, "granted", Toast.LENGTH_LONG).show();
                                     }

                                     @Override
                                     public void denied(boolean never) {
                                         Toast.makeText(MainActivity.this, "denied " + never, Toast.LENGTH_LONG).show();
                                     }
                                 }, new PermissionConfig.Builder()
                                        .addPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                                        .addPermission(Manifest.permission.CAMERA)
                                        .addPermission(Manifest.permission.RECORD_AUDIO)
                                        .build()
                        );
            }
        });

        findViewById(R.id.tv_setting).setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        });


    }
}
