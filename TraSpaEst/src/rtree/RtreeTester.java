package rtree;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

public class RtreeTester{

    private Rtree rtree;
    private ArrayList<IndexablePoint> list;
    @Before
    public void setUp() {
        
        // create a new list
        list = new ArrayList();
        // read file
        File file = new File("pointlist-ready");
        try {
            Scanner scLine = new Scanner(file);
            while (scLine.hasNextLine()) {
                // read each line
                String line = scLine.nextLine();
                String[] data = line.split(",");
                // get id, x, y
                int id = Integer.parseInt(data[0]);
                int x = Integer.parseInt(data[1]);
                int y = Integer.parseInt(data[2]);
                int[] coordinate = {x,y};
                //print(""+coordinate[0]+ " "+coordinate[1]);
                // create an indexable point
                IndexablePoint point = new IndexablePoint(id, coordinate);
                list.add(point);
            }
            
        } catch (FileNotFoundException e) {
            System.err.println("Failed to open" + file);
            System.exit(1);
        }
        print(list.size()+"");
    }
    
    @Test
    public void testConstructor() {
        rtree = new Rtree();
        rtree = new Rtree(2,4,4);
    }
    @Test
    public void testInsert() {
        rtree = new Rtree();
        rtree.constructRtree(list, list.size(), 2, 12, 12);
    }
    @Test
    public void testRangeCntQuery() {
        rtree = new Rtree();
        rtree.constructRtree(list, list.size(), 2, 12, 12);
        int[] coor = {11702567,4011675};
        IndexablePoint ip = new IndexablePoint(2,coor);
        int count = rtree.rangeCntQuery(ip, 2000);
        
        int testcount = countRangePoint(coor[0], coor[1], 2000);
        print(count + " " + testcount);
        assertEquals(count, testcount);
    }
    private void print(String s) {
        System.out.println(s);
    }
    
    @Test
    public void testFindKNN() {
        rtree = new Rtree();
        rtree.constructRtree(list, list.size(), 2, 12, 12);
        int[] coor = {11702567,4011675};
        IndexablePoint ip = new IndexablePoint(2,coor);
        
        
    }
    
    private int countRangePoint(int x, int y, double radium) {
        int count = 0;
        for (IndexablePoint point : list){
            int[] coordinate = point.coords;
            int dx = x - coordinate[0];
            int dy = y - coordinate[1];
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance < radium)
                count ++;
        }
        return count;
        
    }
}


