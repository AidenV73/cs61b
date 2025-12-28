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

    /** The HEAD to points on current working branch */
    public static File head = join(GITLET_DIR, "head");

    /** The Branches directory to save all the branches */
    public static final File branches = join(GITLET_DIR, "branches");

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

        // Make branches directory and initialize master branch
        branches.mkdir();
        File master = join(branches, "master");

        // Head points to master
        writeContents(head, "master");

        // Initialize removal file
        writeObject(removal, new HashSet<String>());

        // Initialize index file
        writeObject(index, new HashMap<String, String>());

        // Make the initial commit
        Commit initialCommit = new Commit();

        // Write initial commit into objects file
        File commitFile = join(objects, initialCommit.getID());
        writeObject(commitFile, initialCommit);

        // Save initial commit inside master branch
        writeContents(master, initialCommit.getID());

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

        // Get current working branch
        File branch = join(branches, readContentsAsString(head));

        // Make a new commit
        String parentID = readContentsAsString(branch);
        Commit normalCommit = new Commit(message, parentID, stagedFile, removalFile);

        // Save new commit into .gitlet/object
        File objectFile = join(objects, normalCommit.getID());
        writeObject(objectFile, normalCommit);

        // Update branch to points on current added commit
        writeContents(branch, normalCommit.getID());

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
        Set<String> removalSet = readObject(removal, HashSet.class);
        // If file is staged then remove from staging area
        @SuppressWarnings("unchecked")
        HashMap<String, String> stagedFile = readObject(index, HashMap.class);
        if (stagedFile.containsKey(filename)) {
            stagedFile.remove(filename);
        } else {
            // If tracked by current commit then add into removal area
            removalSet.add(filename);

            // Serialize removalFile and write into removal
            byte[] removalByte = serialize((Serializable) removalSet);
            writeObject(removal, removalByte);
        }
    }

    /** Show all the commit history */
    public static void logCommand() {
        // Get the current commit's id
        String currentID = readContentsAsString(getBranch());
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
        String currentID = readContentsAsString(getBranch());
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

    /** Checkout file in current commit */
    public static void checkoutFileCommand(String filename) {
        // If checkout current commit and filename
        String commitID = readContentsAsString(getBranch());
        checkoutFileInCommitCommand(commitID, filename);
    }

    /** Checkout file in specific commit */
    public static void checkoutFileInCommitCommand(String commitID, String filename) {
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

    /** Checkout branch */
    public static void checkoutBranchCommand(String branchname) {
        // If branch does not exist
        File[] branchList = branches.listFiles();
        Boolean exist = false;
        for (File f : branchList) {
            String name = f.getName();
            if (name.equals(branchname)) {
                exist = true;
            }
        }
        if (!exist) {
            System.out.println("No such branch exist");
            return;
        }

        // If current branch is branchname
        if (readContentsAsString(head).equals(branchname)) {
            System.out.println("No need to checkout current branch. ");
        }

        // Check if current branch file is tracked(inside currentBlob, index), if not just return
        File[] files = CWD.listFiles();
        for (File f : files) {
            String filename = f.getName();
            Commit currentCommit = getCommit(readContentsAsString(getBranch()));
            HashMap<String, String> currentBlobs = currentCommit.getBlobs();
            HashMap<String, String> stagedFile = readObject(index, HashMap.class);
            if (!currentBlobs.containsKey(filename) && !stagedFile.containsKey(filename)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }
        // Move head pointer to points on this branch
        writeContents(head, branchname);
        // Get branch
        File branch = join(branches, branchname);

        // Get branch current commit ID
        String currentID = readContentsAsString(branch);

        // Read blob inside commit
        Commit currentCommit = getCommit(currentID);
        @SuppressWarnings("unchecked")
        HashMap<String, String> blobs = currentCommit.getBlobs();

        // Override current working directory
        for (String filename : blobs.keySet()) {
            String hashID = blobs.get(filename);
            File f = join(CWD, filename);
            writeContents(f, getContent(hashID));
        }

    }

    /** Creates a new branch with the given name, and points it at the current head commit. */
    public static void branchCommand(String branchname) {
        File newBranch = join(branches, branchname);
        Commit currentCommit = getCurrentCommit();
        writeContents(newBranch, currentCommit.getID());
    }

    /** Helper method */

    /** Return commit by id */
    public static Commit getCommit(String id) {
        File commitFile = join(objects, id);
        Commit currentCommit = readObject(commitFile, Commit.class);
        return currentCommit;
    }

    /** Return current working branch */
    public static File getBranch() {
        return join(branches, readContentsAsString(head));
    }

    /** Get current commit */
    public static Commit getCurrentCommit() {
        String currentID = readContentsAsString(getBranch());
        return getCommit(currentID);
    }

    /** Get content by hashID */
    public static String getContent(String hashID) {
        File f = join(objects, hashID);
        return readContentsAsString(f);
    }
}
