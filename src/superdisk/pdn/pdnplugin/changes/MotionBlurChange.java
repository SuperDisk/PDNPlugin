package superdisk.pdn.pdnplugin.changes;

import heroesgrave.spade.image.RawImage;
import heroesgrave.spade.image.change.SingleChange;

import java.awt.Rectangle;

import superdisk.pdn.pdnplugin.ArgumentOutOfRangeException;
import superdisk.pdn.pdnplugin.Utility;
import superdisk.pdn.structs.ColorBgra;
import superdisk.pdn.structs.PointD;

public class MotionBlurChange extends PDNChange
{
	public static final MotionBlurChange instance = new MotionBlurChange(25, 10, true);
	
	private double angle;
	private int distance;
	private boolean centered;
	
	/**
	 * Creates a new effect that will apply a motion blur to an image
	 * @param angle Angle of the motion blur
	 * @param distance Distance to apply the blur. Valid range is 1 to 200.
	 * @param centered Whether the blur is centered.
	 */
	public MotionBlurChange(double angle, int distance, boolean centered)
	{
		if (distance < 1 || distance > 200)
			throw new ArgumentOutOfRangeException ("distance");

		this.angle = angle;
		this.distance = distance;
		this.centered = centered;
	}
	
	@Override
	public void renderLine(RawImage src, RawImage dst, Rectangle rect)
	{
		int[] dstBuffer = dst.borrowBuffer();
		boolean[] mask = src.borrowMask();
		
		PointD start = new PointD(0, 0);
		double theta = ((double)(angle + 180) * 2 * Math.PI) / 360.0;
		double alpha = (double)distance;
		PointD end = new PointD((float)(alpha * Math.cos (theta)), (float)(-alpha * Math.sin (theta)));

		if (centered) {
			start.x = -end.x / 2.0f;
			start.y = -end.y / 2.0f;

			end.x /= 2.0f;
			end.y /= 2.0f;
		}

		PointD[] points = new PointD[((1 + distance) * 3) / 2];

		if (points.length == 1) {
			points[0] = new PointD (0, 0);
		} else {
			for (int i = 0; i < points.length; ++i) {
				float frac = (float)i / (float)(points.length - 1);
				points[i] = Utility.lerp (start, end, frac);
			}
		}

		//ColorBgra* samples = stackalloc ColorBgra[points.Length];
		ColorBgra[] samples = new ColorBgra[points.length];
		
		int src_width = src.width;
		int src_height = src.height;


		for (int y = rect.y; y <= rect.y+rect.height-1; ++y) {
			int dstPtr = dst.getIndex(rect.x, y);
			//ColorBgra* dstPtr = dst.GetPointAddress (rect.Left, y);

			for (int x = rect.x; x <= rect.x+rect.width-1; ++x) {
				if (mask == null || mask[dstPtr])
				{
					int sampleCount = 0;

    				for (int j = 0; j < points.length; ++j) {
    					PointD pt = new PointD (points[j].x + (float)x, points[j].y + (float)y);
    
    					if (pt.x >= 0 && pt.y >= 0 && pt.x <= (src_width - 1) && pt.y <= (src_height - 1)) {
    						samples[sampleCount] = Utility.getBilinearSampleClamped (src, (float)pt.x, (float)pt.y);
    						++sampleCount;
    					}
    				}
    
    				//*dstPtr = ColorBgra.Blend (samples, sampleCount);
    				dstBuffer[dstPtr] = ColorBgra.blend(samples, sampleCount).getBgra();
				}
				
				++dstPtr;
			}
		}
	}

	@Override
	public SingleChange getInstance()
	{
		return instance;
	}

}
