package mysequence.machineleaning.association.apriori;

import java.util.ArrayList;
import java.util.List;
/**
 * 求集合的子集，不包含自己和空集
 * @author Administrator
 * 算法：先取出第一个，取出一下个的时候与前面的所有项逐个搭配，所以两次FOR就可以了
 * 1  //第一次FOR,i=0
 * 12，2  //第二次FOR,i=1
 * 123，23，13，3  //第三次FOR,i=2
 * 14,124,24,1234,234,134,34//第四次FOR,i=3
 * 最后删除本身，为了Apriori算法而增加这一步
 */ 
//本类为测试使用，不在MyApriori里面
public class Subset {

	public static List<String> lis=new ArrayList<String>();
	public static void main(String[] args) {
		
		subset();
		// TODO Auto-generated method stub
		String[] str =new String[] { "1", "2", "3", "4"};
		StringBuilder sb=new StringBuilder();
		List<String> li=new ArrayList<String>();
		for(int i=0;i<str.length;i++){
			int size=li.size();
			//取出一下个的时候与前面的所有项逐个搭配
			for(int j=0;j<size;j++){
				li.add(li.get(j)+str[i] );
			}
			li.add(str[i]);
			sb.append(str[i]);
		}
		//删除本身
		int n=li.indexOf(sb.toString());
		li.remove(n);
		String strr="dd";
		
	}
	
	public static void subset(){
		String[] str =new String[] { "I1", "I2", "I3", "I4"};
		StringBuilder sb=new StringBuilder();
		List<String> li=new ArrayList<String>();
		for(int i=0;i<str.length;i++){
			int size=li.size();
			//取出一下个的时候与前面的所有项逐个搭配
			for(int j=0;j<size;j++){
				li.add(li.get(j)+","+str[i] );
			}  
			li.add(str[i]);
			sb.append(str[i]);
		}
		//删除本身
		int n=li.indexOf(sb.toString()+",");
		li.remove(n);
		String strr="dd";
	}

}
