import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class NullProductElimination {
    
    private HashMap<String, ArrayList<ArrayList<String>>> grammar;

    public NullProductElimination(HashMap<String, ArrayList<ArrayList<String>>> grammar) {
        this.grammar = grammar;
    }

    private void getVarsLeadToNull(HashSet<String> nullSet) {
        for (String lhs : grammar.keySet()) {
            for (ArrayList<String> rhs : grammar.get(lhs)) {
                if (rhs.get(0).equals("lambda")) {
                    nullSet.add(lhs);
                } else if (rhs.size() == 1 && nullSet.contains(rhs.get(0))) {
                    nullSet.add(lhs);
                }
            }
        }
    }

    private boolean containsNull(ArrayList<String> rhs, HashSet<String> nullSet) {
        for (String symbol : rhs) {
            if (nullSet.contains(symbol) || symbol.equals("lambda")) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<ArrayList<String>> getNullifications(ArrayList<String> rhs, HashSet<String> nullSet) {
        ArrayList<ArrayList<String>> nullies = new ArrayList<>();
        HashSet<Integer> nullIndices = new HashSet<>();
        for (int i = 0; i < rhs.size(); i++) {
            String symbol = rhs.get(i);
            if (nullSet.contains(symbol)) {

                nullIndices.add(i);
            }
        }
        HashSet<HashSet<Integer>> indexSubsets = getAllSubsets(nullIndices);
        for (HashSet<Integer> indexSubset : indexSubsets) {
            ArrayList<String> currentNullification = new ArrayList<>();
            for (int i = 0; i < rhs.size(); i++) {
                if (!indexSubset.contains(i)) {
                    currentNullification.add(rhs.get(i));
                }
            }
            if (currentNullification.size() > 0 && !nullies.contains(currentNullification)) {
                nullies.add(currentNullification);
            }
        }
        return nullies;
    }

    private HashSet<HashSet<Integer>> getAllSubsets(HashSet<Integer> originalSet) {
        HashSet<HashSet<Integer>> allSubsets = new HashSet<>();
        ArrayList<Integer> elementList = new ArrayList<>(originalSet);
        int numberOfSubsets = 1 << originalSet.size(); // 2^n subsets

        for (int subsetMask = 0; subsetMask < numberOfSubsets; subsetMask++) {
            HashSet<Integer> subset = new HashSet<>();
            for (int i = 0; i < elementList.size(); i++) {

                if ((subsetMask & (1 << i)) != 0) {
                    subset.add(elementList.get(i));
                }
            }
            allSubsets.add(subset);
        }
        return allSubsets;
    }

    public HashMap<String, ArrayList<ArrayList<String>>> eliminateNull() {
        HashMap<String, ArrayList<ArrayList<String>>> newGrammar = new HashMap<>();
        HashSet<String> prevNullProdSet = new HashSet<>();
        HashSet<String> currNullProdSet = new HashSet<>();
        do {

            prevNullProdSet = new HashSet<String>(currNullProdSet);

            getVarsLeadToNull(currNullProdSet);
        } while (!currNullProdSet.equals(prevNullProdSet));
        for (String lhs : grammar.keySet()) {
            ArrayList<ArrayList<String>> newRhs = new ArrayList<>();
            for (ArrayList<String> rhs : grammar.get(lhs)) {

                if (containsNull(rhs, currNullProdSet)) {

                    ArrayList<ArrayList<String>> nullifications = getNullifications(rhs, currNullProdSet);

                    newRhs.addAll(nullifications);
                } else {
                    newRhs.add(rhs);
                }
            }
            newGrammar.put(lhs, newRhs);
        }
        HashMap<String, ArrayList<ArrayList<String>>> newNewGrammar = new HashMap<>();
        for (String lhs : newGrammar.keySet()) {
            ArrayList<ArrayList<String>> newRhs = new ArrayList<>();
            for (ArrayList<String> rhs : newGrammar.get(lhs)) {

                if (!rhs.get(0).equals("lambda") &&
                        !(rhs.size() < 2 && currNullProdSet.contains(rhs.get(0)))) {

                    newRhs.add(rhs);
                }
            }
            if (newRhs.size() > 0) {
                newNewGrammar.put(lhs, newRhs);
            }
        }
        return newNewGrammar;
    }
}
