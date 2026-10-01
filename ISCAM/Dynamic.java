/*
** Dynamic: The dynamic for the ISCAM.
**
** Wim Hordijk   Last modified: 13 May 2010
*/

package ISCAM;


/*
** Dynamic: The abstract dynamic class.
*/

abstract class Dynamic
{
  protected int    nrFunctions, currentFunction;
  protected double controlParam;

  /*
  ** Constructor.
  */
  Dynamic (double param)
  {
    controlParam = param;
  }

  /*
  ** GetNrFunctions: Return the number of functions available for the dynamic.
  */
  public int getNrFunctions ()
  {
    return (nrFunctions);
  }

  /*
  ** getDescription: To be implemented by the subclasses.
  */
  abstract String getDescription (int i);

  /*
  ** getParam: Get the current control parameter value.
  */
  public double getParam ()
  {
    return (controlParam);
  }

  /*
  ** setParam: Set the control parameter.
  */
  public void setParam (double param)
  {
    controlParam = param;
  }

  /*
  ** getFunction: Get the index of the function currently used.
  */
  public int getFunction ()
  {
    return (currentFunction);
  }

  /*
  ** setFunction: Set the index of the function currently used.
  */
  public void setFunction (int i)
  {
    if ((i >= 0) && (i < nrFunctions))
    {
      currentFunction = i;
    }
  }

  /*
  ** fixedPoint: To be implemented by the subclasses.
  */
  abstract double fixedPoint (Dynamic x);

  /*
  ** calc: To be implemented by the subclasses.
  */
  abstract double calc (double S, double R);
}


/*
** DynamicS: The possible dynamics for S.
*/

class DynamicS extends Dynamic
{
  /*
  ** Constructor.
  */
  DynamicS (double param)
  {
    super (param);
    nrFunctions = 1;
    currentFunction = 0;
  }

  /*
  ** getDescription: Get a short description of the i'th function.
  **
  ** Parameters:
  **   - i: The index of the function for which to get a description.
  */
  public String getDescription (int i)
  {
    String s;

    switch (i)
    {
      case 0:
	s = new String ("S - RS + C");
	break;
      default:
	s = null;
    }

    return (s);
  }

  /*
  ** fixedPoint: Calculate the fixed point for the S dynamic.
  **
  ** Parameters:
  **   dynR: A reference to the R dynamic.
  */
  public double fixedPoint (Dynamic dynR)
  {
    double val, fpR;

    fpR = dynR.fixedPoint (this);
    switch (currentFunction)
    {
      case 0:
	val =  controlParam / fpR;
	break;
      default:
	val = 0.0;
    }

    return (val);
  }

  /*
  ** calc: Calculate the new S value according to the dynamic.
  */
  public double calc (double S, double R)
  {
    double val;

    switch (currentFunction)
    {
      case 0:
	val =  S - R*S + controlParam;
	break;
      default:
	val = 0.0;
    }

    return (val);
  }
}


/*
** DynamicR: The possible dynamics for R.
*/

class DynamicR extends Dynamic
{
  /*
  ** Constructor.
  */
  DynamicR (double param)
  {
    super (param);
    nrFunctions = 2;
    currentFunction = 0;
  }

  /*
  ** getDescription: Get a short description of the i'th function.
  **
  ** Parameters:
  **   - i: The index of the function for which to get a description.
  */
  public String getDescription (int i)
  {
    String s;

    switch (i)
    {
      case 0:
	s = new String ("C + (1-C)(RS^2/(1+RS^2))");
	break;
      case 1:
	s = new String ("1 / (1 + exp(-5RS+C))");
	break;
      default:
	s = null;
    }

    return (s);
  }

  /*
  ** fixedPoint: Calculate the fixed point for the R dynamic.
  **
  ** Parameters:
  **   dynS: A reference to the S dynamic.
  */
  public double fixedPoint (Dynamic dynS)
  {
    double val, cS;

    cS = dynS.getParam ();
    switch (currentFunction)
    {
      case 0:
	val = controlParam + ((1-controlParam)*((cS*cS)/(1+(cS*cS))));
	break;
      case 1:
	val = (1.0 / (1.0 + Math.exp (-5.0*cS + controlParam)));
	break;
      default:
	val = 0.0;
    }

    return (val);
  }

  /*
  ** calc: Calculate the new R value according to the dynamic.
  */
  public double calc (double S, double R)
  {
    double x, val;

    x = R * S;
    switch (currentFunction)
    {
      case 0:
	val = controlParam + ((1-controlParam)*((x*x)/(1+(x*x))));
	break;
      case 1:
	val = 1.0 / (1.0 + Math.exp (-5.0*x + controlParam));
	break;
      default:
	val = 0.0;
    }

    return (val);
  }
}


/*
** EoF: Dynamic.java
*/
