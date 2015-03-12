package superdisk.pdn.changes;

import heroesgrave.spade.image.RawImage;

import java.awt.Rectangle;

import superdisk.pdn.pdnplugin.ArgumentOutOfRangeException;
import superdisk.pdn.pdnplugin.Utility;
import superdisk.pdn.structs.ColorBgra;
import superdisk.pdn.structs.Point;
import superdisk.pdn.structs.PointD;
import superdisk.pdn.structs.WarpEdgeBehavior;

class TransformData
{
	public double x;
	public double y;
}

public abstract class WarpChange extends PDNChange
{
	protected double defaultRadius = 2;
	protected double defaultRadius2 = 4;

	protected int quality;
	protected Point center_offset;
	protected WarpEdgeBehavior edge_behavior;
	protected ColorBgra primary_color;
	protected ColorBgra secondary_color;
	
	protected WarpChange (int quality, Point centerOffset, WarpEdgeBehavior edgeBehavior, ColorBgra primaryColor, ColorBgra secondaryColor)
	{
		if (quality < 1 || quality > 5)
			throw new ArgumentOutOfRangeException ("quality");

		this.quality = quality;
		this.center_offset = centerOffset;
		this.edge_behavior = edgeBehavior;
		this.primary_color = primaryColor;
		this.secondary_color = secondaryColor;
	}

	//TODO: use mask
	@Override
	public void renderLine(RawImage src, RawImage dst, Rectangle rect)
	{
		int[] dstBuffer = dst.borrowBuffer();
		boolean[] mask = src.borrowMask();
		
		ColorBgra colTransparent = ColorBgra.Transparent;

		int aaSampleCount = quality * quality;
		PointD[] aaPoints = new PointD[aaSampleCount];
		Utility.getRgssOffsets (aaPoints, aaSampleCount, quality);
		ColorBgra[] samples = new ColorBgra[aaSampleCount];

		TransformData td = new TransformData();

		for (int y = rect.y; y <= rect.y+rect.height-1; y++) {
			
			//ColorBgra* dstPtr = dst.GetPointAddress (rect.Left, y);
			int dstPtr = dst.getIndex(rect.x, y);
			
			double relativeY = y - center_offset.y;

			for (int x = rect.x; x <= rect.x+rect.width-1; x++) {
				double relativeX = x - center_offset.x;

				int sampleCount = 0;

				for (int p = 0; p < aaSampleCount; ++p) {
					td.x = relativeX + aaPoints[p].x;
					td.y = relativeY - aaPoints[p].y;

					inverseTransform(td);

					float sampleX = (float)(td.x + center_offset.x);
					float sampleY = (float)(td.y + center_offset.y);

					ColorBgra sample = primary_color;

					if (isOnSurface (src, sampleX, sampleY)) {
						sample = Utility.getBilinearSampleClamped(src, sampleX, sampleY);
					} else {
						switch (edge_behavior) {
							case Clamp:
								sample = Utility.getBilinearSampleClamped (src, sampleX, sampleY);
								break;

							case Wrap:
								sample = Utility.getBilinearSampleWrapped (src, sampleX, sampleY);
								break;

							case Reflect:
								sample = Utility.getBilinearSampleClamped (src, reflectCoord (sampleX, src.width), reflectCoord (sampleY, src.height));
								break;

							case Primary:
								sample = primary_color;
								break;

							case Secondary:
								sample = secondary_color;
								break;

							case Transparent:
								sample = colTransparent;
								break;

							case Original:
								sample = ColorBgra.fromInt(src.getPixel(x, y));
								break;
							default:

								break;
						}
					}

					samples[sampleCount] = sample;
					++sampleCount;
				}

				dstBuffer[dstPtr] = ColorBgra.blend(samples, sampleCount).getBgra();
				//*dstPtr = 
				++dstPtr;
			}
		}
	}
	
	protected abstract void inverseTransform(TransformData data);
	
	private static boolean isOnSurface (RawImage src, float u, float v)
	{
		return (u >= 0 && u <= (src.width - 1) && v >= 0 && v <= (src.height - 1));
	}

	private static float reflectCoord (float value, int max)
	{
		boolean reflection = false;

		while (value < 0) {
			value += max;
			reflection = !reflection;
		}

		while (value > max) {
			value -= max;
			reflection = !reflection;
		}

		if (reflection) {
			value = max - value;
		}

		return value;
	}
	
}
