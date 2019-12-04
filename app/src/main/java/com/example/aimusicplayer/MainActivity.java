package com.example.aimusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.aimusicplayer.App.CHANNEL_ID;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper;
    private ImageView playPauseBtn, nextBtn, previousBtn;
    private TextView songNameTxt;
    private ImageView songLogo;
    private RelativeLayout lower;
    private Button voiceEnableButton;
    private String mode = "ON";
    private MediaPlayer mediaPlayer;
    private int position;
    private ArrayList<File> songs;
    private String mSongName;
    private Animation rotation;
    private MyDialog dialog;
    private NotificationManagerCompat notificationManagerCompat;
    private Notification notification;
    private MediaSessionCompat mediaSession;
    private static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        checkVoiceCommandPermission();
        playPauseBtn = findViewById(R.id.play_pause_btn);
        nextBtn = findViewById(R.id.next_btn);
        previousBtn = findViewById(R.id.previous_btn);
        songLogo = findViewById(R.id.logo);
        rotation = AnimationUtils.loadAnimation(this, R.anim.disc_animation);
        rotation.setRepeatCount(Animation.INFINITE);
        songLogo.startAnimation(rotation);
        lower = findViewById(R.id.lower);
        voiceEnableButton = findViewById(R.id.voice_enable_btn);
        songNameTxt = findViewById(R.id.songName);
        parentRelativeLayout = findViewById(R.id.parent_relative_layout);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        recieveValuesAndStartPlay();
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
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
                ArrayList<String> matchesFound = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matchesFound != null) {
                    if ("ON".equals(mode)) {
                        keeper = matchesFound.get(0);
                        if ("stop".equals(keeper) || "stop the song".equals(keeper)) {
                            playPauseSong();
                            Toast.makeText(MainActivity.this, "Result: " + keeper, Toast.LENGTH_LONG).show();
                        } else if ("play".equals(keeper) || "play the song".equals(keeper)) {
                            playPauseSong();
                            Toast.makeText(MainActivity.this, "Result: " + keeper, Toast.LENGTH_LONG).show();

                        } else if ("next".equals(keeper) || "next song".equals(keeper)) {
                            playNextSong();
                            Toast.makeText(MainActivity.this, "Result: " + keeper, Toast.LENGTH_LONG).show();

                        } else if ("back".equals(keeper) || "previous song".equals(keeper)) {
                            playPreviousSong();
                            Toast.makeText(MainActivity.this, "Result: " + keeper, Toast.LENGTH_LONG).show();

                        } else if ("my music".equals(keeper)) {
                            onBackPressed();
                            Toast.makeText(MainActivity.this, "Result: " + keeper, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Unknown expression", Toast.LENGTH_LONG).show();
                        }

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


        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper = null;

                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                }
                return false;
            }
        });
        voiceEnableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("ON".equals(mode)) {
                    mode = "OFF";
                    voiceEnableButton.setText("Voice Enable Mode - OFF");
                    lower.setVisibility(View.VISIBLE);
                } else {
                    mode = "ON";
                    voiceEnableButton.setText("Voice Enable Mode - ON");
                    lower.setVisibility(View.GONE);
                }
            }
        });
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPauseSong();
            }
        });
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.getCurrentPosition() > 0) {
                    playPreviousSong();
                }
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.getCurrentPosition() > 0) {
                    playNextSong();
                }
            }
        });
        mediaSession = new MediaSessionCompat(this, "tag");

        notificationManagerCompat = NotificationManagerCompat.from(this);
        showNotification();


    }

    private void checkVoiceCommandPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void recieveValuesAndStartPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songs");
        mSongName = songs.get(position).getName();
        String songName = intent.getStringExtra("name");
        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);
        position = intent.getIntExtra("position", 0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
    }

    public void playPauseSong() {
        if (mediaPlayer.isPlaying()) {
            playPauseBtn.setImageResource(R.drawable.play);
            mediaPlayer.pause();
            rotation.cancel();

        } else {
            playPauseBtn.setImageResource(R.drawable.pause);
            mediaPlayer.start();

            rotation.setRepeatCount(Animation.INFINITE);
            songLogo.startAnimation(rotation);

        }
    }

    public void playNextSong() {
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();
        position = ((position + 1) % songs.size());
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mSongName = songs.get(position).toString();
        songNameTxt.setText(mSongName);
        mediaPlayer.start();

        if (mediaPlayer.isPlaying()) {
            playPauseBtn.setImageResource(R.drawable.pause);


        } else {
            playPauseBtn.setImageResource(R.drawable.play);


        }
    }

    public void playPreviousSong() {
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();

        position = ((position - 1) < 0 ? (songs.size() - 1) : (position - 1));

        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mSongName = songs.get(position).toString();
        songNameTxt.setText(mSongName);
        mediaPlayer.start();

        if (mediaPlayer.isPlaying()) {
            playPauseBtn.setImageResource(R.drawable.pause);


        } else {
            playPauseBtn.setImageResource(R.drawable.play);


        }
    }

    @Override
    public void onBackPressed() {
        mediaPlayer.stop();
        speechRecognizer.cancel();
        super.onBackPressed();
    }

    private void showDialog() {
        SharedPreferences preferences = getSharedPreferences("file", MODE_PRIVATE);
        Container.preferences = preferences;
        SharedPreferences.Editor editor = Container.preferences.edit();
        editor.putBoolean("key", Container.isChecked);
        dialog = new MyDialog(this);
        if (Container.isChecked == false) {
            dialog.show();
        }
    }

    @Override
    protected void onResume() {
        showDialog();
        super.onResume();

    }

    private void showNotification() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.notificationlogo);
        Intent playPauseIntent = new Intent(this, MyReceiver.class);
        playPauseIntent.putExtra("songStatue","playPauseSong");
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(this, 0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent nextIntent = new Intent(this, MyReceiver.class);
        nextIntent.putExtra("songStatue", "nextSong");
        PendingIntent nextSongPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent,Intent.FILL_IN_DATA);
        Intent previousIntent = new Intent(this, MyReceiver.class);
        previousIntent.putExtra("songStatue", "previousSong");
        PendingIntent previousSongPendingIntent = PendingIntent.getBroadcast(this, 0, previousIntent, Intent.FILL_IN_DATA);


        notification = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle(mSongName)
                .setSmallIcon(R.drawable.ic_audiotrack_black_24dp).setContentText(null)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setLargeIcon(bitmap)
                .addAction(R.drawable.previousnoti, "previous", previousSongPendingIntent)
                .addAction(R.drawable.pausenoti, "pause", playPausePendingIntent)
                .addAction(R.drawable.nextnoti, "next", nextSongPendingIntent)
                .setContentIntent(PendingIntent.getActivity(this,0,new Intent(this,ShowMusicActivity.class),0))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2).setMediaSession(mediaSession.getSessionToken()))
                .build();
        notificationManagerCompat.notify(1, notification);

    }

    public static MainActivity getInstance() {
        return mainActivity;
    }
}
