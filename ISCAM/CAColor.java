/*
** CAColor.java: Class for defining colors for drawing the CA grid.
**
** Wim Hordijk   Last modified: 15 January 2010
*/

package ISCAM;

import java.awt.Color;


/*
** CAColor: The CA color class.
*/

abstract class CAColor
{
  public static final int MONOCHROME = 0;
  public static final int RAINBOW    = 1;
  public static final int NR_COLORS  = 1024;

  protected Color colorS[], colorR[];

  /*
  ** Constructor.
  */
  CAColor ()
  {
    colorS = new Color[NR_COLORS];
    colorR = new Color[NR_COLORS];
  }

  /*
  ** getColorS: Get an S color.
  **
  ** Parameters:
  **   - i: The index of the color to get.
  **
  ** Returns:
  **   The requested color.
  */
  Color getColorS (int i)
  {
    if ((i >= 0) && (i < NR_COLORS))
    {
      return (colorS[i]);
    }
    else
    {
      return (Color.black);
    }
  }

  /*
  ** getColorR: Get an R color.
  **
  ** Parameters:
  **   - i: The index of the color to get.
  **
  ** Returns:
  **   The requested color.
  */
  Color getColorR (int i)
  {
    if ((i >= 0) && (i < NR_COLORS))
    {
      return (colorR[i]);
    }
    else
    {
      return (Color.black);
    }
  }
}


/*
** CAMonoColor: The CA monochrome color class.
*/

class CAMonoColor extends CAColor
{
  /*
  ** Constructor.
  */
  CAMonoColor ()
  {
    super ();

    int i, c;

    /*
    ** Initialize the colors.
    */
    for (i = 0; i < NR_COLORS; i++)
    {
      c = (int)(i * (256.0/NR_COLORS));
      colorS[i] = new Color (c, 0, 0);
      colorR[i] = new Color (0, 0, c);
    }
  }
}


/*
** CARainbowColor: The CA rainbow color class.
*/

class CARainbowColor extends CAColor
{
  /*
  ** Constructor.
  */
  CARainbowColor ()
  {
    super ();

    int    i, red, green, blue;
    double waveLen, step, r, g, b;
    Color  c;

    /*
    ** Initialize the colors.
    */
    c = new Color (0, 0, 0);
    colorS[0] = c;
    colorR[0] = c;
    step = (680.0 - 380.0) / (NR_COLORS-1);
    r = 0.0;
    g = 0.0;
    b = 0.0;
    for (i = 1; i < NR_COLORS; i++)
    {
      waveLen = 380.0 + (i*step);
      if ((waveLen >= 380.0) && (waveLen < 440.0))
      {
        r = -(waveLen - 440.0) / (440.0 - 380.0);
        g = 0.0;
        b = 1.0;
      }
      else if ((waveLen >= 440.0) && (waveLen < 490.0))
      {
        r = 0.0;
        g = (waveLen - 440.0) / (490.0 - 440.0);
        b = 1.0;
      }
      else if ((waveLen >= 490.0) && (waveLen < 510.0))
      {
        r = 0.0;
        g = 1.0;
        b = -(waveLen - 510.0) / (510.0 - 490.0);
      }
      else if ((waveLen >= 510.0) && (waveLen < 580.0))
      {
        r = (waveLen - 510.0) / (580.0 - 510.0);
        g = 1.0;
        b = 0.0;
      }
      else if ((waveLen >= 580.0) && (waveLen < 645.0))
      {
	r = 1.0;
        g = -(waveLen - 645.0) / (645.0 - 580.0);
        b = 0.0;
      }
      else if ((waveLen >= 645.0) && (waveLen <= 680.0))
      {
        r = 1.0;
        g = 0.0;
        b = 0.0;
      }
      red = (int)(r * 255);
      green = (int)(g * 255);
      blue = (int)(b * 255);
      c = new Color (red, green, blue);
      colorS[i] = c;
      colorR[i] = c;
    }
  }
}


/*
** EoF: CAColor.java
*/
