import java.util.Set;
import java.util.TreeSet;

public class Station {	
	// in Boston area, these are approximately valid:
	// The circumference of the earth along the equator is 24,901.92 miles, 
	// over 360 degrees, yields
	public final static double MILES_PER_LATITUDE_DEGREE = 69.2;
	// longitude degrees are scaled down by cosine(latitude)--
	public final static double MILES_PER_LONGITUDE_DEGREE = 51.2;
	// Using Government Center coordinates--
	public final static double BOSTON_LATITUDE = 42.359705;
	public final static double BOSTON_LONGITUDE = -71.059215;

	private String stationName;  // the combo (stationName, trainLine) uniquely ids a platform
	private double latitude, longitude;  // location of station (same for all platforms of a station)
	private int stationId;  // unique id and in graph as vertex id
	private Set<String> trainLines;  // what train lines this station serves
	
	public Station(String stationName, double lat, double lon, int stationId) {
		this.stationName = stationName;
		this.latitude = lat;
		this.longitude = lon;
		this.stationId = stationId;
		this.trainLines = new TreeSet<String>();
	}
	
	public int getStationId() {
		return stationId;
	}
	
	public String getStationName() {
		return stationName;
	}
	
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void addTrainLine(String trainLine) {
		trainLines.add(trainLine);	
	}
	public Set<String> getTrainLines() {
		return trainLines;
	}
	// Local coordinates in miles from Government Center, central Boston
	public double getNorthSouthMiles() {
		return (latitude-BOSTON_LATITUDE)* MILES_PER_LATITUDE_DEGREE;
	}
	public double getEastWestMiles() {
		return (longitude-BOSTON_LONGITUDE)* MILES_PER_LONGITUDE_DEGREE;
	}

	// distance in miles from Government Center
	public double distanceFromBoston() {
		return  Math.sqrt(getNorthSouthMiles()*getNorthSouthMiles() + getEastWestMiles()*getEastWestMiles());
	}
}
