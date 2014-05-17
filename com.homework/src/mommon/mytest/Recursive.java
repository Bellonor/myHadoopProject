package mytest;

public class Recursive {

	public static void foo(int num){
		num=num-1;
		if(num==0)return;
		
		else
		{
			System.out.println(num);
			foo(num);
			
		}
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 foo(5);
	}

}
