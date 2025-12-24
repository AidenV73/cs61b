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

    /** Record Commit's Parents and Children by Doubly Linked List */
    private LinkedListDeque<Commit> commitList = new LinkedListDeque<>();
    private Commit parent = null;
    private Commit children = null;

    /** Set a head pointer */
    private Commit head;

    /** Set a master pointer */
    private Commit master;

    /** The .gitlet/objects to save blob */
    File objects = join(Repository.objects);

    /** Tracked file */


    /* TODO: fill in the rest of this class. */
    /** Make the initial commit */
    public Commit(String message) {

        // If dont have parents then do initial commit
        if (this.parent == null) {
            // Set the initial information
            this.timestamp = new Date(0);
        } else {
            this.timestamp = new Date();
            // Get parent before update the list
            this.parent = commitList.getLast();
        }

        // Record the message (No need to worry about initial cuz alr set in repo)
        this.message = message;

        // Add this commit into commitList
        commitList.addLast(this);

        // TODO: Update commit's blob
        // Add pointer to point on blob
    }

}
