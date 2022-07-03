package com.example.music_mp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class Play_music extends AppCompatActivity {
    Button btnplay,btnrewind,btnprevious,btnforward,btnnext;
    TextView txttitle,txtstarttime,txtendtime;
    SeekBar seekBar;
    BarVisualizer barVisualizer;
    ImageView imageView;
    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mysongs;
    String songnames;
    Thread UpdateSeekbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        barVisualizer = findViewById(R.id.blast);
        anhxa();
        //barVisualizer = findViewById(R.id.blast);
        if(mediaPlayer != null){
            mediaPlayer.start();
            mediaPlayer.release();
        }
        Intent intent  =getIntent();
        Bundle bundle = intent.getExtras();
        mysongs = (ArrayList)bundle.getParcelableArrayList("songs");
        String name = intent.getStringExtra("songname");
        position  = bundle.getInt("pos",0);
        txttitle.setSelected(true);
        Uri uri = Uri.parse(mysongs.get(position).toString());
        songnames = mysongs.get(position).getName();
        txttitle.setText(songnames);

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

        UpdateSeekbar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while(currentPosition<totalDuration){
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

        seekBar.setMax(mediaPlayer.getDuration());
        UpdateSeekbar.start();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        String endtime = createTime(mediaPlayer.getDuration());
        txtendtime.setText(endtime);

        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String curentTime = createTime(mediaPlayer.getCurrentPosition());
                txtstarttime.setText(curentTime);
                handler.postDelayed(this,delay);
            }
        },delay);

        btnforward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 1000);
                }
            }
        });

        btnrewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-1000);
                }
            }
        });

        int autoSesstion = mediaPlayer.getAudioSessionId();
        if(autoSesstion != -1){
            barVisualizer.setAudioSessionId(autoSesstion);
        }

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    btnplay.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                    mediaPlayer.pause();
                }else{
                    btnplay.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    mediaPlayer.start();

                    TranslateAnimation translateAnimation = new TranslateAnimation(-25,25,-25,25);
                    translateAnimation.setInterpolator(new AccelerateInterpolator());
                    translateAnimation.setDuration(600);
                    translateAnimation.setFillAfter(true);
                    translateAnimation.setFillEnabled(true);
                    translateAnimation.setRepeatMode(Animation.REVERSE);
                    translateAnimation.setRepeatCount(2);
                    imageView.startAnimation(translateAnimation);
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnnext.performClick();
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%mysongs.size());
                Uri uri  = Uri.parse(mysongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                songnames = mysongs.get(position).getName();
                txttitle.setText(songnames);
                txtendtime.setText(createTime(mediaPlayer.getDuration()));
                mediaPlayer.start();
                startAnimation(imageView,360f);
            }
        });
        btnprevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1)<0) ? (mysongs.size()-1):position-1;
                Uri uri = Uri.parse(mysongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                songnames = mysongs.get(position).getName();
                txttitle.setText(songnames);
               txtendtime.setText(createTime(mediaPlayer.getDuration()));
                mediaPlayer.start();
                startAnimation(imageView,-360f);
            }
        });

    }


    public String createTime(int duration){
        String time = "";

        int min = duration/1000/60;
        int sec = duration/1000%60;

        time = time + min + ":";
        if(sec<10){
            time += "0";
        }
        time += sec;
        return time;
    }
    public void startAnimation(View view,Float degree){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView,"rotation",0f,degree);
        objectAnimator.setDuration(1000);
        AnimatorSet animatorSet  =new AnimatorSet();
        animatorSet.playTogether(objectAnimator);
        animatorSet.start();
    }

    private void anhxa() {
        btnplay  = findViewById(R.id.btnplay);
        btnforward = findViewById(R.id.btnforward);
        btnnext = findViewById(R.id.btnnext);
        btnprevious = findViewById(R.id.btnprvious);
        btnrewind = findViewById(R.id.btnrewind);
        txttitle = findViewById(R.id.txtsong);
        seekBar  =findViewById(R.id.seekbar);

        txtstarttime = findViewById(R.id.starttime);
        txtendtime = findViewById(R.id.endttime);
        imageView = findViewById(R.id.imageviewplay);
    }
}