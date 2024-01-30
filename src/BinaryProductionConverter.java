import java.util.ArrayList;
import java.util.HashMap;

public class BinaryProductionConverter {
    private HashMap<String, ArrayList<ArrayList<String>>> grammar;

    public BinaryProductionConverter(HashMap<String, ArrayList<ArrayList<String>>> grammar) {
        this.grammar = grammar;
    }

	public HashMap<String, ArrayList<ArrayList<String>>> convertBinary() {
		HashMap<String, ArrayList<ArrayList<String>>> newGrammar = new HashMap<>();
		for (String lhs : grammar.keySet()) {
	
			for (ArrayList<String> rhs : grammar.get(lhs)) {
				
				if (rhs.size() > 2) {
					int suffix = 1;
					String newLhs = lhs;
					while (rhs.size() > 2) {
						String newRhsFirstSymbol = rhs.remove(0);
						String newVariable;
						do {
							newVariable = newLhs.charAt(0) + String.valueOf(suffix++);
						} while (newGrammar.containsKey(newVariable));
						ArrayList<String> newProductionRhs = new ArrayList<>();
						newProductionRhs.add(newRhsFirstSymbol);
						newProductionRhs.add(newVariable);
						newGrammar.putIfAbsent(newLhs, new ArrayList<>());
						newGrammar.get(newLhs).add(newProductionRhs);

						newLhs = newVariable;
					}
					newGrammar.putIfAbsent(newLhs, new ArrayList<>());
					newGrammar.get(newLhs).add(rhs);
				} else {
					newGrammar.putIfAbsent(lhs, new ArrayList<>());
					newGrammar.get(lhs).add(rhs);
				}
			}
		}
		return newGrammar;
	}
}
