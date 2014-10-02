package android.jbudz.me.textlocker.ui;

import android.app.Activity;
import android.app.Fragment;
import android.jbudz.me.textlocker.R;
import android.jbudz.me.textlocker.model.Passphrase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by jon on 7/5/14.
 */
public class PassphraseFragment extends Fragment{
    private Callbacks mCallbacks;
    private Button mContinue;
    private EditText mPassphraseText;

    public interface Callbacks {
        void onPassphraseContinue();
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
        getActivity().setTitle(R.string.title_activity_passphrase);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_passphrase, container, false);

        mPassphraseText = (EditText) v.findViewById(R.id.passphrase);

        mContinue = (Button) v.findViewById(R.id.passphrase_continue);
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Passphrase.INSTANCE.setPassPhrase(mPassphraseText.getText().toString());
                mCallbacks.onPassphraseContinue();
            }
        });
        return v;
    }
}
