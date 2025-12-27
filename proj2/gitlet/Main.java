package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = args[0];
        if (firstArg == null) {
            System.out.println("Please enter a command");
        }
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                Repository.initialCommand();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                String filename = args[1];
                Repository.addCommand(filename);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                // TODO: handle the `commit [message]` command
                String message = args[1];
                Repository.commitCommand(message);
                break;
            case "rm":
                // TODO: handle the `rm [filename]` command
                filename = args[1];
                Repository.rmCommand(filename);
                break;
            case "log":
                // TODO: handle the `log` command
                Repository.logCommand();
                break;
            case "find":
                // TODO: handle the `find` command
                message = args[1];
                Repository.findCommand(message);
                break;
            case "checkout":
                // TODO: handle the `checkout` command
                if (args.length > 2) {
                    String commitID = args[1];
                    filename = args[2];
                    Repository.checkoutCommand(commitID, filename);
                } else {
                    String val = args[1];
                    Repository.checkoutCommand(val);
                }
        }
    }
}
