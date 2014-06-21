package sequence.machinelearning.association.apriori;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;


/*本例为《数据挖掘:概念与技术(第1版)》 韩家炜,第六章apriori伪代码实现 */
public class MyApriori {

	public static final int  support = 2; // 设定最小支持频次为2 
	public static  int  itemnum = 0; // 设定购物篮中最大项数
	
	public static Map<String,String> dataMap=new HashMap<String,String>();
	public static void datatest(){
		 // 初始化事务集  
      
		dataMap.put("T100","I1,I2,I5");
		dataMap.put("T200", "I2,I4");
		dataMap.put("T300", "I2,I3");
		dataMap.put("T400","I1,I2,I4" );
		dataMap.put("T500","I1,I3" );
		dataMap.put("T600", "I2,I3");
		dataMap.put("T700", "I1,I3");
		dataMap.put("T800", "I1,I2,I3,I5");
		dataMap.put("T900", "I1,I2,I3");
       //map.put("T900", "I1,I2,I3,I6,I7");
      
  }
	public static final void readF1() throws IOException {      
		
		//String filePath="scripts/clustering/canopy/canopy.dat";
		String filePath="datafile/association/items";
		BufferedReader br = new BufferedReader(new InputStreamReader(
        new FileInputStream(filePath)));
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            if(line.length()==0||"".equals(line))continue;
        	String[] str=line.split("\t");               
        	dataMap.put(str[0], str[1]);
            //System.out.println(line);               
        }
        br.close();
        
    }
	/**
	 * 扫描事务集以确定频繁1项集(找出C1)
	 */
	public static List<ItemMap> findFrequentOneItemSets(Map<String,String> map){
		TreeMap<String,Integer> treemap=new TreeMap<String,Integer>();
		Iterator<Entry<String,String>> iter=map.entrySet().iterator();
		Entry<String,String> entry;
		while(iter.hasNext()){
			entry=iter.next();
			String str=entry.getValue();
			if(str.length()<1)continue;
			String[] items=str.split(",");
			//找出购物栏最大的项,为循环连接做准备
			if(items.length>itemnum)itemnum=items.length;
			for(int i=0;i<items.length;i++){
				if(treemap.containsKey(items[i])){
					Integer count=treemap.get(items[i])+1;
					treemap.put(items[i], count);
				}else{
					treemap.put(items[i], 1);
				}
			}
		}
		return DeleteItem(treemap);
	}
	/**
	 * 剪枝:产生候选项，删除最小事务支持的选项
	 * */
	public static List<ItemMap> DeleteItem(TreeMap<String,Integer> map){
		List<ItemMap> listmap=new ArrayList<ItemMap>();
		Iterator<Entry<String,Integer>> iter=map.entrySet().iterator();
		Entry<String,Integer> entry;
		while(iter.hasNext()){
			entry=iter.next();
			if(entry.getValue()>=support){
				ItemMap item=new ItemMap();
				item.setKey(entry.getKey());
				item.setValue(entry.getValue());
				listmap.add(item);
			}
		}
		return listmap;
	}
	//对非初始项进行剪枝
	public static List<ItemMap> DeleteItem2(List<ItemMap> limap){
		List<ItemMap> listmap=new ArrayList<ItemMap>();
		for(int i=0;i<limap.size();i++){
			if(limap.get(i).getValue()>=support){
				ItemMap item=new ItemMap();
				item.setKey(limap.get(i).getKey());
				item.setValue(limap.get(i).getValue());
				listmap.add(item);
			}
		}
		limap.clear();
		return listmap;
	}
	
	//计数,该子集在购物篮中出现的次数
	public static List<ItemMap> countItems( List<ItemMap> list){
		List<ItemMap> listmap=new ArrayList<ItemMap>();
		
		//按传入来的组合项集进行循环
		for(int i=0;i<list.size();i++){
			ItemMap itemap=list.get(i);
			String items[]=itemap.getKey().split(",");
			ItemMap itemap2=new ItemMap();
			itemap2.setKey(itemap.getKey());
			//取出原始数据进行比较，如果购物栏中包含了该子集就+1
			Iterator<Entry<String,String>> iter=dataMap.entrySet().iterator();
			Entry<String,String> entry;
			//取出购物栏中数据，逐行扫描
			while(iter.hasNext()){
				entry=iter.next();
				boolean iscontain=true;
				String itemstr=entry.getValue();
				for(int j=0;j<items.length;j++){
					if(!itemstr.contains(items[j])){
						iscontain=false;
					}
				}
				if(iscontain){
					itemap2.setValue(itemap2.getValue()+1);
				}
			}
			listmap.add(itemap2);
		}
		list.clear();//释放内存
		return listmap;
	}
    /**
	 * 扫描C1产生C2,频繁项集2，产生C2的规则是，C1中的取两位组合
	 */
	public static List<ItemMap> findFrequentTwoItemSets(List<ItemMap> listmap){
		List<ItemMap> list=new ArrayList<ItemMap>();
		Integer rows=listmap.size();
		for(int i=0;i<rows-1;i++){
			for(int j=i+1;j<rows;j++){
				ItemMap item=new ItemMap();
				ItemMap item2=new ItemMap();
				String key=listmap.get(i).getKey()+","+listmap.get(j).getKey();
				item.setKey(key); 
				list.add(item);
			}
		}
		//计数然后删除不合格的项
		return DeleteItem2(countItems(list));
	}
    public static List<List<ItemMap>> generateRequentItems(){
    	//li从第0项开始，与书中从第一项开始稍有不同
    	List<List<ItemMap>> li=new ArrayList<List<ItemMap>>();
    	List<ItemMap> listmap=findFrequentOneItemSets(dataMap);
    	if(listmap.size()>0)li.add(listmap);
    	//如:购物栏中最大有四项，只需要对4-1进行连接，留下一个做规则分析
    	for(int i=1;i<itemnum-1;i++){
    		if(i==1){
    			List<ItemMap> listmap2=findFrequentTwoItemSets(li.get(i-1));
    			if(listmap2.size()>0)li.add(listmap2);
    			continue;
    		}
    		//对C3的连接是在L2的基础上进行的，所以要-1
    		List<ItemMap> listmp=li.get(i-1);
    		Integer rows=listmp.size();
    		List<ItemMap> subli=new ArrayList<ItemMap>();
    		for(int j=0;j<rows-1;j++){
    			ItemMap itm=listmp.get(j);
    			String[] items=itm.getKey().split(",");
    			//i-2项相同就连接，否则跳过
    			for(int n=j+1;n<rows;n++){
        			ItemMap itemNext=listmp.get(n);
        			String[] next=itemNext.getKey().split(",");
        			int maxnum=next.length;
        			//对比前I-2项是否相同，相同就连接，如产生C3连接，那么就要对比第一项是相同，拿C2来对比
        			boolean isEqual=true; 
        			for(int k=0;k<=i-2;k++){
        				//在I-2项中只要有一项不相同就等于否，说明不能连接
        				if(!items[k].toString().equals(next[k].toString())){
        					isEqual=false;
        				}
        			}
        			if(isEqual){
        				ItemMap itmp=new ItemMap();
        				String key=itm.getKey()+","+next[maxnum-1];
        				itmp.setKey(key);
        				itmp.setValue(0);
        				subli.add(itmp);
        			}
    			}
    		}
    	   //计数，删除后加入li中
    	   li.add(DeleteItem2(countItems(subli)));
    		
    	}
    	return li;
    }
    public static void generate(){
    	List<List<ItemMap>> lirule=new ArrayList<List<ItemMap>>();
    	
    	List<List<ItemMap>> li=generateRequentItems();
    	List<ItemMap> lmp=li.get(li.size()-1);
    	for(int i=0;i<lmp.size();i++){
    		List<ItemMap> sublist=new ArrayList<ItemMap>();
    		ItemMap item=lmp.get(i);
    		String[] strkey=item.getKey().split(",");
    		sublist=subset(strkey,li);
    		String key=item.getKey();
    		Integer value=item.getValue();
    		System.out.println("频繁项集产生的推荐规则如下："+key);
    		for(int j=0;j<sublist.size();j++){
    			ItemMap itm=sublist.get(j);
    			String itmKey=itm.getKey();
    			Integer itmValue=itm.getValue();
    			for(int k=0;k<sublist.size();k++){
    				ItemMap nextitem=sublist.get(k);
        			String nextKey=nextitem.getKey();
        			//互不包含就产生规则
        			String[] nextkeys=nextKey.split(",");
        			boolean iscontain=false;
        			
        			for(int n=0;n<nextkeys.length;n++){
        				boolean b1=itmKey.contains(nextkeys[n]);
        				boolean b2=nextkeys[n].contains(itmKey);
            			if(b1||b2){
            				iscontain=true;
            			}
            			//过滤掉单项=>单项,如:I1=>I2
            			if(itmKey.split(",").length==1&&nextkeys.length==1){
            				iscontain=true;
            			}
        			}
        			if(!iscontain){
        				DecimalFormat df = new DecimalFormat("#.##");
        				Double confidence=0.0;
        				confidence=(Double.valueOf(value)/Double.valueOf(itmValue));
        				
        				System.out.println(itmKey+"=>"+nextKey+",	"+"confidence="+value+"/"+itmValue+"="+df.format(confidence*100)+"%");
        			}
    			}
    		}
    		System.out.println();
    		lirule.add(sublist);
    	}
    	
    	
    }
    // 求子集,并求子集出现的次数
	public static List<ItemMap>  subset(String[] str,List<List<ItemMap>> list){
		StringBuilder sb=new StringBuilder();
		List<ItemMap> li=new ArrayList<ItemMap>();
		for(int i=0;i<str.length;i++){
			int size=li.size();
			//取出一下个的时候与前面的所有项逐个搭配
			for(int j=0;j<size;j++){
				ItemMap item1=new ItemMap();
				item1.setKey(li.get(j).getKey()+","+str[i]);
				Integer value=itemsTimes(item1.getKey(),list);
				item1.setValue(value);
				li.add(item1);
			}  
			ItemMap item2=new ItemMap();
			item2.setKey(str[i]);
			Integer value=itemsTimes(str[i],list);
			item2.setValue(value);
			
			li.add(item2);
			sb.append(str[i]+",");
			
		}
		//删除本身
		int len=sb.toString().length();
		String del=sb.toString().substring(0,len-1);
		for(int i=0;i<li.size();i++){
			if(del.equals(li.get(i).getKey())){
				li.remove(i);
				break;
			}
		}
		String dd="dd";
		return li;
	}
	public static Integer itemsTimes(String key,List<List<ItemMap>> list){
		Integer value=0;
		int index=key.split(",").length-1;
		if(index==-1)return null;
		List<ItemMap> lis=list.get(index);
		for(int i=0;i<lis.size();i++){
			if(key.equals(lis.get(i).getKey())){
				value= lis.get(i).getValue();
			}
		}
		return value;
		
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//generateRequentItems();
		//datatest();
		readF1();
		generate();

		ItemMap itm=new ItemMap();
		String dd="Dd";
	}

}
//运行结果
/*频繁项集产生的推荐规则如下：I1,I2,I3
I1=>I2,I3,	confidence=2/6=33.33%
I1,I2=>I3,	confidence=2/4=50%
I2=>I1,I3,	confidence=2/7=28.57%
I1,I3=>I2,	confidence=2/4=50%
I2,I3=>I1,	confidence=2/4=50%
I3=>I1,I2,	confidence=2/5=40%

频繁项集产生的推荐规则如下：I1,I2,I5
I1=>I2,I5,	confidence=2/6=33.33%
I1,I2=>I5,	confidence=2/4=50%
I2=>I1,I5,	confidence=2/7=28.57%
I1,I5=>I2,	confidence=2/2=100%
I2,I5=>I1,	confidence=2/2=100%
I5=>I1,I2,	confidence=2/2=100%*/
