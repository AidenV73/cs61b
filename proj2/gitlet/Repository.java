package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

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
    public static final File index = join(GITLET_DIR, "index");

    /** The directory to save head */
    public static String head;

    /** The directory to save blob */
    public static final File objects = join(GITLET_DIR, "objects");

    /* TODO: fill in the rest of this class. */

    /**
     * Create an initial repository
     * Make the first commit
     * If alr have a repository: throw an error message
     */
    public static void initialCommand() {
        GITLET_DIR.mkdir();
        Commit initialCommit = new Commit();
        File initialFile = join(GITLET_DIR, "initialCommit");
        writeObject(initialFile, initialCommit);
    }

    /** Add the current file into staging area */
    public static void addCommand(String filename) {
        // Make a HashMap to save filename -> hashText
        HashMap<String, String> stageFile = new HashMap<>();

        // Hash fileText
        String fileText = null;
        try {
            fileText = Files.readString(Path.of(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String hashText = sha1(fileText);

        // Add into HashMap
        stageFile.put(filename, hashText);

        // Write HashMap into .gitlet/index
        writeObject(index, stageFile);
    }

    /** Commit changes in staging area */
    public static void commitCommand(String message) {
        objects.mkdir();

        // Read index to get added file
        @SuppressWarnings("unchecked")
        HashMap<String, String> addedFile = readObject(index, HashMap.class);

        // Make a new commit
        String parentID = readContentsAsString(head);
        Commit normalCommit = new Commit(message, parentID, addedFile);

        // Save new commit into .gitlet/object
        byte[] commit = serialize(normalCommit);
        String commitHash = sha1(commit);
        String commitName = commitHash.substring(0,2);
        File objectFile = join(objects, commitName);
        writeContents(objectFile, commitHash);
    }
}
