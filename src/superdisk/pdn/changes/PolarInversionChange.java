package superdisk.pdn.changes;

import superdisk.pdn.pdnplugin.ArgumentOutOfRangeException;
import superdisk.pdn.pdnplugin.Utility;
import superdisk.pdn.structs.ColorBgra;
import superdisk.pdn.structs.Point;
import superdisk.pdn.structs.WarpEdgeBehavior;

public class PolarInversionChange extends WarpChange
{
	public static final PolarInversionChange instance = new PolarInversionChange(0, 2, new Point(), WarpEdgeBehavior.Reflect, new ColorBgra(), new ColorBgra());

	private double amount;
	
	/**
	 * Creates a new effect that will apply a polar inversion to an image.
	 * @param amount Amount of inversion. Valid range is -4 to 4.
	 * @param quality Quality of hte inversion. Valid range is 1 to 5.
	 * @param centerOffset Center of the inversion.
	 * @param edgeBehavior Edge behavior of the inversion.
	 * @param primaryColor Primary color of the inversion.
	 * @param secondaryColor Secondary color of the inversion.
	 */
	public PolarInversionChange(double amount, int quality, Point centerOffset, WarpEdgeBehavior edgeBehavior, ColorBgra primaryColor, ColorBgra secondaryColor)
	{
		super(quality, centerOffset, edgeBehavior, primaryColor, secondaryColor);
	
		if (amount < -4 || amount > 4)
			throw new ArgumentOutOfRangeException ("amount");

		this.amount = amount;
	}
	
	@Override
	protected void inverseTransform(TransformData data)
	{
		double x = data.x;
		double y = data.y;

		// NOTE: when x and y are zero, this will divide by zero and return NaN
		double invertDistance = Utility.lerp (1.0, defaultRadius2 / ((x * x) + (y * y)), amount);

		data.x = x * invertDistance;
		data.y = y * invertDistance;
	}

}
