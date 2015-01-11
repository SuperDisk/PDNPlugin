package superdisk.pdn.pdnplugin.changes;

import heroesgrave.spade.image.RawImage;
import heroesgrave.spade.image.change.SingleChange;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

import superdisk.pdn.pdnplugin.ArgumentOutOfRangeException;
import superdisk.pdn.structs.ColorBgra;


//TODO: Make this class actually adhere to Paint.NET source.
//Right now it's based from Pinta.ImageManipulation, which is
//in turn, its own modification of Paint.net source.
public class TileChange extends PDNChange
{
	private double rotation;
	private int tile_size;
	private int intensity; //TODO: Make this not stupid.
	
	public static final TileChange instance = new TileChange(30, 40, 8);
	
	/**
	 * Creates a new effect that will apply a tile effect to an image
	 * @param rotation Angle to rotate the tiles
	 * @param tileSize Size of hte tiles. Valid range is 2 to 200.
	 * @param intensity Intensity of the tiling effect. Valid range is -20 to 20.
	 */
	public TileChange(double rotation, int tileSize, int intensity)
	{
		if (tileSize < 2 || tileSize > 200)
			throw new ArgumentOutOfRangeException ("tileSize");
		if (intensity < -20 || intensity > 20)
			throw new ArgumentOutOfRangeException ("intensity");

		this.rotation = rotation;
		this.tile_size = tileSize;
		this.intensity = intensity;
	}
	
	@Override
	public void renderLine(RawImage src, RawImage dst, Rectangle rect)
	{
		int[] dstBuffer = dst.borrowBuffer();
		boolean[] mask = src.borrowMask();
		
		int width = dst.width;
		int height = dst.height;
		float hw = width / 2f;
		float hh = height / 2f;
		float sin = (float)Math.sin (rotation * Math.PI / 180.0);
		float cos = (float)Math.cos (rotation * Math.PI / 180.0);
		float scale = (float)Math.PI / tile_size;
		float intensity = tile_size;

		intensity = intensity * intensity / 10 * Math.signum(intensity);

		int aaLevel = 4;
		int aaSamples = aaLevel * aaLevel + 1;
		Point2D[] aaPoints = new Point2D[aaSamples];

		for (int i = 0; i < aaSamples; ++i) {
			double x = (i * aaLevel) / (double)aaSamples;
			double y = i / (double)aaSamples;

			x -= (int)x;

			// RGSS + rotation to maximize AA quality
			aaPoints[i] = new Point2D.Double ((double)(cos * x + sin * y), (double)(cos * y - sin * x));
		}

		for (int y = rect.y; y <= rect.y+rect.height-1; y++) {
			float j = y - hh;
			int dstPtr = dst.getIndex(rect.x, y);
			//ColorBgra* dstPtr = dst.GetPointAddress (rect.Left, y);

			for (int x = rect.x; x <= rect.x+rect.width-1; x++) {
				if (mask == null || mask[dstPtr])
				{
					int b = 0;
				
    				int g = 0;
    				int r = 0;
    				int a = 0;
    				float i = x - hw;
    
    				for (int p = 0; p < aaSamples; ++p) {
    					Point2D pt = aaPoints[p];
    
    					float u = i + (float)pt.getX();
    					float v = j - (float)pt.getY();
    
    					float s = cos * u + sin * v;
    					float t = -sin * u + cos * v;
    
    					s += intensity * (float)Math.tan (s * scale);
    					t += intensity * (float)Math.tan (t * scale);
    					u = cos * s - sin * t;
    					v = sin * s + cos * t;
    
    					int xSample = (int)(hw + u);
    					int ySample = (int)(hh + v);
    
    					xSample = (xSample + width) % width;
    					// This makes it a little faster
    					if (xSample < 0) {
    						xSample = (xSample + width) % width;
    					}
    
    					ySample = (ySample + height) % height;
    					// This makes it a little faster
    					if (ySample < 0) {
    						ySample = (ySample + height) % height;
    					}
    
    					ColorBgra sample = ColorBgra.fromInt(src.getPixel(xSample, ySample));
    					//ColorBgra sample = *src.GetPointAddress (xSample, ySample);
    
    					b += sample.getB();
    					g += sample.getG();
    					r += sample.getR();
    					a += sample.getA();
    				}
    
    				dstBuffer[dstPtr] = ColorBgra.fromBgra ((char)(b / aaSamples), (char)(g / aaSamples),
    					(char)(r / aaSamples), (char)(a / aaSamples)).getBgra();
    				}
    				
				dstPtr++;
			}
		}
	}

	@Override
	public SingleChange getInstance()
	{
		return instance;
	}

}
