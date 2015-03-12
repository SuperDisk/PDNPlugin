package superdisk.pdn.changes;

import heroesgrave.spade.image.RawImage;
import heroesgrave.spade.image.change.SingleChange;

import java.awt.Rectangle;
import java.nio.ByteBuffer;

import superdisk.pdn.pdnplugin.ArgumentOutOfRangeException;
import superdisk.pdn.structs.ColorBgra;
import superdisk.pdn.structs.ColorBgraPointer;

public class GaussianBlurChange extends PDNChange
{
	public static final GaussianBlurChange instance = new GaussianBlurChange(2);
	
	private int radius;
	private int[] w;
	
	public GaussianBlurChange(int radius)
	{
		if (radius < 0 || radius > 200)
			throw new ArgumentOutOfRangeException ("radius");

		this.radius = radius;
		w = createGaussianBlurRow (radius);
	}
	
	public static int[] createGaussianBlurRow (int amount)
	{
		int size = 1 + (amount * 2);
		int[] weights = new int[size];

		for (int i = 0; i <= amount; ++i) {
			// 1 + aa - aa + 2ai - ii
			weights[i] = 16 * (i + 1);
			weights[weights.length - i - 1] = weights[i];
		}

		return weights;
	}
	
	@Override
	public void renderLine(RawImage src, RawImage dst, Rectangle rect)
	{
		int[] srcBuffer = src.borrowBuffer();
		int[] dstBuffer = dst.borrowBuffer();
		boolean[] mask = src.borrowMask();
		
		if (radius == 0) {
			System.arraycopy(srcBuffer, 0, dstBuffer, 0, dstBuffer.length);
			return;
		}

		int r = radius;
		int wlen = w.length;
		
		final int SIZEOF_LONG = 8;

		int localStoreSize = wlen * 6 * 4; //sizeof (4);
		byte[] localStore = new byte[localStoreSize];
		int p = 0;
		
		ByteBuffer pBuf = ByteBuffer.wrap(localStore);

		//Why..?
		//long* waSums = (long*)p;
		int waSums = p;
		p += wlen * SIZEOF_LONG;

		//long* wcSums = (long*)p;
		int wcSums = p;
		p += wlen * SIZEOF_LONG;

		//long* aSums = (long*)p;
		int aSums = p;
		p += wlen * SIZEOF_LONG;

		//long* bSums = (long*)p;
		int bSums = p;
		p += wlen * SIZEOF_LONG;

		//long* gSums = (long*)p;
		int gSums = p;
		p += wlen * SIZEOF_LONG;

		//long* rSums = (long*)p;
		int rSums = p;
		p += wlen * SIZEOF_LONG;

		int src_width = src.width;
		int src_height = src.height;

			if (rect.height >= 1 && rect.width >= 1) {
				for (int y = rect.y; y <= rect.y+rect.height-1; ++y) {
					//Memory.SetToZero (localStore, (ulong)localStoreSize);

					long waSum = 0;
					long wcSum = 0;
					long aSum = 0;
					long bSum = 0;
					long gSum = 0;
					long rSum = 0;

					ColorBgraPointer dstPtr = new ColorBgraPointer(dst, rect.x, y); //dest.GetPointAddress (rect.Left, y);

					for (int wx = 0; wx < wlen; ++wx) {
						int srcX = rect.x + wx - r;
						localStore[waSums+wx] = 0;
						localStore[wcSums+wx] = 0;
						localStore[aSums+wx] = 0;
						localStore[bSums+wx] = 0;
						localStore[gSums+wx] = 0;
						localStore[rSums+wx] = 0;
						/*waSums[wx] = 0;
						wcSums[wx] = 0;
						aSums[wx] = 0;
						bSums[wx] = 0;
						gSums[wx] = 0;
						rSums[wx] = 0;*/

						if (srcX >= 0 && srcX < src_width) {
							for (int wy = 0; wy < wlen; ++wy) {
								int srcY = y + wy - r;

								if (srcY >= 0 && srcY < src_height) {
									ColorBgra c = ColorBgra.fromInt(src.getPixel (srcX, srcY));
									int wp = w[wy];

									localStore[waSums+wx] += wp; //waSums[wx] += wp;
									wp *= c.getA() + (c.getA() >> 7);
									localStore[wcSums+wx] += wp; //wcSums[wx] += wp;
									wp >>= 8;

									localStore[aSums+wx] += wp * c.getA();
									localStore[bSums+wx] += wp * c.getB();
									localStore[gSums+wx] += wp * c.getG();
									localStore[rSums+wx] += wp * c.getR();
								}
							}

							int wwx = w[wx];
							waSum += wwx * localStore[waSums+wx];
							wcSum += wwx * localStore[wcSums+wx];
							aSum += wwx * localStore[aSums+wx];
							bSum += wwx * localStore[bSums+wx];
							gSum += wwx * localStore[gSums+wx];
							rSum += wwx * localStore[rSums+wx];
						}
					}

					wcSum >>= 8;

					if (waSum == 0 || wcSum == 0) {
						
						//dstPtr->Bgra = 0;
						dstPtr.setBgra(0);
					} else {
						int alpha = (int)(aSum / waSum);
						int blue = (int)(bSum / wcSum);
						int green = (int)(gSum / wcSum);
						int red = (int)(rSum / wcSum);

						dstPtr.setBgra(ColorBgra.bgraToInt(blue, green, red, alpha));//->Bgra = ColorBgra.BgraToUInt32 (blue, green, red, alpha);
					}

					//++dstPtr;
					dstPtr.increment();
					
					for (int x = rect.x + 1; x <= rect.x+rect.width-1; ++x) {
						for (int i = 0; i < wlen - 1; ++i) {
							localStore[waSums+i] = localStore[waSums+i+1];
							localStore[wcSums+i] = localStore[wcSums+i+1];
							localStore[aSums+i] = localStore[aSums+i+1];
							localStore[bSums+i] = localStore[bSums+i+1];
							localStore[gSums+i] = localStore[gSums+i+1];
							localStore[rSums+i] = localStore[rSums+i+1];
							
							/*waSums[i] = waSums[i + 1];
							wcSums[i] = wcSums[i + 1];
							aSums[i] = aSums[i + 1];
							bSums[i] = bSums[i + 1];
							gSums[i] = gSums[i + 1];
							rSums[i] = rSums[i + 1];*/
						}

						waSum = 0;
						wcSum = 0;
						aSum = 0;
						bSum = 0;
						gSum = 0;
						rSum = 0;

						int wx;
						for (wx = 0; wx < wlen - 1; ++wx) {
							long wwx = (long)w[wx];
							waSum += wwx * localStore[waSums+wx];//waSums[wx];
							wcSum += wwx * localStore[wcSums+wx];//wcSums[wx];
							aSum += wwx * localStore[aSums+wx];//aSums[wx];
							bSum += wwx * localStore[bSums+wx];//bSums[wx];
							gSum += wwx * localStore[gSums+wx];//gSums[wx];
							rSum += wwx * localStore[rSums+wx];//rSums[wx];
						}

						wx = wlen - 1;

						
						/*waSums[wx] = 0;
						wcSums[wx] = 0;
						aSums[wx] = 0;
						bSums[wx] = 0;
						gSums[wx] = 0;
						rSums[wx] = 0;*/
						
						localStore[waSums+wx] = 0;
						localStore[wcSums+wx] = 0;
						localStore[aSums+wx] = 0;
						localStore[bSums+wx] = 0;
						localStore[gSums+wx] = 0;
						localStore[rSums+wx] = 0;

						int srcX = x + wx - r;

						if (srcX >= 0 && srcX < src_width) {
							for (int wy = 0; wy < wlen; ++wy) {
								int srcY = y + wy - r;

								if (srcY >= 0 && srcY < src_height) {
									ColorBgra c = ColorBgra.fromInt(src.getPixel (srcX, srcY));
									int wp = w[wy];

									localStore[waSums+wx] += wp;//waSums[wx] += wp;
									wp *= c.getA() + (c.getA() >> 7);
									localStore[wcSums+wx] += wp;//wcSums[wx] += wp;
									wp >>= 8;

									/*aSums[wx] += wp * (long)c.getA();
									bSums[wx] += wp * (long)c.getB();
									gSums[wx] += wp * (long)c.getG();
									rSums[wx] += wp * (long)c.getR();*/
									localStore[aSums+wx] += wp * (long)c.getA();
									localStore[bSums+wx] += wp * (long)c.getB();
									localStore[gSums+wx] += wp * (long)c.getG();
									localStore[rSums+wx] += wp * (long)c.getR();
						
								}
							}

							int wr = w[wx];
							waSum += (long)wr * localStore[waSums+wx];//waSums[wx];
							wcSum += (long)wr * localStore[wcSums+wx];//wcSums[wx];
							aSum += (long)wr * localStore[aSums+wx];//aSums[wx];
							bSum += (long)wr * localStore[bSums+wx];//bSums[wx];
							gSum += (long)wr * localStore[gSums+wx];//gSums[wx];
							rSum += (long)wr * localStore[rSums+wx];//rSums[wx];
						}

						wcSum >>= 8;

						if (waSum == 0 || wcSum == 0) {
							dstPtr.setBgra(0);
							//dstPtr->Bgra = 0;
						} else {
							int alpha = (int)(aSum / waSum);
							int blue = (int)(bSum / wcSum);
							int green = (int)(gSum / wcSum);
							int red = (int)(rSum / wcSum);

							dstPtr.setBgra(ColorBgra.bgraToInt(blue, green, red, alpha));
							//dstPtr->Bgra = ColorBgra.BgraToUInt32 (blue, green, red, alpha);
						}

						dstPtr.increment();
						//++dstPtr;
					}
				}
		}
	}

	@Override
	public SingleChange getInstance()
	{
		return instance;
	}

}
