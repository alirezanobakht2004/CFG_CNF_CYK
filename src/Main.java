import java.util.*;
import java.util.stream.Collectors;
import CFG_to_CNF_Converter.Converter;
import CYK_Algorithm.CYK;

public class Main {
    public static void main(String[] args) {
        HashMap<String, ArrayList<ArrayList<String>>> grammar = new HashMap<>();
        Scanner input = new Scanner(System.in);
        System.out.println("\033[1;96m"+"Enter the string you want to check for production:"+"\033[0m");
        String word = input.nextLine();
        System.out.println("\033[1;93m"+"Enter your Context-Free-Grammar:"+"\033[0m");
        String line;
        while (input.hasNextLine()) {
            line = input.nextLine();
            if (line.equals("")) {
                break;
            }
            for (int i = 0; i < splittingInputs(line).length; i++) {
                Map.Entry<String, ArrayList<String>> production = parseProduction(splittingInputs(line)[i]);
                grammar.computeIfAbsent(production.getKey(), k -> new ArrayList<>()).add(production.getValue());
            }
        }
        printGrammar(grammar, "\033[1;33m"+"Parsed input grammar: "+"\033[0m");
        Converter converter = new Converter(grammar);
        HashMap<String, ArrayList<ArrayList<String>>> cnfGrammar = converter.convertToCNF();
        printGrammar(cnfGrammar, "Grammar in CNF:");
        change_NT_Vars(cnfGrammar);
        change_T_Vars(cnfGrammar);
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
        CYK cyk = new CYK(word, "S", Terminal_List, Non_Terminal_List, cykInputGrammar(cnfGrammar));
        cyk.doSteps();
    }

    private static String[] splittingInputs(String input) {
        String[] parts = input.split("\\|");
        String[] res = new String[parts.length];
        res[0] = parts[0];
        for (int i = 1; i < parts.length; i++) {
            String sub = parts[0].substring(0, 4) + " " + parts[i];
            res[i] = sub;
        }
        for (int j = 0; j < res.length; j++) {
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < res[j].length(); i++) {
                char currentChar = res[j].charAt(i);
                if(currentChar=='l' && i + 1 < res[j].length() && res[j].charAt(i + 1)=='a'){
                    i=i+5;
                    output.append("lambda");
                    continue;
                }
                output.append(currentChar);
                if (i + 1 < res[j].length()) {
                    char nextChar = res[j].charAt(i + 1);
                    if (Character.isLetter(currentChar) && Character.isLetter(nextChar)) {
                        output.append(",");
                    }
                }
            }
            res[j]=output.toString();
        }
        return res;
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

    private static void change_NT_Vars(HashMap<String, ArrayList<ArrayList<String>>> cnfGrammar) {
        ArrayList<String> keys = new ArrayList<>(cnfGrammar.keySet());
        ArrayList<String> newKeys = new ArrayList<>();
        ArrayList<String> oldKeys = new ArrayList<>();
        for (String key : keys) {
            if (key.matches("[A-Z]\\d+")) {
                String newKey = generateNewKey_NT(cnfGrammar.keySet());
                newKeys.add(newKey);
                oldKeys.add(key);
                cnfGrammar.put(newKey, cnfGrammar.remove(key));
                System.out.println("\nNon-Terminal-Variable: " + key + " becomes: " + newKey);
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

    private static String generateNewKey_NT(Set<String> keys) {
        String newKey = "";
        for (char c = 'A'; c <= 'Z'; c++) {
            if (!keys.contains(Character.toString(c))) {
                newKey = Character.toString(c);
                break;
            }
        }
        return newKey;
    }

    private static void change_T_Vars(HashMap<String, ArrayList<ArrayList<String>>> cnfGrammar) {
        HashSet<String> usedChars = new HashSet<>();
        for (String key : cnfGrammar.keySet()) {
            ArrayList<ArrayList<String>> value = cnfGrammar.get(key);
            for (int i = 0; i < value.size(); i++) {
                ArrayList<String> innerList = value.get(i);
                for (int j = 0; j < innerList.size(); j++) {
                    String current = innerList.get(j);
                    if (current.matches("[a-z]\\d+")) {
                        String newChar = "a";
                        while (usedChars.contains(newChar)) {
                            newChar = Character.toString((char) (newChar.charAt(0) + 3));
                        }
                        usedChars.add(newChar);
                        innerList.set(j, newChar);
                        System.out.println("\nTerminal: " + current + " becomes: " + newChar);
                    }
                }
            }
        }
    }

    private static TreeMap<String, ArrayList<String>> cykInputGrammar(
            HashMap<String, ArrayList<ArrayList<String>>> cnfGrammar) {
        TreeMap<String, ArrayList<String>> grammar = new TreeMap<>();
        for (String key : cnfGrammar.keySet()) {
            ArrayList<ArrayList<String>> value = cnfGrammar.get(key);
            ArrayList<String> newValue = new ArrayList<>();
            for (ArrayList<String> list : value) {
                StringBuilder sb = new StringBuilder();
                for (String s : list) {
                    sb.append(s);
                }
                newValue.add(sb.toString());
            }
            grammar.put(key, newValue);
        }
        return grammar;
    }
}