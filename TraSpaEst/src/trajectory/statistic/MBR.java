package trajectory.statistic;

public class MBR {
//boolean HASPOINT; // whether the MBR contains point
    
    double minx = 10000000.00;
    double miny = 10000000.00;
    double maxx = -10000000.00;
    double maxy = -10000000.00;
    
    MBR(){}
    
    // extend the region of MBR from given point
    void unionWith(double x, double y){
        
        /****************** debug ******************/
//        System.out.print("X = " + x + "\n");
//        System.out.print("Y = " + y + "\n");
//        System.out.print("MINX = " + minx + "\n");
//        System.out.print("MAXX = " + maxx + "\n");
//        System.out.print("MINY = " + miny + "\n");
//        System.out.print("MAXY = " + maxy + "\n");
//        System.out.print("*******************" + "\n");
        /****************** debug ******************/

            minx = MIN(minx, x);
            maxx = MAX(maxx, x);
            miny = MIN(miny, y);
            maxy = MAX(maxy, y);
    }
    
    double width(){
        return maxx - minx;
    }
    
    double height(){
        return maxy - miny;
    }

    double MIN(double x, double y){
        if(x < y)
            return x;
        else
            return y;
    }
    
    double MAX(double x, double y){
        if(x > y)
            return x;
        else
            return y;
    }

}
