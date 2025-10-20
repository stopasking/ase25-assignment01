public class CommitMsgHook {
    public static void main(String[] args) {
        String commitMessage = args[0];

        if (commitMessage.isEmpty()) {
            System.out.println("Commit message is invalid.");
            System.exit(1);
        }

        System.out.println("Commit message is valid.");
        System.exit(0);
    }
}
