import java.util.*;
import java.util.stream.Collectors;

import CFG_to_CNF_Converter.Converter;
import CYK_Algorithm.CYK;

public class Main {
    public static void main(String[] args) {
        HashMap<String, ArrayList<ArrayList<String>>> grammar = new HashMap<>();
        Scanner input = new Scanner(System.in);
        String line;
        while (input.hasNextLine()) {
            line = input.nextLine();
            if (line.equals("")) {
                break;
            }
            Map.Entry<String, ArrayList<String>> production = parseProduction(line);
            grammar.computeIfAbsent(production.getKey(), k -> new ArrayList<>()).add(production.getValue());
        }
        printGrammar(grammar, "Parsed input grammar: ");
        Converter converter = new Converter(grammar);
        HashMap<String, ArrayList<ArrayList<String>>> cnfGrammar = converter.convertToCNF();
        printGrammar(cnfGrammar, "Grammar in CNF:");
        changeVars(cnfGrammar);
        printGrammar(cnfGrammar, "\nGrammar with modified variables:");
        HashSet<Character> Terminals_Set = new HashSet<Character>();
        for (ArrayList<ArrayList<String>> value : cnfGrammar.values()) {
            for (ArrayList<String> innerList : value) {
                for (String str : innerList) {
                    for (char c : str.toCharArray()) {
                        if (Character.isLowerCase(c)) {
                            Terminals_Set.add(c);
                        }
                    }
                }
            }
        }
        ArrayList<String> Terminal_List = new ArrayList<String>();
        for (char c : Terminals_Set) {
            Terminal_List.add(String.valueOf(c));
        }
        ArrayList<String> Non_Terminal_List = new ArrayList<>(cnfGrammar.keySet());
        System.out.println("\nEnter the word you want to check in Chomsky's Normal Grammar:");
        Scanner input1 = new Scanner(System.in);
        String word = input1.nextLine();
        CYK cyk = new CYK(word, "S", Terminal_List, Non_Terminal_List, null);

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

    private static void changeVars(HashMap<String, ArrayList<ArrayList<String>>> cnfGrammar) {
        ArrayList<String> keys = new ArrayList<>(cnfGrammar.keySet());
        ArrayList<String> newKeys = new ArrayList<>();
        ArrayList<String> oldKeys = new ArrayList<>();
        for (String key : keys) {
            if (key.matches("[A-Z]\\d+")) {
                String newKey = generateNewKey(cnfGrammar.keySet());
                newKeys.add(newKey);
                oldKeys.add(key);
                cnfGrammar.put(newKey, cnfGrammar.remove(key));
            }
        }
        for (String key : cnfGrammar.keySet()) {
            ArrayList<ArrayList<String>> values = cnfGrammar.get(key);
            for (ArrayList<String> value : values) {
                for (int i = 0; i < value.size(); i++) {
                    String s = value.get(i);
                    if (oldKeys.contains(s)) {
                        int index = oldKeys.indexOf(s);
                        value.set(i, newKeys.get(index));
                    }
                }
            }
        }
    }
    
    private static String generateNewKey(Set<String> keys) {
        String newKey = "";
        for (char c = 'A'; c <= 'Z'; c++) {
            if (!keys.contains(Character.toString(c))) {
                newKey = Character.toString(c);
                break;
            }
        }
        return newKey;
    }
}