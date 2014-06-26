package sequence.machinelearning.naivebayes.bayesdemo;
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
/**
 * 案例:http://www.cnblogs.com/zhangchaoyang/articles/2586402.html
 * @author Jamas
 *  也参考了这篇文章：http://www.cnblogs.com/leoo2sk/archive/2010/09/17/naive-bayesian-classifier.html
 */
public class Train {

    private static LinkedList<String> attribute = new LinkedList<String>(); // 存储属性的名称:outlook,temperature,humidity,windy
    private static LinkedList<ArrayList<String>> attributevalue = new LinkedList<ArrayList<String>>(); //outlook:sunny,overcast,rainy 存储每个属性的取值,属性的特征
    private static LinkedList<String[]> data = new LinkedList<String[]>();; // 原始数据
   
    public static final String patternString = "@attribute(.*)[{](.*?)[}]";
    //存储分类，比如，是，否。再比如：检测SNS社区中不真实账号，是真实用户还是僵尸用户
    public static LinkedList<String> sort=new LinkedList<String>();
	
    
    
    
    
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
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
