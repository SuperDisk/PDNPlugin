// {LICENSE}
/*
 * Copyright 2013-2014 HeroesGrave and other Spade developers.
 * 
 * This file is part of Spade
 * 
 * Spade is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package superdisk.pdn.pdnplugin;

import superdisk.pdn.pdnplugin.changes.BulgeChange;
import superdisk.pdn.pdnplugin.changes.EmbossChange;
import superdisk.pdn.pdnplugin.changes.PixelateChange;
import superdisk.pdn.pdnplugin.changes.TileChange;
import superdisk.pdn.pdnplugin.changes.TwistChange;
import heroesgrave.spade.editing.SimpleEffect;
import heroesgrave.spade.plugin.Plugin;
import heroesgrave.spade.plugin.Registrar;

public class PDNPlugin extends Plugin
{
	public static void main(String[] args)
	{
		launchSpadeWithPlugins(args, false, new PDNPlugin());
	}
	
	@Override
	public void load()
	{
		
	}
	
	@Override
	public void register(Registrar registrar)
	{
		/*registrar.registerTool(new Line("Line"), 'L');
		
		registrar.registerOperation(new ResizeImageOp("Resize Image"), 'R');
		registrar.registerOperation(new ResizeCanvasOp("Resize Canvas"), null);
		registrar.registerOperation(new SimpleEffect(PDNPlugin.class, "Flip Vertically", FlipVertChange.instance), 'V');
		registrar.registerOperation(new SimpleEffect(PDNPlugin.class, "Flip Horizontally", FlipHorizChange.instance), 'H');
		
		registrar.registerEffect(new GreyscaleEffect("Greyscale"), 'G');
		registrar.registerEffect(new SimpleEffect(PDNPlugin.class, "Invert Colour", InvertChange.instance), 'I');
		registrar.registerEffect(new SimpleEffect(PDNPlugin.class, "Sepia", SepiaChange.instance), null);
		
		registrar.registerSerialiser(LineChange.class);
		registrar.registerSerialiser(RectChange.class);
		registrar.registerSerialiser(FillRectChange.class);
		registrar.registerSerialiser(FloodPathChange.class);
		registrar.registerSerialiser(GlobalFloodPathChange.class);
		registrar.registerSerialiser(FloodSelectChange.class);
		registrar.registerSerialiser(PixelChange.class);
		registrar.registerSerialiser(MaskRectChange.class);
		registrar.registerSerialiser(MoveChange.class);
		
		registrar.registerSerialiser(FlipVertChange.class);
		registrar.registerSerialiser(FlipHorizChange.class);
		registrar.registerSerialiser(ResizeImageChange.class);
		
		registrar.registerSerialiser(InvertChange.class);
		registrar.registerSerialiser(GreyscaleChange.class);
		registrar.registerSerialiser(TrueGreyscaleChange.class);
		registrar.registerSerialiser(SepiaChange.class);
		
		registrar.registerBlendMode(new Replace());
		registrar.registerBlendMode(new AlphaTestReplace());
		
		registrar.registerExporter(new ExporterGenericImageIO("bmp", "BMP - Microsoft Bitmap Image"));
		registrar.registerExporter(new ExporterGenericImageIO("gif", "GIF - Graphics Interchange Format"));
		registrar.registerExporter(new ExporterJPEG());
		registrar.registerExporter(new ExporterTGA());
		registrar.registerExporter(new ExporterSPD());
		registrar.registerExporter(new ExporterORA());
		
		registrar.registerImporter(new ImporterSPD());
		registrar.registerImporter(new ImporterORA());*/
		
		//registrar.registerEffect(new BulgeEffect("Bulge"), null);
		registrar.registerEffect(new SimpleEffect(PDNPlugin.class, "Emboss", EmbossChange.instance), null);
		registrar.registerEffect(new SimpleEffect(PDNPlugin.class, "Pixelate", PixelateChange.instance), null);
		registrar.registerEffect(new SimpleEffect(PDNPlugin.class, "Tile Reflection", TileChange.instance), null);
		registrar.registerEffect(new SimpleEffect(PDNPlugin.class, "Bulge", BulgeChange.instance), null);
		registrar.registerEffect(new SimpleEffect(PDNPlugin.class, "Twist", TwistChange.instance), null);
	}
}
