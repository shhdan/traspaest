package trajectory.statistic;

import java.util.ArrayList;
import java.util.HashMap;

import EMD.Feature2D;
import EMD.JFastEMD;
import EMD.Signature;

public class EMDCalculation {
	
	//public static int CONCATENATE = 0;
	
	//public static int[] pathList = new int[CONCATENATE];
	
	public static void getHistogram(ArrayList<Trajectory> trajectorySet, ArrayList<Integer> histogram){
		
		HashMap<Integer, Integer> statNode = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> statTime = new HashMap<Integer, Integer>();
        
        for(int i = 0; i < trajectorySet.size(); i++){
        	ArrayList<Point> trajectory = trajectorySet.get(i).get_pointArray();
        	int startTime = (trajectory.get(0).coordinate[1]);
        	for(int j = 0; j < trajectory.size(); j++){
        		statHashMap(statNode, trajectory.get(j).coordinate[0]);
        		
        		int delta = (trajectory.get(j).coordinate[1]) - startTime;
        		statHashMap(statTime, trajectory.get(j).coordinate[0], delta);
        	}
        }
        
        for(Integer key: statNode.keySet()){
        	histogram.add(key);
        	histogram.add(statNode.get(key));
        	histogram.add(statTime.get(key));
        }
        
	}
	
	public static void getHistogram(int[] pathList, ArrayList<Integer> histogram){
		
		int concatenate = pathList.length - 1;
		HashMap<Integer, Integer> statNode = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> statTime = new HashMap<Integer, Integer>();
        double currentAvg = 0;
        for(int iter = 0; iter < concatenate; iter++){
        	
        	HashMap<Integer, Integer> statNodeSub = new HashMap<Integer, Integer>();
            HashMap<Integer, Integer> statTimeSub = new HashMap<Integer, Integer>();
        	ArrayList<Trajectory> trajectorySet = new ArrayList<Trajectory>();
        	
        	Util.get_ClipSet(trajectorySet, pathList[iter], pathList[iter + 1]);
        
        	for(int i = 0; i < trajectorySet.size(); i++){
        		ArrayList<Point> trajectory = trajectorySet.get(i).get_pointArray();
        		int startTime = (trajectory.get(0).coordinate[1]);
        		for(int j = 0; j < trajectory.size(); j++){
        			statHashMap(statNodeSub, trajectory.get(j).coordinate[0]);
        			int delta = (trajectory.get(j).coordinate[1]) - startTime;
        			statHashMap(statTimeSub, trajectory.get(j).coordinate[0], delta);
        		}
        	}
        	for(Integer key: statNodeSub.keySet()){
        		statHashMap(statNode, key, statNodeSub.get(key));
        		int addTime = (int) (currentAvg * statNodeSub.get(key));
        		statHashMap(statTime, key, statTimeSub.get(key) + addTime);
        	}
        	currentAvg = statTimeSub.get(pathList[iter + 1])/statNodeSub.get(pathList[iter + 1]);
        	
        }
        for(Integer key: statNode.keySet()){
    		histogram.add(key);
    		histogram.add(statNode.get(key));
    		histogram.add(statTime.get(key) );
    	}
        
	}
	

    public static void statHashMap(HashMap<Integer, Integer> hashMap,
            int metadata) {
        if (hashMap.containsKey(metadata)) {
            hashMap.put(metadata, hashMap.get(metadata) + 1);
        } else {
            hashMap.put(metadata, new Integer(1));
        }
    }

    public static void statHashMap(HashMap<Integer, Integer> hashMap,
            int metadata, int value) {
        if (hashMap.containsKey(metadata)) {
            hashMap.put(metadata, hashMap.get(metadata) + value);
        } else {
            hashMap.put(metadata, new Integer(value));
        }
    }
    
