package superdisk.pdn.pdnplugin.changes;

import heroesgrave.spade.image.RawImage;
import heroesgrave.spade.image.change.SingleChange;

import java.awt.Rectangle;

import superdisk.pdn.ArgumentOutOfRangeException;
import superdisk.pdn.pdnplugin.ColorBgra;
import superdisk.pdn.pdnplugin.Utility;

public class JuliaFractalChange extends PDNChange
{
	public static final JuliaFractalChange instance = new JuliaFractalChange(4, 2, 1, 0);
	
	private int factor;
	private int quality;
	private int zoom;
	private double angle;
	
	private static final double log2_10000 = Math.log(10000);
	
	public JuliaFractalChange(int factor, int quality, int zoom, double angle)
	{
		if (factor < 1 || factor > 10)
			throw new ArgumentOutOfRangeException ("factor");
		if (quality < 1 || quality > 5)
			throw new ArgumentOutOfRangeException ("quality");
		if (zoom < 0 || zoom > 50)
			throw new ArgumentOutOfRangeException ("zoom");

		this.factor = factor;
		this.quality = quality;
		this.zoom = zoom;
		this.angle = angle;
	}
	
	@Override
	public void renderLine(RawImage src, RawImage dst, Rectangle rect)
	{
		int[] dstBuffer = dst.borrowBuffer();
		boolean[] mask = src.borrowMask();
		
		final double jr = 0.3125;
		final double ji = 0.03;

		int w = dst.width;
		int h = dst.height;
		double invH = 1.0 / h;
		double invZoom = 1.0 / zoom;
		double invQuality = 1.0 / quality;
		double aspect = (double)h / (double)w;
		int count = quality * quality + 1;
		double invCount = 1.0 / (double)count;
		double angleTheta = (angle * Math.PI * 2) / 360.0;

		for (int y = rect.y; y <= rect.y+rect.height-1; y++) {
			int dstPtr = dst.getIndex(rect.x, y);
			//ColorBgra* dstPtr = dst.GetPointAddress (rect.Left, y);

			for (int x = rect.x; x <= rect.x+rect.width-1; x++) {
				int r = 0;
				int g = 0;
				int b = 0;
				int a = 0;

				for (double i = 0; i < count; i++) {
					if (mask == null || mask[dstPtr])
					{
						double u = (2.0 * x - w + (i * invCount)) * invH;
    					double v = (2.0 * y - h + ((i * invQuality) % 1)) * invH;
    
    					double radius = Math.sqrt ((u * u) + (v * v));
    					double radiusP = radius;
    					double theta = Math.atan2 (v, u);
    					double thetaP = theta + angleTheta;
    
    					double uP = radiusP * Math.cos (thetaP);
    					double vP = radiusP * Math.sin (thetaP);
    
    					double jX = (uP - vP * aspect) * invZoom;
    					double jY = (vP + uP * aspect) * invZoom;
    
    					double j = julia (jX, jY, jr, ji);
    
    					double c = factor * j;
    
    					b += Utility.clampToByte (c - 768);
    					g += Utility.clampToByte (c - 512);
    					r += Utility.clampToByte (c - 256);
    					a += Utility.clampToByte (c - 0);
    				}
    
    				//*dstPtr = ColorBgra.FromBgra (Utility.ClampToByte (b / count), Utility.ClampToByte (g / count), Utility.ClampToByte (r / count), Utility.ClampToByte (a / count));
    				dstBuffer[dstPtr] = ColorBgra.fromBgra (Utility.clampToByte (b / count), Utility.clampToByte (g / count), Utility.clampToByte (r / count), Utility.clampToByte (a / count)).getBgra();
				}
				++dstPtr;
			}
		}
	}
	
	private static double julia (double x, double y, double r, double i)
	{
		double c = 0;

		while (c < 256 && x * x + y * y < 10000) {
			double t = x;
			x = x * x - y * y + r;
			y = 2 * t * y + i;
			++c;
		}

		c -= 2 - 2 * log2_10000 / Math.log (x * x + y * y);

		return c;
	}

	@Override
	public SingleChange getInstance()
	{
		return instance;
	}

}
