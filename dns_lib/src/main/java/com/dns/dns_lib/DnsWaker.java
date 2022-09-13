package com.dns.dns_lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class DnsWaker {
    public static String SAMPLE_TTS_VOICE_RECOGNIZE_CHECK_SENTENCE = "혹시 피곤하신가요?";
    public static String SAMPLE_TTS_VOICE_RECOGNIZE_NOT_SLEEPING = "확인되었습니다.";
    public static String SAMPLE_TTS_VOICE_RECOGNIZE_SLEEPING = "창문을 열거나 신나는 노래를 들으시는걸 추천드립니다.";
    private final ArrayList<String> checkWords = new ArrayList<>(Arrays.asList("아니", "안자", "안피곤", "안 피곤"));
    private TextToSpeech textToSpeech;
    private final SpeechRecognizer speechRecognizer;
    private final Intent speechRecognizerIntent;

    public DnsWaker(Context context) {
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int setLanguageResult = textToSpeech.setLanguage(Locale.KOREA);
                if (setLanguageResult == TextToSpeech.LANG_NOT_SUPPORTED || setLanguageResult == TextToSpeech.LANG_MISSING_DATA) {
                    Log.e("TTS Initialization", "TTS initialize failed.");
                } else {
                    Log.d("TTS Initialization", "TTS initialized successfully.");
                }
                textToSpeech.setPitch(0.5f);
                textToSpeech.setSpeechRate(1.0f);
            } else {
                Log.e("TTS Initialization", "TTS initialize failed.");
            }
        });
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
    }

    public void speak(String content, boolean stopPreviousSpeak) {
        textToSpeech.speak(content, stopPreviousSpeak ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD, null, "tts");
    }

    public void listen(Context context, RecognitionListener recognizerListener) {
        ((Activity) context).runOnUiThread(() -> {
            speechRecognizer.setRecognitionListener(recognizerListener);
            speechRecognizer.startListening(speechRecognizerIntent);
        });
    }

    public void runVoiceRecognizeWakerExample(Context context, boolean speakCheckPassedVoice, int alertSoundResourceId) {
        new Thread(() -> {
            speak(SAMPLE_TTS_VOICE_RECOGNIZE_CHECK_SENTENCE, false);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            listen(context, new RecognitionListener() {
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
                public void onError(int errorCode) {
                    if (errorCode == SpeechRecognizer.ERROR_NO_MATCH) {
                        // User not response.
                        if (alertSoundResourceId != -1) {
                            // Alert sound.
                            MediaPlayer mediaPlayer = MediaPlayer.create(context, alertSoundResourceId);
                            mediaPlayer.setOnCompletionListener(mediaPlayer1 -> {
                                speak(SAMPLE_TTS_VOICE_RECOGNIZE_SLEEPING, false);
                            });
                            mediaPlayer.start();
                        } else {
                            speak(SAMPLE_TTS_VOICE_RECOGNIZE_SLEEPING, false);
                        }
                    }
                }

                @Override
                public void onResults(Bundle bundle) {
                    ArrayList<String> words = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    boolean correctAnswer = false;
                    for (String word : words) {
                        for (String checkWord : checkWords) {
                            if (word.contains(checkWord)) {
                                correctAnswer = true;
                                break;
                            }
                        }
                    }

                    if (correctAnswer) {
                        // Check words contain word spoken by the user.
                        if (speakCheckPassedVoice) {
                            // Speak check passed voice is true.
                            speak(SAMPLE_TTS_VOICE_RECOGNIZE_NOT_SLEEPING, false);
                        }
                    } else {
                        // User speak wrong words.
                        if (alertSoundResourceId != -1) {
                            // Alert sound.
                            MediaPlayer mediaPlayer = MediaPlayer.create(context, alertSoundResourceId);
                            mediaPlayer.setOnCompletionListener(mediaPlayer1 -> {
                                speak(SAMPLE_TTS_VOICE_RECOGNIZE_SLEEPING, false);
                            });
                            mediaPlayer.start();
                        } else {
                            speak(SAMPLE_TTS_VOICE_RECOGNIZE_SLEEPING, false);
                        }
                    }
                }

                @Override
                public void onPartialResults(Bundle bundle) {
                }

                @Override
                public void onEvent(int i, Bundle bundle) {
                }
            });
        }).start();
    }
}
