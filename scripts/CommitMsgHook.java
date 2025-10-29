import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;;

public class CommitMsgHook {

    public static final Pattern HEADER_PATTERN = Pattern.compile(
        "^(?<type>[a-zA-Z0-9-]+)(\\((?<scope>[a-zA-Z0-9-]+)\\))?(?<breaking>!)?: (?<desc>.+)$"
    );

    public static final Pattern FOOTER_PATTERN = Pattern.compile(
        "(?i)^(BREAKING[ -]CHANGE|[A-Za-z0-9-]+)(:| #).+"
    );

    public static final List<String> DEFAULT_TYPES = List.of("feat", "fix");
    public static void main(String[] args) throws IOException{
        String commitMessage = args[0];
        //String commitMessage = "doc: add feature with colon";
        
        if (commitMessage.isEmpty()) {
            System.out.println("Commit message is invalid.");
            System.out.println("1");
            System.exit(1);
        }

        Path configPath = Paths.get("commit-types.config");
        //Path configPath = Paths.get(args[1]).toAbsolutePath();
        Set<String> validTypes = new HashSet<String>(DEFAULT_TYPES);

        if (Files.exists(configPath)) {
            System.out.println("IM HERE");
            Files.lines(configPath)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(validTypes::add);
        }

        System.out.println(validTypes);

        String[] lines = commitMessage.strip().split("\\R", -1);

        if (lines.length == 0) {
            System.err.println("Commit message invalid");
            System.out.println("2");
            System.exit(1);
        }

        validateHeader(lines[0], validTypes);
        validateBodyFooter(Arrays.asList(lines));

        System.out.println("Commit message valid");
        System.exit(0);
    }

    public static void validateHeader(String header, Set<String> validTypes){
        Matcher m = HEADER_PATTERN.matcher(header);

        if (!m.matches()) {
            System.err.println("Commit message invalid");
            System.out.println("3");
            System.exit(1);
        }

        String type = m.group("type").toLowerCase();

        if (!validTypes.contains(type)) {
            System.err.println("Commit message invalid");
            System.out.println("4");
            System.exit(1);
        }

        String description = m.group("desc");

        if (description.isBlank()) {
            System.err.println("Commit message invalid");
            System.out.println("5");
            System.exit(1);
        }
    }

    public static void validateBodyFooter(List<String> lines){
        boolean blankLineFound = false;
        boolean footerStarted = false;

        for(int i = 1; i < lines.size(); i++){
            String line = lines.get(i);

            if(line.isBlank()){
                blankLineFound = true;
                continue;
            }

            if(FOOTER_PATTERN.matcher(line).matches()){
                if(!blankLineFound){
                    System.err.println("Commit message invalid");
                    System.out.println("6");
                    System.exit(1);
                }

                footerStarted = true;
                continue;
            }

            if(footerStarted){
                System.err.println("Commit message invalid");
                System.out.println("7");
                System.exit(1);
            }

            blankLineFound = false;
        }
    }
}
