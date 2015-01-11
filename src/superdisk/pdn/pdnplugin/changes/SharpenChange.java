package superdisk.pdn.pdnplugin.changes;

import heroesgrave.spade.image.RawImage;
import heroesgrave.spade.image.change.SingleChange;

import java.awt.Rectangle;

import superdisk.pdn.pdnplugin.ArgumentOutOfRangeException;

public class SharpenChange extends PDNChange
{
	public static final SharpenChange instance = new SharpenChange(2);

	private int amount;
	
	/**
	 * Creates a new effect that will sharpen an image
	 * @param amount
	 */
	public SharpenChange(int amount)
	{
		if (amount < 1 || amount > 20)
			throw new ArgumentOutOfRangeException ("amount");

		this.amount = amount;
	}
	
	@Override
	public void renderLine(RawImage src, RawImage dst, Rectangle rect)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public SingleChange getInstance()
	{
		return instance;
	}

}
