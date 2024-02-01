package CFG_to_CNF_Converter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Converter {

	private HashMap<String,ArrayList<ArrayList<String>>> grammar;

	public Converter(HashMap<String,ArrayList<ArrayList<String>>> grammar) {
		this.grammar = grammar;
	}
	
	public HashMap<String,ArrayList<ArrayList<String>>> convertToCNF() {
		printGrammar(grammar, "\n"+"\033[1;93m"+"Input grammar:"+"\033[0m");
	    grammar = new NullProductElimination(grammar).eliminateNull();
		printGrammar(grammar, "\033[1;93m"+"Null productions eliminated:"+"\033[0m");

		grammar = new UnitProductionElimination(grammar).eliminateUnit();
		printGrammar(grammar, "\033[1;93m"+"Unit productions eliminated:"+"\033[0m");

		grammar = new BinaryProductionConverter(grammar).convertBinary();
		printGrammar(grammar, "\033[1;93m"+"Break up productions longer than two symbols:"+"\033[0m");
		
		grammar = new SingleTerminalConverter(grammar).convertTerminal();
		printGrammar(grammar, "\033[1;93m"+"Make a dedicated production for each terminal:"+"\033[0m");
		return grammar;
	}

	private static void printGrammar(HashMap<String, ArrayList<ArrayList<String>>> grammar, String heading) {
			System.out.print(heading + "\n");
			for (Map.Entry<String, ArrayList<ArrayList<String>>> entry : grammar.entrySet()) {
				System.out.println(entry.getKey() + " -> " + entry.getValue());
			}
			System.out.print("\n\n");
	}
}
