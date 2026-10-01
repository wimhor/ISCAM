/*
** Help.java: Pop up a help window with some basic information about the GUI.
**
** Wim Hordijk   Last modified: 08 December 2009
*/

package ISCAM;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.URL;


/*
** Help: The main help class.
*/

class Help
{
  private JFrame  helpFrame;
  private JButton jbClose;

  /*
  ** Constructor.
  */
  Help ()
  {
    helpFrame = null;
  }

  /*
  ** showHelp: Show the help window.
  */
  public void showHelp ()
  {
    JEditorPane helpText;
    JScrollPane scrollPane;

    /*
    ** Create a new top frame.
    */
    if (helpFrame != null)
    {
      return;
    }
    helpFrame = new JFrame ("Help");
    helpFrame.setLayout (new BorderLayout (0, 10));
    helpFrame.setResizable (false);
    helpFrame.setDefaultCloseOperation (WindowConstants.DO_NOTHING_ON_CLOSE);

    /*
    ** Create a scrollable editor pane with the help text form the html file.
    */
    helpText = null;
    URL helpURL = this.getClass().getResource ("Help.html");
    if (helpURL != null)
    {
      try
      {
	helpText = new JEditorPane (helpURL);
	helpText.setEditable (false);
	scrollPane = new JScrollPane (helpText);
	scrollPane.setPreferredSize (new Dimension (600, 400));
	helpFrame.add (scrollPane, BorderLayout.CENTER);
      }
      catch (Exception e)
      {
	//System.out.println ("Bad URL...");
      }
    }
    else
    {
      //System.out.println ("Could not read file 'Help.html'");
    }

    /*
    ** Add a close button.
    */
    jbClose = new JButton ("Close");
    jbClose.addActionListener (new ActionListener ()
      {
	public void actionPerformed (ActionEvent e)
	{
	  helpFrame.dispose ();
	  helpFrame = null;
	}
      });
    helpFrame.add (jbClose, BorderLayout.SOUTH);
	
    /*
    ** Display the help frame.
    */
    helpFrame.pack ();
    helpFrame.setLocationRelativeTo (null);
    helpFrame.setVisible(true);
  }
}


/*
** EoF: Help.java
*/
