package android.jbudz.me.textlocker.model;

import android.content.Context;

/**
 * Passphrase singleton
 */
public enum Passphrase  {
    INSTANCE;

   private String mPassPhrase;

   public String getPassPhrase() {
        return mPassPhrase;
    }

    public void setPassPhrase(String passPhrase) {
        mPassPhrase = passPhrase;
    }

}

