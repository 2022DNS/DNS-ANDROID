package com.dns.dns_lib;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

public class DnsWaker {
    private int warningCount;
    private TextToSpeech textToSpeech;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;

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
    }

    public void speak(String content, boolean stopPreviousSpeak) {
        textToSpeech.speak(content, stopPreviousSpeak ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD, null, "tts");
    }

    public void listen(Context context, RecognitionListener recognizerListener) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(recognizerListener);
        speechRecognizer.startListening(speechRecognizerIntent);
    }

    public void setSpeakEndListener(UtteranceProgressListener utteranceProgressListener) {
        textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener);
    }
}
