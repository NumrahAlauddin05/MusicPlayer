package com.example.awazplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.InetAddresses;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.security.spec.EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String[] items;
   public static ArrayList<Music> musicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listViewSong);
        runtimePermission();
    }

    public void runtimePermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaySongs();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }

    public ArrayList<Music> findSong() {

//        ArrayList<File> arrayList = new ArrayList<>();
//        File[] files = file.listFiles();

        musicList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] songDetail = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA

        };
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, songDetail, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String title = cursor.getString(1);
                String path = cursor.getString(2);

                Log.e("pos",title);
                Music music = new Music(title, path);
                musicList.add(music);

            }
            cursor.close();
            Log.e("tiltle ",musicList.get(0).getTitle());
        }

//        for (File singlefile : files) {
//            Toast.makeText(this, singlefile.getName(), Toast.LENGTH_SHORT).show();
//            if (singlefile.isDirectory() && !singlefile.isHidden()) {
//                arrayList.addAll(findSong(singlefile));
//            } else {
//                if (singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav")) {
//                    arrayList.add(singlefile);
//                }
//            }
//        }
        return musicList;
    }

    void displaySongs() {
        final ArrayList<Music> mySongs;

        mySongs = findSong();
        items = new String[mySongs.size()];


        for (int i = 0; i < mySongs.size(); i++) {
            items[i] = mySongs.get(i).getTitle().toString().replace(".mp3", "").replace(",wav", "");
        }

       /*ArrayAdapter<String> myAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        listView .setAdapter(myAdapter);*/
        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                String songName = (String) listView.getItemAtPosition(i);
                Bundle bundle = new Bundle();
                Intent playIntent = new Intent(MainActivity.this, playerActivity.class);
                bundle.putSerializable("songs", mySongs);
                playIntent.putExtra("songname", songName);
                playIntent.putExtra("pos", i);
                startActivity(playIntent);
            }
        });
    }


    class customAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            View myView = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView textsong = myView.findViewById(R.id.txtsongname);
            textsong.setSelected(true);
            textsong.setText(items[i]);
            return myView;
        }
    }

}
