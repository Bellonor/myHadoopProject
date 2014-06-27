package sequence.machinelearning.naivebayes.bayesdemo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
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
/**
 * 案例:http://www.cnblogs.com/zhangchaoyang/articles/2586402.html
 * @author Jamas
 *  也参考了这篇文章：http://www.cnblogs.com/leoo2sk/archive/2010/09/17/naive-bayesian-classifier.html
 */
public class Train {

    private static LinkedList<String> lisatt = new LinkedList<String>(); // 存储属性的名称:outlook,temperature,humidity,windy
    private static LinkedList<ArrayList<String>> lisvals = new LinkedList<ArrayList<String>>(); //outlook:sunny,overcast,rainy 存储每个属性的取值,属性的特征
    private static LinkedList<String[]> listdata = new LinkedList<String[]>();; // 原始数据
   
    public static final String patternString = "@attribute(.*)[{](.*?)[}]";
    //存储分类，比如，是，否。再比如：检测SNS社区中不真实账号，是真实用户还是僵尸用户
    public static LinkedList<String> sort=new LinkedList<String>();
	
    //计算P(F1|C)P(F2|C)...P(Fn|C)P(C)，并保存为文本文件 
    /**
     * 
     * @throws IOException
     */
    public void CountProbility() throws IOException{
    	
        String src="datafile/naivebayes/train/out/result.arff";
        delfile(src);
        File file=new File(src);
        if(file.exists())
            file.createNewFile();
        FileOutputStream out=new FileOutputStream(file,true);
        Map<String,Integer> map=new HashMap<String,Integer>();
    	//先计算判定结果的概率，保存为文件
    	for(int i=0;i<sort.size();i++){
            //第一个for对取出sort,第二个for对data中的sort进行计数
    		
    		Integer sum=0;
            String sortname=sort.get(i);
            Double probability=0.0;
            
            for(int j=0;j<listdata.size();j++){
    			String[] line=listdata.get(j);
    			if(line[line.length-1].equals(sortname)){
    				sum=sum+1;
    			}
     	    }
    		map.put(sortname, sum);
    		probability=Double.valueOf(sum)/Double.valueOf(listdata.size());
    		//写入文件
    		StringBuffer sb=new StringBuffer();
            sb.append("@decision P("+sortname+") {"+probability.toString()+"}\n");//如果不加"/n"则不能实现换行。
            System.out.print(sb.toString());
            
            out.write(sb.toString().getBytes("utf-8"));
    	}
    	out.write("@data\n".getBytes("utf-8"));
    	System.out.print("@data\n");
    	//先计算判定结果的概率，保存为文件
    	//out.close(); //到最后写完的时候再关闭
    	//分别统计P(F1|C)P(F2|C)...P(Fn|C)的个数,参考:http://www.ruanyifeng.com/blog/2013/12/naive_bayes_classifier.html
    	 //对属性进行循环
        for(int i=0;i<lisatt.size();i++){
        	
        	String attname=lisatt.get(i);
        	List<String> lisval=lisvals.get(i);
        	//对属性的特征进行循环
        	for(int j=0;j<lisval.size();j++){
        		String attval=lisval.get(j);
        		//先取出sort(yes 还是no情况)
        		for(int n=0;n<sort.size();n++){
            		Integer sum=0;
                    String sortname=sort.get(n);
                    Double probability=0.0;
                    
                    //取出数据集进行for
                    for(int k=0;k<listdata.size();k++){
            			String[] line=listdata.get(k);
            			if(line[line.length-1].equals(sortname)&&line[i].equals(attval)){
            				sum=sum+1;
            			}
             	    }
                    
            		probability=Double.valueOf(sum)/Double.valueOf(map.get(sortname));
            		//写入文件
            		StringBuffer sb=new StringBuffer();
                    sb.append("P("+attname+"="+attval+"|"+sortname+"),"+probability+"\n");//如果不加"/n"则不能实现换行。
                    System.out.print(sb.toString());
                    
                    out.write(sb.toString().getBytes("utf-8"));
        		}
        		
        	}
        }
        out.close();
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
                        String[] type = line.split(",");
                        for(int i=0;i<type.length;i++){
                        	sort.add(type[i].trim());
                        }
                }
            	Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                	lisatt.add(matcher.group(1).trim());
                    String[] values = matcher.group(2).split(",");
                    ArrayList<String> al = new ArrayList<String>(values.length);
                    for (String value : values) {
                        al.add(value.trim());
                    }
                    lisvals.add(al);
                } else if (line.startsWith("@data")) {
                    while ((line = br.readLine()) != null) {
                        if(line=="")
                            continue;
                        String[] row = line.split(",");
                        listdata.add(row);
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
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Train train=new Train();
		train.readARFF(new File("datafile/naivebayes/train/in/weather.nominal.arff"));
		train.CountProbility();
		
	}
	public void delfile(String filepath){
		   File file=new File(filepath);   
		       if(file.exists())   
		      {   
		           //file.createNewFile(); 
				   file.delete();   
		       }    

	   }
}
