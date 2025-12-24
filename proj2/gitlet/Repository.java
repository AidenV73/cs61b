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
        File initialCommitFile = join(GITLET_DIR, "initial commit");
        Commit initialCommit = new Commit("initial commit");
        writeObject(initialCommitFile, initialCommit);
    }

    /**
     * Add file -> hashContent into .gitlet/index
     */
    public static void addCommand(String filename) {
        // Initialize a hashmap
        HashMap<String, String> fileHash = new HashMap<>();

        // Get the hashed content
        String fileContent = null;
        try {
            fileContent = Files.readString(Path.of(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String hashedContent = sha1(fileContent);

        // Add in filename -> hashContent and if not exist create a blob
        objects.mkdir();
        if (!fileHash.containsKey(filename)) {
            String blobName = hashedContent.substring(0,2);
            File blobFile = join(objects, blobName);
            writeContents(blobFile, hashedContent);
        }
        fileHash.put(filename, hashedContent);

        // Put hashmap into directory
        writeObject(index, fileHash);
    }
}
