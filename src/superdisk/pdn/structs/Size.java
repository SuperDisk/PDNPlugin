package superdisk.pdn.structs;

public class Size
{
	public int height;
	public int width;

	public Size (int height, int width)
	{
		this.height = height;
		this.width = width;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Size)
		{
			Size otherSize = (Size)other;
			return this.height == otherSize.height && this.width == otherSize.width;
		} else return false;
	}
}
