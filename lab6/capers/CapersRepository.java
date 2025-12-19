package capers;

import java.io.File;
import java.io.Serializable;

import static capers.Utils.*;

/** A repository for Capers 
 * @author TODO
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 * TODO: change the above structure if you do something different.
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = Utils.join(CWD, ".capers"); // TODO Hint: look at the `join`
                                            //      function in Utils
    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() {
        // TODO
        // Make .capper if it is not exist
        File c = CAPERS_FOLDER;
        if (!c.exists()) {
            c.mkdir();
        }

        // Make dogs if .capers/dogs is not exist
        File dogsdir = Utils.join(c, "dogs");
        if (!dogsdir.exists()) {
            dogsdir.mkdir();
        }
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        // TODO
        File story = Utils.join(CAPERS_FOLDER, "story");
        if (!story.exists()) {
            Utils.writeContents(story, text);
        } else {
            Utils.writeContents(story, Utils.readContents(story) + text + "/n");
        }
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {
        // TODO
        Dog d = new Dog(name, breed, age);
        File dogFile = Utils.join(CAPERS_FOLDER, "dogs", name);
        Utils.writeObject(dogFile, (Serializable) d);
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        // TODO
        File dogFile = Utils.join(CAPERS_FOLDER, "dogs", name);
        Dog d = Utils.readObject(dogFile, Dog.class);
    }
}
