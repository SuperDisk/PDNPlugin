PDNPlugin
=========

This is a Spade plugin that (will) contain all of the Paint.NET effects, ported straight from its C#.

What's done
===========
* Bulge
* Emboss
* Pixelate
* Tile Reflection
* Twist (Sort of.)
* Julia Fractal
* Mandelbrot Fractal
* Motion Blur
* Zoom Blur

What needs to be done
=====================
* Implement some faster structs. Using the standard Java Point2D, Rectangle, etc. currently.
* Speed the darn thing up. The effects are slow as crap. I suspect it has to do more with C#'s ability to have `unsafe` sections than the slowness of my code. C# is fast. Java... ehh....
* Implement the GUI portions of the effects. Right now they're put into use just as a SimpleEffect with no editable parameters during runtime. I dislike doing GUI, and I especially dislike WebLaF, so I'm saving this for last.
* Go through and make sure each effect is true to their Paint.NET counterpart. I'm using Pinta.ImageManipulation as a reference point at the moment, since their code is actually just lightly modified from the Paint.NET stuff to be more clear, but I've found at least once instance where the code differs more than just a little. Shouldn't be too big of a deal.