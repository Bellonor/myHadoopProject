package classfier;

import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

public class PaodingTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        //String line="中华人民共和国";
        String line="据路透社报道，印度尼西亚社会事务部一官员星期二(29日)表示，"      
	+ "日惹市附近当地时间27日晨5时53分发生的里氏6.2级地震已经造成至少5427人死亡，"      
	+ "20000余人受伤，近20万人无家可归。";
        PaodingAnalyzer analyzer=new PaodingAnalyzer();
        StringReader sr=new StringReader(line);
        TokenStream ts=analyzer.tokenStream("", sr);
        try{
        	while(ts.incrementToken()){
        		CharTermAttribute ta=ts.getAttribute(CharTermAttribute.class);
        		System.out.println(ta.toString());
        	}
        	
        }catch(Exception e){
        	
        }
        
	}

}
