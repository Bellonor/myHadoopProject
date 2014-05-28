package mysequence.machineleaning.clustering.canopy;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MyCanopy {

	//x,y之间当且仅当有一个空格，要严格控制，为了与MAHOUT中的输入格式一致，所以这里也采用空格作为分隔。
	static Vector<Point>  li=new Vector<Point>();
	//static List<Point>  li=new ArrayList<Point>();
	static List<Vector<Point>> list=new ArrayList<Vector<Point>>();
	private final static Double t1=8.0;
	private final static Double t2=4.0;
	//简单地采用曼哈顿距离|x1 – x2| + |y1 – y2|
	
	public static final void readF1() throws IOException {      
		String filePath="scripts/clustering/canopy/canopy.dat";
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
	//简单地采用曼哈顿距离|x1 – x2| + |y1 – y2|
	public static  Double DistanceMeasure(Point p1,Point p2){
		return Math.abs(p2.getX()-p1.getX()) +Math.abs(p2.getY()-p1.getY());
	}
	public static void clustering(){
          
		//初始化一个canopy   
		   Point p0=new Point();
		   p0=li.get(0);
		   Vector<Point>  v1=new Vector<Point>();
		   v1.add(p0);
		   list.add(v1);
		   li.remove(0); 
		   System.out.println("中心点为:"+p0.getX()+","+p0.getY());
		while(0<li.size()){
			Point p1=li.get(0);
			//如果属于已有的聚类，记为true,否则false
			boolean inside=false;
			for(int i=0;i<list.size();i++){
				Vector<Point>  v=list.get(i);
				Point p2=v.get(0);
				double dist =DistanceMeasure(p1,p2);
                //如果小于t2，属于当前的聚类,已经够接近了，不需要再聚类了，所以删除
				if(dist<t2){
					inside=true;
					list.get(i).add(p1);
					li.remove(0);
					System.out.println("C"+i+":"+p1.getX()+","+p1.getY()+"dist<t2");
					
				}
				//如果t2<=dist<t1，属于当前的聚类,但还不确定他是否比其它的聚类更加接近，所以不删除，等待下一轮的观察。
                if(t2<=dist&&dist<t1){
                	//参加本类聚类过的点不再参与聚类
                	if(p1.getSign()==i){
						continue;
					}
                	list.get(i).add(p1);
					inside=true;
					li.remove(0);
					li.add(p1);
					
					//是否曾经参与过本次聚类，记上聚类的索引号，下次再来的时候不伺候
					p1.setSign(i);
					
					System.out.println("C"+i+":"+p1.getX()+","+p1.getY()+"t2<=dist<t1");
				}
             }
			//如果不属于现有的任何一个聚类，则新建一个聚类,然后删除该点
			if(!inside){
				   Vector<Point>  vec=new Vector<Point>();
				   vec.add(p1);
				   li.remove(0);
				   list.add(vec);
				   	
			}
			//与各个已经形成的聚类比较距离，比较结束后将其删除，以结束循环
			if(li.get(0).getSign()!=-1){
				li.remove(0);
			}
         }
		String ss="ddd";
	}
	
	

	
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		readF1();
		
		clustering();
		String ss="ddd";
	}

}
