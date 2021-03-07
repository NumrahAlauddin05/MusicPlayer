package com.example.awazplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class playerActivity extends AppCompatActivity {
    Button playbtn,btnnext,btnprev,btnff,btnfr;
    TextView txtsname,txtsstart,txtsstop;
    SeekBar seekmusic;
    BarVisualizer visualizer;
    String sname;
      public static final String EXTRA_NAME="song_name";
      static MediaPlayer mediaPlayer;
      int position;
      ArrayList<File> mySongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        btnprev=findViewById(R.id.btnprev);
        btnnext=findViewById(R.id.btnnext);
        playbtn= findViewById(R.id.playbtn);
        btnff=findViewById(R.id.btnff);
        btnfr=findViewById(R.id.btnfr);
        txtsname=findViewById(R.id.txtsn);
        txtsstart=findViewById(R.id.txtsstart);
        txtsstop=findViewById(R.id.txtsstop);
        seekmusic =findViewById(R.id.seekbar);
        visualizer = findViewById(R.id.blast);
        if(mediaPlayer !=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        mySongs =(ArrayList)bundle.getParcelableArrayList("songs");
        String songName =i.getStringExtra("songname");
        position = bundle.getInt("pos",0);
        txtsname.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        sname = mySongs.get(position).getName();
        txtsname.setText(sname);

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();
        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    playbtn.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }
                else{
                    playbtn.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });
        //next listener
           mediaPlayer .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

               @Override
               public void onCompletion(MediaPlayer mediaPlayer) {
                   btnnext.performClick();

               }
           });
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mediaPlayer.stop();
               mediaPlayer.release();
               position =((position+1)%mySongs.size());
               Uri u = Uri.parse(mySongs.get(position).toString());
               mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
               sname= mySongs.get(position).getName();
               txtsname.setText(sname);
               mediaPlayer.start();
               playbtn.setBackgroundResource(R.drawable.ic_pause);
            }
        });

        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position-1)<0)?(mySongs.size()-1):(position-1);
                Uri u=Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(position).getName();
                txtsname.setText(sname);
                mediaPlayer.start();
                playbtn.setBackgroundResource(R.drawable.ic_pause);

            }
        });




    }
}
