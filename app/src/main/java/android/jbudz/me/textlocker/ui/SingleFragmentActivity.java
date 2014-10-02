package android.jbudz.me.textlocker.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.jbudz.me.textlocker.R;
import android.os.Bundle;

/**
 * Created by jon on 7/3/14.
 */
public abstract class SingleFragmentActivity extends Activity {

    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        int fragmentContainer = R.id.fragmentContainer;

        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(fragmentContainer);

        if (fragment == null) {
            fragment = createFragment();
            manager.beginTransaction()
                    .add(fragmentContainer, fragment)
                    .commit();
        }
    }

    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }
}
