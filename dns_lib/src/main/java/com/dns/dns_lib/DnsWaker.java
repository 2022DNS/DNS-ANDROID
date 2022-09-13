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

/**
 * DnsWaker provides a method for waking the driver. You can also implement new notification logic using internal methods such as speak and listen.
 *
 * @author Sohn Young Jin
 * @since 1.0.0
 */
public class DnsWaker {
    /**
     * Sample sentences for tts.
     */
    public static String SAMPLE_TTS_VOICE_RECOGNIZE_CHECK_SENTENCE = "혹시 피곤하신가요?";
    public static String SAMPLE_TTS_VOICE_RECOGNIZE_NOT_SLEEPING = "확인되었습니다.";
    public static String SAMPLE_TTS_VOICE_RECOGNIZE_SLEEPING = "창문을 열거나 신나는 노래를 들으시는걸 추천드립니다.";

    /**
     * Recognize words for check drowsy driving state.
     */
    private final ArrayList<String> checkWords = new ArrayList<>(Arrays.asList("아니", "안자", "안피곤", "안 피곤", "앉아"));

    /**
     * Text to speech.
     */
    private TextToSpeech textToSpeech;

    /**
     * Speak recognizer.
     */
    private final SpeechRecognizer speechRecognizer;

    /**
     * Intent for speech recognizer.
     */
    private final Intent speechRecognizerIntent;

    /**
     * DnsWaker constructor.
     *
     * @param context Context.
     */
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

    /**
     * Speak with text to speech.
     *
     * @param sentence          Sentence to speak with tts.
     * @param stopPreviousSpeak Stop previous speak and speak current content.
     */
    public void speak(String sentence, boolean stopPreviousSpeak) {
        textToSpeech.speak(sentence, stopPreviousSpeak ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD, null, "tts");
    }

    /**
     * Recognize voice with speech recognizer.
     *
     * @param context            Context.
     * @param recognizerListener Recognize listener.
     */
    public void listen(Context context, RecognitionListener recognizerListener) {
        ((Activity) context).runOnUiThread(() -> {
            speechRecognizer.setRecognitionListener(recognizerListener);
            speechRecognizer.startListening(speechRecognizerIntent);
        });
    }

    /**
     * Alert to driver with provided sound resource when drowsy driving detected.
     *
     * @param context              Context.
     * @param alertSoundResourceId Alert sound resource id.
     * @param sentence             Sentence to speak.
     * @param speakAfterSound      Speak sentence after alert sound end.
     */
    public void alert(Context context, int alertSoundResourceId, String sentence, boolean speakAfterSound) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, alertSoundResourceId);
        if (speakAfterSound) {
            mediaPlayer.setOnCompletionListener(mediaPlayer1 -> {
                speak(sentence, false);
            });
        } else {
            speak(sentence, false);
        }
        mediaPlayer.start();
    }

    /**
     * Example of voice recognize waker.
     *
     * @param context               Context.
     * @param speakCheckPassedVoice Speak check voice when driver pass the test.
     * @param alertSoundResourceId  Alert sound resource id for alert when detected driver drowsy driving.
     */
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
                            alert(context, alertSoundResourceId, SAMPLE_TTS_VOICE_RECOGNIZE_SLEEPING, true);
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
                            alert(context, alertSoundResourceId, SAMPLE_TTS_VOICE_RECOGNIZE_SLEEPING, true);
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
