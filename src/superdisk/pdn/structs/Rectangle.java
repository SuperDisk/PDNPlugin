package superdisk.pdn.structs;

public class Rectangle
{
	public int x;
	public int y;
	public int height;
	public int width;

	public Rectangle (int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Rectangle (Point location, Size size)
	{
		x = location.x;
		y = location.y;
		width = size.width;
		height = size.height;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getBottom()
	{
		return y + height - 1;
	}
	
	public boolean isEmpty()
	{
		return x == 0 && y == 0 && width == 0 && height == 0;
	}
	
	public int getLeft()
	{
		return x;
	}
	
	public Point getLocation()
	{
		return new Point(x, y);
	}
	
	public int getRight()
	{
		return x + width - 1;
	}
	
	public Size getSize()
	{
		return new Size(height, width);
	}
	
	public int getTop()
	{
		return y;
	}

	public boolean Contains (int x, int y)
	{
		return ((x >= this.x) && (x < getRight()) && (y >= this.y) && (y < getBottom()));
	}

	public void Intersect (Rectangle r)
	{
		Rectangle new_r = Rectangle.intersect (this, r);

		x = new_r.x;
		y = new_r.y;
		width = new_r.width;
		height = new_r.height;
	}

	public static Rectangle intersect (Rectangle a, Rectangle b)
	{
		return Rectangle.fromLTRB (
			Math.max (a.getLeft(), b.getLeft()),
			Math.max (a.getTop(), b.getTop()),
			Math.min (a.getRight(), b.getRight()),
			Math.min (a.getBottom(), b.getBottom()));
	}

	@Override
	public String toString ()
	{
		return String.format ("{{X={0},Y={1},Width={2},Height={3}}}", x, y, width, height);
	}

	public static Rectangle fromLTRB (int left, int top, int right, int bottom)
	{
		return new Rectangle (left, top, right - left + 1,
		bottom - top + 1);
	}
	
	public boolean equals(Object o)
	{
		if (o instanceof Rectangle)
		{
			Rectangle r1 = (Rectangle)o;
			return r1.x == this.x && r1.y == this.y && r1.width == this.width && r1.height == this.height;
		} else return false;
	}
}
