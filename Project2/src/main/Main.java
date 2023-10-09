package main;
import distanceMatrix.*;
import java.util.*;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DistanceMatrix d = new DistanceMatrix ("matrix.txt");
		SimulatedAnnealing sa = new SimulatedAnnealing (d,2);
		List <String> s = sa.simulated_annealing();
		System.out.println(sa.distance(s));
	}
	

}
