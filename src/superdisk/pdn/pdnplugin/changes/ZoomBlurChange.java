package superdisk.pdn.pdnplugin.changes;

import heroesgrave.spade.image.RawImage;
import heroesgrave.spade.image.change.SingleChange;

import java.awt.Rectangle;

import superdisk.pdn.pdnplugin.ArgumentOutOfRangeException;
import superdisk.pdn.pdnplugin.Utility;
import superdisk.pdn.structs.ColorBgra;
import superdisk.pdn.structs.Point;

public class ZoomBlurChange extends PDNChange
{
	public static final ZoomBlurChange instance = new ZoomBlurChange(10, new Point());

	private int amount;
	private Point offset;
	
	public ZoomBlurChange(int amount, Point offset)
	{
		if (amount < 0 || amount > 200)
			throw new ArgumentOutOfRangeException ("amount");

		this.amount = amount;
		this.offset = offset;
	}
	
	@Override
	public void renderLine(RawImage src, RawImage dst, Rectangle rect)
	{
		int[] dstBuffer = dst.borrowBuffer();
		int[] srcBuffer = src.borrowBuffer();
		boolean[] mask = src.borrowMask();
		
		if (amount == 0) {
			System.arraycopy(src.borrowBuffer(), 0, dstBuffer, 0, dstBuffer.length);
			return;
		}

		Rectangle src_bounds = new Rectangle(0, 0, src.width, src.height);

		long w = dst.width;
		long h = dst.height;
		long fox = (long)(dst.width * offset.x * 32768.0);
		long foy = (long)(dst.height * offset.x * 32768.0);
		long fcx = fox + (w << 15);
		long fcy = foy + (h << 15);
		long fz = amount;

		final int n = 64;

		for (int y = rect.y; y <= rect.y+rect.height-1; ++y) {
			int dstPtr = dst.getIndex(rect.x, y);
			int srcPtr = src.getIndex(rect.x, y);
			//ColorBgra* dstPtr = dst.GetPointAddress (rect.Left, y);
			//ColorBgra* srcPtr = src.GetPointAddress (rect.Left, y);

			for (int x = rect.x; x <= rect.x+rect.width-1; ++x) {
				if (mask == null || mask[srcPtr])
				{
					long fx = (x << 16) - fcx;
    				long fy = (y << 16) - fcy;
    
    				int sr = 0;
    				int sg = 0;
    				int sb = 0;
    				int sa = 0;
    				int sc = 0;
    				
    				ColorBgra srcVal = ColorBgra.fromInt(srcBuffer[srcPtr]);
    
    				sr += srcVal.getR() * srcVal.getA();
    				sg += srcVal.getG() * srcVal.getA();
    				sb += srcVal.getB() * srcVal.getA();
    				sa += srcVal.getA();
    				++sc;
    
    				for (int i = 0; i < n; ++i) {
    					fx -= ((fx >> 4) * fz) >> 10;
    					fy -= ((fy >> 4) * fz) >> 10;
    
    					int u = (int)(fx + fcx + 32768 >> 16);
    					int v = (int)(fy + fcy + 32768 >> 16);
    
    					if (src_bounds.contains(u, v)) {
    						//ColorBgra* srcPtr2 = src.GetPointAddress (u, v);
    						int srcPtr2 = src.getIndex(u, v);
    						ColorBgra srcVal2 = ColorBgra.fromInt(srcBuffer[srcPtr2]);
    
    						sr += srcVal2.getR() * srcVal2.getA();
    						sg += srcVal2.getG() * srcVal2.getA();
    						sb += srcVal2.getB() * srcVal2.getA();
    						sa += srcVal2.getA();
    						++sc;
    					}
    				}
    
    				if (sa != 0) {
    					dstBuffer[dstPtr] = ColorBgra.fromBgra (
    							Utility.ClampToByte (sb / sa),
    							Utility.ClampToByte (sg / sa),
    							Utility.ClampToByte (sr / sa),
    							Utility.ClampToByte (sa / sc)).getBgra();
    					/*dstPtr = ColorBgra.FromBgra (
    						Utility.ClampToByte (sb / sa),
    						Utility.ClampToByte (sg / sa),
    						Utility.ClampToByte (sr / sa),
    						Utility.ClampToByte (sa / sc));*/
    				} else {
    					dstBuffer[dstPtr] = 0;
    					//dstPtr->Bgra = 0;
    				}
				
				}
				
				++srcPtr;
				++dstPtr;
			}
		}
	}

	@Override
	public SingleChange getInstance()
	{
		return instance;
	}

}
