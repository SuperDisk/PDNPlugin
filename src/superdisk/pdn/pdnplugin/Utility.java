package superdisk.pdn.pdnplugin;

import java.util.Random;

import superdisk.pdn.ArgumentOutOfRangeException;
import superdisk.pdn.structs.ColorBgra;
import superdisk.pdn.structs.ColorBgraPointer;
import superdisk.pdn.structs.PointD;
import heroesgrave.spade.image.RawImage;

public class Utility
{	
	public static final Random rand = new Random();
	
	public static ColorBgra getBilinearSampleClamped(RawImage src, float x, float y)
	{
    	float u = x;
    	float v = y;
    
    	if (u < 0)
    		u = 0;
    	else if (u > src.width - 1)
    		u = src.width - 1;
    
    	if (v < 0)
    		v = 0;
    	else if (v > src.height - 1)
    		v = src.height - 1;

		int iu = (int)Math.floor (u);
		long sxfrac = (long)(256 * (u - (float)iu));
		long sxfracinv = 256 - sxfrac;

		int iv = (int)Math.floor (v);
		long syfrac = (long)(256 * (v - (float)iv));
		long syfracinv = 256 - syfrac;

		long wul = (long)(sxfracinv * syfracinv);
		long wur = (long)(sxfrac * syfracinv);
		long wll = (long)(sxfracinv * syfrac);
		long wlr = (long)(sxfrac * syfrac);

		int sx = iu;
		int sy = iv;
		int sleft = sx;
		int sright;

		if (sleft == (src.width - 1))
			sright = sleft;
		else
			sright = sleft + 1;

		int stop = sy;
		int sbottom;

		if (stop == (src.height - 1))
			sbottom = stop;
		else
			sbottom = stop + 1;

		int[] srcBuffer = src.borrowBuffer();
		int cul = src.getIndex(sleft, stop);
		int cur = cul + (sright - sleft);
		int cll = src.getIndex(sleft, sbottom);
		int clr = cll + (sright - sleft);
		
		/*ColorBgra* cul = src.getPixelAddress (sleft, stop);
		ColorBgra* cur = cul + (sright - sleft);
		ColorBgra* cll = src.getPixelAddress (sleft, sbottom);
		ColorBgra* clr = cll + (sright - sleft);*/

		//System.out.println(ColorBgra.fromInt(srcBuffer[cul]).getBgra());
		ColorBgra c = ColorBgra.BlendColors4W16IP(ColorBgra.fromInt(srcBuffer[cul]), wul, ColorBgra.fromInt(srcBuffer[cur]), wur, ColorBgra.fromInt(srcBuffer[cll]), wll, ColorBgra.fromInt(srcBuffer[clr]), wlr);
		//ColorBgra.BlendColors4W16IP (*cul, wul, *cur, wur, *cll, wll, *clr, wlr);
		return c;
	}
	
	public static ColorBgra getBilinearSampleWrapped (RawImage src, float x, float y)
	{
		return getBilinearSampleWrapped (src, new ColorBgraPointer(src, 0, 0), src.width, src.height, x, y);
	}

	public static ColorBgra getBilinearSampleWrapped (RawImage src, ColorBgra srcDataPtr, int srcWidth, int srcHeight, float x, float y)
	{
		if (!Utility.isNumber (x) || !Utility.isNumber (y))
			return ColorBgra.Transparent;

		float u = x;
		float v = y;

		int iu = (int)Math.floor (u);
		int sxfrac = (int)(256 * (u - (float)iu)); //
		int sxfracinv = 256 - sxfrac;//

		int iv = (int)Math.floor (v);
		int syfrac = (int)(256 * (v - (float)iv));//
		int syfracinv = 256 - syfrac;//

		int wul = (int)(sxfracinv * syfracinv);///
		int wur = (int)(sxfrac * syfracinv);///
		int wll = (int)(sxfracinv * syfrac);///
		int wlr = (int)(sxfrac * syfrac);//

		int sx = iu;
		if (sx < 0)
			sx = (srcWidth - 1) + ((sx + 1) % srcWidth);
		else if (sx > (srcWidth - 1))
			sx = sx % srcWidth;

		int sy = iv;
		if (sy < 0)
			sy = (srcHeight - 1) + ((sy + 1) % srcHeight);
		else if (sy > (srcHeight - 1))
			sy = sy % srcHeight;

		int sleft = sx;
		int sright;

		if (sleft == (srcWidth - 1))
			sright = 0;
		else
			sright = sleft + 1;

		int stop = sy;
		int sbottom;

		if (stop == (srcHeight - 1))
			sbottom = 0;
		else
			sbottom = stop + 1;

		ColorBgra cul = ColorBgra.fromInt(src.getPixel (sleft, stop));
		ColorBgra cur = ColorBgra.fromInt(src.getPixel (sright, stop));
		ColorBgra cll = ColorBgra.fromInt(src.getPixel (sleft, sbottom));
		ColorBgra clr = ColorBgra.fromInt(src.getPixel (sright, sbottom));

		ColorBgra c = ColorBgra.BlendColors4W16IP (cul, wul, cur, wur, cll, wll, clr, wlr);

		return c;
	}
	
	public static char clampToByte (double x)
	{
		if (x > 255) {
			return 255;
		} else if (x < 0) {
			return 0;
		} else {
			return (char)x;
		}
	}

	public static char clampToByte (float x)
	{
		if (x > 255) {
			return 255;
		} else if (x < 0) {
			return 0;
		} else {
			return (char)x;
		}
	}
	
	public static char ClampToByte (int x)
	{
		if (x > 255) {
			return 255;
		} else if (x < 0) {
			return 0;
		} else {
			return (char)x;
		}
	}
	public static void getRgssOffsets (PointD[] samplesArray, int sampleCount, int quality)
	{
		if (sampleCount < 1)
			throw new ArgumentOutOfRangeException ("sampleCount must be [0, int.MaxValue]");

		if (sampleCount != quality * quality)
			throw new ArgumentOutOfRangeException ("sampleCount != (quality * quality)");

		if (sampleCount == 1) {
			samplesArray[0] = new PointD (0.0, 0.0);
		} else {
			for (int i = 0; i < sampleCount; ++i) {
				double y = (i + 1d) / (sampleCount + 1d);
				double x = y * quality;

				x -= (int)x;

				samplesArray[i] = new PointD (x - 0.5d, y - 0.5d);
			}
		}
	}
	
	private static boolean isNumber (float x)
	{
		return x >= Float.MIN_VALUE && x <= Float.MAX_VALUE;
	}
	
	public static double lerp (double from, double to, double frac)
	{
		return (from + frac * (to - from));
	}

	public static PointD lerp (PointD from, PointD to, float frac)
	{
		return new PointD (lerp (from.x, to.x, frac), lerp (from.y, to.y, frac));
	}
	
	public static int randInt(int min, int max) {
	    return rand.nextInt((max - min) + 1) + min;
	}
}
