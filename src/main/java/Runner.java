import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Runner {
    private static final String DICTIONARY_PATH = "dictionary.txt";
    private static final String BOUNDARIES_PATH = "greeting-and-parting.txt";
    private static final String PC_PREFIX = "pc:-> ";
    private static final String ME_SUFFIX = "\nme: > ";

    public static void main(String[] args) throws IOException, URISyntaxException {
        List<List<String>> dictionary = readDictionary(DICTIONARY_PATH);
        List<List<String>> boundaries = readDictionary(BOUNDARIES_PATH);
        List<String> greetings = boundaries.get(0);
        List<String> partings = boundaries.get(1);

        printDictionarySize(dictionary);

        startFakeConversation(greetings, dictionary, partings);
    }

    private static void startFakeConversation(List<String> greetings, List<List<String>> dictionary, List<String> partings) {
        Random random = new SecureRandom();
        Scanner scanner = new Scanner(System.in);

        printConversationLine(greetings.get(random.nextInt(greetings.size())));

        while (true) {
            String question = scanner.nextLine();
            if (isParing(question, partings)) {
                printConversationFinalisation(partings.get(random.nextInt(partings.size())));
                break;
            }

            String answer = buildAnswer(dictionary, random);
            printConversationLine(answer);
        }
    }

    private static List<List<String>> readDictionary(String pathStr) throws IOException, URISyntaxException {
        Path path = Paths.get(ClassLoader.getSystemResource(pathStr).toURI());

        return Files.lines(path)
                .map(String::trim)
                .filter(str -> !str.isEmpty())
                .map(Runner::lineToWords)
                .collect(Collectors.toList());
    }

    private static String buildAnswer(List<List<String>> dictionary, Random random) {
        return dictionary.stream()
                .map(list -> list.get(random.nextInt(list.size())))
                .collect(Collectors.joining(" "));
    }

    private static boolean isParing(String question, List<String> partings) {
        question = question.trim().toLowerCase();
        return partings.contains(question);
    }

    private static void printDictionarySize(List<List<String>> dictionary) {
        int dictionaryPower = dictionary.stream()
                .mapToInt(List::size)
                .reduce(1, (a, b) -> a * b);

//        System.out.printf(" DEBUG: Dictionary contain %s phrases.\n\n", dictionaryPower);
    }

    private static List<String> lineToWords(String line) {
        return Stream.of(line.split(","))
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());
    }

    private static void printConversationLine(String chunk) {
        System.out.print(PC_PREFIX + chunk + ME_SUFFIX);
    }

    private static void printConversationFinalisation(String chunk) {
        System.out.print(PC_PREFIX + chunk);
    }

}
