package android.jbudz.me.textlocker.model;

import android.jbudz.me.textlocker.model.Passphrase;
import android.jbudz.me.textlocker.util.ContentCrypto;

import java.util.UUID;

/**
 * Make notes
 */
final public class Note {
    private volatile UUID mId = UUID.randomUUID();
    private volatile String mTitle;
    private volatile String mText;
    private volatile byte[] mSalt;

    /**
     * Get the note UUID
     * @return {UUID} The note UUID
     */
    public UUID getId() {
        return mId;
    }

    /**
     * Set the note id from a string
     * @param uuid The string representation of a UUID
     */
    public void setIdByString(String uuid) {
        mId = UUID.fromString(uuid);
    }

    /**
     * Getter for the note title
     * @return The title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Setter for the note title
     * @param mTitle The note title
     */
    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    /**
     * Synchronously decrypt the note content
     * @return The decrypted text.  Returns bad decrypt if the key is invalid
     */
    public synchronized String getDecryptedText() {
        String text;
        try {
            text = ContentCrypto.decrypt(mText, getSalt(), Passphrase.INSTANCE.getPassPhrase().toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
            text = "Bad Decrypt";
        }
        return text;
    }

    /**
     * Get the note content
     * @return Note
     */
    public String getText() {
        return mText;
    }

    /**
     * Set the note content
     * @param text The note text
     */
    public void setText(String text) {
        mText = text;
    }

    /**
     * Converts the stored text to its encrypted form
     */
    public synchronized void encryptText() {
        setSalt(ContentCrypto.generateSalt());
        try {
            mText = ContentCrypto.encrypt(mText, getSalt(), Passphrase.INSTANCE.getPassPhrase().toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Get the encryption salt
     * @return salt
     */
    public byte[] getSalt() {
        return mSalt;
    }

    /**
     * Set the encryption salt
     * @param mSalt
     */
    public void setSalt(byte[] mSalt) {
        this.mSalt = mSalt;
    }
}
