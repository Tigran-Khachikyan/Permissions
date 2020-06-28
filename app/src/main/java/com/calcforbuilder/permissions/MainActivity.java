package com.calcforbuilder.permissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 123;
    private static String[] permissionsRequired = {

            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkAndRequestPermissions()) {
            Toast.makeText(this, "App is started with all the permissions", Toast.LENGTH_SHORT).show();
            //........
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {

            List<String> permissionsDenied = new ArrayList<>();
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        permissionsDenied.add(permissions[i]);
                }

                if (permissionsDenied.isEmpty())
                    Toast.makeText(this, "All the required permissions are granted", Toast.LENGTH_SHORT).show();
                else {
                    List<String> permissionToAskAgain = new ArrayList<>();
                    for (String permDenied : permissionsDenied
                    ) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permDenied)) {
                            permissionToAskAgain.add(permDenied);
                        } else {
                            String text = permDenied.split("\\.")[2] + " permission is disabled forever, the App will not work properly";
                            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
                        }
                    }
                    if (!permissionToAskAgain.isEmpty())
                        showDialogAskingPermissionAgain(permissionToAskAgain);
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean checkAndRequestPermissions() {

        List<String> permissionsNeededToAsk = new ArrayList<>();
        for (String permission : permissionsRequired
        ) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                permissionsNeededToAsk.add(permission);
        }

        if (!permissionsNeededToAsk.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeededToAsk.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    private void showDialogAskingPermissionAgain(List<String> permissionToAskAgain) {
        StringBuilder permissionsTextList = new StringBuilder();
        for (int i = 0; i < permissionToAskAgain.size(); i++) {
            String name;
            if (i > 0)
                name = ", " + permissionToAskAgain.get(i).split("\\.")[2];
            else
                name = permissionToAskAgain.get(i).split("\\.")[2];
            permissionsTextList.append(name);
        }
        String textToShow;
        if (permissionToAskAgain.size() > 1)
            textToShow = permissionsTextList.toString() + " permissions are required for this app, try again!";
        else
            textToShow = permissionsTextList.toString() + " permission is required for this app, try again!";

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        checkAndRequestPermissions();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        // proceed with logic by disabling the related features or quit the app.
                        Toast.makeText(MainActivity.this, "Your final decision is accepted, so App is closing!", Toast.LENGTH_LONG).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                    finish();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    finish();
                                }
                            }
                        }).start();
                        break;
                }
            }
        };

        new AlertDialog.Builder(this)
                .setMessage(textToShow)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Not now", listener)
                .create()
                .show();
    }
}
