/*
** CA: Class definition for the Ideal Storage Cellular Automaton.
**
** Wim Hordijk   Last modified: 05 February 2010
*/

package ISCAM;

import java.awt.Graphics;
import java.util.Random;


/*
** CA: The Ideal Storage Cellular Automaton.
*/

class CA
{
  public static final int DRAW_S  = 0;
  public static final int DRAW_R  = 1;
  public static final int DRAW_SR = 2;
  public static final int DRAW_SvsR = 3;

  private CAColor caColor;
  private Cell    grid[][], tmpGrid[][];
  private int     nrRows, nrCols, drawState, rSeed;
  private Dynamic dynamicS, dynamicR;
  private Random  rnd;
  private double  p, fixedPointS, fixedPointR, noiseRange;
  private boolean diffusionNoise;

  /*
  ** Constructor.
  */
  CA (int rows, int cols, Dynamic dynS, Dynamic dynR)
  {
    int i, j;

    /*
    ** Initialize everything.
    */
    nrRows = rows;
    nrCols = cols;
    grid = new Cell[rows][cols];
    tmpGrid = new Cell[rows][cols];
    for (i = 0; i < rows; i++)
    {
      for (j = 0; j < cols; j++)
      {
	grid[i][j] = new Cell (i, j);
	tmpGrid[i][j] = new Cell (i, j);
      }
    }
    dynamicS = dynS;
    dynamicR = dynR;
    fixedPointS = dynS.fixedPoint (dynR);
    fixedPointR = dynR.fixedPoint (dynS);
    rnd = new Random ();
    rSeed = 0;
    p = 0.2;
    diffusionNoise = false;
    noiseRange = 0.2;
    drawState = DRAW_S;
    caColor = new CAMonoColor ();
  }

  /*
  ** getNrRows: Get the number of rows in the grid.
  */
  public int getNrRows ()
  {
    return (nrRows);
  }

  /*
  ** getNrCols: Get the number of colums in the grid.
  */
  public int getNrCols ()
  {
    return (nrCols);
  }

  /*
  ** setSize: Set the number of rows and columns in the grid.
  */
  public void setSize (int rows, int cols)
  {
    int i, j;

    /*
    ** Recreate the grid.
    */
    nrRows = rows;
    nrCols = cols;
    grid = new Cell[rows][cols];
    tmpGrid = new Cell[rows][cols];
    for (i = 0; i < rows; i++)
    {
      for (j = 0; j < cols; j++)
      {
	grid[i][j] = new Cell (i, j);
	tmpGrid[i][j] = new Cell (i, j);
      }
    }
  }

  /*
  ** getDynamicS: Get the current dynamic for S.
  */
  public Dynamic getDynamicS ()
  {
    return (dynamicS);
  }

  /*
  ** getDynamicR: Get the current dynamic for R.
  */
  public Dynamic getDynamicR ()
  {
    return (dynamicR);
  }

  /*
  ** setDynamicS: Set the dynamic for S.
  */
  public void setDynamicS (Dynamic dynS)
  {
    dynamicS = dynS;
  }

  /*
  ** setDynamicR: Set the dynamic for R.
  */
  public void setDynamicR (Dynamic dynR)
  {
    dynamicR = dynR;
  }

  /*
  ** getP: Get the value of p (the "diffusion rate").
  */
  public double getP ()
  {
    return (p);
  }

  /*
  ** setP: Set the value of p (in [0.0;1.0]).
  **
  ** Parameters:
  **   - newP: The new value of p.
  */
  public void setP (double newP)
  {
    p = newP;
    if (p < 0.0)
    {
      p = 0.0;
    }
    if (p > 1.0)
    {
      p = 1.0;
    }
  }

  /*
  ** getDiffusionNoise: Get the diffusion noise setting.
  **
  ** Returns:
  **   True or false, according to whether diffusion noise is on or not.
  */
  public boolean getDiffusionNoise ()
  {
    return (diffusionNoise);
  }

  /*
  ** setDiffusionNoise: Set the diffusion noise on or off.
  **
  ** Parameters:
  **   dNoise: The diffusion noise setting (true or false).
  */
  public void setDiffusionNoise (boolean dNoise)
  {
    diffusionNoise = dNoise;
  }

  /*
  ** getNoiseRange: Get the diffusion noise range.
  **
  ** Returns:
  **   The current diffusion noise range.
  */
  public double getNoiseRange ()
  {
    return (noiseRange);
  }

  /*
  ** setNoiseRange: Set the diffusion noise range.
  **
  ** Parameters:
  **   - r: The noise range to set (in [0.0;1.0]).
  */
  public void setNoiseRange (double r)
  {
    noiseRange = r;
    if (noiseRange < 0.0)
    {
      noiseRange = 0.0;
    }
    if (noiseRange > 1.0)
    {
      noiseRange = 1.0;
    }
  }

  /*
  ** getDrawState: Get the current draw state.
  */
  public int getDrawState ()
  {
    return (drawState);
  }

  /*
  ** setDrawState: Set the current draw state.
  **
  ** Parameters:
  **   - dState: The draw state to set (must be DRAW_S, DRAW_R, DRAW_SR,
  **             or DRAW_SvsR).
  */
  public void setDrawState (int dState)
  {
    if ((dState == DRAW_S) || (dState == DRAW_R) || (dState == DRAW_SR) ||
	(dState == DRAW_SvsR))
    {
      drawState = dState;
    }
  }

