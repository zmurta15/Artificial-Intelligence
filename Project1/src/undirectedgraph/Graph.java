package undirectedgraph;

import java.util.*;
import searchalgorithm.*;
import searchproblem.SearchProblem;
import searchproblem.State;

public class Graph {
	private HashMap<String,Vertex> vertices;
	private HashMap<Integer,Edge> edges;	
	private ArrayList<VertexSet> vSets;
	
	private long expansions;
	private long generated;
	private long repeated;
	private double time;
	
	public Graph() {
		this.vertices = new HashMap<String,Vertex>();
		this.edges = new HashMap<Integer,Edge>();
		this.vSets = new ArrayList<VertexSet>();
		this.expansions = 0;
		this.generated = 0;
		this.repeated = 0;
		this.time = 0;
	}
	
	public void addVertice(String label, double lat, double lng) {
		Vertex v =  new Vertex(label);
		this.vertices.put(label, v);
		v.setCoordinates(lat, lng);
	}
	
	public Vertex getVertice(String label) {
		return this.vertices.get(label);
	}
	
	public void addVerticeSet(String label) {
		VertexSet vSet =  new VertexSet(label);
		this.vSets.add(vSet);
	}
	
	public VertexSet getVerticeSet(String setLabel) {
		for (VertexSet vSet : vSets) {
			if (vSet.getLabel()==setLabel) 
				return vSet;
		}
		return null;
	}
	
	public void addVerticeToSet(String labelSet,String labelVertex) {
		Vertex v = this.vertices.get(labelVertex);
		for (VertexSet vSet : vSets) {
			if (vSet.getLabel()==labelSet) {
				vSet.addVertice(v);
				break;
			}
		}
	}
	
	public boolean addEdge(Vertex one, Vertex two, double weight) {
		if (one.equals(two)) return false;
		Edge e = new Edge(one,two,weight);
		if (edges.containsKey(e.hashcode())) return false;
		if (one.containsNeighbor(e) || two.containsNeighbor(e))  return false;	
		edges.put(e.hashcode(), e);
		one.addNeighbor(e);
		two.addNeighbor(e);
		return true;
	} 
	
	public boolean addEdge(String oneLabel, String twoLabel, double weight) {
		Vertex one = getVertice(oneLabel);
		Vertex two = getVertice(twoLabel);
		return addEdge(one,two,weight);
	}
	
	public boolean addEdge(String oneLabel, String twoLabel) {
		Vertex one = getVertice(oneLabel);
		Vertex two = getVertice(twoLabel);
		return addEdge(one,two,one.straightLineDistance(two));
	}

	public void showLinks() {
		System.out.println("********************* LINKS *********************");
		for (Vertex current: vertices.values()) {		
			System.out.print(current + ": ");
			for (Edge e: current.getNeighbors()) {
				System.out.print(e.getNeighbor(current) + " (" + e.getWeight() + "); ");
			}
			System.out.println();
		}
		System.out.println("*************************************************");
	}
	
	public void showSets() {
		System.out.println("********************* SETS *********************");
		for (VertexSet vSet: vSets) {
			System.out.println(vSet);
		}
		System.out.println("*************************************************");
	}
	
	public Node searchSolution(String initLabel, String goalLabel, Algorithms algID) {
		State init = new State(this.getVertice(initLabel));
		State goal = new State(this.getVertice(goalLabel));
		SearchProblem prob = new SearchProblem(init,goal);
		SearchAlgorithm alg = null;
		switch(algID) {
			case BreadthFirstSearch :
				alg = new BreadthFirstSearch(prob);
				break;
			case DepthFirstSearch :
				alg = new DepthFirstSearch(prob);
				break; 
			case UniformCostSearch :
				alg = new UniformCostSearch(prob);
				break; 
			case GreedySearch :
				alg = new GreedySearch(prob);
				break; 
			case AStarSearch :
				alg = new AStarSearch(prob);
				break; 
		   default : 
			  System.out.println("ERROR: algorithm not implemented!");
		}
		Node n = alg.searchSolution();	
		Map<String,Number> m = alg.getMetrics();
		this.expansions += (long)m.get("Node Expansions");
		this.generated += (long)m.get("Nodes Generated");
		this.repeated += (long)m.get("State repetitions");
		this.time += (double)m.get("Runtime (ms)");
		return n;
	}
	
