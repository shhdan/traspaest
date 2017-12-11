/* ************THIS IS A TEMPORARY CLASS FOR CONCATENATION!**************/

package trajectory.statistic;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class PreProcess {
	
	/* the threshold of number of trajectory between two points */
	public static final int NUM_OF_TRAJECTORY = 128;
	
	public static final int FILENUM = 500;
	
	public static final int POINTNUM = 387588;
	public static int FILE_NUM = 4;
	
	public static String FILE_PATH1 = "/media/dragon_data/uqdhe/BeijingFiveDays/mydata/";
	public static String FILE_PATH2 = "/media/bigdata/uqdhe/";
	
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
            		outputWriter.append(columnIndex - 1 + ",");
            		outputWriter.append(lineData[columnIndex]+ "");
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

	public static void  generateInvertedConnectedList(String inputFile, String outputFile) throws IOException{
		
		FileInputStream inputStreamConnectedList = new FileInputStream(inputFile);
		FileWriter file = new FileWriter(outputFile);
		
		ArrayList<ArrayList<Integer>> invertedConnectedList = new ArrayList<ArrayList<Integer>>();
		
		for(int i = 0; i < POINTNUM; i++){
			ArrayList<Integer> list = new ArrayList<Integer>();
			invertedConnectedList.add(list);
		}

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
            
            //System.out.println(lineData[0]);
            
            /* scan the element of a line */
            for(int columnIndex = 1; columnIndex < lineData.length; columnIndex++){
            	/* if the element is larger than the threshold 
            	 * then keep the point ID in the list, and put this
            	 * pair of point IDs into a hash set */
            	if(Integer.parseInt(lineData[columnIndex]) >= NUM_OF_TRAJECTORY){
            		invertedConnectedList.get(columnIndex - 1).add(lineIndex);
            		invertedConnectedList.get(columnIndex - 1).add(Integer.parseInt(lineData[columnIndex]));
            	}
            }
            /* line index */
            lineIndex++;
        }
        System.out.println("the number of line = " + lineIndex);
        
        for(int i = 0; i < invertedConnectedList.size(); i++){
        	outputWriter.append(i + "");
        	for(int j = 0; j < invertedConnectedList.get(i).size(); j ++){
        		outputWriter.append(",");
        		outputWriter.append(invertedConnectedList.get(i).get(j) + "");
        	}
        	outputWriter.newLine();
        }
        outputWriter.flush();
        outputWriter.close();
       
	}	
	
