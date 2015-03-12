package superdisk.pdn.pixeloperations;

import superdisk.pdn.structs.ColorBgraPointer;

public abstract class PixelOp
{
	public abstract void apply (ColorBgraPointer src, ColorBgraPointer dst, int length);
}
