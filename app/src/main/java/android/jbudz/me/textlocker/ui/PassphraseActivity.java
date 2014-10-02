package android.jbudz.me.textlocker.ui;

import android.app.Fragment;
import android.content.Intent;

public class PassphraseActivity extends SingleFragmentActivity
implements PassphraseFragment.Callbacks {
    @Override
    public Fragment createFragment() {
        return new PassphraseFragment();
    }

    @Override
    public void onPassphraseContinue() {
        Intent i = new Intent(this, TextListActivity.class);
        startActivity(i);
    }
}
