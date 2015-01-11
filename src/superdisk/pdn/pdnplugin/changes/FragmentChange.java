package superdisk.pdn.pdnplugin.changes;

import heroesgrave.spade.image.RawImage;

import java.awt.Rectangle;

import superdisk.pdn.pdnplugin.ArgumentOutOfRangeException;
import superdisk.pdn.structs.ColorBgra;
import superdisk.pdn.structs.Point;

public class FragmentChange extends PDNChange
{
	public static final FragmentChange instance = new FragmentChange(4, 8, 0);
	
	private int fragments;
	private int distance;
	private double rotation;
	
	/**
	 * Creates a new effect that makes an image look fragmented
	 * @param fragments Number of fragments to apply. Valid range is 2 to 50
	 * @param distance Distance beween fragments. Valid range is 0 to 100
	 * @param rotation Angle to apply
	 */
	public FragmentChange(int fragments, int distance, double rotation)
	{
		if (fragments < 2 || fragments > 50)
			throw new ArgumentOutOfRangeException ("fragments");
		if (distance < 0 || distance > 100)
			throw new ArgumentOutOfRangeException ("distance");

		this.fragments = fragments;
		this.distance = distance;
		this.rotation = rotation;
	}
	
	@Override
	public void renderLine(RawImage src, RawImage dst, Rectangle rect)
	{
		int[] dstBuffer = dst.borrowBuffer();
		boolean[] mask = src.borrowMask();
		
		Point[] pointOffsets = recalcPointOffsets (fragments, rotation, distance);

		int poLength = pointOffsets.length;
		Point[] pointOffsetsPtr = new Point[poLength];

		for (int i = 0; i < poLength; ++i)
			pointOffsetsPtr[i] = pointOffsets[i];

		ColorBgra[] samples = new ColorBgra[poLength];

		int src_width = src.width;
		int src_height = src.height;

		for (int y = rect.y; y <= rect.y+rect.height-1; y++) {
			//ColorBgra* dstPtr = dst.GetPointAddress (rect.x, y);
			int dstPtr = dst.getIndex(rect.x, y);

			for (int x = rect.x; x <= rect.x+rect.width-1; x++) {
				if (mask == null || mask[dstPtr])
				{
    				int sampleCount = 0;
    
    				for (int i = 0; i < poLength; ++i) {
    					int u = x - pointOffsetsPtr[i].x;
    					int v = y - pointOffsetsPtr[i].y;
    
    					if (u >= 0 && u < src_width && v >= 0 && v < src_height) {
    						samples[sampleCount] = ColorBgra.fromInt(src.getPixel(u, v));
    						++sampleCount;
    					}
    				}
    
    				dstBuffer[dstPtr] = ColorBgra.blend(samples, sampleCount).getBgra();
				}
				++dstPtr;
			}
		}
	}
	
	private Point[] recalcPointOffsets (int fragments, double rotationAngle, int distance)
	{
		double pointStep = 2 * Math.PI / (double)fragments;
		double rotationRadians = ((rotationAngle - 90.0) * Math.PI) / 180.0;

		Point[] pointOffsets = new Point[fragments];

		for (int i = 0; i < fragments; i++) {
			double currentRadians = rotationRadians + (pointStep * i);
			pointOffsets[i] = new Point (
				//(int)Math.Round (distance * -Math.sin (currentRadians), MidpointRounding.AwayFromZero),
				//(int)Math.Round (distance * -Math.cos (currentRadians), MidpointRounding.AwayFromZero));
				(int)Math.round (distance * -Math.sin (currentRadians)),
				(int)Math.round (distance * -Math.cos (currentRadians)));
		}

		return pointOffsets;
	}
}
