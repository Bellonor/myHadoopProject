package mysequence.machineleaning.clustering.kmeans;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import mysequence.machineleaning.clustering.canopy.Point;

public class MyKmeans {

	static Vector<Point>  li=new Vector<Point>();
	//static List<Point>  li=new ArrayList<Point>();
	static List<Vector<Point>> list=new ArrayList<Vector<Point>>();
	private final static Integer K=2;
	private final static Double converge=0.01;
	public static final void readF1() throws IOException {      
		String filePath="datafile/cluster/simple_k-means.txt";
		BufferedReader br = new BufferedReader(new InputStreamReader(
        new FileInputStream(filePath)));
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            if(line.length()==0||"".equals(line))continue;
        	String[] str=line.split(" ");               
            Point p0=new Point();
    		p0.setX(Double.valueOf(str[0]));
    		p0.setY(Double.valueOf(str[1]));
    		li.add(p0);
            //System.out.println(line);               
        }
        br.close();
    }
	  //math.sqrt(double n)
    //扩展下，如果要给m开n次方就用java.lang.StrictMath.pow(m,1.0/n);
	//采用欧氏距离
	public static  Double DistanceMeasure(Point p1,Point p2){
		
		Double tmp=StrictMath.pow(p2.getX()-p1.getX(), 2)+StrictMath.pow(p2.getY()-p1.getY(), 2);
		return Math.sqrt(tmp);
	}
	//计算新的簇心
	public static void CalCentroid(List<Vector<Point>> lit){
		for(int i=0;i<lit.size();i++){
			Vector<Point> subli=lit.get(i);
			Point po=new Point();
			Double sumX=0.0;
			Double sumY=0.0;
			Double Clusterlen=Double.valueOf(subli.size());
			for(int j=0;j<Clusterlen;j++){
				Point nextp=subli.get(j);
				sumX=sumX+nextp.getX();
				sumY=sumY+nextp.getY();
			}
			po.setX(sumX/Clusterlen);
			po.setY(sumY/Clusterlen);
			lit.get(i).clear();
			lit.get(i).add(po);
		}
		String test="ll";
		
	}
	public static void Kluster(){
		
		for(int k=0;k<K;k++){
			Vector<Point> vect=new Vector<Point>();
			Point p=new Point();
			p=li.get(k);
			vect.add(p);
			list.add(vect);
		}
		//默认每一个list里的Vector第0个元素是质心
		for(int i=K;i<li.size();i++){
			Point p=new Point();
			 p=li.get(i);
			int index = -1;
			
            double neardist = Double.MAX_VALUE;
			for(int k=0;k<K;k++){
				Point centre=list.get(k).get(0);
				double currentdist=DistanceMeasure(p,centre);
				if(currentdist<neardist){
					neardist=currentdist;
					index=k;
				}
			}
			System.out.println("第一次迭代");
			System.out.println("C"+index+":的点为："+p.getX()+","+p.getY());
			list.get(index).add(p);
			
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		readF1();
		Kluster();
		CalCentroid(list);
	}

}
