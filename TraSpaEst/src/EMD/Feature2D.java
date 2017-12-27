/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author uqdhe
 */
package EMD;
public class Feature2D implements Feature {

    private double x;
    private double y;
    
    //added by Doris
    private double avgOrder;
    private boolean orderTag = false;
    // this is the weight of spatial distance:[0,1]. 1-weight is the weight for order
    private double weight;
    
    public Feature2D(double x, double y, double order){
        this.x = x;
        this.y = y;
        this.avgOrder = order;
    }
    
    public void setOrder(double order){
        this.avgOrder = order;
    }
    
    public void setOrderTag(boolean tag){
        this.orderTag = tag;
    }
    
    public void setWeight(double w){
        this.weight = w;
    }

    public Feature2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public final static double MAX_SPATIAL_DISTANCE = 10;
    public final static double MAX_TEMPORAL_DISTANCE = 600;
    public double groundDist(Feature f) {
        Feature2D f2d = (Feature2D)f;
//        double deltaX = x - f2d.x;
//        double deltaY = y - f2d.y;
//        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        //the real distance on the earth with longitude and latitude
        if(orderTag == true){
            double spatialDist = calculateDistanceInKilometer(x,y,f2d.x,f2d.y);
            if(spatialDist > MAX_SPATIAL_DISTANCE)
            	spatialDist = 1;
            else
            	spatialDist = spatialDist / MAX_SPATIAL_DISTANCE;
            //double spatialDist = FastEMD.distancematrix[(int)x][(int)f2d.x];
            double order = Math.abs(avgOrder - f2d.avgOrder);
            if(order > MAX_TEMPORAL_DISTANCE)
            	order = 1;
            else
            	order = order / MAX_TEMPORAL_DISTANCE;
            return weight * spatialDist + (1 - weight) * order;
        }
        
        return calculateDistanceInKilometer(x, y, f2d.x, f2d.y);
        //return FastEMD.distancematrix[(int)x][(int)f2d.x];
        
    }
    
    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    public static double calculateDistanceInKilometer(double userLat, double userLng,
    double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
          + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
          * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return  (AVERAGE_RADIUS_OF_EARTH_KM * c);
    }
}
