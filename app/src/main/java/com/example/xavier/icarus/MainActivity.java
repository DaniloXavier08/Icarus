package com.example.xavier.icarus;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    // MediaPlayer is responsable to run MIDI
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Save Instance and define activity to show
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Floating Action button
        FloatingActionButton fab = findViewById(R.id.fab);
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
        Button buttonPlay = findViewById(R.id.play);
        Button buttonPause = findViewById(R.id.pause);
        buttonPlay.setOnClickListener(buttonPlayOnClickListener);
        buttonPause.setOnClickListener(buttonPauseOnClickListener);

        Button buttonOpen = findViewById(R.id.open);
        buttonOpen.setOnClickListener(buttonOpenOnClickListener);

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
                try{
                    mediaPlayer.start();
                    Toast.makeText(MainActivity.this, "mediaPlayer.start()", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
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


    Button.OnClickListener buttonOpenOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v){
            openMidiFile();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Toast.makeText(MainActivity.this, resultData.toString(), Toast.LENGTH_LONG).show();
                mediaPlayer = MediaPlayer.create(this, uri);
            }
        }
    }


    private static final int READ_REQUEST_CODE = 42;

    protected void openMidiFile() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("audio/midi");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

}
