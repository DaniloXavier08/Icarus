package com.example.xavier.icarus;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // MediaPlayer is responsable to run MIDI
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Save Instance and define activity to show
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Floating Action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Play foi acionado!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Media Player stuff
        mediaPlayer = MediaPlayer.create(this, R.raw.happy_birthday);

        // Button stuff
        Button buttonPlay = (Button) findViewById(R.id.play);
        Button buttonPause = (Button) findViewById(R.id.pause);
        buttonPlay.setOnClickListener(buttonPlayOnClickListener);
        buttonPause.setOnClickListener(buttonPauseOnClickListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Buttons onClick methods
    Button.OnClickListener buttonPlayOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                Toast.makeText(MainActivity.this, "mediaPlayer.star", Toast.LENGTH_SHORT).show();
            }
        }
    };
    Button.OnClickListener buttonPauseOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                Toast.makeText(MainActivity.this, "mediaPlayer.pause()", Toast.LENGTH_SHORT).show();
            }
        }
    };


    private void showFiles() {
        try {

            List<String> minhaLista;
            minhaLista = new ArrayList<String>();

            String path;
            File sdCardRoot = Environment.getExternalStorageDirectory();
            File dir = new File(sdCardRoot.getAbsolutePath() + "/Test/");

            Toast.makeText(this, sdCardRoot.toString(), Toast.LENGTH_LONG).show();

            if (dir.exists()) {

                if (dir.listFiles() != null) {
                    for (File f : dir.listFiles()) {
                        if (f.isFile()) {
                            path = f.getName();
                            minhaLista.add(path);
                        }

                        //if (path.contains(".mid")) {

                        //}
                    }
                }
            }

            ListView listView = (ListView) findViewById(R.id.lista_arquivos);

            listView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, minhaLista));

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

}
