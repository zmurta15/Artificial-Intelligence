package main;

import searchalgorithm.*;
//import searchproblem.*;
import undirectedgraph.*;
import java.util.*;

public class Main {

	public static void main(String[] args) {
		Graph graph = Romenia.defineGraph();
        //graph.showLinks();
        //graph.showSets();
		List<String> labels = new ArrayList<String>();
		labels.add("Dobrogea");
		labels.add("Banat");
		graph.searchSolutionTwoMoreProvincia("Arad", "Bucharest", Algorithms.AStarSearch, labels);
        //graph.searchSolutionOneProvincia("Arad", "Bucharest", Algorithms.AStarSearch, "Dobrogea");
        //graph.showSolution(n);
	}

}
