package com.dns.dns_android;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dns.dns_lib.DnsPermission;
import com.dns.dns_lib.DnsRecognition;
import com.dns.dns_lib.DnsRecognitionListener;
import com.dns.dns_lib.DnsWaker;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DnsWaker dnsWaker = new DnsWaker(MainActivity.this);

        String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (DnsPermission.requestPermissions(MainActivity.this, this, PERMISSION_REQUEST_CODE, new ArrayList<>(Arrays.asList(permissions)))) {
//            showCameraPreview();
        }

        findViewById(R.id.btnSpeak).setOnClickListener(view -> {
            dnsWaker.setSpeakEndListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String s) {

                }

                @Override
                public void onDone(String s) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dnsWaker.listen(MainActivity.this, new RecognitionListener() {
                                @Override
                                public void onReadyForSpeech(Bundle bundle) {
                                }

                                @Override
                                public void onBeginningOfSpeech() {
                                }

                                @Override
                                public void onRmsChanged(float v) {
                                }

                                @Override
                                public void onBufferReceived(byte[] bytes) {
                                }

                                @Override
                                public void onEndOfSpeech() {
                                }

                                @Override
                                public void onError(int i) {
                                }

                                @Override
                                public void onResults(Bundle bundle) {
                                    ArrayList<String> words = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                                    boolean correctAnswer = false;
                                    for (String word : words) {
                                        if (word.contains("안자") || words.contains("앉아") || words.contains("아니")) {
                                            correctAnswer = true;
                                            break;
                                        }
                                    }
                                    if (correctAnswer) {
                                        dnsWaker.speak("확인되었습니다.", false);
                                    } else {
                                        dnsWaker.speak("졸음운전이 의심됩니다. 창문을 열거나 라디오를 트시는 걸 추천드립니다.", false);
                                    }
                                }

                                @Override
                                public void onPartialResults(Bundle bundle) {
                                }

                                @Override
                                public void onEvent(int i, Bundle bundle) {
                                }
                            });
                        }
                    });
                }

                @Override
                public void onError(String s) {

                }
            });
            dnsWaker.speak("졸음운전이 의심됩니다. 혹시 피곤하신가요?", false);
        });
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