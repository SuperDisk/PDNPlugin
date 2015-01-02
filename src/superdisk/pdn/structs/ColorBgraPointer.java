package superdisk.pdn.structs;

import heroesgrave.spade.image.RawImage;

public class ColorBgraPointer extends ColorBgra
{
	private int index;
	private int[] buffer;
	
	public ColorBgraPointer(RawImage img, int x, int y)
	{
		setBgra(img.getPixel(x, y));
		index = img.getIndex(x, y);
		buffer = img.borrowBuffer();
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
}
