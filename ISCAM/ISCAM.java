/*
** ISCAM.java: The main class for the Ideal Storage Cellular Automaton Model
**             (ISCAM) program. This class mainly sets up the GUI, and is
**             somewhat long and boring. But it needs to be done, so take a
**             deep breath...
**
** Wim Hordijk   Last modified: 1 October 2026
*/

package ISCAM;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.EtchedBorder;
import java.util.*;
import java.io.*;
import javax.imageio.ImageIO;


/*
** ISCAM: The main class for the ISCAM.
*/

class ISCAM
{
  private CA                ca;
  private JFrame            mainFrame;
  private Graphics          caGraphics;
  private boolean           runCA, gridLines;
  private CAPanel           caPanel;
  private JSpinner          spGridSize, spCtrlParamS, spCtrlParamR, spNoiseRange;
  private JSlider           slP, slZoom, slShow, slSpeed;
  private JTextField        tfRndSeed, tfIteration;
  private JComboBox<String> cbDynS, cbDynR;
  private JButton           jbQuit, jbStep, jbRun, jbStop, jbInit, jbSave, jbHelp,
                            jbLoad, jbJPG;
  private JRadioButton      rbDiffNoiseOn, rbDiffNoiseOff;
  private int               cellSize, gridSize, delay, show, iteration;
  private Dimension         d;
  private Help              helpWindow;