  /*
  ** getSeed: Get the current random seed.
  */
  public int getSeed ()
  {
    return (rSeed);
  }

  /*
  ** setSeed: Set the random seed.
  **
  ** Parameters:
  **   - s: The random seed to set.
  */
  public void setSeed (int s)
  {
    rSeed = s;
    if (rSeed < 0)
    {
      rSeed = 0;
    }
  }

  /*
  ** getCAColor: Get a reference to the color map.
  */
  CAColor getCAColor ()
  {
    return (caColor);
  }

  /*
  ** setCAColor: Set the color map.
  **
  ** Parameters:
  **   - map: The color map to set.
  */
  public void setCAColor (int map)
  {
    switch (map)
    {
      case CAColor.MONOCHROME:
	caColor = new CAMonoColor ();
	break;
      case CAColor.RAINBOW:
	caColor = new CARainbowColor ();
	break;
      default:
    }
  }

  /*
  ** getFixedPointS: Get the fixed point of the S dynamic.
  */
  public double getFixedPointS ()
  {
    return (fixedPointS);
  }

  /*
  ** getFixedPointR: Get the fixed point of the R dynamic.
  */
  public double getFixedPointR ()
  {
    return (fixedPointR);
  }

  /*
  ** getCell: Get a reference to a cell.
  **
  ** Parameters:
  **   - i: The row number of the cell.
  **   - j: The column number of the cell.
  **
  ** Returns:
  **   The cell at position (i, j).
  */
  public Cell getCell (int i, int j)
  {
    if ((i >= 0) && (i < nrRows) && (j >= 0) && (j < nrCols))
    {
      return (grid[i][j]);
    }
    else
    {
      return null;
    }
  }

  /*
  ** init: Initialize the CA at random.
  */
  public void init ()
  {
    int    i, j;
    double s, r;

    /*
    ** Randomly initialize each cell.
    */
    if (rSeed > 0)
    {
      rnd.setSeed (rSeed);
    }
    for (i = 0; i < nrRows; i++)
    {
      for (j = 0; j < nrCols; j++)
      {
	s = 2.0*rnd.nextDouble ();   // Random value in [0.0;2.0)
	r = rnd.nextDouble ();       // Random value in [0.0;1.0)
	grid[i][j].setState (s, r);
      }
    }
  }

  /*
  ** step: Perform one update step of the entire CA grid.
  */
  public void step ()
  {
    int    i, j, nbrI, nbrJ;
    double newS, newR, nbhR, diff;

    /*
    ** Add diffusion noise (if on).
    */
    if (diffusionNoise)
    {
      diff = p + (2.0 * noiseRange * rnd.nextDouble ()) - noiseRange;
      if (diff < 0.0)
      {
	diff = 0.0;
      }
      if (diff > 1.0)
      {
	diff = 1.0;
      }
    }
    else
    {
      diff = p;
    }
    //System.out.println ("diff=" + diff);
    /*
    ** Update the cell states.
    */
    for (i = 0; i < nrRows; i++)
    {
      for (j = 0; j < nrCols; j++)
      {
	/*
	** Calculate the new S value.
	*/
	newS = dynamicS.calc (grid[i][j].getS (), grid[i][j].getR ());
        /*
	** Calculate the average (Moore) neighborhood R value.
	*/
        nbhR = 0.0;
	for (nbrI = i-1; nbrI <= i+1; nbrI++)
	{
	  for (nbrJ = j-1; nbrJ <= j+1; nbrJ++)
	  {
	    if ((nbrI != i) || (nbrJ != j))
	    {
	      nbhR += grid[(nbrI+nrRows)%nrRows][(nbrJ+nrCols)%nrCols].getR ();
	    }
	  }
	}
	nbhR /= 8.0;
	/*
	** Calculate the new R value.
	*/
	newR = dynamicR.calc (grid[i][j].getS (),
			      ((1-diff)*grid[i][j].getR ()) + (diff*nbhR));
	/*
	** Store the new cell state.
	*/
	tmpGrid[i][j].setState (newS, newR);
      }
    }
    /*
    ** Copy the new cell states to the current grid.
    */
    for (i = 0; i < nrRows; i++)
    {
      for (j = 0; j < nrCols; j++)
      {
	grid[i][j].setState (tmpGrid[i][j].getS (), tmpGrid[i][j].getR ());
      }
    }
  }

  /*
  ** draw: Draw the current CA state.
  **
  ** Parameters:
  **   - g:    The Graphics object to draw on.
  **   - size: The size of the cells to draw (for the grid) or the size of
  **           the drawing pane (for the phase space).
  */
  public void draw (Graphics g, int size)
  {
    int i, j;

    /*
    ** Recalculate the fixed point if necessary.
    */
    if ((drawState == DRAW_SR) || (drawState == DRAW_SvsR))
    {
      fixedPointS = dynamicS.fixedPoint (dynamicR);
      fixedPointR = dynamicR.fixedPoint (dynamicS);
    }

    /*
    ** Draw the individual cells.
    */
    for (i = 0; i < nrRows; i++)
    {
      for (j = 0; j < nrCols; j++)
      {
	grid[i][j].draw (g, size, this);
      }
    }
    if (drawState == DRAW_SvsR)
    {
      grid[0][0].draw (g, size, this);
    }
  }
}


/*
** EoF: CA.java
*/
