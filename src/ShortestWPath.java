import edu.princeton.cs.algs4.*;

public class ShortestWPath {
	private EdgeWeightedDigraph ewd;
	private DijkstraSP d;
	private static MetroSystem mS;
	private static PlatformGraph pg;
	
	public ShortestWPath(PlatformGraph pg, int sourceVertex) {
		this.ewd = convertToWeighted(pg);
		this.d = new DijkstraSP(ewd, sourceVertex);
	}
	
	public Iterable<DirectedEdge> pathTo(int endVertex) {
		return d.pathTo(endVertex);
	}
	
	// NOTE: I did it again. I will never get used to using .equals to compare strings.
	public static EdgeWeightedDigraph convertToWeighted(PlatformGraph pg) {
		// for each edge in directed graph, add weight based on formula 1, 3, or 7
		// 7 for platform-platform in same station
		// 3 for silver line
		// 1 for all others (platform-platform different stations)
		EdgeWeightedDigraph weighted = new EdgeWeightedDigraph(pg.getDigraph().V());
		for (int i = 2; i < pg.getDigraph().V(); i++) {
			for(int adj : pg.getDigraph().adj(i)) {
				if (pg.platformOf(i).getStation() == pg.platformOf(adj).getStation()) {
					weighted.addEdge(new DirectedEdge(i, adj, 7));
				} else if (pg.platformOf(i).getTrainLineColor().equals("Sil") && pg.platformOf(adj).getTrainLineColor().equals("Sil")) {
					weighted.addEdge(new DirectedEdge(i, adj, 3));
				} else {
					weighted.addEdge(new DirectedEdge(i, adj, 1));
				}
			}
		}
		return weighted;
	}
	
	public static void printRoute(Iterable<DirectedEdge> path) {
		for (DirectedEdge de : path) {
			Station fromStation = pg.platformOf(de.from()).getStation();
			Station toStation = pg.platformOf(de.to()).getStation();
			System.out.print(fromStation.getStationName() + fromStation.getTrainLines() + " -> " + toStation.getStationName() + toStation.getTrainLines() + "(" + de.weight() + ") ");
		}
	}
	
	public static void main(String[] args) {
		mS = new MetroSystem(args[0]);
		pg = mS.getPlatformGraph();
		// lambda function to find vertex for station string
		java.util.function.Function<String, Integer> findVertex = (station) -> {
			for (int i = 2; i < pg.getDigraph().V(); i++) {
				Platform p = pg.platformOf(i);
				if (!p.getStation().getStationName().equals(station))
					continue;
				return (Integer) p.getPlatformId();
			}
			return null;
		};
		// find platform vertex for JFK/UMass and Wonderland
		int JFKVertex = findVertex.apply("JFK/UMass");
		int WonderlandVertex = findVertex.apply("Wonderland");
		ShortestWPath swp = new ShortestWPath(pg, JFKVertex);
		printRoute(swp.pathTo(WonderlandVertex));
	}
}
