package sequence.machinelearning.decisiontree.myid3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.LinkedList;

public class MyID3 {

    private static LinkedList<String> attribute = new LinkedList<String>(); // 存储属性的名称
    private static LinkedList<ArrayList<String>> attributevalue = new LinkedList<ArrayList<String>>(); // 存储每个属性的取值
    private static LinkedList<String[]> data = new LinkedList<String[]>();; // 原始数据
   
    public static final String patternString = "@attribute(.*)[{](.*?)[}]";
	public static String[] yesNo;
	public static TreeNode root;
	

    public void createDTree(){
    	root=new TreeNode();
    	
    }
    /**
     * 
     * @param lines 传入要分析的数据集
     * @param index 哪个属性？attribute的index
     */
    public Double fisrtGain(LinkedList<String[]> lines,int index){
    	Double gain=-1.0;
    	List<Double> li=new ArrayList<Double>();
    	//统计Yes No的次数
    	for(int i=0;i<yesNo.length;i++){
    		Double sum=0.0;
    		for(int j=0;j<lines.size();j++){
    			String[] line=lines.get(j);
    			//data为结构化数据,如果数据最后一列==yes,sum+1
    			if(line[line.length-1].equals(yesNo[i])){
    				sum=sum+1;
    			}
    		}
    		li.add(sum);
    	}
    	//计算Entropy(S)计算Entropy(S) 见参考书《机器学习 》Tom.Mitchell著  第3.4.1.2节
    	Double entropyS=TheMath.getEntropy(lines.size(), li);
    	//下面计算gain
    	
    	List<String> la=attributevalue.get(index);
    	List<Point> lasv=new ArrayList<Point>();
    	for(int n=0;n<la.size();n++){
    		String attvalue=la.get(n);
        	//统计Yes No的次数
    		List<Double> lisub=new ArrayList<Double>();//如：sunny 是yes时发生的次数，是no发生的次数
    		Double Sv=0.0;//公式3.4中的Sv 见参考书《机器学习(Tom.Mitchell著)》
        	for(int i=0;i<yesNo.length;i++){
        		Double sum=0.0;
        		for(int j=0;j<lines.size();j++){
        			String[] line=lines.get(j);
        			//data为结构化数据,如果数据最后一列==yes,sum+1
        			if(line[index].equals(attvalue)&&line[line.length-1].equals(yesNo[i])){
        				sum=sum+1;
        			}
        		}
        		Sv=Sv+sum;//计算总数
        		lisub.add(sum);
        	}
        	//计算Entropy(S) 见参考书《机器学习(Tom.Mitchell著)》
        	Double entropySv=TheMath.getEntropy(Sv.intValue(), lisub);
        	//
        	Point p=new Point();
        	p.setSv(Sv);
        	p.setEntropySv(entropySv);
        	lasv.add(p);
    	}
    	//gain=TheMath.getGain(entropyS,lines.size(),lasv);
    	return gain;
    }
    public void insertNode(){
    	
    }
    public void getMaxGain(){
    	
    }
    
    public static void main(String[] args) {
		// TODO Auto-generated method stub
    	MyID3 myid3 = new MyID3();
    	myid3.readARFF(new File("datafile/decisiontree/test/in/weather.nominal.arff"));
    	myid3.fisrtGain(data,0);
	}
    //读取arff文件，给attribute、attributevalue、data赋值
    public void readARFF(File file) {
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            Pattern pattern = Pattern.compile(patternString);
            while ((line = br.readLine()) != null) {
            	if (line.startsWith("@decision")) {
                   line = br.readLine();
                        if(line=="")
                            continue;
                        yesNo = line.split(",");
                }
            	Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    attribute.add(matcher.group(1).trim());
                    String[] values = matcher.group(2).split(",");
                    ArrayList<String> al = new ArrayList<String>(values.length);
                    for (String value : values) {
                        al.add(value.trim());
                    }
                    attributevalue.add(al);
                } else if (line.startsWith("@data")) {
                    while ((line = br.readLine()) != null) {
                        if(line=="")
                            continue;
                        String[] row = line.split(",");
                        data.add(row);
                    }
                } else {
                    continue;
                }
            }
            br.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
	

}
