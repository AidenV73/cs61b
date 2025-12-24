package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

import static gitlet.Utils.join;
import static gitlet.Utils.readObject;


/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Aiden
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;

    /** Timestamp of this Commit */
    private Date timestamp;

    /** Parents of current Commit */
    private String parentID;

    /** Tracked file */
    HashMap<String, String> blobs = new HashMap<>();

    /* TODO: fill in the rest of this class. */
    /** Make the initial commit */
    public Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0);
        this.parentID = null;
    }

    /** Make the normal commit */
    public Commit(String message, String parentID, HashMap<String, String> stagingArea) {
        this.message = message;
        this.timestamp = new Date();
        this.parentID = parentID;
        this.blobs = updateTrackedFile(stagingArea);
    }

    /** Update Tracked File */
    public HashMap<String, String> updateTrackedFile(HashMap<String, String> stagingArea) {
        stagingArea.forEach((key, value) -> blobs.put((String) key, (String) value));
        return blobs;
    }
}
