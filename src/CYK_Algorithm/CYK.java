package CYK_Algorithm;
import java.util.*;

public class CYK {

    private String word;
    private String startingSymbol;
    private ArrayList<String> terminals = new ArrayList<String>();
    private ArrayList<String> nonTerminals = new ArrayList<String>();
    private TreeMap<String, ArrayList<String>> grammar = new TreeMap<>();

    public CYK(String word, String startingSymbol, ArrayList<String> terminals, ArrayList<String> nonTerminals,
            TreeMap<String, ArrayList<String>> grammar) {
        this.word = word;
        this.startingSymbol = startingSymbol;
        this.terminals = terminals;
        this.nonTerminals = nonTerminals;
        this.grammar = grammar;
    }

    public void doSteps() {
        String[][] cykTable = createCYKTable();
        printResult(doCyk(cykTable));
    }

    public void printResult(String[][] cykTable) {
        System.out.println("Word: " + word);
        System.out.println("\nG = (" + terminals.toString().replace("[", "{").replace("]", "}")
                + ", " + nonTerminals.toString().replace("[", "{").replace("]", "}")
                + ", P, " + startingSymbol + ")\n\nWith Productions P as:");
        for (String s : grammar.keySet()) {
            System.out.println(
                    s + " -> " + grammar.get(s).toString().replaceAll("[\\[\\]\\,]", "").replaceAll("\\s", " | "));
        }
        System.out.println("\nApplying CYK-Algorithm:\n");
        drawTable(cykTable);
    }

    public void drawTable(String[][] cykTable) {
        int l = findLongestString(cykTable) + 2;
        String formatString = "| %-" + l + "s ";
        String s = "";
        StringBuilder sb = new StringBuilder();
        sb.append("+");
        for (int x = 0; x <= l + 2; x++) {
            if (x == l + 2) {
                sb.append("+");
            } else {
                sb.append("-");
            }
        }
        String low = sb.toString();
        sb.delete(0, 1);
        String lowRight = sb.toString();
        for (int i = 0; i < cykTable.length; i++) {
            for (int j = 0; j <= cykTable[i].length; j++) {
                System.out.print((j == 0) ? low : (i <= 1 && j == cykTable[i].length - 1) ? "" : lowRight);
            }
            System.out.println();
            for (int j = 0; j < cykTable[i].length; j++) {
                s = (cykTable[i][j].isEmpty()) ? "-" : cykTable[i][j];
                System.out.format(formatString, s.replaceAll("\\s", ","));
                if (j == cykTable[i].length - 1) {
                    System.out.print("|");
                }
            }
            System.out.println();
        }
        System.out.println(low + "\n");
        if (cykTable[cykTable.length - 1][cykTable[cykTable.length - 1].length - 1].contains(startingSymbol)) {
            System.out.println("The word \"" + word + "\" is an element of the CFG G and can be derived from it.");
        } else {
            System.out.println(
                    "The word \"" + word + "\" is not an element of the CFG G and can not be derived from it.");
        }
    }

    public static int findLongestString(String[][] cykTable) {
        int x = 0;
        for (String[] s : cykTable) {
            for (String d : s) {
                if (d.length() > x) {
                    x = d.length();
                }
            }
        }
        return x;
    }

    public String[][] createCYKTable() {
        int length = word.length();
        String[][] cykTable = new String[length + 1][];
        cykTable[0] = new String[length];
        for (int i = 1; i < cykTable.length; i++) {
            cykTable[i] = new String[length - (i - 1)];
        }
        for (int i = 1; i < cykTable.length; i++) {
            for (int j = 0; j < cykTable[i].length; j++) {
                cykTable[i][j] = "";
            }
        }
        return cykTable;
    }

    public String[][] doCyk(String[][] cykTable) {
        for (int i = 0; i < cykTable[0].length; i++) {
            cykTable[0][i] = manageWord(word, i);
        }
        for (int i = 0; i < cykTable[1].length; i++) {
            String[] validCombinations = checkIfProduces(new String[] { cykTable[0][i] });
            cykTable[1][i] = toString(validCombinations);
        }
        if (word.length() <= 1) {
            return cykTable;
        }
        for (int i = 0; i < cykTable[2].length; i++) {
            String[] downwards = toArray(cykTable[1][i]);
            String[] diagonal = toArray(cykTable[1][i + 1]);
            String[] validCombinations = checkIfProduces(getAllCombinations(downwards, diagonal));
            cykTable[2][i] = toString(validCombinations);
        }
        if (word.length() <= 2) {
            return cykTable;
        }
        TreeSet<String> currentValues = new TreeSet<String>();

        for (int i = 3; i < cykTable.length; i++) {
            for (int j = 0; j < cykTable[i].length; j++) {
                for (int compareFrom = 1; compareFrom < i; compareFrom++) {
                    String[] downwards = cykTable[compareFrom][j].split("\\s");
                    String[] diagonal = cykTable[i - compareFrom][j + compareFrom].split("\\s");
                    String[] combinations = getAllCombinations(downwards, diagonal);
                    String[] validCombinations = checkIfProduces(combinations);
                    if (cykTable[i][j].isEmpty()) {
                        cykTable[i][j] = toString(validCombinations);
                    } else {
                        String[] oldValues = toArray(cykTable[i][j]);
                        ArrayList<String> newValues = new ArrayList<String>(Arrays.asList(oldValues));
                        newValues.addAll(Arrays.asList(validCombinations));
                        currentValues.addAll(newValues);
                        cykTable[i][j] = toString(currentValues.toArray(new String[currentValues.size()]));
                    }
                }
                currentValues.clear();
            }
        }
        return cykTable;
    }

    public static String manageWord(String word, int position) {
        return Character.toString(word.charAt(position));

    }

    public String[] checkIfProduces(String[] toCheck) {
        ArrayList<String> storage = new ArrayList<>();
        for (String s : grammar.keySet()) {
            for (String current : toCheck) {
                if (grammar.get(s).contains(current)) {
                    storage.add(s);
                }
            }
        }
        if (storage.size() == 0) {
            return new String[] {};
        }
        return storage.toArray(new String[storage.size()]);
    }

    public static String[] getAllCombinations(String[] from, String[] to) {
        int length = from.length * to.length;
        int counter = 0;
        String[] combinations = new String[length];
        if (length == 0) {
            return combinations;
        }
        ;
        for (int i = 0; i < from.length; i++) {
            for (int j = 0; j < to.length; j++) {
                combinations[counter] = from[i] + to[j];
                counter++;
            }
        }
        return combinations;
    }

    public static String toString(String[] input) {
        return Arrays.toString(input).replaceAll("[\\[\\]\\,]", "");
    }

    public static String[] toArray(String input) {
        return input.split("\\s");
    }

}