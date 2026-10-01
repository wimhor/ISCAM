/*
** Cell.java: Class definition for an individual cell in the ISCAM.
**
** Wim Hordijk   Last modified: 03 December 2009
*/

package ISCAM;

import java.awt.Graphics;
import java.awt.Color;


/*
** Cell: An individual cell in the ISCAM.
*/

class Cell
{
  private double S, R;
  private int    row, col;

  /*
  ** Constructor.
  */
  Cell (int i, int j)
  {
    row = i;
    col = j;
    S = 0.0;
    R = 0.0;
  }

  /*
  ** getS: Get the current value of S.
  */
  public double getS ()
  {
    return S;
  }

  /*
  ** getR: Get the current value of R.
  */
  public double getR ()
  {
    return R;
  }

  /*
  ** setState: Set the state of the cell.
  */
  public void setState (double newS, double newR)
  {
    S = newS;
    R = newR;
  }

  /*
  ** draw: Draw the current state of the cell.
  **
  ** Parameters:
  **   - g:    The Graphics object to draw on.
  **   - size: The size of the cell to draw (for drawing the grid), or the
  **           size of the drawing panel (for drawing the phase space).
  **   - ca:   The CA this cell is part of.
  */
  public void draw (Graphics g, int size, CA ca)
  {
    int     x, y, i;
    double  relS, relR, phi;
    Color   color;
    CAColor caColor;

    caColor = ca.getCAColor ();
    switch (ca.getDrawState ())
    {
      /*
      ** Draw S.
      */
      case CA.DRAW_S:
	//x = (size*col) + 1;
	//y = (size*row) + 1;
	x = (size*col);
	y = (size*row);
	i = (int)((S/2.0) * caColor.NR_COLORS);   // For S in [0.0;2.0]
	if (i < 0)
	{
	  i = 0;
	}
	if (i >= caColor.NR_COLORS)
	{
	  i = caColor.NR_COLORS - 1;
	}
	color = caColor.getColorS (i);
	g.setColor (color);
	//g.fillRect (x, y, size-1, size-1);  // With grid lines.
	g.fillRect (x, y, size, size);  // Without grid lines.
	break;
      /*
      ** Draw R.
      */
      case CA.DRAW_R:
	//x = (size*col) + 1;
	//y = (size*row) + 1;
	x = (size*col);
	y = (size*row);
	i = (int)(R * caColor.NR_COLORS);    // For R in [0.0;1.0]
	if (i < 0)
	{
	  i = 0;
	}
	if (i >= caColor.NR_COLORS)
	{
	  i = caColor.NR_COLORS;
	}
	color = caColor.getColorR (i);
	g.setColor (color);
	//g.fillRect (x, y, size-1, size-1);
	g.fillRect (x, y, size, size);
	break;
      /*
      ** Draw S and R.
      */
      case CA.DRAW_SR:
	//x = (size*col) + 1;
	//y = (size*row) + 1;
	x = (size*col);
	y = (size*row);
	relS = S - ca.getFixedPointS ();
	relR = R - ca.getFixedPointR ();
	phi = Math.atan2 (relR, relS);
	if (phi < 0.0)
	{
	  phi = (2.0*Math.PI) + phi;
	}
	i = (int)((phi / (2.0*Math.PI)) * caColor.NR_COLORS);
	if (i < 0)
	{
	  i = 0;
	}
	if (i >= caColor.NR_COLORS)
	{
	  i = caColor.NR_COLORS;
	}
	color = caColor.getColorS (i);
	g.setColor (color);
	//g.fillRect (x, y, size-1, size-1);
	g.fillRect (x, y, size, size);
	break;
      /*
      ** Draw S vs. R.
      */
      case CA.DRAW_SvsR:
	x = (int)((S/2.0) * size);
	y = size - (int)(R * size);
	if ((row == 0) && (col == 0))
	{
	  g.setColor (Color.red);
	  g.fillRect (x-2, y-2, 5, 5);
	}
	else
	{
	  g.setColor (Color.black);
	  g.drawLine (x, y, x, y);
	}
	break;
      default:
	// Do nothing.
    }
  }
}


/*
** EoF: Cell.java
*/
