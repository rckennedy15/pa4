
// For cs310 pa4: the platform graph, a digraph
import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.algs4.*;

// PlatformGraph is a digraph describing how all the physical platforms
// of the subway system are connected. A platform is a place for people to
// wait for a train that runs in a certain direction on a certain trainline
// at a certain station. Each station has 2 platforms for each trainline that
// runs through it.
public class PlatformGraph {
	// The platforms, one for each vertex, i.e. app info for that vertex
	private Platform[] platforms = null;
	// The graph, made available by getGraph()
	private Digraph platformGraph;

	public PlatformGraph(Digraph g, Platform[] platforms) {
		platformGraph = g;
		this.platforms = platforms;		
	}
	public Digraph getDigraph() {
		return platformGraph;
	}

	// provide app info on this vertex via a Platform object
	// platform ids start at 2 to avoid 0 and allow pairs
	// to be id even and id+1 odd.
	public Platform platformOf(int id) {
		if (id > 1 && id < platforms.length) {
			return platforms[id];
		} else
			return null;
	}

	// In Platform graph, find platform for opposite direction (same trainline) at a station
	// Each pair of platforms are given ids e and e+1 where e is an even number.
	// Thus the last bit says which side the platform is on.
	public int oppositePlatformOf(int id) {
		int platformDir = id & 1; // last bit gives side, 0 or 1
		int oppPlatformId;
		if (platformDir == 1)
			oppPlatformId = id - 1; // clear last bit (it was on)
		else
			oppPlatformId = id + 1; // set last bit (it was off)
		return oppPlatformId;
	}

	// Report on how this station is directly connected to other stations in the
	// system via its various platforms
	public void printStationPlatformConnections(String station) {
		System.out.println("printStationPlatformConnections for station " + station);
		for (int i = 2; i < platformGraph.V(); i++) {
			Platform p = platformOf(i);
			if (!p.getStation().getStationName().equals(station))
				continue;
			System.out.println(" Found platform " + i + " for station " + station);
			String lineColor = p.getTrainLineColor();
			// count neighbors on same line, find ones with exactly one such
			int count = 0;
			for (int n : platformGraph.adj(i)) {
				Platform neighbor = platformOf(n);
				if (neighbor.getStation().getStationName().equals(station))
					continue; // ignore in-station connection for this report
				// Red line to RedA line connection is honored here, as well as Red to Red
				if (neighbor.getTrainLineColor().equals(lineColor)) {
					count++;
					System.out.println("  Found down-track neighbor of platform (platform "+n +") at "
							+ neighbor.getStation().getStationName() + " on the " + neighbor.getTrainLine() + " line");
					if (count > 1) {
						System.out.println("  Found additional down-track neighbor (i.e. split in tracks) of platform (platform "+n +") at "
								+ neighbor.getStation().getStationName() + " on the " + neighbor.getTrainLine() + " line");
					}
				}
			}
			if (count == 0) {
				System.out.println("  This platform is at end of line");
			}
		}
	}
	
	// Find end of a given train line, that is, the platform for the end station
	// on this line that lets you ride down the line, determined by finding
	// the opposite platform, the one for exit-off-train only, and then
	// finding its opposite platform using oppositePlatformOf.
	// Two such platforms exist for each line. Return the one further from
	// Government Center (the one easier to label on a map).
	public Platform endOfLinePlatform(String line) {
		Platform endPlatforms[] = new Platform[2]; // for saving found end-platformss
		int endPlatformIndex = 0;
		for (int i = 2; i < platformGraph.V(); i++) {
			Platform p = platformOf(i);
			if (!p.getTrainLine().equals(line))  // looking for platforms on this line
				continue;
			// System.out.println("considering " + p.getStationName());
			// count neighbors on same line, but not same station, find ones with none
			int count = 0;
			for (int n : platformGraph.adj(i)) {
				Platform np = platformOf(n);
				if (np.getTrainLine().equals(line)
						&& np.getStation().getStationName() != p.getStation().getStationName()) {
					// System.out.println("found "+np.getStationName() );
					count++;
				}
			}
			if (count == 0) {
				System.out.println("found end platform " + p.getPlatformId() + " at " + p.getStation().getStationName());
				// want the opposite platform, for start of chain of platforms done the line
				int platformId = p.getPlatformId();
				int oppPlatformId = oppositePlatformOf(platformId);
				if (endPlatformIndex >= 2) {
					System.out.println("**************Too many endstations: can't add " + platformOf(oppPlatformId));
					return null;
				}
				// save found end platform
				endPlatforms[endPlatformIndex++] = platformOf(oppPlatformId);
			}
			
		}
		// Here hopefully with 2 end platforms
		if (endPlatformIndex != 2) {
			System.out.println("found " + endPlatformIndex + "endstations, expecting 2");
			return null;
		}
		// OK have 2 endstations, find further one out and return it
		double distance0 = endPlatforms[0].getStation().distanceFromBoston();
		double distance1 = endPlatforms[1].getStation().distanceFromBoston();
		return distance0 > distance1 ? endPlatforms[0] : endPlatforms[1];
	}

	// Print stations on a trainline, starting from the end station that is
	// further out from central Boston, using the Platform graph
	public void printTrainLine(String trainLine) {
		System.out.println("printTrainLine for " + trainLine);
		Platform endPlatform = endOfLinePlatform(trainLine);
		if (endPlatform == null) {
			System.out.println("printTrainLine failed: end station not found");
			return;
		}
		int platformId = endPlatform.getPlatformId();
		// tricky part: avoid choosing previously-visited stations, so track them
		boolean found = false;
		List<String> foundStations = new ArrayList<String>();
		foundStations.add(endPlatform.getStation().getStationName());
		do {
			found = false;
			for (int i : platformGraph.adj(platformId)) {
				if (platformOf(i).getTrainLine().equals(trainLine)) {
					String foundStationName = platformOf(i).getStation().getStationName();
					// System.out.println("found " + foundStationName);
					if (foundStations.contains(foundStationName)) {
						continue; // don't go backwards along line
					}
					found = true;
					platformId = i; // one to use next
					foundStations.add(foundStationName); // prevent backtracking on line
					break; // done
				}
			}
		} while (found);
		System.out.println(foundStations);
	}

	public static void main(String[] args) {
		MetroSystem mSys = new MetroSystem(args[0]);
		PlatformGraph platformGraph = mSys.getPlatformGraph();
		// System.out.println(G.toString());
		Platform p = platformGraph.platformOf(2);
		System.out.println("Platform 2 is for station " + p.getStation().getStationName());
		platformGraph.printStationPlatformConnections("ParkStreet");
	}
}


