package com.example.xavier.icarus;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.midisheetmusic.FileUri;
import com.midisheetmusic.IconArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;

    // Button onClick methods.
    FloatingActionButton.OnClickListener fabOpenOnClickListener = new FloatingActionButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            openMidiFile();
        }
    };

    /**
     * The complete list of midi files
     */
    private ArrayList<FileUri> songList;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Save Instance and define activity to show
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set files list
        listView = findViewById(R.id.file_list);
        loadList();
        IconArrayAdapter<FileUri> adapter = new IconArrayAdapter<>(this, R.id.file_list, songList);
        listView.setAdapter(adapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                FileUri fileUri = (FileUri) listView.getAdapter().getItem(position);
                openSheetMusic(fileUri);
            }
        });

        // Button stuff
        FloatingActionButton buttonOpen = findViewById(R.id.open);
        buttonOpen.setOnClickListener(fabOpenOnClickListener);
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
            if (resultData != null) {
                try {
                    Uri uri = resultData.getData();
                    FileUri file = new FileUri(this.getContentResolver(), uri, MediaStore.MediaColumns.DISPLAY_NAME);
                    openSheetMusic(file);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Erro: " + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select a MIDI file.
     */
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

    /**
     * Load all the sample midi songs from the assets directory into songList.
     * Look for files ending with ".mid"
     */
    void loadList() {
        songList = new ArrayList<>();

        try {
            AssetManager assets = this.getResources().getAssets();
            String files[] = assets.list("");
            if (files != null) {
                for (String file : files) {
                    if (file.endsWith(".mid")) {
                        FileUri uri = new FileUri(assets, file, file);
                        songList.add(uri);
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG + 20).show();
        }
        Collections.sort(songList, songList.get(0));
    }

    /**
     * Open a MIDI file on Sheet Music Activity.
     *
     * @param file A file URI
     */
    private void openSheetMusic(FileUri file) {
        try {
            byte[] data = file.getData();

            Intent intent = new Intent(this, SheetMusicActivity.class);
            intent.putExtra(SheetMusicActivity.MidiDataID, data);
            intent.putExtra(SheetMusicActivity.MidiTitleID, file.toString());
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }
}
