package distanceMatrix;
import java.util.*;

public class SimulatedAnnealing {
	
	private DistanceMatrix distanceMatrix;
	private double initialTemperature;
	private int nIter;
	
	
	
	public SimulatedAnnealing (DistanceMatrix distanceMatrix, int nIter) {
		this.distanceMatrix = distanceMatrix;
		this.nIter = nIter;
	}
	
	private double decayFunction(double temperature) {
		return temperature * 0.9;
	}
	
	private double initial () {
		this.initialTemperature = 50;
		return initialTemperature;
	}
	
	private List<String> initialSolution () {
		return distanceMatrix.getCities();
	}
	
	private int var_n_iter () {
		nIter = (int) Math.abs(200/initialTemperature);
		return nIter;
	}
	
	public int distance (List<String> s) {
		int total = 0;
		for (int i = 0; i < s.size() - 1; i ++) {
			total+= distanceMatrix.distance(s.get(i), s.get(i+1)); 
		}
		total += distanceMatrix.distance(s.get(s.size()-1), s.get(0));
		
		return total;
	}
	
	private List<String> vizinho (List<String> corrente) {
		int size = corrente.size();
		Random rand = new Random();
		int randomNumber = rand.nextInt(size+1);
		int randomNumber2 = rand.nextInt(size+1);
		while(randomNumber == randomNumber2) {
			randomNumber2 = rand.nextInt(size+1);
		}
		if (randomNumber2 < randomNumber) {
			int aux = randomNumber2;
			randomNumber2 = randomNumber;
			randomNumber = aux;
		}
		List<String> aux = new ArrayList<String>();
		for (int i = randomNumber + 1; i < randomNumber2; i++ ) {
			aux.add(corrente.get(i));
		}
		Collections.reverse(aux);
	
		List<String> aux2 = new ArrayList<String>();
		for (int i =0 ; i <= randomNumber ; i++) {
			aux2.add(corrente.get(i));
		}
		aux2.addAll(aux);
		
		for (int i = randomNumber2; i< size; i++) {
			aux2.add(corrente.get(i));
		}
		
		return aux2;
	}
	
	private boolean criterio_paragem () {
		if (initialTemperature < 0.5) {
			return true;
		}
		return false;
	}
	
	private boolean prob (double p) {
		Random r = new Random();
		if (r.nextDouble() < p) {
			return true;
		}
		return false;
	}
	
	public List<String> simulated_annealing() {
		List<String> current = initialSolution();
		List<String> best = current;
		List<String> next = null; 
		double t = this.initial();
		while(true) {
			for(int n = 1; n<nIter; n++) {
				next = this.vizinho(current);
				int d = this.distance(next) - this.distance(current);
				if ( d < 0) {
					current = next;
					if (this.distance(current) < this.distance(best)) {
						best = current;
					}
				} else {
					double p = Math.exp(-d/t);
					if (this.prob(p)) {
						current = next;
					}
				}
			}
			if (this.criterio_paragem()) {
				return best;
			}
			nIter = this.var_n_iter();
			initialTemperature = this.decayFunction(initialTemperature);
			System.out.println(current);
		}
	}
	
	
	
	
	
	
}
