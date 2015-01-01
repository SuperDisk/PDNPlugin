package superdisk.pdn.pdnplugin.changes;

import java.awt.Rectangle;

import heroesgrave.spade.image.RawImage;
import heroesgrave.spade.image.change.IImageChange;
import heroesgrave.spade.image.change.SingleChange;

public abstract class PDNChange extends SingleChange implements IImageChange
{
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
	
	public abstract void renderLine(RawImage src, RawImage dst, Rectangle rect);
}
