package CFG_to_CNF_Converter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SingleTerminalConverter {
    private HashMap<String,ArrayList<ArrayList<String>>> grammar;

	public SingleTerminalConverter(HashMap<String,ArrayList<ArrayList<String>>> grammar) {
		this.grammar = grammar;
	}

    public HashMap<String,ArrayList<ArrayList<String>>> convertTerminal() {
		HashMap<String, String> handledTerminals = new HashMap<>();
		HashSet<String> varsToDelete = new HashSet<>();
		String prod;
		for (String lhs : grammar.keySet()) {
			if (grammar.get(lhs).size() == 1 && grammar.get(lhs).get(0).size() == 1) {
				prod = grammar.get(lhs).get(0).get(0);
				if (Character.isLowerCase(prod.charAt(0))) {
					if (handledTerminals.containsKey(prod)) {
						varsToDelete.add(lhs);
					} else {
						handledTerminals.put(prod, lhs);
					}
				}
			}
		}


		HashMap<String, ArrayList<ArrayList<String>>> newGrammar = new HashMap<>();
		ArrayList<ArrayList<String>> newRhs;
		for (String lhs : grammar.keySet()) {
			for (ArrayList<String> rhs : grammar.get(lhs)) {
				for (String symbol : rhs) {
					if (Character.isLowerCase(symbol.charAt(0)) && !handledTerminals.containsKey(symbol)) {
						int suffix = 1;
						String newVariable;
						do {
							newVariable = symbol.toUpperCase().charAt(0) + String.valueOf(suffix++);
						} while (grammar.containsKey(newVariable));
						newRhs = new ArrayList<>();
						newRhs.add(new ArrayList<>());
						newRhs.get(0).add(symbol);
						newGrammar.put(newVariable, newRhs);
						handledTerminals.put(symbol, newVariable);
					}
				}
			}
		}

		ArrayList<ArrayList<String>> newProdList;
		ArrayList<String> newProd;
		for (String lhs : grammar.keySet()) {
			newProdList = new ArrayList<>();
			for (ArrayList<String> rhs : grammar.get(lhs)) {
				newProd = new ArrayList<>();
				for (String symbol : rhs) {
					if (Character.isLowerCase(symbol.charAt(0)) && rhs.size() > 1) {
						newProd.add(handledTerminals.get(symbol));
					} else {
						newProd.add(symbol);
					}
				}
				newProdList.add(newProd);
			}
			newGrammar.put(lhs, newProdList);
		}

		return newGrammar;
	}
}
