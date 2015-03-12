package superdisk.pdn.pixeloperations;

import java.awt.Rectangle;

import heroesgrave.spade.image.RawImage;
import superdisk.pdn.structs.ColorBgra;
import superdisk.pdn.structs.ColorBgraPointer;

public abstract class UnaryPixelOp extends PixelOp
{
	public abstract ColorBgra apply(ColorBgra color);
	
	public void apply(RawImage surface)
	{
		apply(surface, new Rectangle(0, 0, surface.width, surface.height));
	}
	
	public void apply(RawImage surface, Rectangle roi)
	{
		applyLoop(surface, roi);
	}
	
	public void apply(RawImage src, RawImage dst)
	{
		apply(src, dst, new Rectangle(0, 0, src.width, src.height));
	}
	
	public void apply(RawImage src, RawImage dst, Rectangle roi)
	{
		applyLoop(src, dst, roi);
	}
	
	public void apply(ColorBgraPointer ptr, int length)
	{
		while (length > 0) {
			ptr.setBgra(apply(ptr).getBgra());
			ptr.increment();
			--length;
		}
	}

	@Override
	public void apply (ColorBgraPointer src, ColorBgraPointer dst, int length)
	{
		while (length > 0) {
			dst.setBgra(apply(src).getBgra());
			dst.increment();
			src.increment();
			--length;
		}
	}
	
	protected void applyLoop(RawImage src, Rectangle roi)
	{
		for (int y = roi.y; y <= roi.y+roi.height-1; ++y) {
			ColorBgraPointer dstPtr = new ColorBgraPointer(src, roi.x, y);//src.GetPointAddress (roi.X, y);
			apply(dstPtr, roi.width-1);
		}
	}
	
	protected void applyLoop(RawImage src, RawImage dst, Rectangle roi)
	{
		for (int y = roi.y; y <= roi.y+roi.height-1; ++y) {
			ColorBgraPointer dstPtr = new ColorBgraPointer(dst, roi.x, y);//dst.GetPointAddress (roi.X, y);
			ColorBgraPointer srcPtr = new ColorBgraPointer(src, roi.x, y); //src.GetPointAddress (roi.X, y);
			apply(srcPtr, dstPtr, roi.width-1);
		}
	}
}
