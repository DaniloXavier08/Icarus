package com.example.xavier.icarus;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.midisheetmusic.ClefSymbol;
import com.midisheetmusic.MidiFile;
import com.midisheetmusic.MidiFileException;
import com.midisheetmusic.MidiOptions;
import com.midisheetmusic.MidiPlayer;
import com.midisheetmusic.Piano;
import com.midisheetmusic.SheetMusic;
import com.midisheetmusic.TimeSigSymbol;

import java.util.zip.CRC32;

public class SheetMusicActivity extends Activity {

    public static final String MidiDataID = "MidiDataID";
    public static final String MidiTitleID = "MidiTitleID";

    private MidiPlayer player;   /* The play/stop/rewind toolbar */
    private Piano piano;         /* The piano at the top */
    private SheetMusic sheet;    /* The sheet music */
    private LinearLayout layout; /* THe layout */
    private MidiFile midiFile;   /* The midi file to play */
    private MidiOptions options; /* The options for sheet music and sound */
    private long midiCRC;      /* CRC of the midi bytes */

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ClefSymbol.LoadImages(this);
        TimeSigSymbol.LoadImages(this);
        MidiPlayer.LoadImages(this);

        // Parse the MidiFile from the ray bytes
        byte[] data = this.getIntent().getByteArrayExtra(MidiDataID);
        String title = this.getIntent().getStringExtra(MidiTitleID);
        this.setTitle("MidiSheetMusic: " + title);
        try{
            midiFile = new MidiFile(data, title);
        } catch (MidiFileException e){
            this.finish();
            return;
        }

        // Initialize the settings (MidiOptions).
        // If previous settings have been saved, used those
        options = new MidiOptions(midiFile);
        CRC32 crc = new CRC32();
        crc.update(data);
        midiCRC = crc.getValue();
        SharedPreferences settings = getPreferences(0);
        options.scrollVert = settings.getBoolean("scrollVert", false);
        options.shade1Color = settings.getInt("shade1Color", options.shade1Color);
        options.shade2Color = settings.getInt("shade2Color", options.shade2Color);
        String json = settings.getString("" + midiCRC, null);
        MidiOptions savedOptions = MidiOptions.fromJson(json);
        if (savedOptions != null) {
            options.merge(savedOptions);
        }
        createView();
        createSheetMusic(options);
    }

    /**
     * Pause the music if the user backs to main activity.
     */
    public void onStop(){
        super.onStop();
        player.Pause();
    }

    /**
     * Create the Midi Player view
     */
    private void createView () {
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        player = new MidiPlayer(this);
        piano = new Piano(this);
        layout.addView(player);
        layout.addView(piano);
        setContentView(layout);
        player.SetPiano(piano);
        layout.requestLayout();
    }

    /**
     * Create the Sheet Music view with the given options.
     * @param options
     */
    private void createSheetMusic(MidiOptions options) {
        if (sheet != null) {
            layout.removeView(sheet);
        }
        if (!options.showPiano) {
            piano.setVisibility(View.GONE);
        }
        else {
            piano.setVisibility(View.VISIBLE);
        }
        sheet = new SheetMusic(this);
        sheet.init(midiFile, options);
        sheet.setPlayer(player);
        layout.addView(sheet);
        piano.SetMidiFile(midiFile, options, player);
        piano.SetShadeColors(options.shade1Color, options.shade2Color);
        player.SetMidiFile(midiFile, options, sheet);
        layout.requestLayout();
        sheet.callOnDraw();
    }
}
