package superdisk.pdn.pdnplugin.changes;

import heroesgrave.spade.image.RawImage;
import heroesgrave.spade.image.change.SingleChange;

import java.awt.Rectangle;

import superdisk.pdn.ArgumentOutOfRangeException;
import superdisk.pdn.pdnplugin.ColorBgra;
import superdisk.pdn.pdnplugin.Utility;

public class MandelbrotFractalChange extends PDNChange
{
	public static final MandelbrotFractalChange instance = new MandelbrotFractalChange(1, 2, 10, 0, false);
	
	private int factor;
	private int quality;
	private int zoom;
	private double angle;
	private boolean invert_colors;

	private static final double max = 100000;
	private static final double invLogMax = 1.0 / Math.log (max);
	private static double zoomFactor = 20.0;
	private final double xOffsetBasis = -0.7;
	private double xOffset = xOffsetBasis;

	private final double yOffsetBasis = -0.29;
	private double yOffset = yOffsetBasis;

	/**
	 * Creates a new effect that will draw a Mandelbrot fractal.
	 * @param factor Factor to use. Valid range is 1 to 10.
	 * @param quality Quality of the fractal. Valid range is 1 to 5.
	 * @param zoom Size of the fractal. Valid range is 0 to 100.
	 * @param angle Angle of the fractal to render.
	 * @param invertColors Invert the fractal colors.
	 */
	public MandelbrotFractalChange(int factor, int quality, int zoom, double angle, boolean invertColors)
	{
		if (factor < 1 || factor > 10)
			throw new ArgumentOutOfRangeException ("factor");
		if (quality < 1 || quality > 5)
			throw new ArgumentOutOfRangeException ("quality");
		if (zoom < 0 || zoom > 100)
			throw new ArgumentOutOfRangeException ("zoom");

		this.factor = factor;
		this.quality = quality;
		this.zoom = zoom;
		this.angle = angle;
		this.invert_colors = invertColors;
	}
	
	@Override
	public void renderLine(RawImage src, RawImage dst, Rectangle rect)
	{
		int[] dstBuffer = dst.borrowBuffer();
		boolean[] mask = src.borrowMask();
		
		int w = dst.width;
		int h = dst.height;

		double invH = 1.0 / h;
		double zoom2 = 1 + zoomFactor * zoom;
		double invZoom = 1.0 / zoom2;

		double invQuality = 1.0 / (double)quality;

		int count = quality * quality + 1;
		double invCount = 1.0 / (double)count;
		double angleTheta = (angle * 2 * Math.PI) / 360;

		for (int y = rect.y; y <= rect.y+rect.height-1; y++) {
			int dstPtr = dst.getIndex(rect.x, y);
			//ColorBgra* dstPtr = dst.GetPointAddress (rect.Left, y);

			for (int x = rect.x; x <= rect.x+rect.width-1; x++) {
				if (mask == null || mask[dstPtr])
				{
    				int r = 0;
    				int g = 0;
    				int b = 0;
    				int a = 0;
    
    				for (double i = 0; i < count; i++) {
    					double u = (2.0 * x - w + (i * invCount)) * invH;
    					double v = (2.0 * y - h + ((i * invQuality) % 1)) * invH;
    
    					double radius = Math.sqrt ((u * u) + (v * v));
    					double radiusP = radius;
    					double theta = Math.atan2 (v, u);
    					double thetaP = theta + angleTheta;
    
    					double uP = radiusP * Math.cos (thetaP);
    					double vP = radiusP * Math.sin (thetaP);
    
    					double m = mandelbrot ((uP * invZoom) + this.xOffset, (vP * invZoom) + this.yOffset, factor);
    
    					double c = 64 + factor * m;
    
    					r += Utility.clampToByte (c - 768);
    					g += Utility.clampToByte (c - 512);
    					b += Utility.clampToByte (c - 256);
    					a += Utility.clampToByte (c - 0);
    				}
    
    				//*dstPtr = ColorBgra.FromBgra (Utility.ClampToByte (b / count), Utility.ClampToByte (g / count), Utility.ClampToByte (r / count), Utility.ClampToByte (a / count));
    				dstBuffer[dstPtr] = ColorBgra.fromBgra (Utility.clampToByte (b / count), Utility.clampToByte (g / count), Utility.clampToByte (r / count), Utility.clampToByte (a / count)).getBgra();
				}
				++dstPtr;
			}
		}

		if (invert_colors) {
			for (int y = rect.y; y <= rect.y+rect.height-1; y++) {
				int dstPtr = dst.getIndex(rect.x, y);
				//ColorBgra* dstPtr = dst.GetPointAddress (rect.Left, y);

				for (int x = rect.x; x <= rect.x+rect.width-1; ++x) {
					if (mask == null || mask[dstPtr])
					{
						//ColorBgra c = *dstPtr;
    					ColorBgra c = ColorBgra.fromInt(dstBuffer[dstPtr]);
    					
    					c.setB((char)(255 - c.getB()));
    					c.setG((char)(255 - c.getG()));
    					c.setR((char)(255 - c.getR()));
    
    					//*dstPtr = c;
    					dstBuffer[dstPtr] = c.getBgra();
					}
					++dstPtr;
				}
			}
		}
	}
	
	private static double mandelbrot (double r, double i, int factor)
	{
		int c = 0;
		double x = 0;
		double y = 0;

		while ((c * factor) < 1024 && ((x * x) + (y * y)) < max) {
			double t = x;

			x = x * x - y * y + r;
			y = 2 * t * y + i;

			++c;
		}

		return c - Math.log (y * y + x * x) * invLogMax;
	}

	@Override
	public SingleChange getInstance()
	{
		return instance;
	}

}
