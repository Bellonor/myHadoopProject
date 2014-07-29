package sequence.machinelearning.naivebayes.textmining;

import java.io.IOException;

import jeasy.analysis.MMAnalyzer;

/**
 * 极易分词测试
 * @author Administrator
 *
 */
public class ParticipleTest {

	public static void main(String[] args)      
	{      
	String text = "据路透社报道，印度尼西亚社会事务部一官员星期二(29日)表示，"      
	+ "日惹市附近当地时间27日晨5时53分发生的里氏6.2级地震已经造成至少5427人死亡，"      
	+ "20000余人受伤，近20万人无家可归。";      
	    
	MMAnalyzer analyzer = new MMAnalyzer();      
	try      
	{      
	//System.out.println(analyzer.segment(text, " | "));     
		System.out.println(analyzer.segment(text, "\n"));  
	}      
	catch (IOException e)      
	{      
	e.printStackTrace();      
	}      
	} 

}
