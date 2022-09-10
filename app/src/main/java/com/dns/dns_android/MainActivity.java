package com.dns.dns_android;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dns.dns_lib.DnsPermission;
import com.dns.dns_lib.DnsRecognition;

public class MainActivity extends AppCompatActivity {
    private boolean enable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (DnsPermission.requestCameraPermission(MainActivity.this, this, 123)) {
            showCameraPreview();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 123:
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
        DnsRecognition dnsRecognition = new DnsRecognition(getApplicationContext(), DnsRecognition.BACK_CAMERA);

        ConstraintLayout constraintLayout = findViewById(R.id.cl_root);

        dnsRecognition.addCameraView(constraintLayout);
    }
}