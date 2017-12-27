package trajectory.statistic;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ConcantenationCoverage {
	
	//public static final int ROW = 2000;
	public static String FILE_PATH1 = "/media/dragon_data/uqdhe/BeijingFiveDays/mydata/";
	public static String FILE_PATH2 = "/media/bigdata/uqdhe/";
	public static final int FILE = 4;
    public static void main(String[] args) throws IOException {
        // file directory
    	
    	FileWriter fileLog = new FileWriter(FILE_PATH2 + FILE + "/concatenationcoverage-log-" + FILE);

		BufferedWriter outputWriterLog = new BufferedWriter(fileLog);

       String fileDir = FILE_PATH2 + FILE + "/connectedlist-" + FILE;
       ArrayList<ArrayList<Integer>> connectedList = new ArrayList<>();
       FileInputStream inputStream = null;
       inputStream = new FileInputStream(fileDir);
       Scanner sc = new Scanner(inputStream, "UTF-8");
       
       int N = 0;
       int lineNum = 0;
       while(sc.hasNextLine()){
           String line = sc.nextLine();
           String[] data = line.split(",");
           if(data.length > 1)
        	   N++;
           ArrayList<Integer> list = new ArrayList<>();
           for(int i = 1; i < data.length; i = i + 2){
               list.add(Integer.parseInt(data[i]));
           }
           connectedList.add(list);
           if(lineNum % 10000 == 0){
        	   System.out.println("finish line " + lineNum + "  N = " + N);
           }
           lineNum++;
       }
       long counter = 0;
       //int N = connectedList.size();
       System.out.println("the length of the connectedlist = " + N);
       outputWriterLog.append("the length of the connectedlist = " + N);
       N = connectedList.size();
       int checkLineUse = 0;
       int checkLineUseless = 0;
       for(int i = 0; i < 1000; i++){
            System.out.println("the i = " + i + " and the counter = " + counter + " and the checkLine = " + checkLineUse);
            outputWriterLog.append("the i = " + i + " and the counter = " + counter + " and the checkLine = " + checkLineUse);
           
    	   if(connectedList.get(i).size() < 1)
    		   continue;
    	   
    	   checkLineUse++;
    	   
           for(int j = 0; j < 1000; j++){
               ArrayList<Integer> list = connectedList.get(i);

               if(list.contains(j)){
                   counter++;
                   continue;
               }
               
               else{
                   for(int k = 0; k < list.size(); k++){
                       ArrayList<Integer> list1 = connectedList.get(list.get(k));
                       
                       if(list1.contains(j)){
                           counter++;
                           break;
                       }
                       
                       else{
                           for(int l = 0; l < list1.size(); l++){
                               ArrayList<Integer> list2 = connectedList.get(list1.get(l));
                               
                               if(list2.contains(j)){
                                   counter++;
                                   k = list.size();
                                   break;
                               }
                               
                               else{
                                   for(int m = 0; m < list2.size(); m++){
                                       ArrayList<Integer> list3 = connectedList.get(list2.get(m));
                                       
                                       if(list3.contains(j)){
                                           counter++;
                                           l = list1.size();
                                           k = list.size();
                                           break;
                                       }
                                   }
                               }
                           }
                       }
                   }
               }
           }
           
       }
       
       System.out.println("the final result counter = " + counter);
       outputWriterLog.append("the final result counter = " + counter);
    }

}
