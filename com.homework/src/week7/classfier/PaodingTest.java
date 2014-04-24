package classfier;

import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

public class PaodingTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        String line="中华人民共和国";
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
