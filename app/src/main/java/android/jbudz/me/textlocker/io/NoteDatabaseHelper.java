package android.jbudz.me.textlocker.io;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.jbudz.me.textlocker.model.Note;
import android.jbudz.me.textlocker.util.FileUtils;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jon on 7/5/14.
 */
public class NoteDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "textlocker.db";

    private static final int VERSION = 1;
    private Context mContext;

    public static final String TABLE_NOTE = "note";
    public static final String COLUMN_NOTE_TITLE = "title";
    public static final String COLUMN_NOTE_CONTENT = "content";
    public static final String COLUMN_NOTE_UUID = "uuid";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NOTE_SALT = "salt";
    public static final String DB_FILEPATH = "/data/data/android.jbudz.me.textlocker/databases/" + DB_NAME;

    public NoteDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table note (" +
                "_id integer primary key autoincrement," +
                " title VARCHAR," +
                " content VARCHAR," +
                " uuid VARCHAR," +
                " salt BLOB) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public synchronized long insertNote(Note note) {
        if (noteExists(note)) {
            return updateNote(note);
        }

        ContentValues cv = getCvFromNote(note);
        return getWritableDatabase().insert(TABLE_NOTE, null, cv);
    }

    public synchronized long updateNote(Note note) {
        String[] selectionArgs = {
                note.getId().toString()
        };
        ContentValues cv = getCvFromNote(note);
        return getWritableDatabase().update(TABLE_NOTE, cv, COLUMN_NOTE_UUID + " = ?", selectionArgs);
    }

    public synchronized int deleteNote(Note note) {
        String[] selectionArgs = {
                note.getId().toString()
        };
        return getWritableDatabase().delete(TABLE_NOTE, COLUMN_NOTE_UUID + " = ?", selectionArgs);

    }

    public synchronized Cursor getNotes() {
        String sortOrder = COLUMN_ID + " asc";
        return getReadableDatabase().query(TABLE_NOTE, null, null, null, null, null, sortOrder);
    }

    private synchronized boolean noteExists(Note note) {
        String[] selectionArgs = {
                note.getId().toString()
        };
        Cursor c = getReadableDatabase().query(TABLE_NOTE, null, COLUMN_NOTE_UUID + " = ?", selectionArgs,
                null, null, null, null);
        return c.getCount() > 0;
    }

    private ContentValues getCvFromNote(Note note) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOTE_TITLE, note.getTitle());
        cv.put(COLUMN_NOTE_CONTENT, note.getText());
        cv.put(COLUMN_NOTE_UUID, note.getId().toString());
        cv.put(COLUMN_NOTE_SALT, note.getSalt());
        return cv;
    }

    public boolean importDatabase() throws IOException {
        close();
        File newDb = new File(mContext.getExternalFilesDir(null), DB_NAME);
        File oldDb = new File(DB_FILEPATH);
        if(newDb.exists()) {
            FileUtils.copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
            getWritableDatabase().close();
            return true;
        }
        return false;
    }

    public File exportDatabase() throws IOException {
        File currentDb = new File(DB_FILEPATH);
        String extState = Environment.getExternalStorageState();
        if (extState.equals(Environment.MEDIA_MOUNTED)) {
            File exportDb = new File(mContext.getExternalFilesDir(null), DB_NAME);
            FileUtils.copyFile(new FileInputStream(currentDb), new FileOutputStream(exportDb));
            return exportDb;
        }
        return null;
    }

}
