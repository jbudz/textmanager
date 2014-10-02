package android.jbudz.me.textlocker.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.jbudz.me.textlocker.R;
import android.jbudz.me.textlocker.model.Note;
import android.jbudz.me.textlocker.model.NoteList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.UUID;

/**
 * Created by jon on 7/3/14.
 */
public class NoteFragment extends Fragment {
    public static final String NOTE_ID = "me.jbudz.android.NOTE_ID";
    private static final String TAG = "NoteFragment";
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onDone();
    }

    private Note mNote;
    private UUID mNoteId;
    private EditText mTitle;
    private EditText mContent;
    private Button mDoneButton;

    public static NoteFragment newInstance(UUID noteId) {
        Bundle args = new Bundle();
        args.putSerializable(NOTE_ID, noteId);
        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(args);
        return fragment;
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        mNoteId = (UUID) getArguments().getSerializable(NOTE_ID);
        mNote = NoteList.get(getActivity()).getNote(mNoteId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note, container, false);

        mTitle = (EditText) v.findViewById(R.id.note_title);
        mTitle.setText(mNote.getTitle());

        mContent = (EditText) v.findViewById(R.id.note_content);
        new DecryptNote().execute();


        mDoneButton = (Button) v.findViewById(R.id.note_done);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitle.getText().toString();
                String content = mContent.getText().toString();
                if (title.isEmpty() && content.isEmpty()) {
                    NoteList.get(getActivity()).deleteNote(mNote);
                    mCallbacks.onDone();
                } else {
                    new EncryptNote().execute(content, title);
                }
            }
        });

        if(!contentEmpty()) {
            disableEditFields();
        }

        return v;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.note_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem edit = menu.findItem(R.id.action_edit);
        if(contentEmpty()) {
            edit.setVisible(false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                this.enableEditFields();
                //Normally we need to call Activity.invalidateOptionsMenu to signal them menu has changed
                //But this is the only icon.
                item.setVisible(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private boolean contentEmpty() {
        return mTitle.getText().length() == 0 && mContent.getText().length() == 0;
    }

    private void disableEditFields() {
        mContent.setFocusable(false);
        mDoneButton.setVisibility(View.INVISIBLE);
        mTitle.setFocusable(false);
    }

    private void enableEditFields() {
        mContent.setFocusableInTouchMode(true);
        mDoneButton.setVisibility(View.VISIBLE);
        mTitle.setFocusableInTouchMode(true);

        mTitle.requestFocus();
    }



    private class EncryptNote extends AsyncTask<String, Void, Void> {
        ProgressDialog progress = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            progress.setMessage("Encrypting...");
            progress.show();

        }

        @Override
        protected Void doInBackground(String... note) {
            mNote.setText(note[0]);
            mNote.encryptText();

            mNote.setTitle(note[1]);
            NoteList.get(getActivity()).saveToDb(mNoteId);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progress.dismiss();
            mCallbacks.onDone();
        }
    }

    private class DecryptNote extends AsyncTask<Void, Void, String> {
        ProgressDialog progress = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            progress.setMessage("Decrypting...");
            progress.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            return mNote.getDecryptedText();
        }

        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
            mContent.setText(result);
        }
    }
}
