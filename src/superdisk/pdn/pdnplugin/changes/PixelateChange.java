package superdisk.pdn.pdnplugin.changes;

import heroesgrave.spade.image.RawImage;
import superdisk.pdn.ArgumentOutOfRangeException;
import heroesgrave.spade.image.change.SingleChange;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import superdisk.pdn.structs.ColorBgra;

public class PixelateChange extends PDNChange
{
	public static PixelateChange instance = new PixelateChange(10);
	
	private int cell_size;

	public PixelateChange(int cellSize)
	{	
		if (cellSize < 0 || cellSize > 100)
			throw new ArgumentOutOfRangeException("cellSize");
		
		this.cell_size = cellSize;
	}

	@Override
	public void renderLine(RawImage src, RawImage dst, Rectangle rect)
	{
		int[] dstBuffer = dst.borrowBuffer();
		boolean[] mask = src.borrowMask();
		
		for (int y = rect.y; y <= rect.y+rect.height-1; ++y) {
			int yEnd = y + 1;

			for (int x = rect.x; x <= rect.x+rect.width-1; ++x) {
				Rectangle cellRect = getCellBox (x, y, cell_size);
				cellRect = cellRect.intersection(new Rectangle(0, 0, dst.width, dst.height)); //bounds
				//cellRect.Intersect(dest.Bounds);
				ColorBgra color = computeCellColor (x, y, src, cell_size, new Rectangle(0, 0, src.width, src.height));

				int xEnd = Math.min(rect.x+rect.width-1, cellRect.x+cellRect.width-1);
				yEnd = Math.min(rect.y+rect.height-1, cellRect.y+cellRect.height-1);

				for (int y2 = y; y2 <= yEnd; ++y2) {
					int ptr = dst.getIndex(x, y2);
					for (int x2 = x; x2 <= xEnd; ++x2) {
						if (mask == null || mask[ptr])
							dstBuffer[ptr] = color.getBgra();
						++ptr;
					}
				}

				x = xEnd;
			}

			y = yEnd;
		}
	}
	
	private ColorBgra computeCellColor (int x, int y, RawImage src, int cellSize, Rectangle srcBounds)
	{
		Rectangle cell = getCellBox (x, y, cellSize);
		cell = cell.intersection(srcBounds);

		int left = cell.x;
		int right = cell.x + cell.width - 1;
		int bottom = cell.y + cell.height - 1;
		int top = cell.y;

		ColorBgra colorTopLeft = ColorBgra.fromInt(src.getPixel (left, top));
		ColorBgra colorTopRight = ColorBgra.fromInt(src.getPixel (right, top));
		ColorBgra colorBottomLeft = ColorBgra.fromInt(src.getPixel (left, bottom));
		ColorBgra colorBottomRight = ColorBgra.fromInt(src.getPixel (right, bottom));

		ColorBgra c = ColorBgra.BlendColors4W16IP (colorTopLeft, 16384, colorTopRight, 16384, colorBottomLeft, 16384, colorBottomRight, 16384);

		return c;
	}
	
	private Rectangle getCellBox (int x, int y, int cellSize)
	{
		int widthBoxNum = x % cellSize;
		int heightBoxNum = y % cellSize;
		Point leftUpper = new Point (x - widthBoxNum, y - heightBoxNum);

		Rectangle returnMe = new Rectangle (leftUpper, new Dimension(cellSize, cellSize));

		return returnMe;
	}

	@Override
	public SingleChange getInstance()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
