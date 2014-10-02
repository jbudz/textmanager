package android.jbudz.me.textlocker.model;

import android.content.Context;
import android.database.Cursor;
import android.jbudz.me.textlocker.io.NoteDatabaseHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by jon on 7/5/14.
 */
public class NoteList {
    private ArrayList<Note> mNotes;
    private NoteDatabaseHelper mNoteDb;

    private static NoteList sNoteList;
    private Context mAppContext;

    private NoteList(Context appContext) {
        mAppContext = appContext;
        mNotes = new ArrayList<Note>();
        mNoteDb = new NoteDatabaseHelper(appContext);
        populateNotes();
    }

    private void populateNotes() {
        Cursor c = mNoteDb.getNotes();
        while (c.moveToNext()) {
            String title = c.getString(c.getColumnIndex(NoteDatabaseHelper.COLUMN_NOTE_TITLE));
            String content = c.getString(c.getColumnIndex(NoteDatabaseHelper.COLUMN_NOTE_CONTENT));
            String UUID = c.getString(c.getColumnIndex(NoteDatabaseHelper.COLUMN_NOTE_UUID));
            byte[] salt = c.getBlob(c.getColumnIndex(NoteDatabaseHelper.COLUMN_NOTE_SALT));

            Note n = new Note();
            n.setSalt(salt);
            n.setText(content);
            n.setTitle(title);
            n.setIdByString(UUID);
            addNote(n);
        }
    }

    public static NoteList get(Context context) {
        if (sNoteList == null) {
            sNoteList = new NoteList(context.getApplicationContext());
        }
        return sNoteList;
    }

    public Note getNote(UUID uuid) {
        for (Note n : mNotes) {
            if (n.getId().equals(uuid)) {
                return n;
            }
        }
        return null;
    }

    public void removeEmptyNotes() {
        for (Note n : mNotes) {
            if ((n.getText() == null || n.getText().length() == 0) &&
                    n.getTitle() == null || n.getTitle().length() == 0) {
                deleteNote(n);
            }
        }
    }

    public void saveToDb(UUID uuid) {
        mNoteDb.insertNote(getNote(uuid));
    }

    public ArrayList<Note> getNotes() {
        return mNotes;
    }

    public void addNote(Note note) {
        mNotes.add(note);
    }

    public void deleteNote(Note note) {
        mNotes.remove(note);
        mNoteDb.deleteNote(note);
    }

    public void clear() {
        mNotes.clear();
    }

    public File exportDataBase() throws IOException {
        return mNoteDb.exportDatabase();
    }

    public boolean importDataBase() throws IOException {
        this.clear();
        mNoteDb.importDatabase();
        this.populateNotes();
        return true;
    }
}
