import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;

public class Main {
	
	// HashSet of Pages to make sure we never have duplicates.
	public static HashSet<Page> pages = new HashSet<Page>();
	
	// Mapping of URL to Page that represents it.
	public static HashMap<String, Page> pageURLs = new HashMap<String, Page>();
	
	public static double tau = 0.01;
	public static double lambda = 0.15;
	
	public static void main(String[] args) {
		
		try {
	        lambda = Double.parseDouble(args[0]);
	        tau = Double.parseDouble(args[1]);
	        
			GZIPInputStream decompressedStream = new GZIPInputStream(new FileInputStream("./links.srt.gz"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(decompressedStream));
			String line = reader.readLine();
			
			// Count is the ID given to every unique page encountered.
			int count = 0;
			
			while(line != null){
				// Line format is always "source \t destination"
				String[] links = line.split("\t");
				Page source = pageURLs.get(links[0]);
				Page destination = pageURLs.get(links[1]);
				
				// Create Pages if they don't exist.
				if(source == null)
					source = new Page(links[0], count++);
				if(destination == null)
					destination = new Page(links[1], count++);
				
				source.addOutgoing(destination);
				
				// Recognize that there's another inlink going to the target.
				destination.incrementInlinks();
				
				pages.add(source);
				pages.add(destination);
				
				pageURLs.put(links[0], source);
				pageURLs.put(links[1], destination);
				
				line = reader.readLine();
			}
			reader.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		PageRank();
	}
	
	// Perform the PageRank algorithm.
	public static void PageRank(){
		// Old PageRank for which Page IDs (0 ... pages.size()) are the indices.
		double[] I = new double[pages.size()];
		
		// New PageRank.
		double[] R = new double[pages.size()];
		
		// pages represents P, so |P| = pages.size.
		for(int i = 0; i < I.length; i++){
			I[i] = (double) 1 / (double) pages.size();
		}
		
		while(true){
			// The number of Pages encountered with no outgoing links.
			int accumulation = 0;
			
			for(int i = 0; i < R.length; i++){
				R[i] = lambda / (double) pages.size();
			}
			
			for(Page p : pages){
				ArrayList<Page> Q = p.getOutgoing();
				if(Q.size() > 0){
					for(Page q : Q){
						// Increase PageRank of Pages that have incoming links.
						R[q.getID()] += ((double) 1 - lambda) * I[p.getID()] / (double) Q.size();
					}
				}else{
					// Tallies up rank sinks.
					p.accumulator++;
					accumulation++;
				}

				// Copy R into I.
				System.arraycopy(R, 0, I, 0, I.length);
			}
			
			// Implements rank sink by increasing rank by [# of times a different Page experienced rank sink] * [rank sink formula].
			for(Page p : pages){
				R[p.getID()] += (accumulation - p.accumulator) * ((double) 1 - lambda) * I[p.getID()] / (double) pages.size();
				p.accumulator = 0;
			}
			
			if(isConverged(I, R))
				break;
		}
		
		// R[p.getID] will always represent p's PageRank, so officially set them.
		for(Page p : pages)
			p.setRank(R[p.getID()]);
		
		printOutput();
	}
	
	// Check if the ranks are converging.
	public static boolean isConverged(double[] I, double[] R){
		double totalDifference = 0;		

		// Checks the difference between old and new individual PageRanks, accumulating the differences.
		for(int i = 0; i < I.length; i++){
			totalDifference += Math.abs(I[i] - R[i]);
		}

		if(totalDifference < tau)
			return true;
		
		return false;
	}
	
	// Sorts arrays and prints to file.
	public static void printOutput(){
		Page[] arr = (Page[]) pages.toArray(new Page[pages.size()]);
		PrintWriter writer;
		
		// Sort array of Pages by number of inlinks.
		Arrays.sort(arr, Page.InlinkComparator);
		try {
			writer = new PrintWriter("inlinks.txt", "UTF-8");
			for(int i = 0; i < 50; i++){
				writer.println((i + 1) + ". " + arr[i].getTitle() + ", inlinks: " + arr[i].getInlinks());
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		// Sort array of Pages by PageRank.
		Arrays.sort(arr, Page.PageRankComparator);
		try {
			writer = new PrintWriter("pagerank.txt", "UTF-8");
			for(int i = 0; i < 50; i++){
				writer.println((i + 1) + ". " + arr[i].getTitle() + ", PageRank: " + arr[i].getRank());
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
