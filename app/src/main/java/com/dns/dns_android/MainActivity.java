package com.dns.dns_android;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dns.dns_lib.DnsPermission;
import com.dns.dns_lib.DnsRecognition;
import com.dns.dns_lib.DnsRecognitionListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
        if (DnsPermission.requestPermissions(MainActivity.this, this, PERMISSION_REQUEST_CODE, new ArrayList<>(Arrays.asList(permissions)))) {
            showCameraPreview();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                    showCameraPreview();
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void showCameraPreview() {
        DnsRecognition dnsRecognition = new DnsRecognition(getApplicationContext(), DnsRecognition.BACK_CAMERA, new DnsRecognitionListener() {
            @Override
            public void detectFaceLandmarksNotCorrectlyListener() {

            }

            @Override
            public void failedToDetectFaceLandmarksListener() {

            }

            @Override
            public void failedToDetectFaceListener() {

            }

            @Override
            public void drowsyDrivingNotDetectedListener() {

            }

            @Override
            public void drowsyDrivingDetectedListener() {

            }
        });

        ConstraintLayout constraintLayout = findViewById(R.id.cl_root);

        dnsRecognition.addCameraView(constraintLayout);
    }
}