package android.jbudz.me.textlocker.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.jbudz.me.textlocker.R;
import android.jbudz.me.textlocker.model.Note;
import android.jbudz.me.textlocker.model.NoteList;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

/**
 * Created by jon on 7/3/14.
 */
public class TextListFragment extends ListFragment {
    private NoteList mNotesList;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onEditNote(Note note);
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mNotesList = NoteList.get(getActivity());
        mNotesList.removeEmptyNotes();
        ArrayAdapter<Note> adapter = new ArrayAdapter<Note>(getActivity(),
                android.R.layout.simple_expandable_list_item_1, mNotesList.getNotes()) {

            public View getView(int pos, View convertView, ViewGroup parent) {
                if(convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_note, null);
                }

                Note n = getItem(pos);
                TextView titleTextView = (TextView) convertView.findViewById(R.id.list_item_note_title);
                titleTextView.setText(n.getTitle());

                return convertView;
            }
        };

        setListAdapter(adapter);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //Inflate from base class, android.R.id.list for setup Context Action
        //View v = super.onCreateView(inflater, parent, savedInstanceState);1
        View v = inflater.inflate(R.layout.fragment_listview, parent, false);
        setupContextAction((ListView) v.findViewById(R.id.listview));
        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Note note = (Note)((ArrayAdapter) getListAdapter()).getItem(position);
        mCallbacks.onEditNote(note);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.text_list_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                //Start a new fragment
                Note note = new Note();
                NoteList.get(getActivity()).addNote(note);
                mCallbacks.onEditNote(note);
                return true;
            case R.id.action_export:
                AlertDialog.Builder export = new AlertDialog.Builder(getActivity());
                try {
                    File exportDb = NoteList.get(getActivity()).exportDataBase();
                    export.setMessage("Export saved to " + exportDb.getPath());
                } catch(IOException e) {
                    Log.e("TextListFragment", "Error exporting");
                    export.setMessage("Error exporting");
                } finally {
                    AlertDialog dialog = export.create();
                    dialog.show();
                }

                return true;

            case R.id.action_import:
                AlertDialog.Builder importDb = new AlertDialog.Builder(getActivity());

                try {
                    NoteList.get(getActivity()).importDataBase();
                    ArrayAdapter<Note> adapter = (ArrayAdapter)getListAdapter();
                    adapter.notifyDataSetChanged();
                } catch (IOException ioe) {
                    Log.e("TextListFragment", "Error importing");
                    importDb.setMessage("Error importing");
                    importDb.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayAdapter<Note> adapter = (ArrayAdapter)getListAdapter();
        adapter.notifyDataSetChanged();
    }

    private void setupContextAction(ListView v) {
        ListView listView = v;
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.list_cab, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_delete:
                        deleteItems();
                        mode.finish();
                        return true;
                }
                return false;
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

            private void deleteItems() {
                ArrayAdapter adapter = (ArrayAdapter) getListAdapter();
                NoteList noteList = NoteList.get(getActivity());

                for (int i = adapter.getCount() - 1; i >= 0; i--) {
                    if (getListView().isItemChecked(i)) {
                        Note note = (Note) adapter.getItem(i);
                        noteList.deleteNote(note);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
