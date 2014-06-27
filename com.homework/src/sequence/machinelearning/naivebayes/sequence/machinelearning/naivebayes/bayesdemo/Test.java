package sequence.machinelearning.naivebayes.bayesdemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	private static Map<String,Double> cmap=new HashMap<String,Double>();
	private static Map<String,Double> pmap=new HashMap<String,Double>();
    public static final String patternString = "@decision(.*)[{](.*?)[}]";
	public BigDecimal getProbability(String[] line,String decision){
		
		String ckey="P("+decision+")";
		//获取P(yes)的概率
		BigDecimal result=new BigDecimal(cmap.get(ckey));
			for(int j=0;j<line.length;j++){
				String attval=line[j].toString();
				String pkey="P("+Train.lisatt.get(j)+"="+attval+"|"+decision+")";
				//取得P(outlook=sunny|yes)的概率相
				BigDecimal pi=new BigDecimal(pmap.get(pkey));
				result=result.multiply(pi);
			}
		//System.out.println(arraytoString(line)+" 为"+decision+"的参考数值是："+result.toString().substring(0,5));
		return result;
	}
	public void printResult(){
		for(int i=0;i<Train.listdata.size();i++){
			String[] line=Train.listdata.get(i);
			BigDecimal p=new BigDecimal(0);
			int index=-1;
			for(int j=0;j<Train.sort.size();j++){
				BigDecimal pnext=getProbability(line,Train.sort.get(j));
				if(p.compareTo(pnext)==-1){
					p=pnext;
					index=j;
				}
			}
			System.out.println(arraytoString(line)+"   判断的结果是："+Train.sort.get(index)+"	      --参考数值是："+p.toString().substring(0,5));
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Train train=new Train();
		//读取测试集
		train.readARFF(new File("datafile/naivebayes/test/in/test.arff"));
		Test test=new Test();
		//读取训练结果
		test.readResult(new File("datafile/naivebayes/train/out/trainresult.arff"));
		test.printResult();
	}
	//数组转字符串
	public String arraytoString(String[] line){
		
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < line.length; i++){
		 sb. append(line[i]+",");
		}
        String newStr = sb.toString();
        return newStr.substring(0, newStr.length()-1);
	}
    //读取arff文件，给attribute、attributevalue、data赋值
    public void readResult(File file) {
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            Pattern pattern = Pattern.compile(patternString);
            while ((line = br.readLine()) != null) {
            	Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                	String[] values = matcher.group(2).split(",");
                    Double val=Double.valueOf(values[0]);
                    cmap.put(matcher.group(1).trim(), val);
                } else if (line.startsWith("@data")) {
                    while ((line = br.readLine()) != null) {
                        if(line=="")
                            continue;
                        String[] row = line.split(",");
                        Double val=Double.valueOf(row[1]);
                        pmap.put(row[0], val);
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