  /*
  ** Constructor.
  */
  ISCAM ()
  {
    int                       i;
    JPanel                    buttonPanel;
    Box                       dummyPanel, optionsPanel;
    JScrollPane               scrollPane;
    JRadioButton              rbDisplayS, rbDisplayR, rbDisplaySR, rbDisplaySvsR,
                              rbColorMap1, rbColorMap2, rbOn, rbOff;
    ButtonGroup               group;
    Dynamic                   dynamicS, dynamicR;
    Hashtable<Integer,JLabel> lTable;

    /*
    ** Set some default values.
    */
    cellSize = 9;
    gridSize = 100;
    delay = 50;
    show = 1;
    runCA = false;
    gridLines = false;
    iteration = 0;
    helpWindow = new Help ();

    /*
    ** Create the dynamics and the CA.
    */
    dynamicS = new DynamicS (0.5);
    dynamicR = new DynamicR (2.5);
    dynamicR.setFunction (1);
    ca = new CA (gridSize, gridSize, dynamicS, dynamicR);
    ca.setSeed (0);
    ca.setP (0.2);

    /*
    ** Create the main frame.
    */
    mainFrame = new JFrame ("ISCAM");
    mainFrame.setLayout (new BorderLayout ());
    mainFrame.setResizable (false);
    mainFrame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

    /*
    ** Add the CA panel and make it clickable and scrollable.
    */
    caPanel = new CAPanel ();
    caPanel.setBackground (Color.white);
    d = new Dimension (gridSize*cellSize+1, gridSize*cellSize+1);
    caPanel.setPreferredSize (d);
    caPanel.addMouseListener (new ClickOnGrid ());
    scrollPane = new JScrollPane (caPanel);
    d = new Dimension ((gridSize+1)*cellSize, (gridSize+1)*cellSize);
    scrollPane.setPreferredSize (d);
    mainFrame.add (scrollPane, BorderLayout.CENTER);

    /*
    ** Add the user options.
    */
    optionsPanel = new Box (BoxLayout.Y_AXIS);
    mainFrame.add (optionsPanel, BorderLayout.EAST);
    /* Grid size */
    dummyPanel = new Box (BoxLayout.X_AXIS);
    dummyPanel.setBorder (BorderFactory.createEtchedBorder
			  (EtchedBorder.LOWERED));
    optionsPanel.add (dummyPanel);
    dummyPanel.add (new JLabel ("Grid size:  "));
    dummyPanel.add (Box.createHorizontalGlue ());
    spGridSize = new JSpinner (new SpinnerNumberModel (gridSize, 10, 1000, 10));
    spGridSize.setMaximumSize (spGridSize.getPreferredSize ());
    dummyPanel.add (spGridSize);
    spGridSize.addChangeListener (new ChangeListener ()
      {
	public void stateChanged (ChangeEvent e)
	{
	  gridSize = ((Integer)spGridSize.getValue ()).intValue ();
	  ca.setSize (gridSize, gridSize);
	  setIteration (0);
	  d = new Dimension (gridSize*cellSize + 1, gridSize*cellSize + 1);
	  caPanel.setSize (d);
	  caPanel.setPreferredSize (d);
	}
      });
    /* Random seed */
    dummyPanel = new Box (BoxLayout.X_AXIS);
    dummyPanel.setBorder (BorderFactory.createEtchedBorder
			  (EtchedBorder.LOWERED));
    optionsPanel.add (dummyPanel);
    dummyPanel.add (new JLabel ("Random seed:  "));
    dummyPanel.add (Box.createHorizontalGlue ());
    tfRndSeed = new JTextField ("0", 6);
    tfRndSeed.setHorizontalAlignment (JTextField.RIGHT);
    tfRndSeed.setMaximumSize (tfRndSeed.getPreferredSize ());
    dummyPanel.add (tfRndSeed);
    tfRndSeed.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  int s = Integer.parseInt (tfRndSeed.getText ());
	  ca.setSeed (s);
	}
      });
    /* Empty space */
    optionsPanel.add (new JLabel (" "));
    /* S dynamic */
    dummyPanel = new Box (BoxLayout.X_AXIS);
    dummyPanel.setBorder (BorderFactory.createEtchedBorder
			  (EtchedBorder.LOWERED));
    optionsPanel.add (dummyPanel);
    dummyPanel.add (new JLabel ("S dynamic:  "));
    dummyPanel.add (Box.createHorizontalGlue ());
    cbDynS = new JComboBox<String> ();
    cbDynS.setMaximumSize (cbDynS.getPreferredSize ());
    for (i = 0; i < dynamicS.nrFunctions; i++)
    {
      cbDynS.addItem (dynamicS.getDescription (i));
    }
    cbDynS.setSelectedIndex (0);
    dummyPanel.add (cbDynS);
    cbDynS.addItemListener (new ItemListener ()
      {
	public void itemStateChanged (ItemEvent e)
	{
	  (ca.getDynamicS ()).setFunction (cbDynS.getSelectedIndex ());
	}
      });
    /* R dynamic */
    dummyPanel = new Box (BoxLayout.X_AXIS);
    dummyPanel.setBorder (BorderFactory.createEtchedBorder
			  (EtchedBorder.LOWERED));
    optionsPanel.add (dummyPanel);
    dummyPanel.add (new JLabel ("R dynamic:  "));
    dummyPanel.add (Box.createHorizontalGlue ());
    cbDynR = new JComboBox<String> ();
    cbDynR.setMaximumSize (cbDynR.getPreferredSize ());
    for (i = 0; i < dynamicR.nrFunctions; i++)
    {
      cbDynR.addItem (dynamicR.getDescription (i));
    }
    cbDynR.setSelectedIndex (1);
    dummyPanel.add (cbDynR);
    cbDynR.addItemListener (new ItemListener ()
      {
	public void itemStateChanged (ItemEvent e)
	{
	  (ca.getDynamicR ()).setFunction (cbDynR.getSelectedIndex ());
	}
      });
    /* S control parameter */
    dummyPanel = new Box (BoxLayout.X_AXIS);
    dummyPanel.setBorder (BorderFactory.createEtchedBorder
			  (EtchedBorder.LOWERED));
    optionsPanel.add (dummyPanel);
    dummyPanel.add (new JLabel ("S control parameter:  "));
    dummyPanel.add (Box.createHorizontalGlue ());
    spCtrlParamS = new JSpinner (new SpinnerNumberModel (0.5, 0.0, 2.0, 0.01));
    d = spCtrlParamS.getPreferredSize ();
    d.setSize (50, d.getHeight ());
    spCtrlParamS.setMaximumSize (d);
    spCtrlParamS.setPreferredSize (d);
    dummyPanel.add (spCtrlParamS);
    spCtrlParamS.addChangeListener (new ChangeListener ()
      {
	public void stateChanged (ChangeEvent e)
	{
	  double c = ((Double)spCtrlParamS.getValue ()).doubleValue ();
	  ca.getDynamicS().setParam (c);
	}
      });
    /* R control parameter */
    dummyPanel = new Box (BoxLayout.X_AXIS);
    dummyPanel.setBorder (BorderFactory.createEtchedBorder
			  (EtchedBorder.LOWERED));
    optionsPanel.add (dummyPanel);
    dummyPanel.add (new JLabel ("R control parameter:  "));
    dummyPanel.add (Box.createHorizontalGlue ());
    spCtrlParamR = new JSpinner (new SpinnerNumberModel (2.5, 0.0, 10.0, 0.1));
    d = spCtrlParamR.getPreferredSize ();
    d.setSize (50, d.getHeight ());
    spCtrlParamR.setMaximumSize (d);
    spCtrlParamR.setPreferredSize (d);
    dummyPanel.add (spCtrlParamR);
    spCtrlParamR.addChangeListener (new ChangeListener ()
      {
	public void stateChanged (ChangeEvent e)
	{
	  double c = ((Double)spCtrlParamR.getValue ()).doubleValue ();
	  ca.getDynamicR().setParam (c);
	}
      });
    /* Empty space */
    optionsPanel.add (new JLabel (" "));
    /* Diffusion */
    dummyPanel = new Box (BoxLayout.X_AXIS);
    dummyPanel.setBorder (BorderFactory.createEtchedBorder
			  (EtchedBorder.LOWERED));
    optionsPanel.add (dummyPanel);
    dummyPanel.add (new JLabel ("Diffusion:  "));
    dummyPanel.add (Box.createHorizontalGlue ());
    slP = new JSlider (0, 100, 20);
    slP.setPaintLabels (true);
    slP.setMajorTickSpacing (20);
    slP.setMinorTickSpacing (5);
    slP.setPaintTicks (true);
    lTable = new Hashtable<Integer,JLabel> ();
    lTable.put (0, new JLabel ("0.0"));
    lTable.put (20, new JLabel ("0.2"));
    lTable.put (40, new JLabel ("0.4"));
    lTable.put (60, new JLabel ("0.6"));
    lTable.put (80, new JLabel ("0.8"));
    lTable.put (100, new JLabel ("1.0"));
    slP.setLabelTable (lTable);
    dummyPanel.add (slP);
    slP.addChangeListener (new ChangeListener ()
      {
	public void stateChanged (ChangeEvent e)
	{
	  int p = slP.getValue ();
	  ca.setP (p/100.0);
	}
      });
    /* Diffusion noise */
    dummyPanel = new Box (BoxLayout.X_AXIS);
    dummyPanel.setBorder (BorderFactory.createEtchedBorder
			  (EtchedBorder.LOWERED));
    optionsPanel.add (dummyPanel);
    dummyPanel.add (new JLabel ("Diffusion noise:  "));
    dummyPanel.add (Box.createHorizontalGlue ());
    rbDiffNoiseOn = new JRadioButton ("On", false);
    rbDiffNoiseOn.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  ca.setDiffusionNoise (true);
	  spNoiseRange.setEnabled (true);
	}
      });
    rbDiffNoiseOff = new JRadioButton ("Off", true);
    rbDiffNoiseOff.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  ca.setDiffusionNoise (false);
	  spNoiseRange.setEnabled (false);
	}
      });
    group = new ButtonGroup ();
    group.add (rbDiffNoiseOn);
    group.add (rbDiffNoiseOff);
    dummyPanel.add (rbDiffNoiseOff);
    dummyPanel.add (rbDiffNoiseOn);
    spNoiseRange = new JSpinner (new SpinnerNumberModel (0.2, 0.0, 1.0, 0.05));
    d = spNoiseRange.getPreferredSize ();
    d.setSize (50, d.getHeight ());
    spNoiseRange.setMaximumSize (d);
    spNoiseRange.setPreferredSize (d);
    spNoiseRange.setEnabled (false);
    dummyPanel.add (spNoiseRange);
    spNoiseRange.addChangeListener (new ChangeListener ()
      {
	public void stateChanged (ChangeEvent e)
	{
	  double c = ((Double)spNoiseRange.getValue ()).doubleValue ();
	  ca.setNoiseRange (c);
	}
      });
    /* Empty space */
    optionsPanel.add (new JLabel (" "));
    /* Zoom */
    dummyPanel = new Box (BoxLayout.X_AXIS);
    dummyPanel.setBorder (BorderFactory.createEtchedBorder
			  (EtchedBorder.LOWERED));
    optionsPanel.add (dummyPanel);
    dummyPanel.add (new JLabel ("Zoom:  "));
    dummyPanel.add (Box.createHorizontalGlue ());
    slZoom = new JSlider (2, 20, cellSize);
    slZoom.setPaintLabels (true);
    slZoom.setMajorTickSpacing (6);
    slZoom.setMinorTickSpacing (1);
    slZoom.setPaintTicks (true);
    slZoom.setSnapToTicks (true);
    dummyPanel.add (slZoom);
    slZoom.addChangeListener (new ChangeListener ()
      {
	public void stateChanged (ChangeEvent e)
	{
	  cellSize = slZoom.getValue ();
	  d = new Dimension (gridSize*cellSize + 1, gridSize*cellSize + 1);
	  caPanel.setSize (d);
	  caPanel.setPreferredSize (d);
	}
      });
    /* Show */
    dummyPanel = new Box (BoxLayout.X_AXIS);
    dummyPanel.setBorder (BorderFactory.createEtchedBorder
			  (EtchedBorder.LOWERED));
    optionsPanel.add (dummyPanel);
    dummyPanel.add (new JLabel ("Show:  "));
    dummyPanel.add (Box.createHorizontalGlue ());
    slShow = new JSlider (0, 20, 1);
    slShow.setPaintLabels (true);
    slShow.setMajorTickSpacing (5);
    slShow.setMinorTickSpacing (1);
    slShow.setPaintTicks (true);
    slShow.setSnapToTicks (true);
    dummyPanel.add (slShow);
    slShow.addChangeListener (new ChangeListener ()
      {
	public void stateChanged (ChangeEvent e)
	{
	  show = slShow.getValue ();
	  if (show == 0)
	  {
	    show = 1;
	  }
	}
      });
    /* Speed */
    dummyPanel = new Box (BoxLayout.X_AXIS);
    dummyPanel.setBorder (BorderFactory.createEtchedBorder
			  (EtchedBorder.LOWERED));
    optionsPanel.add (dummyPanel);
    dummyPanel.add (new JLabel ("Speed:  "));
    dummyPanel.add (Box.createHorizontalGlue ());
    slSpeed = new JSlider (0, 100, 50);
    slSpeed.setPaintLabels (true);
    slSpeed.setMajorTickSpacing (50);
    slSpeed.setMinorTickSpacing (10);
    slSpeed.setPaintTicks (true);
    slSpeed.setSnapToTicks (true);
    lTable = new Hashtable<Integer,JLabel> ();
    lTable.put (0, new JLabel ("Slow"));
    lTable.put (50, new JLabel ("Medium"));
    lTable.put (100, new JLabel ("Fast"));
    slSpeed.setLabelTable (lTable);
    dummyPanel.add (slSpeed);
    slSpeed.addChangeListener (new ChangeListener ()
      {
	public void stateChanged (ChangeEvent e)
	{
	  int s = slSpeed.getValue ();
	  delay = 100 - s;
	}
      });
    /* Empty space */
    optionsPanel.add (new JLabel (" "));
    /* Display */
    dummyPanel = new Box (BoxLayout.X_AXIS);
    dummyPanel.setBorder (BorderFactory.createEtchedBorder
			  (EtchedBorder.LOWERED));
    optionsPanel.add (dummyPanel);
    dummyPanel.add (new JLabel ("Display:  "));
    dummyPanel.add (Box.createHorizontalGlue ());
    rbDisplayS = new JRadioButton ("S", true);
    rbDisplayS.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  ca.setDrawState (CA.DRAW_S);
	  caPanel.repaint ();
	}
      });
    rbDisplayR = new JRadioButton ("R", false);
    rbDisplayR.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  ca.setDrawState (CA.DRAW_R);
	  caPanel.repaint ();
	}
      });
    rbDisplaySR = new JRadioButton ("S,R", false);
    rbDisplaySR.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  ca.setDrawState (CA.DRAW_SR);
	  caPanel.repaint ();
	}
      });
    rbDisplaySvsR = new JRadioButton ("S vs R", false);
    rbDisplaySvsR.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  ca.setDrawState (CA.DRAW_SvsR);
	  caPanel.repaint ();
	}
      });
    group = new ButtonGroup ();
    group.add (rbDisplayS);
    group.add (rbDisplayR);
    group.add (rbDisplaySR);
    group.add (rbDisplaySvsR);
    dummyPanel.add (rbDisplayS);
    dummyPanel.add (rbDisplayR);
    dummyPanel.add (rbDisplaySR);
    dummyPanel.add (rbDisplaySvsR);
    /* Grid lines */
    dummyPanel = new Box (BoxLayout.X_AXIS);
    dummyPanel.setBorder (BorderFactory.createEtchedBorder
			  (EtchedBorder.LOWERED));
    optionsPanel.add (dummyPanel);
    dummyPanel.add (new JLabel ("Grid lines:  "));
    dummyPanel.add (Box.createHorizontalGlue ());
    rbOn = new JRadioButton ("On", false);
    rbOn.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  gridLines = true;
	  caPanel.repaint ();
	}
      });
    rbOff = new JRadioButton ("Off", true);
    rbOff.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  gridLines = false;
	  caPanel.repaint ();
	}
      });
    group = new ButtonGroup ();
    group.add (rbOn);
    group.add (rbOff);
    dummyPanel.add (rbOff);
    dummyPanel.add (rbOn);
    /* Color map */
    dummyPanel = new Box (BoxLayout.X_AXIS);
    dummyPanel.setBorder (BorderFactory.createEtchedBorder
			  (EtchedBorder.LOWERED));
    optionsPanel.add (dummyPanel);
    dummyPanel.add (new JLabel ("Color map:  "));
    dummyPanel.add (Box.createHorizontalGlue ());
    rbColorMap1 = new JRadioButton ("Monochrome", true);
    rbColorMap1.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  ca.setCAColor (CAColor.MONOCHROME);
	  caPanel.repaint ();
	}
      });
    rbColorMap2 = new JRadioButton ("Rainbow", false);
    rbColorMap2.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  ca.setCAColor (CAColor.RAINBOW);
	  caPanel.repaint ();
	}
      });
    group = new ButtonGroup ();
    group.add (rbColorMap1);
    group.add (rbColorMap2);
    dummyPanel.add (rbColorMap1);
    dummyPanel.add (rbColorMap2);
    /* Empty space */
    optionsPanel.add (Box.createVerticalGlue ());
    /* Iteration number */
    dummyPanel = new Box (BoxLayout.X_AXIS);
    optionsPanel.add (dummyPanel);
    dummyPanel.add (Box.createHorizontalGlue ());
    dummyPanel.add (new JLabel ("Iteration: "));
    tfIteration = new JTextField (Integer.toString (iteration), 6);
    tfIteration.setHorizontalAlignment (JTextField.RIGHT);
    tfIteration.setMaximumSize (tfIteration.getPreferredSize ());
    tfIteration.setEditable (false);
    dummyPanel.add (tfIteration);

    /*
    ** Add some action buttons.
    */
    buttonPanel = new JPanel ();
    buttonPanel.setBorder (BorderFactory.createEtchedBorder
			   (EtchedBorder.LOWERED));
    mainFrame.add (buttonPanel, BorderLayout.SOUTH);
    /* Init */
    jbInit = new JButton ("Init");
    jbInit.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  runCA = false;
	  ca.init ();
	  caPanel.repaint ();
	  setIteration (0);
	}
      });
    buttonPanel.add (jbInit);
    /* Step */
    jbStep = new JButton ("Step");
    jbStep.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  runCA = false;
	  ca.step ();
	  caPanel.repaint ();
	  setIteration (iteration+1);
	}
      });
    buttonPanel.add (jbStep);
    /* Run */
    jbRun = new JButton ("Run");
    jbRun.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  IterateCA iterCA;

	  /*
	  ** Disable some buttons.
	  */
	  spGridSize.setEnabled (false);
	  jbInit.setEnabled (false);
	  jbStep.setEnabled (false);
	  jbRun.setEnabled (false);
	  jbJPG.setEnabled (false);
	  jbSave.setEnabled (false);
	  jbLoad.setEnabled (false);

	  /*
	  ** Start a new thread to iterate the CA.
	  */
	  iterCA = new IterateCA ();
	  iterCA.start ();
	}
      });
    buttonPanel.add (jbRun);
    /* Stop */
    jbStop = new JButton ("Stop");
    jbStop.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  runCA = false;

	  /*
	  ** Enable some buttons.
	  */
	  spGridSize.setEnabled (true);
	  jbInit.setEnabled (true);
	  jbStep.setEnabled (true);
	  jbRun.setEnabled (true);
	  jbJPG.setEnabled (true);
	  jbSave.setEnabled (true);
	  jbLoad.setEnabled (true);
	}
      });
    buttonPanel.add (jbStop);
    /* Empty space */
    buttonPanel.add (new JLabel ("     "));
    /* JPG */
    jbJPG = new JButton ("JPG");
    jbJPG.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  int               w, h, val;
	  BufferedImage     bImage;
	  Graphics2D        g2;
	  JFileChooser      chooser;

	  w = gridSize*cellSize + 1;
	  h = gridSize*cellSize + 1;
	  bImage = new BufferedImage (w, h, BufferedImage.TYPE_INT_RGB);
	  g2 = bImage.createGraphics ();
	  caPanel.paint (g2);
	  g2.dispose ();
	  chooser = new JFileChooser (".");
	  val = chooser.showOpenDialog (mainFrame);
	  if(val == JFileChooser.APPROVE_OPTION)
	  {
	    try
	    {
	      ImageIO.write (bImage, "jpg",
			     new File (chooser.getSelectedFile().getPath()));
	    } catch (IOException ex) {}
	  }
	}
      });
    buttonPanel.add (jbJPG);
    /* Save */
    jbSave = new JButton ("Save");
    jbSave.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  int          val, i, j;
	  JFileChooser chooser;
	  PrintWriter  file;

	  /*
	  ** Save the current configuration to a file.
	  */
	  chooser = new JFileChooser (".");
	  val = chooser.showOpenDialog (mainFrame);
	  if(val == JFileChooser.APPROVE_OPTION)
	  {
	    try
	    {
	      file = new PrintWriter (chooser.getSelectedFile().getPath());
	      file.println ("gridSize = " + gridSize);
	      file.println ("rndSeed = " + ca.getSeed ());
	      file.println ("dynamicS = " + ca.getDynamicS().getFunction ());
	      file.println ("dynamicR = " + ca.getDynamicR().getFunction ());
	      file.println ("ctrlParamS = " + ca.getDynamicS().getParam ());
	      file.println ("ctrlParamR = " + ca.getDynamicR().getParam ());
	      file.println ("diffusion = " + ca.getP ());
	      file.print   ("diffNoise = ");
	      if (ca.getDiffusionNoise ())
	      {
		file.println ("on");
	      }
	      else
	      {
		file.println ("off");
	      }
	      file.println ("noiseRange = " + ca.getNoiseRange ());
	      file.println ("zoom = " + cellSize);
	      file.println ("show = " + slShow.getValue ());
	      file.println ("speed = " + slSpeed.getValue ());
	      file.println ("iteration = " + tfIteration.getText ());
	      file.println ("grid = ");
	      for (i = 0; i < gridSize; i++)
	      {
		for (j = 0; j < gridSize; j++)
		{
		  file.print (i + " " + j + " ");
		  file.print (ca.getCell(i, j).getS () + " ");
		  file.println (ca.getCell(i, j).getR ());
		}
	      }
	      file.close ();
	    } catch (IOException ex) {}
	  }
	}
      });
    buttonPanel.add (jbSave);
    /* Load */
    jbLoad = new JButton ("Load");
    jbLoad.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  int          val, i, j;
	  double       d, d2;
	  JFileChooser chooser;
	  File         configFile;
	  Scanner      file;
	  String       s1, s2;

	  /*
	  ** Load a configuration from a file.
	  */
	  file = null;
	  chooser = new JFileChooser (".");
	  val = chooser.showOpenDialog (mainFrame);
	  if(val == JFileChooser.APPROVE_OPTION)
	  {
	    try
	    {
	      configFile = new File (chooser.getSelectedFile().getPath());
	      file = new Scanner (configFile);
	      /* gridSize */
	      s1 = file.next ();
	      s2 = file.next ();
	      if ((s1.compareTo ("gridSize") != 0) || (s2.compareTo ("=") != 0))
	      {
		throw new IOException ();
	      }
	      i = file.nextInt ();
	      spGridSize.setValue (i);
	      /* rndSeed */
	      s1 = file.next ();
	      s2 = file.next ();
	      if ((s1.compareTo ("rndSeed") != 0) || (s2.compareTo ("=") != 0))
	      {
		throw new IOException ();
	      }
	      i = file.nextInt ();
	      tfRndSeed.setText (Integer.toString (i));
	      ca.setSeed (i);
	      /* S dynamic */
	      s1 = file.next ();
	      s2 = file.next ();
	      if ((s1.compareTo ("dynamicS") != 0) || (s2.compareTo ("=") != 0))
	      {
		throw new IOException ();
	      }
	      i = file.nextInt ();
	      cbDynS.setSelectedIndex (i);
	      /* R dynamic */
	      s1 = file.next ();
	      s2 = file.next ();
	      if ((s1.compareTo ("dynamicR") != 0) || (s2.compareTo ("=") != 0))
	      {
		throw new IOException ();
	      }
	      i = file.nextInt ();
	      cbDynR.setSelectedIndex (i);
	      /* S control parameter */
	      s1 = file.next ();
	      s2 = file.next ();
	      if ((s1.compareTo ("ctrlParamS") != 0) ||
		  (s2.compareTo ("=") != 0))
	      {
		throw new IOException ();
	      }
	      d = file.nextDouble ();
	      spCtrlParamS.setValue (d);
	      /* R control parameter */
	      s1 = file.next ();
	      s2 = file.next ();
	      if ((s1.compareTo ("ctrlParamR") != 0) ||
		  (s2.compareTo ("=") != 0))
	      {
		throw new IOException ();
	      }
	      d = file.nextDouble ();
	      spCtrlParamR.setValue (d);
	      /* Diffusion */
	      s1 = file.next ();
	      s2 = file.next ();
	      if ((s1.compareTo ("diffusion") != 0) ||
		  (s2.compareTo ("=") != 0))
	      {
		throw new IOException ();
	      }
	      d = file.nextDouble ();
	      slP.setValue ((int)(100 * d));
	      /* Diffusion noise */
	      s1 = file.next ();
	      s2 = file.next ();
	      if ((s1.compareTo ("diffNoise") != 0) ||
		  (s2.compareTo ("=") != 0))
	      {
		throw new IOException ();
	      }
	      s1 = file.next ();
	      if (s1.compareTo ("on") == 0)
	      {
		rbDiffNoiseOn.doClick ();
	      }
	      else
	      {
		rbDiffNoiseOff.doClick ();
	      }
	      /* Noise range */
	      s1 = file.next ();
	      s2 = file.next ();
	      if ((s1.compareTo ("noiseRange") != 0) ||
		  (s2.compareTo ("=") != 0))
	      {
		throw new IOException ();
	      }
	      d = file.nextDouble ();
	      spNoiseRange.setValue (d);	      
	      /* Zoom */
	      s1 = file.next ();
	      s2 = file.next ();
	      if ((s1.compareTo ("zoom") != 0) ||
		  (s2.compareTo ("=") != 0))
	      {
		throw new IOException ();
	      }
	      i = file.nextInt ();
	      slZoom.setValue (i);
	      /* Show */
	      s1 = file.next ();
	      s2 = file.next ();
	      if ((s1.compareTo ("show") != 0) ||
		  (s2.compareTo ("=") != 0))
	      {
		throw new IOException ();
	      }
	      i = file.nextInt ();
	      slShow.setValue (i);
	      /* Speed */
	      s1 = file.next ();
	      s2 = file.next ();
	      if ((s1.compareTo ("speed") != 0) ||
		  (s2.compareTo ("=") != 0))
	      {
		throw new IOException ();
	      }
	      i = file.nextInt ();
	      slSpeed.setValue (i);
	      /* Iteration */
	      s1 = file.next ();
	      s2 = file.next ();
	      if ((s1.compareTo ("iteration") != 0) ||
		  (s2.compareTo ("=") != 0))
	      {
		throw new IOException ();
	      }
	      i = file.nextInt ();
	      setIteration (i);
	      /* Grid */
	      s1 = file.next ();
	      s2 = file.next ();
	      if ((s1.compareTo ("grid") != 0) ||
		  (s2.compareTo ("=") != 0))
	      {
		throw new IOException ();
	      }
	      while (file.hasNext ())
	      {
		i = file.nextInt ();
		j = file.nextInt ();
		d = file.nextDouble ();
		d2 = file.nextDouble ();
		ca.getCell(i, j).setState (d, d2);
	      }
	      caPanel.repaint ();
	    }
	    catch (IOException ex)
	    {
	      System.out.println ("Invalid file format.");
	    }
	    finally
	    {
	      if (file != null)
	      {
		file.close ();
	      }
	    }
	  }
	}
      });
    buttonPanel.add (jbLoad);
    /*
    ** Help
    */
    jbHelp = new JButton ("Help");
    jbHelp.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  helpWindow.showHelp ();
	}
      });
    buttonPanel.add (jbHelp);
    /* Empty space */
    buttonPanel.add (new JLabel ("          "));
    /* Quit */
    jbQuit = new JButton ("Quit");
    jbQuit.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  System.exit (0);
	}
      });
    buttonPanel.add (jbQuit);


    /*
    ** Display the main frame and get the Graphics object of the CA panel
    ** (after a short delay, otherwise things may not have been displayed yet).
    */
    mainFrame.pack ();
    mainFrame.setLocationRelativeTo (null);
    mainFrame.setVisible(true);
    try
    {
      Thread.sleep (500);
    }
    catch (InterruptedException e) {}
    caGraphics = caPanel.getGraphics ();
  }

  /*
  ** setIteration: Set the iteration count to a new value and update the
  **               iteration display accordingly.
  */
  private void setIteration (int iter)
  {
    iteration = iter;
    if (iteration < 0)
    {
      iteration = 0;
    }
    tfIteration.setText (Integer.toString (iteration));
  }

  /*
  ** IterateCA: Thread for iterating the CA until the "Stop" button is clicked.
  */
  class IterateCA extends Thread
  {
    /*
    ** Constructor.
    */
    IterateCA ()
    {
      runCA = true;
    }

    /*
    ** run: Iterate the CA.
    */
    public void run ()
    {
      while (runCA)
      {
	ca.step ();
	setIteration (iteration+1);
	if (iteration % show == 0)
	{
	  caPanel.repaint ();
	  if (delay > 0)
	  {
	    try
	    {
	      Thread.sleep (delay);
	    }
	    catch (InterruptedException e)
	    {
	      return;
	    }
	  }
	}
      }
    }
  }

  /*
  ** CAPanel: Panel for drawing the CA grid or phase space.
  */
  class CAPanel extends JPanel
  {
    /*
    ** paintComponent: Override the paintComponent method to draw the CA grid.
    */
    protected void paintComponent (Graphics g)
    {
      int i, x, y;

      super.paintComponent (g);
      /*
      ** Draw the grid or the phase space.
      */
      switch (ca.getDrawState ())
      {
	case CA.DRAW_S:
	case CA.DRAW_R:
	case CA.DRAW_SR:
	  /*
	  ** Cell states.
	  */
	  ca.draw (g, cellSize);
	  /*
	  ** CA grid.
	  */
	  if (gridLines)
	  {
	    g.setColor (Color.darkGray);
	    for (i = 0; i <= gridSize; i++)
	    {
	      g.drawLine (i*cellSize, 0, i*cellSize, gridSize*cellSize);
	      g.drawLine (0, i*cellSize, gridSize*cellSize, i*cellSize);
	    }
	  }
	  break;
	case CA.DRAW_SvsR:
	  /*
	  ** CA phase space.
	  */
	  g.drawLine (0, 0, gridSize*cellSize, 0);
	  g.drawLine (0, 0, 0, gridSize*cellSize);
	  g.drawLine (0, gridSize*cellSize, gridSize*cellSize,
		      gridSize*cellSize);
	  g.drawLine (gridSize*cellSize, 0, gridSize*cellSize,
		      gridSize*cellSize);
	  ca.draw (g, gridSize*cellSize);
	  x = (int)((ca.getFixedPointS ()/2.0) * gridSize * cellSize);
	  y = (int)((1.0-ca.getFixedPointR ()) * gridSize * cellSize);
	  g.setColor (Color.blue);
	  g.fillRect (x-2, y-2, 5, 5);
	  break;
	default:
	  // Do nothing.
      }
    }
  }

  /*
  ** ClickOnGrid: A listener for mouseclicks on the CA grid.
  */
  private class ClickOnGrid extends MouseAdapter
  {
    private Cell       c;
    private JFrame     cellState;
    private JTextField tfS, tfR;

    ClickOnGrid ()
    {
      cellState = null;
    }

    public void mouseClicked (MouseEvent e)
    {
      int        x, y, i, j;
      double     r, s;
      JButton    jbClose;
      JTextField tfCoord;
      String     val;

      /*
      ** Get the cell.
      */
      if (ca.getDrawState () != CA.DRAW_SvsR)
      {   
	x = e.getX ();
	y = e.getY ();
	i = y / cellSize;
	j = x / cellSize;
	c = ca.getCell (i, j);
      }
      else
      {
	i = 0;
	j = 0;
	c = null;
      }
      /*
      ** Display the cell state.
      */
      if (c != null)
      {
	s = c.getS ();
	r = c.getR ();
	if (cellState != null)
	{
	  cellState.dispose ();
	}
	cellState = new JFrame ("Cell");
	cellState.setLayout (new GridLayout (0, 2));
	cellState.setResizable (false);
	cellState.add (new JLabel ("row, col: "));
	tfCoord = new JTextField (i + ", " + j);
	tfCoord.setEditable (false);
	cellState.add (tfCoord);
	cellState.add (new JLabel ("S value: "));
	val = Double.toString (s);
	tfS = new JTextField (val, 10);
	tfS.moveCaretPosition (0);
	tfS.addActionListener (new ActionListener ()
	  {
	    public void actionPerformed (ActionEvent e)
	    {
	      double s = Double.parseDouble (tfS.getText ());
	      double r = c.getR ();
	      c.setState (s, r);
	      c.draw (caGraphics, cellSize, ca);
	    }
	  });
	cellState.add (tfS);
	cellState.add (new JLabel ("R value: "));
	val = Double.toString (r);
	tfR = new JTextField (val, 10);
	tfR.moveCaretPosition (0);
	tfR.addActionListener (new ActionListener ()
	  {
	    public void actionPerformed (ActionEvent e)
	    {
	      double s = c.getS ();
	      double r = Double.parseDouble (tfR.getText ());
	      c.setState (s, r);
	      c.draw (caGraphics, cellSize, ca);
	    }
	  });
	cellState.add (tfR);
	cellState.add (new JLabel (" "));
	jbClose = new JButton ("Close");
	jbClose.addActionListener (new ActionListener ()
	  {
	    public void actionPerformed (ActionEvent e)
	    {
	      cellState.dispose ();
	      cellState = null;
	    }
	  });
	cellState.add (jbClose);
	cellState.pack ();
	cellState.setLocationRelativeTo (null);
	cellState.setVisible(true);
      }
    }
  }

  /*
  ** main: The main routine of the program. Just to start everything up...
  */
  public static void main (String args[])
  {
    ISCAM iscam = new ISCAM ();
  }
}


/*
** EoF: ISCAM.java
*/
