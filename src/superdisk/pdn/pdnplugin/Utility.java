package superdisk.pdn.pdnplugin;

import java.util.Random;

import superdisk.pdn.structs.ColorBgra;
import superdisk.pdn.structs.PointD;
import heroesgrave.spade.image.RawImage;

public class Utility
{	
	public static final Random rand = new Random();
	
	public static int GetBilinearSampleClamped(RawImage src, float x, float y)
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
		
		/*ColorBgra* cul = src.GetPointAddress (sleft, stop);
		ColorBgra* cur = cul + (sright - sleft);
		ColorBgra* cll = src.GetPointAddress (sleft, sbottom);
		ColorBgra* clr = cll + (sright - sleft);*/

		//System.out.println(ColorBgra.fromInt(srcBuffer[cul]).getBgra());
		ColorBgra c = ColorBgra.BlendColors4W16IP(ColorBgra.fromInt(srcBuffer[cul]), wul, ColorBgra.fromInt(srcBuffer[cur]), wur, ColorBgra.fromInt(srcBuffer[cll]), wll, ColorBgra.fromInt(srcBuffer[clr]), wlr);
		//ColorBgra.BlendColors4W16IP (*cul, wul, *cur, wur, *cll, wll, *clr, wlr);
		return c.getBgra();
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