public static void  generateBothConnectedList(String inputFile, String outputFile1, String outputFile2) throws IOException{
		
		FileInputStream inputStreamConnectedList = new FileInputStream(inputFile);
		FileWriter file1 = new FileWriter(outputFile1);
		FileWriter file2 = new FileWriter(outputFile2);
		
		
		ArrayList<ArrayList<Integer>> invertedConnectedList = new ArrayList<ArrayList<Integer>>();
		
		for(int i = 0; i < POINTNUM; i++){
			ArrayList<Integer> list = new ArrayList<Integer>();
			invertedConnectedList.add(list);
		}

		BufferedWriter outputWriter1 = new BufferedWriter(file1);
		BufferedWriter outputWriter2 = new BufferedWriter(file2);
		
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
            
            outputWriter1.append(lineIndex + "");
            //System.out.println(lineConnectedList);
            
            /* split a line into items, store into an array */
            String[] lineData = lineConnectedList.split(SplitBy);
            
            //System.out.println(lineData[0]);
            
            /* scan the element of a line */
            for(int columnIndex = 1; columnIndex < lineData.length; columnIndex++){
            	/* if the element is larger than the threshold 
            	 * then keep the point ID in the list, and put this
            	 * pair of point IDs into a hash set */
            	if(Integer.parseInt(lineData[columnIndex]) >= NUM_OF_TRAJECTORY){
            		invertedConnectedList.get(columnIndex - 1).add(lineIndex);
            		invertedConnectedList.get(columnIndex - 1).add(Integer.parseInt(lineData[columnIndex]));
            		outputWriter1.append(",");
            		outputWriter1.append(columnIndex - 1 + ",");
            		outputWriter1.append(lineData[columnIndex]+ "");
            	}
            }
            outputWriter1.newLine();
            /* line index */
            lineIndex++;
        }
        
        outputWriter1.flush();
        outputWriter1.close();
        
        System.out.println("the number of line = " + lineIndex);
        
        for(int i = 0; i < invertedConnectedList.size(); i++){
        	outputWriter2.append(i + "");
        	for(int j = 0; j < invertedConnectedList.get(i).size(); j ++){
        		outputWriter2.append(",");
        		outputWriter2.append(invertedConnectedList.get(i).get(j) + "");
        	}
        	outputWriter2.newLine();
        }
        outputWriter2.flush();
        outputWriter2.close();
       
	}
	
	public static void  builConnectedListByInvertedConnectedList(String inputFile, String outputFile) throws IOException{
	
		FileInputStream inputStreamInvertedConnectedList = new FileInputStream(inputFile);
		FileWriter file = new FileWriter(outputFile);
	
		ArrayList<ArrayList<Integer>> ConnectedList = new ArrayList<ArrayList<Integer>>();
	
		for(int i = 0; i < POINTNUM; i++){
			ArrayList<Integer> list = new ArrayList<Integer>();
			ConnectedList.add(list);
		}

		BufferedWriter outputWriter = new BufferedWriter(file);
	
		@SuppressWarnings("resource")
		Scanner scInvertedConnectedList = new Scanner(inputStreamInvertedConnectedList, "UTF-8");
		String lineInvertedConnectedList = "";
		String SplitBy = ",";
		int lineIndex = 0;
    
		while (scInvertedConnectedList.hasNextLine()) {
    	
/*        	if(lineIndex == 10)
    		break;*/
   	
			if(lineIndex % 10000 == 0)
				System.out.println("lineIndex = " + lineIndex);
			
			/* scan a line */
            lineInvertedConnectedList = scInvertedConnectedList.nextLine();
            /* split a line into items, store into an array */
            String[] lineData = lineInvertedConnectedList.split(SplitBy);
            
            //System.out.println(lineData[0]);
            
            /* scan the element of a line */
            for(int columnIndex = 1; columnIndex < lineData.length; columnIndex = columnIndex + 2){
            	/* if the element is larger than the threshold 
            	 * then keep the point ID in the list, and put this
            	 * pair of point IDs into a hash set */
            	int id = Integer.parseInt(lineData[columnIndex]);
            	ConnectedList.get(id).add(lineIndex);
            	ConnectedList.get(id).add(Integer.parseInt(lineData[columnIndex + 1]));
            	
            }

            /* line index */
            lineIndex++;
        }
        
        System.out.println("the number of line = " + lineIndex);
        
        for(int i = 0; i < ConnectedList.size(); i++){
        	outputWriter.append(i + "");
        	for(int j = 0; j < ConnectedList.get(i).size(); j ++){
        		outputWriter.append(",");
        		outputWriter.append(ConnectedList.get(i).get(j) + "");
        	}
        	outputWriter.newLine();
        }
        outputWriter.flush();
        outputWriter.close();
		
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
        int numNode = 0;
        
        while (scConnectedList.hasNextLine()) {
        	
/*        	if(lineIndex == 10)
        		break;*/
        	
        	if(lineIndex % 10000 == 0)
        		System.out.println("lineIndex = " + lineIndex);
        	
        	/* scan a line */
            lineConnectedList = scConnectedList.nextLine();
            
            /* split a line into items, store into an array */
            String[] lineData = lineConnectedList.split(SplitBy);
            int degree = (lineData.length - 1)/2;
            
            if(degree > maxDegree)
            	maxDegree = degree;
            
            if(degree > 0)
            	numNode++;
            totalDegree = totalDegree + degree;
            
            lineIndex++;
        }
        System.out.println("totalDegree = " + totalDegree);
        System.out.println("maxDegree = " + maxDegree);
        System.out.println("numNode = " + numNode);
        
        averageDegree = totalDegree/numNode;
        System.out.println("AverageDegree = " + averageDegree);
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
		//generateConnectedList("/home/uqdhe/workspace/TraSpaEst/odpairmatrix", "/home/uqdhe/"
        //		+ "workspace/TraSpaEst/connectedlist");
        		
//        generateBothConnectedList("/media/bigdata/uqdhe/odpair-stas", 
//        		"/media/bigdata/uqdhe/"+NUM_OF_TRAJECTORY+ "/connectedlist-"+NUM_OF_TRAJECTORY , 
//        		"/media/bigdata/uqdhe/"+NUM_OF_TRAJECTORY+"/invertedconnectedlist-"+NUM_OF_TRAJECTORY);
		//generateConnectedList("/media/bigdata/uqdhe/odpair-stas", "/media/bigdata/uqdhe/connectedlist");
		
		double averageDegree = scanConnectedList(FILE_PATH2 + FILE_NUM 
        		+ "/connectedlist-new-" + FILE_NUM);
		System.out.println("the average degree = " + averageDegree + "\n");
		
		//scanResultList("/media/dragon_data/uqdhe/BeijingFiveDays/mydata/output-node/", 
		//		"/media/dragon_data/uqdhe/BeijingFiveDays/mydata/output-stat-node");
		
		//builConnectedListByInvertedConnectedList("/media/bigdata/uqdhe/"+NUM_OF_TRAJECTORY+"/invertedconnectedlist-new-"+NUM_OF_TRAJECTORY,
		//		"/media/bigdata/uqdhe/"+NUM_OF_TRAJECTORY+ "/connectedlist-new-"+NUM_OF_TRAJECTORY);
		
		
		System.out.println("This is the end of the program!");
		
	}
	

}