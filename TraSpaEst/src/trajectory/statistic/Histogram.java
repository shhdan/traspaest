package trajectory.statistic;

public class Histogram {
	
	private final long[] occurrence; // occurrence[i] = the number of occurrences of value i
	private int bin; // the number of bin
	
	/* initialization */
	public Histogram(int num){
		bin = num;
		occurrence = new long[num];
		for(int i = 0; i < num; i++)
			occurrence[i] = 0;
	}
	
	/* add one occurrence of the value i */
	public void addDataOccurrence(int i){
		occurrence[i]++;
	}
	
	/* add multiple occurrence of the value i */
	public void addDataOccurrence(int i, long num){
		occurrence[i] = occurrence[i] + num;
	}
	
	/* get the summary of all the occurrence */
	public long sumOccurrence(){
		long sum = 0;
		
		for(int i = 0; i < bin; i++)
			sum = sum + occurrence[i];
		
		return sum;
	}
	
	/* calculate the distance between this histogram to another histogram */
	public double distanceHistogram(Histogram hist){
		double distance = 0.0;
		
		long sumThis = this.sumOccurrence();
		long sumHist = hist.sumOccurrence();
		
		double sumDistance = 0.0;
		
		/* check whether the bin from two histograms are the same */
		if(this.bin != hist.bin){
			System.out.println("Error: [Histogram::distanceHistogram()] "
					+ "The bins from these two histograms does not equal!");
			return distance;
		}
			
		for(int i = 0; i < this.bin; i++){
			sumDistance = Math.abs((double)this.occurrence[i]/sumThis - (double)hist.occurrence[i]/sumHist);
		}
		
		distance = sumDistance/this.bin;
		
		return distance;
	}

}
