
// For cs310 pa4 Boston metro graph 
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import edu.princeton.cs.algs4.*;

public class StationGraph {

	private Station[] stations = null;
	private Map<String, Station> stationMap = null;
	// The graph, made available by getGraph()
	private Graph stationGraph;

	public StationGraph(Graph g, Map<String, Station> sMap) {
		stationGraph = g;
		stationMap = sMap;		
		// create stations table for lookup by stationId
		// station ids start at 1, like the file does
		stations = new Station[stationMap.size() + 1];
		for (String s : stationMap.keySet()) {
			Station station = stationMap.get(s);
			stations[station.getStationId()] = station;
		}
	}
	
	public Graph getGraph() {
		return stationGraph;
	}

	public Station stationOf(int id) {
		if (id > 0 && id < stations.length) {
			return stations[id];
		} else
			return null;
	}
	
	public Station stationOf(String name) {
		return stationMap.get(name);
	}
	
	// Report on how this station is directly connected to other stations in the
	// system using the Station graph
	public void printStationNeighbors(String stationName) {
	
		Station station = stationMap.get(stationName);
		if (station == null) {
			System.out.println("printStationNeighbors: can't find station " + stationName);	
			return;
		}
		System.out.println("printStationNeighbors for " + stationName + ", id " + station.getStationId() + " train lines "
				+ station.getTrainLines());
		for (int i : stationGraph.adj(station.getStationId())) {
			System.out.println("Neighbor station: " + stations[i].getStationName() + " id " + i + ", train lines "
					+ stations[i].getTrainLines());
		}
	}

	// Find end of a given train line, that is, the station
	// that has only one neighbor on the same train line
	// Two such stations exist for each line. Return the one further from
	// Government Center.
	public Station endOfLineStation(String line) {
		// Note: does not validate line, and hence can return null
		// Note: does not account for split lines: RedA and RedB
		// 			 are treated as separate lines for instance.
		
		// Every line has two end stations, but this function will only 
		// return the one furthest from Boston
		Station[] endStations = new Station[2];
		
		for (int i = 1; i <= stations.length; i++) {
			if (stationOf(i) == null)
				continue;
			// gets Station for i (id), gets the train lines (Red, Green, ...),
			// and checks if trainLine is in this array
			if (stationOf(i).getTrainLines().contains(line)) {
				int numberOfAdjacentStations = 0;
				for (Integer adj : stationGraph.adj(i)) {
					if (stationOf(adj).getTrainLines().contains(line)) {
						numberOfAdjacentStations++;
					}
				}
				
				if (numberOfAdjacentStations == 1) {
					if (endStations[0] == null) {
						endStations[0] = stationOf(i);
					} else {
						endStations[1] = stationOf(i);
					}
				}
			}
		}
		return endStations[0].distanceFromBoston() > endStations[1].distanceFromBoston() 
					? endStations[0] 
					: endStations[1];
	}

	// Print stations on a trainline, starting from the end station that is
	// further out from central Boston, using the Station graph
	public void printTrainLine1(String trainLine) {
		// Nested lambda function to pretty print each station
		// (can you tell I'm a JS dev lol)
		java.util.function.Consumer<Station> printStation = (s) -> {
			System.out.print("" + s.getStationName() + s.getTrainLines() + " ");
		};
		
		System.out.println("printTrainLine1 for " + trainLine);
		Station endStation = endOfLineStation(trainLine);
		Set<Station> visitedStations = new HashSet<>();
		Station currentStation = endStation;
		
		printStation.accept(currentStation);
		for (int i = 1; i <= stations.length; i++) {
			for (Integer adj : stationGraph.adj(currentStation.getStationId())) {
				if (stationOf(adj).getTrainLines().contains(trainLine)) {
					if (!visitedStations.contains(stationOf(adj))) {
						visitedStations.add(currentStation);
						currentStation = stationOf(adj);
						printStation.accept(currentStation);
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		MetroSystem mS = new MetroSystem(args[0]);
		StationGraph stationGraph = mS.getStationGraph();
		stationGraph.printStationNeighbors("JFK/UMass");
		stationGraph.printStationNeighbors("ParkStreet");
		System.out.println("-------");
		stationGraph.printTrainLine1("Red");
	}
}


