package android.jbudz.me.textlocker.ui;

import android.app.Fragment;
import android.content.Intent;
import android.jbudz.me.textlocker.model.Note;


public class TextListActivity extends SingleFragmentActivity
    implements TextListFragment.Callbacks {
    protected Fragment createFragment() {
        return new TextListFragment();
    }

    public void onEditNote(Note note) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(NoteFragment.NOTE_ID, note.getId());
        startActivity(intent);

    }
}
