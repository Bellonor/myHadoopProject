package sequence.machinelearning.decisiontree.myid3;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TheMath {
	
	//信息熵的计算公式，这里仅是离散型二项分布的熵计算
	/**
	 * 
	 * @param S 样本总数
	 * @param li 各个事件发生的次数
	 * @return
	 */
	public static  Double getEntropy(Double S,List<Double> li){
		Double entropy=new Double(-1.0);
		for(int i=0;i<li.size();i++){
			entropy=entropy+sigma(li.get(i),S);
		}
		return entropy;
	}
	//信息增益公式
	/**
	 * 
	 * @param entropyS S的信息熵
	 * @param total 传入的总数
	 * @param sv sv.key 是sv的个数,sv.value是entropy(sv)
	 * @return
	 */
	public static Double getGain(Double entropyS,Double total,Map<Double,Double> sv){
		Double gain=new Double(-1.0);
		Double enSum=new Double(0.0);
		Map.Entry<Double, Double>entry;
		Iterator<Entry<Double,Double>> iter=sv.entrySet().iterator();
		while(iter.hasNext()){
			entry=iter.next();
			enSum=enSum+((entry.getKey()/total)*entry.getValue());
		}
		gain=entropyS-enSum;
		return gain;
	}
	
	//公式 -pi*log2(x)
	public static Double sigma(Double x, Double total)
	{
		if (x == 0)
		{
			return 0.0;
		}
		double x_pi = getProbability(x,total);
		return -(x_pi*logYBase2(x_pi));
	}

	//取2为底的对数
	public static double logYBase2(double y)
	{
		return Math.log(y) / Math.log(2);
	}
	
	//等可能事件概率
	public static double getProbability(double x, double total)
	{
		return x * Double.parseDouble("1.0") / total;
	}
}
