/* ************THIS IS A TEMPORARY CLASS FOR CONCATENATION!**************/

package trajectory.statistic;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class PreProcess {
	
	/* the threshold of number of trajectory between two points */
	public static final int NUM_OF_TRAJECTORY = 1;
	
	public static final int FILENUM = 500;
	
	/* read from a matrix which was generated beforehand storing 
	 * the number of trajectories passing from one point to another 
	 * point, in order to reduce the space of the matrix by keeping
	 * only the point IDs having more than a certain number of 
	 * trajectories in between */
	public static void  generateConnectedList(String inputFile, String outputFile) throws IOException{
		
		FileInputStream inputStreamConnectedList = new FileInputStream(inputFile);
		FileWriter file = new FileWriter(outputFile);

		BufferedWriter outputWriter = new BufferedWriter(file);
		
        @SuppressWarnings("resource")
		Scanner scConnectedList = new Scanner(inputStreamConnectedList, "UTF-8");
        String lineConnectedList = "";
        String SplitBy = ",";
        int lineIndex = 0;
        
        while (scConnectedList.hasNextLine()) {
        	
/*        	if(lineIndex == 10)
        		break;*/
        	
        	if(lineIndex % 10000 == 0)
        		System.out.println("lineIndex = " + lineIndex);
        	
        	/* scan a line */
            lineConnectedList = scConnectedList.nextLine();
            
            //System.out.println(lineConnectedList);
            
            /* split a line into items, store into an array */
            String[] lineData = lineConnectedList.split(SplitBy);
            outputWriter.append(lineIndex + "");
            
            //System.out.println(lineData[0]);
            
            /* scan the element of a line */
            for(int columnIndex = 1; columnIndex < lineData.length; columnIndex++){
            	/* if the element is larger than the threshold 
            	 * then keep the point ID in the list, and put this
            	 * pair of point IDs into a hash set */
            	if(Integer.parseInt(lineData[columnIndex]) >= NUM_OF_TRAJECTORY){
            		outputWriter.append(",");
            		outputWriter.append(columnIndex - 1 + "");
            	}
            }
            outputWriter.newLine();
            /* line index */
            lineIndex++;
        }
        outputWriter.flush();
        outputWriter.close();
        System.out.println("the number of line = " + lineIndex);
	}
	
	public static double scanConnectedList(String inputFile) throws FileNotFoundException{
		double averageDegree = 0.0;
	
		FileInputStream inputStreamConnectedList = new FileInputStream(inputFile);
		
        @SuppressWarnings("resource")
		Scanner scConnectedList = new Scanner(inputStreamConnectedList, "UTF-8");
        String lineConnectedList = "";
        String SplitBy = ",";
        long totalDegree = 0;
        int lineIndex = 0;
        long maxDegree = 0;
        
        while (scConnectedList.hasNextLine()) {
        	
/*        	if(lineIndex == 10)
        		break;*/
        	
        	if(lineIndex % 10000 == 0)
        		System.out.println("lineIndex = " + lineIndex);
        	
        	/* scan a line */
            lineConnectedList = scConnectedList.nextLine();
            
            /* split a line into items, store into an array */
            String[] lineData = lineConnectedList.split(SplitBy);
            
            if(lineData.length > maxDegree)
            	maxDegree = lineData.length - 1;
            
            totalDegree = totalDegree + lineData.length - 1;
            
            lineIndex++;
        }
        System.out.println("totalDegree = " + totalDegree);
        System.out.println("maxDegree = " + maxDegree);
        averageDegree = totalDegree/lineIndex;
        
        return averageDegree;
		
	}
	
	public static void scanResultList(String inputFile, String outputFile) 
			throws IOException{
		
		
		FileWriter file = new FileWriter(outputFile);

		BufferedWriter outputWriter = new BufferedWriter(file);
		
		for(int index = 0; index < FILENUM; index++){
			FileInputStream inputStreamResultList = 
					new FileInputStream(inputFile + index);
			
	        @SuppressWarnings("resource")
			Scanner scResultList = new Scanner(inputStreamResultList, "UTF-8");
	        String lineResultList = "";
	        String SplitBy = " ";

	        int lineIndex = 0;

        
	        while (scResultList.hasNextLine()) {
	        	
	/*        	if(lineIndex == 10)
	        		break;*/
	        	
	        	if(lineIndex % 10000 == 0)
	        		System.out.println("lineIndex = " + lineIndex);
	        	
	        	/* scan a line */
	        	lineResultList = scResultList.nextLine();
	            
	            /* split a line into items, store into an array */
	            String[] lineData = lineResultList.split(SplitBy);
	            System.out.println(lineData[0]);
	            
	            if(lineIndex == 0){
	            	outputWriter.append(lineData[4]);
	            }
	            else{
	            	outputWriter.append(lineData[7]);
	            }
	            outputWriter.append("\t");
	            lineIndex++;
	            
	        }
	        outputWriter.newLine();
	        
		}
		outputWriter.flush();
		outputWriter.close();
		
	}
	
	public static void main(String[] args) throws IOException {
		//generateConnectedList("/media/bigdata/uqdhe/odpair-stas", "/media/dragon_data/uqdhe/"
        //		+ "BeijingFiveDays/mydata/connectedlist");
		
		//double averageDegree = scanConnectedList("/media/dragon_data/uqdhe/"
        //		+ "BeijingFiveDays/mydata/connectedlist");
		//System.out.println("the average degree = " + averageDegree + "\n");
		
		scanResultList("/media/dragon_data/uqdhe/BeijingFiveDays/mydata/output-node/", 
				"/media/dragon_data/uqdhe/BeijingFiveDays/mydata/output-stat-node");
		
		System.out.println("This is the end of the program!");
		
	}
	

}