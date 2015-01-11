package superdisk.pdn.pdnplugin.changes;

import heroesgrave.spade.image.RawImage;
import heroesgrave.spade.image.change.SingleChange;

import java.awt.Rectangle;

import superdisk.pdn.pdnplugin.ArgumentOutOfRangeException;
import superdisk.pdn.structs.ColorBgra;

public class EmbossChange extends PDNChange
{
	private double angle;
	public static final EmbossChange instance = new EmbossChange(0);
	
	public EmbossChange(double angle)
	{
		if (angle < 0 || angle > 360)
			throw new ArgumentOutOfRangeException("angle");
		
		this.angle = angle;
	}

	@Override
	public void renderLine(RawImage src, RawImage dst, Rectangle rect)
	{
		double[][] weights = getWeights();
		int[] dstBuffer = dst.borrowBuffer();
		boolean[] mask = src.borrowMask();
		
		int srcWidth = src.width;
		int srcHeight = src.height;
		
		for (int y = rect.y; y <= rect.y+rect.height-1; ++y) {
			int fyStart = 0;
			int fyEnd = 3;

			if (y == 0)
				fyStart = 1;

			if (y == srcHeight - 1)
				fyEnd = 2;

			// loop through each point in the line 
			int dstPtr = dst.getIndex(rect.x, y);
			//ColorBgra* dstPtr = dst.GetPointAddress (rect.Left, y);

			for (int x = rect.x; x <= rect.x+rect.width-1; ++x) {
				if (mask == null || mask[dstPtr])
				{
					int fxStart = 0;
					
    				int fxEnd = 3;
    
    				if (x == 0)
    					fxStart = 1;
    
    				if (x == srcWidth - 1)
    					fxEnd = 2;
    
    				// loop through each weight
    				double sum = 0.0;
    
    				for (int fy = fyStart; fy < fyEnd; ++fy) {
    					for (int fx = fxStart; fx < fxEnd; ++fx) {
    						double weight = weights[fy][fx];
    						ColorBgra c = ColorBgra.fromInt(src.getPixel(x - 1 + fx, y - 1 + fy));
    						double intensity = (double)c.getIntensityByte();
    						sum += weight * intensity;
    					}
    				}
    
    				int iSum = (int)sum;
    				iSum += 128;
    
    				if (iSum > 255)
    					iSum = 255;
    
    				if (iSum < 0)
    					iSum = 0;
    
    				dstBuffer[dstPtr] = ColorBgra.fromBgra ((char)iSum, (char)iSum, (char)iSum, (char)255).getBgra();
				}
				
				++dstPtr;
			}
		}
	}

	@Override
	public SingleChange getInstance()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	private double[][] getWeights()
	{
		double r = (double)angle * 2.0 * Math.PI / 360.0;
		// angle delta for each weight
		double dr = Math.PI / 4.0;

		// for r = 0 this builds an emboss filter pointing straight left
		double[][] weights = new double[3][3];

		weights[0][0] = Math.cos (r + dr);
		weights[0][1] = Math.cos (r + 2.0 * dr);
		weights[0][2] = Math.cos (r + 3.0 * dr);

		weights[1][0] = Math.cos (r);
		weights[1][1] = 0;
		weights[1][2] = Math.cos (r + 4.0 * dr);

		weights[2][0] = Math.cos (r - dr);
		weights[2][1] = Math.cos (r - 2.0 * dr);
		weights[2][2] = Math.cos (r - 3.0 * dr);

		return weights;
	}
}
