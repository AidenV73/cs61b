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
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String firstArg = args[0];
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
                // checkout -- [file]
                if (args[1].equals("--")) {
                    filename = args[2];
                    Repository.checkoutCommand(filename);
                } else if (args.length == 4 && args[2].equals("--")){   // checkout [commitID] -- [file]
                    String commitID = args[1];
                    filename = args[3];
                    Repository.checkoutCommand(commitID, filename);
                }
                break;
            case "branch":
                // TODO: handle the `branch [branchname]` command
                String branchname = args[1];
                Repository.branchCommand(branchname);
                break;
        }
    }
}
