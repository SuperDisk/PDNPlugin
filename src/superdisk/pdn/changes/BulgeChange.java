package superdisk.pdn.changes;

import java.awt.Rectangle;

import superdisk.pdn.pdnplugin.ArgumentOutOfRangeException;
import superdisk.pdn.pdnplugin.Utility;
import heroesgrave.spade.image.RawImage;
import heroesgrave.spade.image.change.SingleChange;

public class BulgeChange extends PDNChange
{

	private int amount;
	private int xOff, yOff;
	
	public static final BulgeChange instance = new BulgeChange(-100, 0, 0);
	
	/**
	 * Creates a new effect that will add a bulge to an image at a
	 * specified point.
	 * 
	 * @param amount Amount to bulge. Valid range is -200 to 100.
	 * @param x Bulge origin x
	 * @param y Bulge origin y
	 */
	public BulgeChange(int amount, int x, int y)
	{
		if (amount < -200 || amount > 100)
			throw new ArgumentOutOfRangeException("amount");
		
		this.amount = amount;
		this.xOff = x;
		this.yOff = y;
	}
	
	@Override
	public void renderLine(RawImage src, RawImage dst, Rectangle rect)
	{
		int[] srcBuffer = src.borrowBuffer();
		int[] dstBuffer = dst.borrowBuffer();
		boolean[] mask = src.borrowMask();
		
		float bulge = (float)amount;

		float hw = dst.width / 2f;
		float hh = dst.height / 2f;
		float maxrad = Math.min (hw, hh);
		float amt = bulge / 100f;

		hh = hh + (float)yOff * hh;
		hw = hw + (float)xOff * hw;

		for (int y = rect.y; y <= rect.y+rect.height-1; y++) {
			int dstPtr = dst.getIndex(rect.x, y); //dst.GetPointAddress (rect.Left, y);
			int srcPtr = src.getIndex(rect.x, y); //src.GetPointAddress (rect.Left, y);
			
			float v = y - hh;

			for (int x = rect.x; x <= rect.x+rect.width-1; x++) { //WTF
				
				if (mask == null || mask[srcPtr])
				{
					float u = x - hw;
    				float r = (float)Math.sqrt (u * u + v * v);
    				float rscale1 = (1f - (r / maxrad));
    
    				if (rscale1 > 0) {
    					float rscale2 = 1 - amt * rscale1 * rscale1;
    
    					float xp = u * rscale2;
    					float yp = v * rscale2;
    
    					dstBuffer[dstPtr] = Utility.getBilinearSampleClamped (src, xp + hw, yp + hh).getBgra();
    				} else {
    					dstBuffer[dstPtr] = srcBuffer[srcPtr];
    				}
				}
				
				++dstPtr;
				++srcPtr;
			}
		}
	}

	@Override
	public SingleChange getInstance()
	{
		return instance;
	}
}
