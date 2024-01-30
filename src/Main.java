import java.util.*;

public class Main {
    public static void main(String[] args) {
        HashMap<String, ArrayList<ArrayList<String>>> grammar = new HashMap<>();
        Scanner input = new Scanner(System.in);
        String line;

    }

    private static Map.Entry<String, ArrayList<String>> parseProduction(String line) {
        String[] parts = line.split("->");
        if (parts.length == 2) {
            String key = parts[0].trim();
            ArrayList<String> value = new ArrayList<>(Arrays.asList(parts[1].trim().split(",")));
            return new AbstractMap.SimpleEntry<>(key, value);
        }
        return null;
    }
}