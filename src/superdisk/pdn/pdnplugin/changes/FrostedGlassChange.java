package superdisk.pdn.pdnplugin.changes;

import heroesgrave.spade.image.RawImage;

import java.awt.Rectangle;
import java.util.Random;

import superdisk.pdn.pdnplugin.ArgumentOutOfRangeException;
import superdisk.pdn.structs.ColorBgra;

public class FrostedGlassChange extends PDNChange
{
	public static final FrostedGlassChange instance = new FrostedGlassChange(1);
	
	private int amount;
	private Random random = new Random();
	
	/**
	 * Creates a new effect that applies a frosted glass look to an image
	 * @param amount Amount of effect to apply. Valid range is 1 to 10.
	 */
	public FrostedGlassChange(int amount)
	{
		if (amount < 1 || amount > 10)
			throw new ArgumentOutOfRangeException ("amount");

		this.amount = amount;
	}
	
	@Override
	public void renderLine(RawImage src, RawImage dst, Rectangle rect)
	{
		int[] dstBuffer = dst.borrowBuffer();
		int[] srcBuffer = src.borrowBuffer();
		boolean[] mask = src.borrowMask();
		
		int width = src.width;
		int height = src.height;
		int r = amount;
		Random localRandom = this.random;

		int[] intensityCount = new int[256];
		int[] avgRed = new int[256]; //
		int[] avgGreen = new int[256]; //
		int[] avgBlue = new int[256]; //
		int[] avgAlpha = new int[256]; //
		char[] intensityChoices = new char[(1 + (r * 2)) * (1 + (r * 2))];

		int rectTop = rect.y;
		int rectBottom = rect.y+rect.height-1;
		int rectLeft = rect.x;
		int rectRight = rect.x+rect.width-1;

		for (int y = rectTop; y <= rectBottom; ++y) {
			int dstPtr = dst.getIndex(rect.x, y);
			//ColorBgra* dstPtr = dst.GetPointAddress (rect.Left, y);

			int top = y - r;
			int bottom = y + r + 1;

			if (top < 0) {
				top = 0;
			}

			if (bottom > height) {
				bottom = height;
			}

			for (int x = rectLeft; x <= rectRight; ++x) {
				int intensityChoicesIndex = 0;

				for (int i = 0; i < 256; ++i) {
					intensityCount[i] = 0;
					avgRed[i] = 0;
					avgGreen[i] = 0;
					avgBlue[i] = 0;
					avgAlpha[i] = 0;
				}

				int left = x - r;
				int right = x + r + 1;

				if (left < 0) {
					left = 0;
				}

				if (right > width) {
					right = width;
				}

				for (int j = top; j < bottom; ++j) {
					if (j < 0 || j >= height) {
						continue;
					}

					//ColorBgra* srcPtr = src.GetPointAddress (left, j);
					int srcPtr = src.getIndex(left, j);
					
					for (int i = left; i < right; ++i) {
						ColorBgra s = ColorBgra.fromInt(srcBuffer[srcPtr]);
						char intensity = s.getIntensityByte(); //srcPtr->GetIntensityByte ();

						intensityChoices[intensityChoicesIndex] = intensity;
						++intensityChoicesIndex;

						++intensityCount[intensity];

						
						avgRed[intensity] += s.getR();
						avgGreen[intensity] += s.getG();
						avgBlue[intensity] += s.getB();
						avgAlpha[intensity] += s.getA();

						++srcPtr;
					}
				}

				int randNum;

				randNum = localRandom.nextInt(intensityChoicesIndex);


				char chosenIntensity = intensityChoices[randNum];

				char R = (char)(avgRed[chosenIntensity] / intensityCount[chosenIntensity]);
				char G = (char)(avgGreen[chosenIntensity] / intensityCount[chosenIntensity]);
				char B = (char)(avgBlue[chosenIntensity] / intensityCount[chosenIntensity]);
				char A = (char)(avgAlpha[chosenIntensity] / intensityCount[chosenIntensity]);

				dstBuffer[dstPtr] = ColorBgra.fromBgra (B, G, R, A).getBgra();
				++dstPtr;

				// prepare the array for the next loop iteration
				for (int i = 0; i < intensityChoicesIndex; ++i) {
					intensityChoices[i] = 0;
				}
			}
		}
	}
}
