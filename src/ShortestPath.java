import edu.princeton.cs.algs4.*;

public class ShortestPath {
	public static void main(String[] args) {
		MetroSystem mS = new MetroSystem(args[0]);
		StationGraph stationGraph = mS.getStationGraph();
		
		// id 98 == JFK/UMass
		// id 24 == Bowdowin
		// id 3 == Wonderland
		BreadthFirstPaths JFK = new BreadthFirstPaths(stationGraph.getGraph(), 98);
		// Bowdowin
		System.out.println("\nBFS Shortest Path from JFK to Bowdowin: ");
		if (JFK.hasPathTo(24)) {
			for (int v : JFK.pathTo(24)) {
				Station currentStation = stationGraph.stationOf(v);
				System.out.print("" + currentStation.getStationName() + currentStation.getTrainLines() + " ");
			}
		}
		// Wonderland
		System.out.println("\nBFS Shortest Path from JFK to Wonderland: ");
		if (JFK.hasPathTo(3)) {
			for (int v : JFK.pathTo(3)) {
				Station currentStation = stationGraph.stationOf(v);
				System.out.print("" + currentStation.getStationName() + currentStation.getTrainLines() + " ");
			}
		}
	}
}