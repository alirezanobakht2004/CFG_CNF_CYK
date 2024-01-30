import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        HashMap<String, ArrayList<ArrayList<String>>> grammar = new HashMap<>();
        Scanner input = new Scanner(System.in);
        String line;
        while (input.hasNextLine()) {
            line=input.nextLine();
            if (line.equals("")) {
                break;
            }
            Map.Entry<String, ArrayList<String>> production = parseProduction(line);
            grammar.computeIfAbsent(production.getKey(), k -> new ArrayList<>()).add(production.getValue());
        }
        printGrammar(grammar, "Parsed input grammar: ");

        Converter converter = new Converter(grammar);
        HashMap<String,ArrayList<ArrayList<String>>> cnfGrammar = converter.convertToCNF();
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

     private static void printGrammar(HashMap<String, ArrayList<ArrayList<String>>> grammar, String heading) {
     System.out.println(heading);
     for (Map.Entry<String, ArrayList<ArrayList<String>>> entry : grammar.entrySet()) {
         String rules = entry.getValue().stream()
             .map(rule -> String.join(",", rule))
             .collect(Collectors.joining(" | "));
         System.out.println(entry.getKey() + " -> " + rules);
     }
 }
}