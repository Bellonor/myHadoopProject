package sequence.machinelearning.decisiontree.id3test;

public class DTreeUtil {
	
	public static double sigma(int x, int total)
	{
		if (x == 0)
		{
			return 0;
		}
		double x_pi = getPi(x,total);
		return -(x_pi*logYBase2(x_pi));
	}

	
	public static double logYBase2(double y)
	{
		return Math.log(y) / Math.log(2);
	}
	
	
	public static double getPi(int x, int total)
	{
		return x * Double.parseDouble("1.0") / total;
	}
}
