package superdisk.pdn.pdnplugin.changes;

import java.awt.Rectangle;

import heroesgrave.spade.image.RawImage;
import heroesgrave.spade.image.change.IImageChange;
import heroesgrave.spade.image.change.SingleChange;

public class PDNChange extends SingleChange implements IImageChange
{
	//public static final PDNChange instance = new PDNChange();
	
	@Override
	public RawImage apply(RawImage image)
	{
		RawImage dst = new RawImage(image.width, image.height);
		RawImage src = image;
		
		for (int i = 0; i < src.height; i++)
		{
			renderLine(src, dst, new Rectangle(0, i, src.width, 1));
		}
		
		return dst;
	}
	
	public void renderLine(RawImage src, RawImage dst, Rectangle rect)
	{
		render(src.borrowBuffer(), dst.borrowBuffer(), rect.width);
	}
	
	protected void render(int[] src, int[] dst, int length)
	{
		while (length > 0)
		{
			dst[length] = render(src[length]);
			length--;
		}
	}
	
	protected int render(int color)
	{
		return color;
	}

	@Override
	public SingleChange getInstance()
	{
		return null;
		//return instance;
	}
}
