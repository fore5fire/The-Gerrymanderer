
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

public class Main {

	private static final int DISTRICT_COUNT = 10;
	private static final int MAX_DISTRICT_SIZE = 650000;
	private static final int MIN_DISTRICT_SIZE = 550000;
	
	private static int democratVoteTarget = 0;
	
	private static County[] counties;
	private static int[] districtSizes;
	private static LinkedHashSet<County> unusedCounties;
	private static LinkedHashSet<County>[] districts;
	private static PrintWriter writer;
	private static int max = -1;
	private static int[] districtMargins = new int[DISTRICT_COUNT];
	private static double startTime;
	
	private static long numberOfCountiesSelected = 0;
	public static void main(String[] args) {

		if (args.length < 1) {
			System.err.println("Please enter an input file path");
			return;
		}
		
		
		FileInputStream inStream;
		BufferedReader reader;
		try {
			inStream = new FileInputStream(args[0]);
			reader = new BufferedReader(new InputStreamReader(inStream));
			writer = new PrintWriter(args[0] + ".output");
		}
		catch (Exception e) {
			System.err.println("File could not be opened");
			e.printStackTrace();
			return;
		}
		
		int numCounties = 0;
		
		try {
		
			String line = reader.readLine();
			numCounties = Integer.parseInt(line);
			
			 counties = new County[numCounties];
			
			for (int i = 0; i < numCounties; i++) {
				counties[i] = new County();
				counties[i].name = reader.readLine();
				counties[i].republicanCount = Integer.parseInt(reader.readLine());
				counties[i].democratCount = Integer.parseInt(reader.readLine());
				counties[i].totalPopulation = counties[i].democratCount + counties[i].republicanCount;
				line = reader.readLine();
				 while (!line.equals("`")) {
					counties[i].adjacentCountiesIndexes.add(Integer.parseInt(line) - 1);
					line = reader.readLine();
				}
			}
			reader.close();
			
			for (int i = 0; i < counties.length; i++) {
				counties[i].adjacentCounties = new County[counties[i].adjacentCountiesIndexes.size()];
				for (int j = 0; j < counties[i].adjacentCountiesIndexes.size(); j++) {
					int k = counties[i].adjacentCountiesIndexes.get(j);
					counties[i].adjacentCounties[j] = counties[k];
				}
				
				counties[i].margin = counties[i].democratCount - counties[i].republicanCount;
			}
		}
		catch (IOException e) {
			System.err.println("An error occurred");
			e.printStackTrace();
			return;
		}
		districts = new LinkedHashSet[DISTRICT_COUNT];
		
		for (int i = 0; i < districts.length; i++) {
			districts[i] = new LinkedHashSet<County>(numCounties, 1);
		}
		
		unusedCounties = new LinkedHashSet<County>(numCounties, 1);
		
		for (County c : counties) {
			unusedCounties.add(c);
		}
		
		districtSizes = new int[DISTRICT_COUNT];
		
		// Manually choose districts
		
		
		unusedCounties.remove(counties[11]);
		unusedCounties.remove(counties[10]);
		unusedCounties.remove(counties[12]);
		unusedCounties.remove(counties[21]);
		unusedCounties.remove(counties[22]);
		districtSizes[0] = 600000;
		districtMargins[0] = 11600;
		districts[0].add(counties[11]);
		districts[0].add(counties[10]);
		districts[0].add(counties[12]);
		districts[0].add(counties[21]);
		districts[0].add(counties[22]);
		
		unusedCounties.remove(counties[9]);
		unusedCounties.remove(counties[2]);
		unusedCounties.remove(counties[0]);
		unusedCounties.remove(counties[1]);
		unusedCounties.remove(counties[8]);
		unusedCounties.remove(counties[15]);
		districtSizes[1] = 605000;
		districtMargins[1] = 20600;
		districts[1].add(counties[9]);
		districts[1].add(counties[2]);
		districts[1].add(counties[0]);
		districts[1].add(counties[1]);
		districts[1].add(counties[8]);
		districts[1].add(counties[15]);
		
		unusedCounties.remove(counties[3]);
		unusedCounties.remove(counties[4]);
		unusedCounties.remove(counties[5]);
		unusedCounties.remove(counties[7]);
		unusedCounties.remove(counties[17]);
		unusedCounties.remove(counties[18]);
		unusedCounties.remove(counties[27]);
		districtSizes[2] = 595000;
		districtMargins[2] = 18600;
		districts[2].add(counties[3]);
		districts[2].add(counties[4]);
		districts[2].add(counties[5]);
		districts[2].add(counties[7]);
		districts[2].add(counties[17]);
		districts[2].add(counties[18]);
		districts[2].add(counties[27]);

		unusedCounties.remove(counties[6]);
		districts[3].add(counties[6]);
		
		Timer timer = new Timer(1, -1, new Main(), "printProgress");
		
		timer.start();
		startTime = Timer.getTime();
		System.out.println(gerrymander(counties[6], 3));
		timer.stop();
		System.out.println(arrayString(districts));
		writer.close();
	}
	private static int gerrymander(County current, int currentDistrict) {
		
		numberOfCountiesSelected++;
		
		if (unusedCounties.size() == 0) {
			//System.out.println("Found possibility");
			/*
			for (int i = 0; i < districts.length; i++) {
				if (districtSizes[i] > MAX_DISTRICT_SIZE || districtSizes[i] < MIN_DISTRICT_SIZE) {
					//System.out.println("District " + i + " out of range with size " + districtSizes[i]);
					return -1;
				}
			}
			*/
			//System.out.println("\tAll districts correct size");
			/*
			for (int i = 0; i < districts.length; i++) {
				for (County c : districts[i]) {
					boolean isAdjacent = false;
					for (int adjacentIndex : c.adjacentCounties) {
						isAdjacent = isAdjacent || districts[i].contains(counties[adjacentIndex]);
					}
					if (!isAdjacent) {
						System.out.println("Non adjacent counties in district " + arrayString(districts));
						return -1;
					}
				}
			}
			*/
			//System.out.println(arrayString(districts));

			if (districtSizes[DISTRICT_COUNT - 1] < MIN_DISTRICT_SIZE) {
				return -1;
			}
			
			int democratWins = 0;
			for (int i = 0; i < districts.length; i++) {
				int totalRepublicans = 0;
				int totalDemocrats = 0;
				for (County c : districts[i]) {
					totalDemocrats += c.democratCount;
					totalRepublicans += c.republicanCount;
				}
				if (totalRepublicans < totalDemocrats) {
					democratWins++;
				}
			}
			if (democratWins > democratVoteTarget) {
				democratVoteTarget = democratWins;
				System.out.println(democratWins + "\n" + arrayString(districts));
				writeln(democratWins + "\n" + arrayString(districts));
			}
			return democratWins;
			//return republicanWins;
		}
		//System.out.println(arrayString(districts));

		if (districtSizes[currentDistrict] > MIN_DISTRICT_SIZE && currentDistrict < DISTRICT_COUNT - 1 && districtMargins[currentDistrict] > 0) {
			County firstUnusedCounty = null;
			for (County c : unusedCounties) {
				firstUnusedCounty = c;
				break;
			}
			unusedCounties.remove(firstUnusedCounty);
			districts[currentDistrict + 1].add(firstUnusedCounty);
			districtMargins[currentDistrict + 1] += firstUnusedCounty.democratCount - firstUnusedCounty.republicanCount;
			districtSizes[currentDistrict + 1] += firstUnusedCounty.totalPopulation;
			int result = gerrymander(firstUnusedCounty, currentDistrict + 1);
			if (result >= democratVoteTarget) {
				return result;
			}
			else {
				unusedCounties.add(firstUnusedCounty);
				districts[currentDistrict + 1].remove(firstUnusedCounty);
				districtMargins[currentDistrict + 1] -= firstUnusedCounty.democratCount - firstUnusedCounty.republicanCount;
				districtSizes[currentDistrict + 1] -= firstUnusedCounty.totalPopulation;
			}
		}
		//System.out.println(arrayString(districts));
		County.targetSortMargin = -districtMargins[currentDistrict];
		Arrays.sort(current.adjacentCounties);
		
		for (County c : current.adjacentCounties) {

			if (unusedCounties.contains(c) && districtSizes[currentDistrict] + c.totalPopulation <= MAX_DISTRICT_SIZE) {
			
				unusedCounties.remove(c);
				districts[currentDistrict].add(c);
				districtMargins[currentDistrict] += c.democratCount - c.republicanCount;
				districtSizes[currentDistrict] += c.totalPopulation;
				int result = gerrymander(c, currentDistrict);
				if (result >= democratVoteTarget) {
					return result;
				}
				else {
					unusedCounties.add(c);
					districts[currentDistrict].remove(c);
					System.gc();
					districtMargins[currentDistrict] -= c.democratCount - c.republicanCount;
					districtSizes[currentDistrict] -= c.totalPopulation;
				}
			}
		}
		
		return -1;
			
	}
	
	private static String arrayString(Object[] s) {
		
		if (s.length == 0) {
			return "[]";
		}
		String ret = "[";
		try {
			for (int i = 0; i < s.length - 1; i++) {
				Object o = s[i];
				ret += o + ", ";
			}
			ret += s[s.length - 1];
		}
		catch (ConcurrentModificationException e) {
			return "";
		}
		return ret + "]";
	}
	
	private static void writeln(String s) {
		writer.println(s);
		writer.flush();
	}
	public void printProgress() {
		System.out.printf("County selections attempted: " + numberOfCountiesSelected + "\tTime elapsed: %.3f" + "\t" + arrayString(districts) + '\n', Timer.getTime() - startTime);
	}
}


class County implements Comparable {
	public String name;
	public int totalPopulation;
	public int democratCount;
	public int republicanCount;
	public int margin;
	public County[] adjacentCounties;
	public ArrayList<Integer> adjacentCountiesIndexes = new ArrayList<Integer>();
	public static int targetSortMargin;
	public String toString() {
		return name;
	}

	public int compareTo(Object arg0) {
		County c = (County) arg0;
		int a = targetSortMargin - margin;
		int b = targetSortMargin - ((County)arg0).margin;
		return a + b;
	}
}
