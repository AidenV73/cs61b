package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;


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

    /** The ID of this Commit. */
    private String id;

    /** The message of this Commit. */
    private String message;

    /** Timestamp of this Commit */
    private Date timestamp;

    /** Date(formatted timestamp) */
    private String date;

    /** Parents of current Commit */
    private String parentID;

    /** Tracked file */
    HashMap<String, String> blobs = new HashMap<>();

    /* TODO: fill in the rest of this class. */
    /** Make the initial commit */
    public Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0);
        this.date = formateTimeStamp(timestamp);
        this.parentID = "";
        this.id = sha1(Utils.serialize(this));
    }


    /** Make the commit with add and removal */
    public Commit(String message, String parentID, HashMap<String, String> stagedFile, HashSet<String> removalFile) {
        this.message = message;
        this.timestamp = new Date();
        this.date = formateTimeStamp(timestamp);
        this.parentID = parentID;
        this.blobs = updateTrackedFile(stagedFile, removalFile);
        this.id = sha1(Utils.serialize(this));
    }

    /** Return parent's blobs */
    public HashMap<String, String> getParentBlobs() {
        File parentFile = join(Repository.objects, parentID);
        Commit parentCommit = readObject(parentFile, Commit.class);
        return parentCommit.getBlobs();
    }

    /** Update Tracked File */
    public HashMap<String, String> updateTrackedFile(HashMap<String, String> stagingArea, HashSet<String> removalFile) {
        blobs = new HashMap<>(getParentBlobs());
        stagingArea.forEach((key, value) -> blobs.put((String) key, (String) value));
        for (String filename : removalFile) {
            if (blobs.containsKey(filename)) {
                blobs.remove(filename);
            }
        }
        return blobs;
    }

    /** Getters */

    /** Return message of commit */
    public String getMessage() {
        return this.message;
    }

    /** Return timestamp to commit */
    public Date getTimestamp() {
        return this.timestamp;
    }

    /** Return date in string */
    public String getDate() {
        return this.date;
    }

    /** Return parentID of commit */
    public String getParentID() {
        return this.parentID;
    }

    /** Return blobs (tracked file) of commit */
    public HashMap<String, String> getBlobs() {
        return this.blobs;
    }

    /** Return serialized commit */
    public byte[] serialize() {
        return Utils.serialize(this);
    }

    /** Return commit's id */
    public String getID() {
        return this.id;
    }

    /** Helper method */
    /** Format timestamp */
    public String formateTimeStamp(Date timestamp) {
        Formatter fmt = new Formatter();
        fmt.format("%1$ta %1$tb %1$td %1$tT %1$tY %1$tz", timestamp);
        return fmt.toString();
    }

}
