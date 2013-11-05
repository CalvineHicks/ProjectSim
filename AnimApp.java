/*
  Fundamental class to be extended to make an Animated Application
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// JOGL packages:
import javax.media.opengl.*;  // for most OpenGL stuff
import javax.media.opengl.glu.*;  // for GLU
import com.sun.opengl.util.*;  // for GLUT

public class AnimApp extends JFrame implements WindowListener,
                                                KeyListener,
                                                MouseListener,
                                                MouseMotionListener,
                                                GLEventListener
{
  // simple test application
  public static void main(String[] args)
  {
    // window title, step time in nanoseconds, pixelwidth, pixelheight
    new AnimApp("Basic Animated Application", 512, 512, 33333333 );
  }

  // pixel size of the GLCanvas
  private int pixelWidth;
  private int pixelHeight;

  protected GL gl;
  protected GLU glu;

  private long stepNumber;
  private int framesPerSecond;

  private long timeOfLastStep;  // time last step was done
  private long stepTime;  // time we want a step to take in nanoseconds
  private double period;

  // ---------------------------------------------------

  public AnimApp( String title, int pw, int ph, long stepTimeNanos )
  {
    super(title);
    Container c = getContentPane();
    c.setLayout( new BorderLayout() );

    JPanel renderPanel = new JPanel();
    renderPanel.setLayout( new BorderLayout() );
    renderPanel.setOpaque(false);
    pixelWidth = pw;  pixelHeight = ph;
    renderPanel.setPreferredSize( new Dimension(pixelWidth,pixelHeight));
    GLCanvas canvas = new GLCanvas(); 
    canvas.addGLEventListener( this );
    renderPanel.add(canvas, BorderLayout.CENTER);

    c.add( renderPanel, BorderLayout.CENTER);

    stepTime = stepTimeNanos;  // duration of step in nanoseconds
    framesPerSecond = (int) (1000000000/stepTime);
    period = stepTime / 1e9;  // time of a full step in seconds

    addWindowListener( this );
    pack();
    setVisible(true);

  } // end of AnimApp constructor

  // implement WindowListener:
  // ******************************************************************
  public void windowActivated(WindowEvent e){} 
  public void windowClosed(WindowEvent e){}
  public void windowClosing(WindowEvent e)
  { new Thread( new Runnable() 
          { public void run() { System.exit(0); } }).start();
  } // end of windowClosing()
  public void windowDeactivated(WindowEvent e){} 
  public void windowDeiconified(WindowEvent e){} 
  public void windowIconified(WindowEvent e){} 
  public void windowOpened(WindowEvent e){} 

  // implement GLEventListener
  // ******************************************************************

  public void init(GLAutoDrawable drawable) 
  {
    gl = drawable.getGL();    /* don't make this gl a global! */
    glu = new GLU();    // this is okay as a global, but only use in callbacks
    drawable.addKeyListener( this );
    drawable.addMouseListener( this );
    drawable.addMouseMotionListener( this );
    timeOfLastStep = System.nanoTime();
    AALib.init();
    AALib.setup( gl );
    setup();
  } // end of init

  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) 
  {
    pixelWidth = width;  pixelHeight = height;
    gl = drawable.getGL();
    if (height == 0)
      height = 1;    // to avoid division by 0 in aspect ratio below
    gl.glViewport(x, y, width, height);  // size of drawing area 
  } // end of reshape()

  // display lets the doFrame method be executed with a GL to display to
  public void display(GLAutoDrawable drawable) 
  {
    gl = drawable.getGL();
    AALib.setup( gl );

    stepNumber++;
    step();

    // make sure enough time has gone by, then request another display
    long timeNow = System.nanoTime();
    // time left in this step in milliseconds
    long delayTime = (stepTime - (timeNow - timeOfLastStep))/1000000;
    if( delayTime <= 0 )
    {// sleep at least a little so input events can be processed
      delayTime = 1;
    }
    try{
      Thread.sleep( delayTime );
    }
    catch(Exception e)
    {
      System.out.println( e );
    }
    timeOfLastStep = System.nanoTime();
    // request another step
    drawable.repaint();
  } // end of display

  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, 
                             boolean deviceChanged) 
  /* Called when the display mode or device has changed.
     Currently unimplemented in JOGL */
  {}

//***************************************************************
//  methods that can be called
//***************************************************************

  private double scaleFactor = 1;

  public void setScaleFactor( double sf )
  {
    scaleFactor = sf;
  }

  public double getScaleFactor()
  {
    return scaleFactor;
  }

  public int getPixelWidth()
  {
    return pixelWidth;
  }

  public int getPixelHeight()
  {
    return pixelHeight;
  }

  public long getStepNumber()
  {  return stepNumber;  }

//***************************************************************
//  methods that should be overridden listed below
//  (don't override any of the methods above)
//***************************************************************

  // override this method to specify actions that need to be done
  // when the application starts
  protected void setup(){}

  // override this method to program the actions  your application
  // takes each step
  protected void step()
  {
    System.out.println( stepNumber );
  }

  // override these methods to specify how your application
  // responds to key events and mouse events

  // implement KeyListener:
  // ******************************************************************
  public void keyPressed(KeyEvent e){}
  public void keyReleased(KeyEvent e){}
  public void keyTyped(KeyEvent e){}

  // implement MouseListener:
  // ******************************************************************
  public void mouseClicked(MouseEvent e){}
  public void mouseEntered(MouseEvent e){}
  public void mouseExited(MouseEvent e){}
  public void mousePressed(MouseEvent e){}
  public void mouseReleased(MouseEvent e){}
  
  // implement MouseMotionListener:
  // ******************************************************************
  public void mouseDragged(MouseEvent e){}
  public void mouseMoved(MouseEvent e){}

} // AnimApp
