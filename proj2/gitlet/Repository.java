package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /**
     * The staging area
     */
    // Addition
    public static  File index = join(GITLET_DIR, "index");
    // Removal
    public static File removal = join(GITLET_DIR, "removal");

    /** The HEAD to points on current working commit */
    public static File head = join(GITLET_DIR, "head");

    /** The directory to save blob */
    public static final File objects = join(GITLET_DIR, "objects");

    /* TODO: fill in the rest of this class. */

    /**
     * Create an initial repository
     * Make the first commit
     * If alr have a repository: throw an error message
     */
    public static void initialCommand() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }

        GITLET_DIR.mkdir();

        // Make objects directory
        objects.mkdir();

        // Initialize removal file
        writeObject(removal, new HashSet<String>());

        // Initialize index file
        writeObject(index, new HashMap<String, String>());

        // Make the initial commit
        Commit initialCommit = new Commit();

        // Write initial commit into objects file
        File commitFile = join(objects, initialCommit.getID());
        writeObject(commitFile, initialCommit);

        // Move the head pointer
        writeContents(head, initialCommit.getID());
    }

    /** Add the current file into staging area */
    public static void addCommand(String filename) {
        // Make a HashMap to save filename -> hashText
        @SuppressWarnings("unchecked")
        HashMap<String, String> stageFile = readObject(index, HashMap.class);

        // Hash fileText
        File currentFile = join(CWD, filename);
        String fileText = readContentsAsString(currentFile);

        String hashText = sha1(fileText);  // Also use as blobID

        // Write blob and its content（not hashed) into .gitlet/objects/<blobID>
        File blobFile = join(objects, hashText);
        if (!blobFile.exists()) {
            writeContents(blobFile, fileText);
        }

        // Add into HashMap
        stageFile.put(filename, hashText);

        // Write HashMap into .gitlet/index
        writeObject(index, stageFile);
    }

    /** Commit changes in staging area and removal area*/
    public static void commitCommand(String message) {
        objects.mkdir();

        // If no commit message
        if (message == null) {
            System.out.println("Please enter a commit message");
            return;
        }

        // Read index to get staged file and removal to get removal file
        @SuppressWarnings("unchecked")
        HashMap<String, String> stagedFile = readObject(index, HashMap.class);
        @SuppressWarnings("unchecked")
        HashSet<String> removalFile = readObject(removal, HashSet.class);

        // If staged file is null ( No add ) and removal file is null (No rm)
        if (stagedFile.isEmpty() && removalFile.isEmpty()) {
            System.out.println("No changes added to modified");
            return;
        }

        // Make a new commit
        String parentID = readContentsAsString(head);
        Commit normalCommit = new Commit(message, parentID, stagedFile, removalFile);

        // Save new commit into .gitlet/object
        File objectFile = join(objects, normalCommit.getID());
        writeObject(objectFile, normalCommit);

        // Head points to current commit
        writeContents(head, normalCommit.getID());

        // Clear the staged file and removal file after commit
        stagedFile.clear();
        removalFile.clear();

        // Write back into index and removal
        writeObject(index, stagedFile);
        writeObject(removal, removalFile);
    }

    /** Remove file */
    public static void rmCommand(String filename) {
        @SuppressWarnings("unchecked")
        Set<String> removalFile = readObject(removal, HashSet.class);
        // If file is staged then remove from staging area
        @SuppressWarnings("unchecked")
        HashMap<String, String> stagedFile = readObject(index, HashMap.class);
        if (stagedFile.containsKey(filename)) {
            stagedFile.remove(filename);
        } else {
            // If tracked by current commit then add into removal area
            removalFile.add(filename);

            // Serialize removalFile and write into removal
            byte[] removalByte = serialize((Serializable) removalFile);
            writeObject(removal, removalByte);
        }
    }

    /** Show all the commit history */
    public static void logCommand() {
        // Get the current commit's id
        String currentID = readContentsAsString(head);
        while (currentID != "") {
            Commit currentCommit = getCommit(currentID);
            System.out.println("===");
            System.out.println("commit " + currentCommit.getID());
            System.out.println("Date: " + currentCommit.getDate());
            System.out.println(currentCommit.getMessage());
            System.out.println("");
            currentID = currentCommit.getParentID();
        }
    }

    /** Find the commit with commit message */
    public static void findCommand(String message) {
        // Iterate through all file in objects
        String currentID = readContentsAsString(head);
        Boolean commitExist = false;
        while (currentID != "") {
            Commit currentCommit = getCommit(currentID);
            if (currentCommit.getMessage().equals(message)) {
                System.out.println(currentID);
                commitExist = true;
            }
            currentID = currentCommit.getParentID();
        }
        if (!commitExist) {
            System.out.println("Found no commit with that message.");
        }
    }

    /** Get file in commit or branches */
    public static void checkoutCommand(String val) {
        // If checkout current commit and filename
        String commitID = readContentsAsString(head);
        checkoutCommand(commitID, val);
        // TODO: If checkout branches then takes all files in the commit at the head of the given branch
    }

    public static void checkoutCommand(String commitID, String filename) {
        // Get current commit
        Commit currentCommit = getCommit(commitID);

        // Get blobs
        HashMap<String, String> currentBlob = currentCommit.getBlobs();

        // Find filename -> content key value pair in blobs
        if (currentBlob.containsKey(filename)) {
            // Get filename -> blobID
            String blobID = currentBlob.get(filename);

            // Get blobID -> content
            File f = join(objects, blobID);
            String content = readContentsAsString(f);

            // Write content into CWD/filename
            File current = join(CWD, filename);
            writeContents(current, content);

        } else {
            System.out.println("File does not exist in that commit.");
        }
    }

    /** Helper method */
    /** Return commit by id */
    public static Commit getCommit(String id) {
        File commitFile = join(objects, id);
        Commit currentCommit = readObject(commitFile, Commit.class);
        return currentCommit;
    }
}
