import java.util.ArrayList;
import java.util.Comparator;

public class Page {

	private String title;
	private ArrayList<Page> outgoing;
	
	// Will only ever be 0 or 1, if it is 1 then the rank sink forumula will apply once less for this Page, since one of the rank sinks was caused by this Page.
	public int accumulator = 0;
	
	private double rank;
	private int id;
	private int inlinks = 0;
	
	public Page(String title, int id){
		this.title = title;
		this.id = id;
		outgoing = new ArrayList<Page>();
	}
	
	public void addOutgoing(Page p){
		outgoing.add(p);
	}
	
	public void setRank(double rank){
		this.rank = rank;
	}
	
	public void incrementInlinks(){
		inlinks++;
	}
	
	public int getInlinks(){
		return inlinks;
	}
	
	public ArrayList<Page> getOutgoing(){
		return outgoing;
	}
	
	public String getTitle(){
		return title;
	}
	
	public int getID(){
		return id;
	}
	
	public double getRank(){
		return rank;
	}
	
	// Used in sorting the Page list, compares such that higher PageRank values are sorted to the beginning of the list.
	public static Comparator<Page> PageRankComparator = new Comparator<Page>() {
		public int compare(Page t1, Page t2) {
			if(t2.getRank() < t1.getRank()){
				return -1;
			}else if(t2.getRank() > t1.getRank()){
				return 1;
			}
			return 0;
		}
	};
		
	// Used in sorting the Page list, compares such that higher inlink counts are sorted to the beginning of the list.
	public static Comparator<Page> InlinkComparator = new Comparator<Page>() {
		public int compare(Page t1, Page t2) {
			if(t2.getInlinks() < t1.getInlinks()){
				return -1;
			}else if(t2.getInlinks() > t1.getInlinks()){
				return 1;
			}
			return 0;
		}
	};

}
