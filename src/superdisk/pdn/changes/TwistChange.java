package superdisk.pdn.changes;

import heroesgrave.spade.image.RawImage;
import heroesgrave.spade.image.change.SingleChange;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

import superdisk.pdn.pdnplugin.ArgumentOutOfRangeException;
import superdisk.pdn.structs.ColorBgra;

//TODO: Implement offset. WTF pinta team!?
public class TwistChange extends PDNChange
{
	
	public final static TwistChange instance = new TwistChange(45, 2);
	
	private int amount;
	private int antialias;
	
	public TwistChange(int amount, int antialias)
	{
		if (amount < -100 || amount > 100)
			throw new ArgumentOutOfRangeException ("amount");
		if (antialias < 0 || antialias > 5)
			throw new ArgumentOutOfRangeException ("antialias");

		this.amount = amount;
		this.antialias = antialias;
	}

	@Override
	public void renderLine(RawImage src, RawImage dst, Rectangle rect)
	{
		int[] dstBuffer = dst.borrowBuffer();
		int[] srcBuffer = src.borrowBuffer();
		boolean[] mask = src.borrowMask();
		
		float twist = amount;

		float hw = dst.width / 2.0f;
		float hh = dst.height / 2.0f;
		float maxrad = Math.min (hw, hh);

		twist = twist * twist * Math.signum(twist);

		int aaLevel = antialias;
		int aaSamples = aaLevel * aaLevel + 1;
		Point2D[] aaPoints = new Point2D[aaSamples];

		for (int i = 0; i < aaSamples; ++i) {
			Point2D pt = new Point2D.Float(
				((i * aaLevel) / (float)aaSamples),
				i / (float)aaSamples);

			pt.setLocation((int)pt.getX(), pt.getY());
			aaPoints[i] = pt;
		}

		for (int y = rect.y; y <= rect.y+rect.height-1; y++) {
			float j = y - hh;
			int dstPtr = dst.getIndex(rect.x, y);
			int srcPtr = src.getIndex(rect.x, y);
			//ColorBgra* dstPtr = dst.GetPointAddress (rect.Left, y);
			//ColorBgra* srcPtr = src.GetPointAddress (rect.Left, y);

			for (int x = rect.x; x <= rect.x+rect.width-1; x++) {
				if (mask == null || mask[dstPtr])
				{
					float i = x - hw;
					

    				if (i * i + j * j > (maxrad + 1) * (maxrad + 1)) {
    					dstBuffer[dstPtr] = srcBuffer[srcPtr];
    					//*dstPtr = *srcPtr;
    				} else {
    					int b = 0;
    					int g = 0;
    					int r = 0;
    					int a = 0;
    
    					for (int p = 0; p < aaSamples; ++p) {
    						float u = i + (float)aaPoints[p].getX();
    						float v = j + (float)aaPoints[p].getY();
    						double rad = Math.sqrt (u * u + v * v);
    						double theta = Math.atan2 (v, u);
    
    						double t = 1 - rad / maxrad;
    
    						t = (t < 0) ? 0 : (t * t * t);
    
    						theta += (t * twist) / 100;
    
    						ColorBgra sample = ColorBgra.fromInt(src.getPixel(
    							(int)(hw + (float)(rad * Math.cos (theta))),
    							(int)(hh + (float)(rad * Math.sin (theta)))));
    
    						b += sample.getB();
    						g += sample.getG();
    						r += sample.getR();
    						a += sample.getA();
    					}
    
    					dstBuffer[dstPtr] = ColorBgra.fromBgra(
    						(char)(b / aaSamples),
    						(char)(g / aaSamples),
    						(char)(r / aaSamples),
    						(char)(a / aaSamples)).getBgra();
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