	public Node searchSolutionOneProvincia(String initLabel, String goalLabel,Algorithms algID, String label) {
		VertexSet v = this.getVerticeSet(label);
		Graph ne = new Graph();
		ne.addVertice(initLabel, this.getVertice(initLabel).getLatitude(),this.getVertice(initLabel).getLongitude());
		ne.addVertice(goalLabel, this.getVertice(goalLabel).getLatitude(),this.getVertice(goalLabel).getLongitude());
			for (Vertex v1 : v.getVertices()) {
				ne.addVertice(v1.getLabel(), v1.getLatitude(), v1.getLongitude());
				ne.addEdge(initLabel, v1.getLabel(), this.searchSolution(initLabel, v1.getLabel(), algID).getPathCost());
				ne.addEdge(v1.getLabel(), goalLabel,this.searchSolution(v1.getLabel(), goalLabel, algID).getPathCost());
			}
		Node n = ne.searchSolution(initLabel, goalLabel, algID);
		ne.showLinks();
		ne.showSets();
		ne.showSolution(n);
		return n;
	}
	
	public Node searchSolutionTwoMoreProvincia(String initLabel, String goalLabel,Algorithms algID, List<String> labels) {
		Graph g = new Graph();
		g.addVertice(initLabel, this.getVertice(initLabel).getLatitude(),this.getVertice(initLabel).getLongitude());
		g.addVertice(goalLabel, this.getVertice(goalLabel).getLatitude(),this.getVertice(goalLabel).getLongitude());
		
		if (labels.size() == 1) {
			return this.searchSolutionOneProvincia(initLabel, goalLabel, algID, labels.get(0));
		}
		
		//Lista das provincias
		List<VertexSet> vert = new ArrayList<VertexSet>();
		for (String s: labels) {
			vert.add(this.getVerticeSet(s));
		}
		
		//Adiciona todos os vértices
		for (VertexSet v: vert) {
			for (Vertex v2: v.getVertices()) {
				g.addVertice(v2.getLabel(), v2.getLatitude(), v2.getLongitude());
			}
		}
		
		//begin
		for (Vertex v1: vert.get(0).getVertices()) {
			g.addEdge(initLabel, v1.getLabel(), this.searchSolution(initLabel, v1.getLabel(), algID).getPathCost());
		}
		
		for (int i = 0;i < vert.size()-1; i++) {
			VertexSet aux = vert.get(i);
			VertexSet aux2 = vert.get(i+1);
			for (Vertex v : aux.getVertices()) {
				for (Vertex v2 : aux2.getVertices()) {
					g.addEdge(v.getLabel(),v2.getLabel(), this.searchSolution(v.getLabel(),v2.getLabel(), algID).getPathCost());
				}
			}
		}
		
		//end
		for (Vertex v1: vert.get(vert.size()-1).getVertices()) {
			g.addEdge(v1.getLabel(), goalLabel,this.searchSolution(v1.getLabel(), goalLabel, algID).getPathCost());
		}
		
		Node n = g.searchSolution(initLabel, goalLabel, algID);
		g.showLinks();
		g.showSets();
		g.showSolution(n);
		return n;
	}

	public void showSolution(Node n) {
		System.out.println("******************* SOLUTION ********************");
		System.out.println("Node Expansions: " + this.expansions);
		System.out.println("Nodes Generated: " + this.generated);
		System.out.println("State Repetitions: " + this.repeated);
		System.out.printf("Runtime (ms): %6.3f \n",this.time);
		Node ni = null;
		List<Object> solution = n.getPath();
		double dist = 0;
		for (int i = 0; i<solution.size()-1;i++) {
			System.out.printf("| %-9s | %4.0f | ",solution.get(i), dist);
			ni = searchSolution(solution.get(i).toString(), solution.get(i+1).toString(), Algorithms.AStarSearch);
			System.out.print(ni.getPath());	
			System.out.println(" -> " + (int)ni.getPathCost());
			dist += ni.getPathCost();
		}
		System.out.printf("| %-9s | %4.0f | \n",solution.get(solution.size()-1), dist);
		System.out.println("*************************************************");
	}
	
	
}
