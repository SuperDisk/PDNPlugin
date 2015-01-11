package superdisk.pdn.pdnplugin.changes;

import heroesgrave.spade.image.RawImage;

import java.awt.Rectangle;

import superdisk.pdn.ArgumentOutOfRangeException;
import superdisk.pdn.pdnplugin.Utility;
import superdisk.pdn.structs.ColorBgra;
import superdisk.pdn.structs.Point;
import superdisk.pdn.structs.PointD;

public class RadialBlurChange extends PDNChange
{

	public static final RadialBlurChange instance = new RadialBlurChange(1, new PointD(), 2);
	
	private double angle;
	private PointD offset;
	private int quality;
	
	/**
	 * Creates a new effect that will apply a radial blur to an image
	 * @param angle Angle of the blur
	 * @param offset Center point of the blur
	 * @param quality Quality of the radial blur. Valid range is 1-5.
	 */
	public RadialBlurChange(double angle, PointD offset, int quality)
	{
		if (quality < 1 || quality > 5)
			throw new ArgumentOutOfRangeException ("quality");

		this.angle = angle;
		this.offset = offset;
		this.quality = quality;
	}
	
	@Override
	public void renderLine(RawImage src, RawImage dst, Rectangle rect)
	{
		int[] dstBuffer = dst.borrowBuffer();
		int[] srcBuffer = src.borrowBuffer();
		boolean[] mask = src.borrowMask();
		
		if (angle == 0) {
			System.arraycopy(src.borrowBuffer(), 0, dstBuffer, 0, dstBuffer.length);
		}

		int w = dst.width;
		int h = dst.height;
		int fcx = (w << 15) + (int)(offset.x * (w << 15));
		int fcy = (h << 15) + (int)(offset.y * (h << 15));

		int n = (quality * quality) * (30 + quality * quality);

		int fr = (int)(angle * Math.PI * 65536.0 / 181.0);

		for (int y = rect.y; y <= rect.y+rect.height-1; ++y) {
			//ColorBgra* dstPtr = dst.GetPointAddress (rect.Left, y);
			//ColorBgra* srcPtr = src.GetPointAddress (rect.Left, y);
			int dstPtr = dst.getIndex(rect.x, y);
			int srcPtr = src.getIndex(rect.x, y);

			for (int x = rect.x; x <= rect.x+rect.width-1; ++x) {
				if (mask == null || mask[dstPtr])
				{
    				int fx = (x << 16) - fcx;
    				int fy = (y << 16) - fcy;
    
    				int fsr = fr / n;
    
    				int sr = 0;
    				int sg = 0;
    				int sb = 0;
    				int sa = 0;
    				int sc = 0;
    
    				ColorBgra srcColor = ColorBgra.fromInt(srcBuffer[srcPtr]);
    				sr += srcColor.getR() * srcColor.getA();
    				sg += srcColor.getG() * srcColor.getA();
    				sb += srcColor.getB() * srcColor.getA();
    				sa += srcColor.getA();
    				++sc;
    
    				int ox1 = fx;
    				int ox2 = fx;
    				int oy1 = fy;
    				int oy2 = fy;
    
    				for (int i = 0; i < n; ++i) {
    					//rotate (ref ox1, ref oy1, fsr);
    					//rotate (ref ox2, ref oy2, -fsr);
    					
    					Point o1 = new Point(ox1, oy1);
    					rotate(o1, fsr);
    					ox1 = o1.x;
    					oy1 = o1.y;
    					
    					Point o2 = new Point(ox2, oy2);
    					rotate(o2, -fsr);
    					ox2 = o2.x;
    					oy2 = o2.y;
    
    					int u1 = ox1 + fcx + 32768 >> 16;
    					int v1 = oy1 + fcy + 32768 >> 16;
    
    					if (u1 > 0 && v1 > 0 && u1 < w && v1 < h) {
    						//ColorBgra* sample = src.GetPointAddress (u1, v1);
    						ColorBgra sample = ColorBgra.fromInt(src.getPixel(u1, v1));
    						
    						sr += sample.getR() * sample.getA();
    						sg += sample.getG() * sample.getA();
    						sb += sample.getB() * sample.getA();
    						sa += sample.getA();
    						++sc;
    					}
    
    					int u2 = ox2 + fcx + 32768 >> 16;
    					int v2 = oy2 + fcy + 32768 >> 16;
    
    					if (u2 > 0 && v2 > 0 && u2 < w && v2 < h) {
    						//ColorBgra* sample = src.GetPointAddress (u2, v2);
    						ColorBgra sample = ColorBgra.fromInt(src.getPixel(u2, v2));
    						
    						sr += sample.getR() * sample.getA();
    						sg += sample.getG() * sample.getA();
    						sb += sample.getB() * sample.getA();
    						sa += sample.getA();
    						++sc;
    					}
    				}
    
    				if (sa > 0) {
    					//dstBuffer[dstPtr]
    					dstBuffer[dstPtr] = ColorBgra.fromBgra (
    						Utility.clampToByte (sb / sa),
    						Utility.clampToByte (sg / sa),
    						Utility.clampToByte (sr / sa),
    						Utility.clampToByte (sa / sc)).getBgra();
    				} else {
    					dstBuffer[dstPtr] = 0;
    				}
				
				}

				++dstPtr;
				++srcPtr;
			}
		}
	}
	
	private static void rotate (Point f, int fr)
	{
		int cx = f.x;
		int cy = f.y;
		
		//sin(x) ~~ x
		//cos(x)~~ 1 - x^2/2
		f.x = cx - ((cy >> 8) * fr >> 8) - ((cx >> 14) * (fr * fr >> 11) >> 8);
		f.y = cy + ((cx >> 8) * fr >> 8) - ((cy >> 14) * (fr * fr >> 11) >> 8);
	}
}
