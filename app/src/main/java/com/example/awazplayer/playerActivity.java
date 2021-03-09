package com.example.awazplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.awazplayer.MainActivity.musicList;

public class playerActivity extends AppCompatActivity {
    Button playbtn, btnnext, btnprev, btnff, btnfr;
    TextView txtsname, txtsstart, txtsstop;
    SeekBar seekmusic;
    BarVisualizer visualizer;
    ImageView imageView;
    String sname;
    public static final String EXTRA_NAME = "song_name";
    MediaPlayer mediaPlayer;
    int position;
    static ArrayList<Music> mySongs;
    Thread updateseekbar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (visualizer != null) {
            visualizer.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnprev = findViewById(R.id.btnprev);
        btnnext = findViewById(R.id.btnnext);
        playbtn = findViewById(R.id.playbtn);
        btnff = findViewById(R.id.btnff);
        btnfr = findViewById(R.id.btnfr);
        txtsname = findViewById(R.id.txtsn);
        txtsstart = findViewById(R.id.txtsstart);
        txtsstop = findViewById(R.id.txtsstop);
        seekmusic = findViewById(R.id.seekbar);
        visualizer = findViewById(R.id.blast);
        imageView = findViewById(R.id.imageView);


        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        // mySongs = (ArrayList<Music>)bundle.getSerializable("songs");
//        Log.e("tite 2 ",mySongs.get(position).getTitle());
        String songName = i.getStringExtra("songname");
        position = getIntent().getIntExtra("pos", 0);
        Log.e("pos", String.valueOf(position));
        mySongs = musicList;
        txtsname.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).getPath());
        sname = musicList.get(position).getTitle();
        Log.e("path ", String.valueOf(uri));
        txtsname.setText(sname);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=new MediaPlayer();
            try {
                mediaPlayer.setDataSource(getApplicationContext(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // mediaPlayer = MediaPlayer.create(playerActivity.this, uri);
            mediaPlayer.start();
        } else {
            mediaPlayer=new MediaPlayer();
            try {
                mediaPlayer.setDataSource(getApplicationContext(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
        }


        updateseekbar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentposition = 0;
                while (currentposition < totalDuration) {
                    try {
                        sleep(500);
                        currentposition = mediaPlayer.getCurrentPosition();
                        seekmusic.setProgress(currentposition);
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        seekmusic.setMax(mediaPlayer.getDuration());
        updateseekbar.start();
        seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        String endTime = createTime(mediaPlayer.getDuration());
        txtsstop.setText(endTime);
        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                txtsstart.setText(currentTime);
                handler.postDelayed(this, delay);

            }
        }, delay);

        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    playbtn.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                } else {
                    playbtn.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });


        //next listener
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btnnext.performClick();

            }
        });
        int audiosessionId = mediaPlayer.getAudioSessionId();
        if (audiosessionId != -1) {
            visualizer.setAudioSessionId(audiosessionId);
        }

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position + 1) % mySongs.size());
                Uri u = Uri.parse(mySongs.get(position).getPath());
                //mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mySongs.get(position).getTitle();
                txtsname.setText(sname);
                mediaPlayer=new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(getApplicationContext(),u);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // mediaPlayer = MediaPlayer.create(playerActivity.this, uri);
                mediaPlayer.start();
                playbtn.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imageView);
                int audiosessionId = mediaPlayer.getAudioSessionId();
                if (audiosessionId != -1) {
                    visualizer.setAudioSessionId(audiosessionId);
                }
            }
        });

        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position - 1) < 0) ? (mySongs.size() - 1) : (position - 1);
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mySongs.get(position).getTitle();
                txtsname.setText(sname);
                mediaPlayer.start();
                playbtn.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imageView);
                int audiosessionId = mediaPlayer.getAudioSessionId();
                if (audiosessionId != -1) {
                    visualizer.setAudioSessionId(audiosessionId);
                }

            }
        });
        btnff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                }
            }
        });
        btnfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
                }

            }
        });


    }


    public void startAnimation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animator.start();
    }

    public String createTime(int duration) {
        String time = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;
        time += min + ":";
        if (sec < 0) {
            time += "0";
        }
        time += sec;
        return time;
    }


}
