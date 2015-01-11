package superdisk.pdn.structs;

import superdisk.pdn.pdnplugin.ArgumentOutOfRangeException;

public class ColorBgra
{

	private char b, g, r, a;
	
	public static final int BlueChannel = 0;
    public static final int GreenChannel = 1;
    public static final int RedChannel = 2;
    public static final int AlphaChannel = 3;
    public static final int SizeOf = 4;
	
	public ColorBgra parseHexString(String hexString)
	{
		return ColorBgra.fromInt(Integer.parseInt(hexString));
	}
	
	public String toHexString()
	{
		return Integer.toHexString(bgraToInt(b, g, r, a));
	}
	
	public static ColorBgra fromBgra(int b, int g, int r, int a)
	{
		ColorBgra color = new ColorBgra();
		color.setB((char)b);
		color.setG((char)g);
		color.setR((char)r);
		color.setA((char)a);
		return color;
	}
	
	public static ColorBgra fromBgra(char b, char g, char r, char a)
	{
		ColorBgra color = new ColorBgra();
		color.setB(b);
		color.setG(g);
		color.setR(r);
		color.setA(a);
		return color;
	}
	
	public void setB(char b)
	{
		this.b = b;
	}
	
	public void setG(char g)
	{
		this.g = g;
	}
	
	public void setR(char r)
	{
		this.r = r;
	}
	
	public void setA(char a)
	{
		this.a = a;
	}
	
	public void setBgra(int bgra)
	{
		b = (char)((bgra) & 0xFF);
		g = (char)((bgra >> 8) & 0xFF);
		r = (char)((bgra >> 16) & 0xFF);
		a = (char)((bgra >> 24) & 0xFF);
	}
	
	public char getB()
	{
		return b;
	}
	
	public char getG()
	{
		return g;
	}
	
	public char getR()
	{
		return r;
	}
	
	public char getA()
	{
		return a;
	}
	
	public int getBgra()
	{
		return ColorBgra.bgraToInt(b, g, r, a);
	}
	
	public double getIntensity()
	{
		return ((0.114 * (double)b) + (0.587 * (double)g) + (0.299 * (double)r)) / 255.0;
	}
	
	public char getIntensityByte()
    {
        return (char)((7471 * b + 38470 * g + 19595 * r) >> 16);
    }
	
	public char getMaxColorChannelValue()
    {
        return (char)Math.max(this.b, Math.max(this.g, this.r));
    }
	
	public char getAverageColorChannelValue()
    {
        return (char)((this.b + this.g + this.r) / 3);
    }
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof ColorBgra)
		{
			ColorBgra other = (ColorBgra)o;
			return (other.getR() == r) && (other.getG() == g) && (other.getB() == b) && (other.getA() == a);
		} else return false;
	}
	
	@Override
	public int hashCode()
	{
		return ColorBgra.bgraToInt(b, g, r, a);
	}
	
	public ColorBgra newAlpha(char newA)
	{
		return ColorBgra.fromBgra(b, g, r, newA);
	}
	
	public static int bgraToInt(char b, char g, char r, char a)
	{
		return (int)b + ((int)g << 8) + ((int)r << 16) + ((int)a << 24);
	}
	
	public static int bgraToInt(int b, int g, int r, int a)
	{
		return (int)b + ((int)g << 8) + ((int)r << 16) + ((int)a << 24);
	}
	
	public static ColorBgra fromBgr(char b, char g, char r)
	{
		return ColorBgra.fromBgra(b, g, r, (char)255);
	}
	
	public static ColorBgra fromInt(int bgra)
	{
		ColorBgra color = new ColorBgra();
		color.setBgra(bgra);
		return color;
	}
	
	//What crap. The original function used ulong, which scared me.
	//Changing them to long works just fine.
	//WWHHAGHHAGHSAHDGLIH
	public static ColorBgra blend(ColorBgra[] colors, int count)
    {
		if (count < 0)
        {
            throw new ArgumentOutOfRangeException("count must be 0 or greater");
        }

        if (count == 0)
        {
            return ColorBgra.Transparent;
        }

        long aSum = 0;

        for (int i = 0; i < count; ++i)
        {
            aSum += (long)colors[i].getA();
        }

        char b = 0;
        char g = 0;
        char r = 0;
        char a = (char)(aSum / (long)count);

        if (aSum != 0)
        {
            long bSum = 0;
            long gSum = 0;
            long rSum = 0;

            for (int i = 0; i < count; ++i)
            {
                bSum += (long)(colors[i].getA() * colors[i].getB());
                gSum += (long)(colors[i].getA() * colors[i].getG());
                rSum += (long)(colors[i].getA() * colors[i].getR());
            }

            b = (char)(bSum / aSum);
            g = (char)(gSum / aSum);
            r = (char)(rSum / aSum);
        }

        return ColorBgra.fromBgra(b, g, r, a);
    }
	
	public static ColorBgra BlendColors4W16IP(ColorBgra c1, long w1, ColorBgra c2, long w2, ColorBgra c3, long w3, ColorBgra c4, long w4)
    {
        final long ww = 32768;
        long af = (c1.getA() * w1) + (c2.getA() * w2) + (c3.getA() * w3) + (c4.getA() * w4);
        long a = (af + ww) >> 16;

        long b;
        long g;
        long r;

        if (a == 0)
        {
            b = 0;
            g = 0;
            r = 0;
        }
        else
        {
            b = (long)((((long)c1.getA() * c1.getB() * w1) + ((long)c2.getA() * c2.getB() * w2) + ((long)c3.getA() * c3.getB() * w3) + ((long)c4.getA() * c4.getB() * w4)) / af);
            g = (long)((((long)c1.getA() * c1.getG() * w1) + ((long)c2.getA() * c2.getG() * w2) + ((long)c3.getA() * c3.getG() * w3) + ((long)c4.getA() * c4.getG() * w4)) / af);
            r = (long)((((long)c1.getA() * c1.getR() * w1) + ((long)c2.getA() * c2.getR() * w2) + ((long)c3.getA() * c3.getR() * w3) + ((long)c4.getA() * c4.getR() * w4)) / af);
        }

        return ColorBgra.fromBgra((char)b, (char)g, (char)r, (char)a);
    }
	
	public static final ColorBgra Transparent = ColorBgra.fromBgra (255, 255, 255, 0);

	public static final ColorBgra Black = ColorBgra.fromBgra (0, 0, 0, 255);
	public static final ColorBgra Blue = ColorBgra.fromBgra (255, 0, 0, 255);
	public static final ColorBgra Cyan = ColorBgra.fromBgra (255, 255, 0, 255);
	public static final ColorBgra Green =  ColorBgra.fromBgra (0, 128, 0, 255);
	public static final ColorBgra Magenta = ColorBgra.fromBgra (255, 0, 255, 255);
	public static final ColorBgra Red = ColorBgra.fromBgra (0, 0, 255, 255);
	public static final ColorBgra White = ColorBgra.fromBgra (255, 255, 255, 255);
	public static final ColorBgra Yellow = ColorBgra.fromBgra (0, 255, 255, 255);
}
