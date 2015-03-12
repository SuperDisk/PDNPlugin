package superdisk.pdn.changes;

import heroesgrave.spade.image.RawImage;

import java.awt.Rectangle;

import superdisk.pdn.pdnplugin.ArgumentOutOfRangeException;
import superdisk.pdn.pixeloperations.RedEyeRemoveOp;

public class RedEyeRemoveChange extends PDNChange
{
	public static final RedEyeRemoveChange instance = new RedEyeRemoveChange(100, 90);
	
	private RedEyeRemoveOp op;
	
	/**
	 * Creates a new effect that will remove red within a certain tolerance from an image.
	 * @param tolerance Tolerance of red to remove. Valid range is 0 to 100.
	 * @param saturation Saturation of effect. Valid range is 0 to 100.
	 */
	public RedEyeRemoveChange(int tolerance, int saturation)
	{
		if (tolerance < 0 || tolerance > 100)
			throw new ArgumentOutOfRangeException ("tolerance");
		if (saturation < 0 || saturation > 100)
			throw new ArgumentOutOfRangeException ("saturation");

		op = new RedEyeRemoveOp (tolerance, saturation);
	}
	
	@Override
	public void renderLine(RawImage src, RawImage dst, Rectangle rect)
	{
		op.apply(src, dst, rect);
	}
}
