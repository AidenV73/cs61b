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
    public static File index = join(GITLET_DIR, "index");
    // Removal
    public static File removal = join(GITLET_DIR, "removal");

    /**
     * The HEAD to points on current working branch
     */
    public static File head = join(GITLET_DIR, "head");

    /**
     * The Branches directory to save all the branches
     */
    public static final File branches = join(GITLET_DIR, "branches");

    /**
     * The directory to save blob
     */
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

    /**
     * Add the current file into staging area
     */
    public static void addCommand(String filename) {
        // Make a HashMap to save filename -> hashText
        @SuppressWarnings("unchecked")
        HashMap<String, String> stageFile = readObject(index, HashMap.class);

        // Hash fileText
        File currentFile = join(CWD, filename);
        if (!currentFile.exists()) {
            System.out.println("File does not exist");
            return;
        }
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

    /**
     * Commit changes in staging area and removal area
     */
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

    /**
     * Remove file
     */
    public static void rmCommand(String filename) {
        @SuppressWarnings("unchecked")
        Set<String> removalSet = readObject(removal, HashSet.class);
        // If file is staged then remove from staging area
        @SuppressWarnings("unchecked")
        HashMap<String, String> stagedFile = readObject(index, HashMap.class);
        if (stagedFile.containsKey(filename)) {
            stagedFile.remove(filename);
            writeObject(index, stagedFile);
        } else {
            // If tracked by current commit then add into removal area
            removalSet.add(filename);

            // Serialize removalFile and write into removal
            byte[] removalByte = serialize((Serializable) removalSet);
            writeObject(removal, removalByte);
        }
    }

    /**
     * Show commit history in the current branch
     */
    public static void logCommand() {
        // Get the current commit's id
        String currentID = readContentsAsString(getBranch());
        while (currentID != "") {
            Commit currentCommit = getCommit(currentID);
            System.out.println("===");
            System.out.println("commit " + currentCommit.getID());
            System.out.println("Date: " + currentCommit.getDate());
            System.out.println(currentCommit.getMessage());
            System.out.println();
            currentID = currentCommit.getParentID();
        }
    }

    /**
     * Show commit history of all branch
     */
    public static void globalLogCommand() {
        // Iterate through all files in objects, if commit then print
        List<String> filenames = plainFilenamesIn(objects);
        for (String filename : filenames) {
            File f = join(objects, filename);
            try {
                Commit c = readObject(f, Commit.class);

                System.out.println("===");
                System.out.println("commit " + c.getID());
                System.out.println("Date: " + c.getDate());
                System.out.println(c.getMessage());
                System.out.println();
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /**
     * Find the commit with commit message
     */
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

    /**
     * Checkout file in current commit
     */
    public static void checkoutFileCommand(String filename) {
        // If checkout current commit and filename
        String commitID = readContentsAsString(getBranch());
        checkoutFileInCommitCommand(commitID, filename);
    }

    /**
     * Checkout file in specific commit
     */
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

    /**
     * Checkout branch
     */
    public static void checkoutBranchCommand(String branchname) {
        // If branch does not exist
        List<String> branchList = plainFilenamesIn(branches);
        Boolean exist = false;
        for (String filename : branchList) {
            if (filename.equals(branchname)) {
                exist = true;
                break;
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

        // Check if file in CWD if it is untracked or will be overwritten in new branch
        boolean untracked = checkUntracked();
        boolean overWritten = checkOverwritten(branchname);

        // If it is untracked && it be overwritten then printout error message and return
        /**if (untracked && overWritten) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            return;
        }*/


        // Move head pointer to points on this branch
        writeContents(head, branchname);

        // Override current working directory
        // Get new branch commit
        File newBranch = join(branches, branchname);
        Commit newCommit = getCommit(readContentsAsString(newBranch));
        HashMap<String, String> newBlobs = newCommit.getBlobs();

        for (String filename : newBlobs.keySet()) {
            String hashID = newBlobs.get(filename);
            File f = join(CWD, filename);
            writeContents(f, getContent(hashID));
        }
    }

    /**
     * Creates a new branch with the given name, and points it at the current head commit.
     */
    public static void branchCommand(String branchname) {
        File newBranch = join(branches, branchname);
        Commit currentCommit = getCurrentCommit();
        writeContents(newBranch, currentCommit.getID());
    }

    /**
     * Displays what branches currently exist, and marks the current branch with a *.
     * Displays what files have been staged for addition or removal
     */
    public static void statusCommand() {
        // Show all branches
        System.out.println("=== Branches ===");
        showBranches();
        System.out.println();

        // Show staged file
        System.out.println("=== Staged Files ===");
        showStagedFile();
        System.out.println();

        // Show removal file
        System.out.println("=== Removed Files ===");
        showRemovalFile();
        System.out.println();

        // TODO: Show modified but not staged files
        System.out.println("=== Modifications Not Staged For Commit ===");
        showModifiedButNotStagedFile();
        System.out.println();

        // Show untracked file
        System.out.println("=== Untracked Files ===");
        showUntrackedFile();
    }

    /**  Merges files from the given branch into the current branch. */
    public static void mergeCommand(String givenBranch) {
        // Failure case
        // If there is uncommitted change
        @SuppressWarnings("unchecked")
        HashMap<String, String> stagedFiles = readObject(index, HashMap.class);
        HashSet<String> removalFiles = readObject(removal, HashSet.class);
        if (!stagedFiles.isEmpty() || !removalFiles.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }

        // If branch does not exist
        List<String> allBranch = plainFilenamesIn(branches);
        if (!allBranch.contains(givenBranch)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }

        // If merge current branch
        if (readContentsAsString(head).equals(givenBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        // If there is untracked file
        if (checkUntracked()) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            return;
        }
        // Consider split point(latest common ancestor) from the current branch and given branch
        String splitPointID = findSplitPoint(givenBranch);
        Commit splitPoint = getCommit(splitPointID);

        // If the split point is the same commit as the given branch (New commit on current branch but no commit in given branch)
        // Just Compare their ID (Since id is unique)
        // Find branch commit
        File branchFile = join(branches, givenBranch);
        String branchCommitID = readContentsAsString(branchFile);

        // If branchCommitID is just split point
        if (branchCommitID.equals(splitPointID)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }

        // If the split point is the current branch, then the effect is to check out the given branch (New commit on given branch but no commit in current branch)
        String currentCommitID = readContentsAsString(getBranch());

        if (currentCommitID.equals(splitPointID)) {
            System.out.println("Current branch fast-forwarded");
            checkoutBranchCommand(givenBranch);
            return;
        }

        /** Get modified files of current and branch then do merge by different condition */

        // Get files that are modified in branch
        Commit branchCommit = getCommit(branchCommitID);
        HashMap<String, String> branchBlobs = branchCommit.getBlobs();
        HashSet<String> modifiedFilesInBranch = new HashSet<>();
        for (String filename : branchBlobs.keySet()) {
            // If modified then add to modifiedFiles list
            if (isModifiedBetweenCommit(filename, branchCommit, splitPoint)) {
                modifiedFilesInBranch.add(filename);
            }
        }

        // Get files that are modified in CWD
        HashSet<String> allFilesInCurrentAndCWD = new HashSet<>();
        List<String> CWDFiles = plainFilenamesIn(CWD);
        Set<String> currentFiles = getCommit(currentCommitID).getBlobs().keySet();
        allFilesInCurrentAndCWD.addAll(CWDFiles);
        allFilesInCurrentAndCWD.addAll(currentFiles);

        HashSet<String> modifiedFilesInCurrent = new HashSet<>();
        for (String filename: allFilesInCurrentAndCWD) {
            if (isModifiedInCWD(filename, splitPoint)) {
                modifiedFilesInCurrent.add(filename);
            }
        }

        // Conditions
        // Iterate all files in CWD, current commit, branch commit
        HashSet<String> allFiles = new HashSet<>();
        allFiles.addAll(allFilesInCurrentAndCWD);
        allFiles.addAll(branchBlobs.keySet());

        for (String filename : allFiles) {
            File currentFile = join(CWD, filename);

            // If not modified since split point and modified in branch then override
            if (!modifiedFilesInCurrent.contains(filename) && modifiedFilesInBranch.contains(filename)) {
                // Get branch content
                String branchContent = getBranchContent(filename, branchBlobs);
                // If deleted in current
                if (!CWDFiles.contains(filename)) {
                    printError("", branchContent);
                } else {
                    // Write branchContent into CWD file
                    writeContents(currentFile, branchContent);
                    // Add file to staged area
                    addCommand(filename);
                }
            }
            // If modified since split point and not modified in branch then stay
            else if (modifiedFilesInCurrent.contains(filename) && !modifiedFilesInBranch.contains(filename)) {
                // If current modified but deleted in branch
                String currentContent = getCWDContent(filename);
                if (!branchBlobs.containsKey(filename)) {
                    printError(currentContent, "");
                }
            }


            // If modified in both then check if same content or both deleted, if so stay
            else if (modifiedFilesInCurrent.contains(filename) && modifiedFilesInBranch.contains(filename)) {
                String currentContent = getCWDContent(filename);
                String branchContent = getBranchContent(filename, branchBlobs);
                // Compare content and check it is deleted in both
                if (currentContent.equals(branchContent) || (!CWDFiles.contains(filename) && !branchBlobs.containsKey(filename))) {
                    continue;
                } else {
                    // If content are different
                    if (!currentContent.equals(branchContent)) {
                        printError(currentContent, branchContent);
                        continue;
                    }
                }
            }

            // If added after split point in current but not exist in branch then stay
            else if (modifiedFilesInCurrent.contains(filename) && !branchBlobs.containsKey(filename)) {
                return;
            }
            // If added in branch after split point but not exist in current then add and staged
            else if (!CWDFiles.contains(filename) && modifiedFilesInBranch.contains(filename)) {
                String branchContent = getBranchContent(filename, branchBlobs);
                File f = join(CWD, filename);
                writeContents(f, branchContent);
                addCommand(filename);
            }
            // If not modified in current and missed in branch then delete it from ur computer
            else if (splitPoint.getBlobs().containsKey(filename) && !modifiedFilesInCurrent.contains(filename) && !branchBlobs.containsKey(filename)) {
                File f = join(CWD, filename);
                f.delete();
            }
            // If not modified in branch and missed in current then add to removal
            else if (!CWDFiles.contains(filename) && modifiedFilesInBranch.contains(filename)) {
                rmCommand(filename);
            }
        }
    }

    /** Helper method */

    /**
     * Return commit by id
     */
    public static Commit getCommit(String id) {
        File commitFile = join(objects, id);
        Commit currentCommit = readObject(commitFile, Commit.class);
        return currentCommit;
    }

    /**
     * Return current working branch
     */
    public static File getBranch() {
        return join(branches, readContentsAsString(head));
    }

    /**
     * Get current commit
     */
    public static Commit getCurrentCommit() {
        String currentID = readContentsAsString(getBranch());
        return getCommit(currentID);
    }

    /**
     * Get content by hashID
     */
    public static String getContent(String hashID) {
        File f = join(objects, hashID);
        if (f.exists()) {
            return readContentsAsString(f);
        } else {
            return "";
        }
    }

    /** Check CWD if there is file untracked and return untracked filename list*/
    public static ArrayList<String> getUntrackedFile() {
        Commit currentCommit = getCurrentCommit();
        // Read current blob
        HashMap<String, String> currentBlob = currentCommit.getBlobs();

        // Read stagedFile
        @SuppressWarnings("unchecked")
        HashMap<String, String> stagedFile = readObject(index, HashMap.class);

        // If a file is not in blob or not in stagedFile then it is untracked
        List<String> filenames = plainFilenamesIn(CWD);
        ArrayList<String> untrackedFiles = new ArrayList<>();
        for (String filename : filenames) {
            if (!currentBlob.containsKey(filename) && !stagedFile.containsKey(filename)) {
                untrackedFiles.add(filename);
            }
        }
        return untrackedFiles;
    }

    /** Return true if there is untracked file */
    public static boolean checkUntracked() {
        Commit currentCommit = getCurrentCommit();
        // Read current blob
        HashMap<String, String> currentBlob = currentCommit.getBlobs();

        // Read stagedFile
        @SuppressWarnings("unchecked")
        HashMap<String, String> stagedFile = readObject(index, HashMap.class);

        // If a file is not in blob or not in stagedFile then it is untracked
        List<String> filenames = plainFilenamesIn(CWD);
        for (String filename : filenames) {
            if (!currentBlob.containsKey(filename) && !stagedFile.containsKey(filename)) {
                return true;
            }
        }
        return false;
    }

    /** Check will be overwritten for checkout to new branch */
    public static boolean checkOverwritten(String branchname) {
        // New branch
        File newBranch = join(branches, branchname);
        Commit newCommit = getCommit(readContentsAsString(newBranch));
        HashMap<String, String> newBlobs = newCommit.getBlobs();

        // Iterate
        List<String> filenames = plainFilenamesIn(CWD);
        for (String filename : filenames) {
            // If there is same file inside new branch then will be overwritten
            if (newBlobs.containsKey(filename)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Iterate all branches and printout
     */
    public static void showBranches() {
        // Get the current branches first (* infront current branch)
        String currentBranch = readContentsAsString(head);

        // Iterate all branch in branches
        List<String> branchNames = plainFilenamesIn(branches);
        for (String branchName : branchNames) {
            // if branchName is current branch then add *
            if (branchName.equals(currentBranch)) {
                System.out.println("*" + branchName);
            } else {  // if branchName is just branch ewe
                System.out.println(branchName);
            }
        }
    }

    /**
     * Iterate file inside index and printout
     */
    public static void showStagedFile() {
        // Get blob and iterate key(a.k.a filename)
        @SuppressWarnings("unchecked")
        HashMap<String, String> stagedFiles = readObject(index, HashMap.class);
        if (stagedFiles.isEmpty()) {
            System.out.print("");
        } else {
            for (String filename : stagedFiles.keySet()) {
                System.out.println(filename);
            }
        }
    }

    /**
     * Iterate file inside removal and printout
     */
    public static void showRemovalFile() {
        // Iterate file inside removal and printout
        @SuppressWarnings("unchecked")
        HashSet<String> files = readObject(removal, HashSet.class);
        if (files.isEmpty()) {
            System.out.print("");
        } else {
            for (String filename : files) {
                System.out.println(filename);
            }
        }
    }

    /** Iterate all file in CWD and printout modifications that is untracked */
    public static void showModifiedButNotStagedFile() {
        // Get CWD files
        List<String> CWDfiles = plainFilenamesIn(CWD);

        // Get latest commit
        Commit latestCommit = getCommit(readContentsAsString(getBranch()));
        HashMap<String, String> latestBlobs = latestCommit.getBlobs();

        // Get all files
        HashSet<String> allFiles = new HashSet<>();
        allFiles.addAll(CWDfiles);
        allFiles.addAll(latestBlobs.keySet());

        // Get staged file
        @SuppressWarnings("unchecked")
        HashMap<String, String> stagedFiles = readObject(index, HashMap.class);

        // Iterate all file and check if it is modified and not staged
        for (String filename : allFiles) {
            // If exist in latest commit and CWD then is modified
            if (isModifiedInCWD(filename, latestCommit) && !stagedFiles.containsKey(filename)) {
                // Add file that is modified
                System.out.println(filename + "(modified)");
            }
            // If exist in latest commit but rm in CWD
            else if (latestBlobs.containsKey(filename) && !CWDfiles.contains(filename)) {
                System.out.println(filename + "(deleted)");
            }

        }
    }

    /** Iterate untracked file list and printout */
    public static void showUntrackedFile() {
        // Get untracked file list
        ArrayList<String> untrackedFiles = getUntrackedFile();

        // Iterate the list and printout
        // If the list is null then print ""
        if (untrackedFiles.isEmpty()) {
            System.out.print("");
        } else {  // Do iteration
            for (String filename : untrackedFiles) {
                System.out.println(filename);
            }
        }
    }

    /** Return split point's commit ID on current branch and given branch */
    public static String findSplitPoint(String branchname) {
        // Save current branch ancestor and use containskey (Since this is O(n))
        HashSet<String> currentAncestor = new HashSet<>();

        String currentID = readContentsAsString(getBranch());

        // Iterate all parents in current branch
        while (currentID != "") {
            Commit currentCommit = getCommit(currentID);
            currentAncestor.add(currentCommit.getID());
            currentID = currentCommit.getParentID();
        }

        // Iterate all parents in given branch
        File givenBranch = join(branches, branchname);
        String branchCommitID = readContentsAsString(givenBranch);

        while (branchCommitID != "") {
            if (currentAncestor.contains(branchCommitID)) {
                return branchCommitID;
            }
            Commit branchCommit = getCommit(branchCommitID);
            branchCommitID = branchCommit.getParentID();
        }
        return null;
    }

    /** Return content of branch latest commit */
    public static String getBranchContent(String filename, HashMap<String, String> branchBlobs) {
        String branchID = branchBlobs.get(filename);
        return getContent(branchID);
    }

    /** Return content of CWD */
    public static String getCWDContent(String filename) {
        File f = join(CWD, filename);
        return readContentsAsString(f);
    }

    /**
     * Return true if a file in CWD is modified from a commit
     * */
    public static boolean isModifiedInCWD(String filename, Commit c) {
        // Get files in CWD
        List<String> CWDFiles = plainFilenamesIn(CWD);

        // Get file in current commit
        HashMap<String, String> givenBlobs = c.getBlobs();

        if (CWDFiles.contains(filename) && givenBlobs.containsKey(filename)) {
            File f = join(CWD, filename);
            String currentContent = readContentsAsString(f);

            String givenContent = getContent(givenBlobs.get(filename));

            if (currentContent.equals(givenContent)) {
                return false;
            } else {
                 return true;
            }
        }
        // If exist in CWD but missing at givenCommit (Added)
        else if (CWDFiles.contains(filename) && !givenBlobs.containsKey(filename)) {
            return true;
        }
        // If exist in givenCommit but missing at CWD
        else if (!CWDFiles.contains(filename) && givenBlobs.containsKey(filename)) {
            return true;
        }
        else {
            return false;
        }
    }

    /** Return true if a file is modified between two commit */
    public static boolean isModifiedBetweenCommit(String filename, Commit c1, Commit c2) {
        HashMap<String, String> b1 = c1.getBlobs();
        String contentHash1 = b1.get(filename);

        HashMap<String, String> b2 = c2.getBlobs();
        String contentHash2 = b2.get(filename);

        if (contentHash1.equals(contentHash2)) {
            return false;
        } else {
            return true;
        }
    }

    /** Print error message to avoid redundant */
    public static void printError(String currentContent, String branchContent) {
        System.out.println("<<<<<<< HEAD");
        System.out.println(currentContent);
        System.out.println("=======");
        System.out.println(branchContent);
        System.out.println(">>>>>>>");
    }
}