    public static double[] EMDCalculate(int origin, int destination, int[] pathList){
    	double[] EMD = new double[2];
    	
    	ArrayList<Trajectory> ODSet = new ArrayList<Trajectory>();
    	
    	Util.get_ClipSet(ODSet, origin, destination);
    	ArrayList<Integer> ODHistogram = new ArrayList<Integer>();
    	getHistogram(ODSet, ODHistogram);
    	int N = ODHistogram.size()/3;
    	int[] roadP = new int[N];
    	double[] weightSignP = new double[N];
    	double[] orderP = new double[N];
    	for(int i = 0 ; i < N; i++){
    		roadP[i] = ODHistogram.get(3*i);
    		weightSignP[i] = (double)ODHistogram.get(3*i + 1);
    		orderP[i] = (double)ODHistogram.get(3*i + 2);
    	}
    	
//    	for(int i = 0; i < ODHistogram.size(); i++){
//    		System.out.print(ODHistogram.get(i) + ",");   		
//    	}
//    	System.out.print("\n");
    	
    	ArrayList<Integer> ConcatenateHistogram = new ArrayList<Integer>();
    	getHistogram(pathList, ConcatenateHistogram);
    	
//    	for(int i = 0; i < ConcatenateHistogram.size(); i++){
//    		System.out.print(ConcatenateHistogram.get(i) + ",");
//    		
//    	}
//    	System.out.print("\n");
    	
    	int M = ConcatenateHistogram.size()/3;
    	int[] roadQ = new int[M];
    	double[] weightSignQ = new double[M];
    	double[] orderQ = new double[M];
    	for(int i = 0 ; i < M; i++){
    		roadQ[i] = ConcatenateHistogram.get(3*i);
    		weightSignQ[i] = (double)ConcatenateHistogram.get(3*i + 1);
    		orderQ[i] = (double)ConcatenateHistogram.get(3*i + 2);
    	}
    	
        boolean tag = true;
        double [] a = {1, 0.995};
        for(int j = 0; j < 2; j++){
            double weight = a[j];
    	
            Signature P = new Signature();
            Signature Q = new Signature();
        
            P.setNumberOfFeatures(N);
            Q.setNumberOfFeatures(M);
        
            Feature2D[] featureP = new Feature2D[N];
            Feature2D[] featureQ = new Feature2D[M];
        
            for(int i = 0; i < N; i++){
            	Feature2D f = new Feature2D(ConcatenateUtil.pointList.get(roadP[i]).latitude, 
            			ConcatenateUtil.pointList.get(roadP[i]).longitude);
            //Feature2D f = new Feature2D(roadP[i],0);
            	f.setOrder(orderP[i]);
            	f.setOrderTag(tag);
            	f.setWeight(weight);
            	featureP[i] = f;
            }
        
            for(int i = 0; i < M; i++){
            	Feature2D f = new Feature2D(ConcatenateUtil.pointList.get(roadQ[i]).latitude,ConcatenateUtil.pointList.get(roadQ[i]).longitude);
            //Feature2D f = new Feature2D(roadQ[i],0);
            	f.setOrder(orderQ[i]);
            	f.setOrderTag(tag);
            	f.setWeight(weight);
            	featureQ[i] = f;
            }
        
            P.setFeatures(featureP);
            Q.setFeatures(featureQ);
        

        
            double Psum = 0;
            double Qsum = 0;
        
            for(int i = 0; i < N; i++)
            	Psum = Psum + weightSignP[i];
        
            for(int i = 0; i < M; i++)
            	Qsum = Qsum + weightSignQ[i];
        
            for(int i = 0; i < N; i++)
            	weightSignP[i] = weightSignP[i]/Psum;
        
            for(int i = 0; i < M; i++)
            	weightSignQ[i] = weightSignQ[i]/Qsum;
        
            P.setWeights(weightSignP);
            Q.setWeights(weightSignQ);
        
            EMD[j] = JFastEMD.distance(P, Q, -1);
        }
    	return EMD;
    }


}
