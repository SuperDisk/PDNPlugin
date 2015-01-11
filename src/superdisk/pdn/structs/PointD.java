package superdisk.pdn.structs;

public class PointD
{
	public double x;
	public double y;

	public static PointD Empty;

	public PointD()
	{
		
	}
	
	public PointD (double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public double Magnitude ()
	{
		return Math.sqrt (x * x + y * y);
	}

	public double Distance (PointD e)
	{
		return new PointD (x - e.x, y - e.y).Magnitude ();
	}
}