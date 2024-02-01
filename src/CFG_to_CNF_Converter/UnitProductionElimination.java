package CFG_to_CNF_Converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class UnitProductionElimination {
	private HashMap<String, ArrayList<ArrayList<String>>> grammar;

	public UnitProductionElimination(HashMap<String, ArrayList<ArrayList<String>>> grammar) {
		this.grammar = grammar;
	}

	private void addUnitsToSet(HashSet<String> unitSet) {
		for (String lhs : unitSet) {
			getUnitsFromRhs(lhs, unitSet);
		}
	}

	private void getUnitsFromRhs(String lhs, HashSet<String> unitSet) {
		for (ArrayList<String> rhs : grammar.get(lhs)) {
			if (rhs.size() == 1 && Character.isUpperCase(rhs.get(0).charAt(0))) {
				unitSet.add(rhs.get(0));
			}
		}
	}

	private ArrayList<ArrayList<String>> getNonUnits(String lhs) {
		ArrayList<ArrayList<String>> nonUnits = new ArrayList<>();
		for (ArrayList<String> rhs : grammar.get(lhs)) {
			if (rhs.size() > 1) {
				nonUnits.add(rhs);
			}
		}
		return nonUnits;
	}

	public HashMap<String, ArrayList<ArrayList<String>>> eliminateUnit() {
		HashMap<String, ArrayList<ArrayList<String>>> newGrammar = new HashMap<>();
		HashMap<String, HashSet<String>> unitProds = new HashMap<>();

		for (String lhs : grammar.keySet()) {
			HashSet<String> prevUnitSet = new HashSet<>();
			getUnitsFromRhs(lhs, prevUnitSet);

			HashSet<String> currUnitSet = new HashSet<>();
			do {
				prevUnitSet = new HashSet<String>(currUnitSet);
				addUnitsToSet(currUnitSet);
			} while (!currUnitSet.equals(prevUnitSet));
			currUnitSet.remove(lhs);
			unitProds.put(lhs, currUnitSet);
		}

		ArrayList<ArrayList<String>> nonUnitProds;
		for (String lhs : unitProds.keySet()) {
			for (String rhs : unitProds.get(lhs)) {
				nonUnitProds = getNonUnits(rhs);
				grammar.get(lhs).addAll(nonUnitProds);
			}
		}

		for (String lhs : grammar.keySet()) {
			ArrayList<ArrayList<String>> newRhs = new ArrayList<>();
			for (ArrayList<String> rhs : grammar.get(lhs)) {
				if (rhs.size() > 1 || Character.isLowerCase(rhs.get(0).charAt(0))) {
					newRhs.add(rhs);
				}
			}
			if (newRhs.size() > 0) {
				newGrammar.put(lhs, newRhs);
			}
		}

		return newGrammar;
	}
}
