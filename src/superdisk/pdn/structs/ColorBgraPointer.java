package superdisk.pdn.structs;

import heroesgrave.spade.image.RawImage;

public class ColorBgraPointer extends ColorBgra
{
	private int index;
	private int[] buffer;
	
	public ColorBgraPointer(RawImage img, int x, int y)
	{
		index = img.getIndex(x, y);
		buffer = img.borrowBuffer();
		setBgra(img.getPixel(x, y));
	}
	
	public ColorBgraPointer(RawImage img, int index)
	{
		this.index = index;
		buffer = img.borrowBuffer();
		setBgra(buffer[index]);
	}
	
	@Override
	public void setBgra(int bgra)
	{
		super.setBgra(bgra);
		buffer[index] = bgra;
	}
	
	public void setIndex(int index)
	{
		super.setBgra(buffer[index]);
		this.index = index;
	}
	
	public void increment()
	{
		setIndex(index + 1);
	}
	
	public void decrement()
	{
		setIndex(index - 1);
	}
}
