package com.example.awazplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class Testing extends AppCompatActivity {
    int position;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        Bundle bundle=getIntent().getExtras();
        position=bundle.getInt("pos");

        uri=Uri.parse(MainActivity.musicList.get(position).getPath());
        MediaPlayer player = new MediaPlayer();
        try {
            Log.e("uri ",String.valueOf(uri));
            Log.e("media ",player.toString());
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(getApplicationContext(),uri);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
