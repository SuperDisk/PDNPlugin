package superdisk.pdn.pixeloperations;

import superdisk.pdn.structs.ColorBgra;

public class RedEyeRemoveOp extends UnaryPixelOp
{
	private int tolerence;
	private double set_saturation;
	
	public RedEyeRemoveOp(int tol, int sat)
	{
		tolerence = tol;
		set_saturation = (double)sat / 100;
	}
	
	@Override
	public ColorBgra apply(ColorBgra color)
	{
		// The higher the saturation, the more red it is
		int saturation = getSaturation (color);

		// The higher the difference between the other colors, the more red it is
		int difference = color.getR() - Math.max (color.getB(), color.getG());

		// If it is within tolerence, and the saturation is high
		if ((difference > tolerence) && (saturation > 100)) {
			double i = 255.0 * color.getIntensity();
			char ib = (char)(i * set_saturation); // adjust the red color for user inputted saturation
			return ColorBgra.fromBgra (color.getB(), color.getG(), ib, color.getA());
		} else {
			return color;
		}
	}
	
	private int getSaturation (ColorBgra color)
	{
		double min;
		double max;
		double delta;

		double r = (double)color.getR() / 255;
		double g = (double)color.getG() / 255;
		double b = (double)color.getB() / 255;

		double s;

		min = Math.min (Math.min (r, g), b);
		max = Math.max (Math.max (r, g), b);
		delta = max - min;

		if (max == 0 || delta == 0) {
			// R, G, and B must be 0, or all the same.
			// In this case, S is 0, and H is undefined.
			// Using H = 0 is as good as any...
			s = 0;
		} else {
			s = delta / max;
		}

		return (int)(s * 255);
	}

}
