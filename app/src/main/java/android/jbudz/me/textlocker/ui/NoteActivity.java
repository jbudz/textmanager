package android.jbudz.me.textlocker.ui;

import android.app.Fragment;

import java.util.UUID;

/**
 * Created by jon on 7/3/14.
 */
public class NoteActivity extends SingleFragmentActivity implements NoteFragment.Callbacks {
    @Override
    protected Fragment createFragment() {
        UUID noteId = (UUID) getIntent().getSerializableExtra(NoteFragment.NOTE_ID);
        return NoteFragment.newInstance(noteId);
    }

    @Override
    public void onDone() {
        finish();
    }
}
