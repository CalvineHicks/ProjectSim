//tutorial openGL.org


import javax.media.opengl.*;  // for most OpenGL stuff
import javax.media.opengl.glu.*;  // for GLU
import com.sun.opengl.util.*;  // for GLUT
import java.awt.*;
import java.io.*;
import javax.swing.*;

import javax.sound.midi.*;

import java.util.ArrayList;

public class AALib
{

//************************** methods a naive user can safely use ************

  // set the background color on the sheet of graph paper
  public static void setBackColor( RGB bc )
  {
    gl.glClearColor( (float) bc.red/255,
                     (float) bc.green/255, (float) bc.blue/255, 0 );
  }

  public static void clearBackground()
  {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
  }

  // set up 2D viewing, with depth
  public static void setLimits( double l, double r, 
                                double b, double t )
  {
    gl.glMatrixMode( GL.GL_PROJECTION );
    gl.glLoadIdentity();
    gl.glOrtho( l, r, b, t, MINDEPTH, MAXDEPTH );
    gl.glMatrixMode( GL.GL_MODELVIEW );
    gl.glLoadIdentity();
  }

  // set up 3D camera with l,r,b,t,n,f defining the view volume (frustum)
  // with modeling to simulate camera at eye, looking at c, with up direction
  public static void setCamera( double l, double r,
                                double b, double t,
                                double n, double f,
                                Triple eye, Triple center, Triple up )
  {
    gl.glMatrixMode( GL.GL_PROJECTION );
    gl.glLoadIdentity();
    // create view volume
    gl.glFrustum( l, r, b, t, n, f );
    gl.glMatrixMode( GL.GL_MODELVIEW );
    gl.glLoadIdentity();
    // simulate camera location and orientation
    glu.gluLookAt( eye.x, eye.y, eye.z, center.x, center.y, center.z,
                  up.x, up.y, up.z );
  }

  public static void setViewport( int x, int y, int w, int h )
  {
    gl.glViewport( x, y, w, h );
  }

  // set drawing color
  public static void setColor( RGB color )
  {
    gl.glColor3d( color.red/255., color.green/255., color.blue/255. );
  }

  // set depth of drawing for 2D
  public static void setDepth( double d )
  {
    if( MINDEPTH<=d && d<=MAXDEPTH )
      depth = -d;
    else 
    {
      System.out.println( d + " is out of the legal depth range " +
         " from " + MINDEPTH + " to " + MAXDEPTH );
      System.exit(1);
    }
  }

  // draw disk with center (x,y) and radius r
  public static void drawDisk( double x, double y, double r )
  {
    drawCircleEntity( true, x, y, r );
  }

  // draw circle with center (x,y) and radius r
  public static void drawCircle( double x, double y, double r )
  {
    drawCircleEntity( false, x, y, r );
  }

  // draw circle with center (x,y) and radius r with
  // picture named name texture mapped onto it
  // rotated through an angle
  public static void drawDisk( double x, double y, double r, double angle,
                                String name )
  {
    drawTexDisk( x, y, r, name, angle );
  }

  // draw rectangle with center point (x,y), width w, height h
  public static void drawRect( double x, double y, double w, double h )
  {
    gl.glPushMatrix();
      gl.glTranslated( x, y, depth );
      gl.glScaled( w, h, 1 );

      gl.glBegin( GL.GL_POLYGON );
        gl.glVertex3d( -0.5, -0.5, 0 );
        gl.glVertex3d( 0.5, -0.5, 0 );
        gl.glVertex3d( 0.5, 0.5, 0 );
        gl.glVertex3d( -0.5, 0.5, 0 );
      gl.glEnd();
    gl.glPopMatrix();
  }

  // draw text on screen with lower left corner at (x,y),
  // expanded or shrunk to scale 
  // with given thickness (as reps from 1 up)
  public static void drawText( String message, double x, double y, 
                                    double scale, int reps )
  {
    GLUT glut = new GLUT();
    int font = GLUT.STROKE_ROMAN;
  
    double shift = scale; 
  
    gl.glPushMatrix();
      for( int k=-reps; k<=reps; k++ ) 
        for( int j=-reps; j<=reps; j++ )
        {
          gl.glPushMatrix();
            gl.glTranslated( x + k*shift/3, y + j*shift/3, depth );
            gl.glScaled( scale, scale, 1 );
            glut.glutStrokeString( font, message );
          gl.glPopMatrix();
        }
    gl.glPopMatrix();

  }

  // return the width of the message, taking scale and reps into account
  public static double textWidth( String message, 
                                    double scale, int reps )
  {
    GLUT glut = new GLUT();
    int font = GLUT.STROKE_ROMAN;

    int pixels = glut.glutStrokeLength( font, message );

    return pixels*scale;
  }

  // draw message at scale with reps horizontally centered in box with 
  // left, right edges at given y value for baseline
  public static void drawHorizCenteredText( String message, 
                        double left, double right, double baseline,
                        double scale, int reps )
  {
    double w = textWidth( message, scale, reps );
    drawText( message, (left+right-w)/2, baseline, scale, reps );
  }
 
  public static void begin( String kind )
  {
    if( kind.equals( "polygon" ) )
      gl.glBegin( GL.GL_POLYGON );
    else if( kind.equals( "border" ) )
      gl.glBegin( GL.GL_LINE_LOOP );
    else if( kind.equals( "points" ) )
      gl.glBegin( GL.GL_POINTS );
    else if( kind.equals( "lines" ) )
      gl.glBegin( GL.GL_LINES );
    else
    {
      System.out.println("Unknown kind of primitive " + kind );
      System.exit(1);
    }
   
  }

  // draw a 2D point at given depth
  public static void point( double x, double y )
  {
    gl.glVertex3d( x, y, depth );
  }

  // draw any desired point in 3D space
  public static void point( double x, double y, double z )
  {
    gl.glVertex3d( x, y, z );
  }

  public static void end()
  {
    gl.glEnd();
  }

  public static void setPointSize( double s )
  {
    gl.glPointSize( (float) s );
  }

  public static double getDepth()
  {
    return -depth;
  }

//************************** don't look at stuff below unless **************
//                           you know what you're doing!

  
  // initialize things needed for these methods
  public static void init()
  {
    // build polygon approximation to circle once and for all
    double rads = 0;
    int num = SIDESFORCIRCLE;
    double dRads = 2*Math.PI/num;

    // compute this many points around unit circle
    circlePoints = new double[num][2];

    for( int k=0; k<num; k++ )
    {
      circlePoints[k][0] = Math.cos( rads );
      circlePoints[k][1] = Math.sin( rads );
      rads += dRads;
    }

  }

  //*************************** graphics stuff  **************************

  public static int SIDESFORCIRCLE = 36;
  private static GL gl;
  private static GLU glu;
  public static double MINDEPTH=1, MAXDEPTH=100;
  private static double depth = -1;

  private static double[][] circlePoints;

  private static AnimApp app;

  // AnimApp calls this in each display to refresh the gl
  public static void setup( GL g )
  {  
    gl = g;
    glu = new GLU();
    
    // OpenGL things that are done once per display
    gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
    gl.glEnable( GL.GL_DEPTH_TEST );

    gl.glDisable( GL.GL_BLEND );
    gl.glTexEnvi( GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE );

  }

  public static GL getGL()
  {  return gl;  }

  // draw circle (filled or not) 
  // (actually, a regular polygon with a lot of sides)
  // with center (x,y) and radius r
  // using unit circle points and transformations
  private static void drawCircleEntity( boolean filled,
                   double x, double y, double r )
  {
    gl.glPushMatrix();  // make sure this method doesn't mess up outside world
      gl.glTranslated( x, y, depth );
      gl.glScaled( r, r, 1 );
      if( filled ) gl.glBegin( GL.GL_POLYGON );
      else gl.glBegin( GL.GL_LINE_LOOP );
        for( int k=0; k<SIDESFORCIRCLE; ++k )
        {
          gl.glVertex3d( circlePoints[k][0], circlePoints[k][1], 0 );
        }   
      gl.glEnd();
    gl.glPopMatrix();
  }

  //****************** picture stuff *******************************************

 // draw rectangle with center point (x,y), width w, height h,
  //  rotated through angle, with picture with specified name texture
  //  mapped onto it
  public static boolean drawRect( double x, double y, double w, double h,
                                 double ang, String name )
  {
    // turn on texturing
    Picture.turnOn( gl );

    // use the named picture, or draw nothing
    if( ! usePicture( name ) )
    {
      System.out.println("couldn't use picture named [" + name + "]" );
      return false;
    }

    gl.glPushMatrix();
      gl.glTranslated( x, y, depth );
      gl.glRotated( ang, 0, 0, 1 );
      gl.glScaled( w, h, 1 );

      gl.glBegin( GL.GL_POLYGON );
        gl.glTexCoord2d( 0.0, 0.0 );
        gl.glVertex3d( -0.5, -0.5, 0 );
        gl.glTexCoord2d( 1.0, 0.0 );
        gl.glVertex3d( 0.5, -0.5, 0 );
        gl.glTexCoord2d( 1.0, 1.0 );
        gl.glVertex3d( 0.5, 0.5, 0 );
        gl.glTexCoord2d( 0.0, 1.0 );
        gl.glVertex3d( -0.5, 0.5, 0 );
      gl.glEnd();

    gl.glPopMatrix();

    // deactivate texture mapping
    Picture.turnOff( gl );

    return true;
  }

  // draw disk 
  // with center (x,y) and radius r
  // rotated through angle
  //  with picture with given name mapped onto it
  private static boolean drawTexDisk( double x, double y, double r, String picName, double ang )
  {
    // turn on texturing
    Picture.turnOn( gl );

    // use the named picture, or draw nothing
    if( ! usePicture( picName ) )
    {
      System.out.println("couldn't use picture named [" + picName + "]" );
      return false;
    }

    gl.glPushMatrix();
      gl.glTranslated( x, y, depth );
      gl.glScaled( r, r, 1 );

      gl.glRotated( ang, 0, 0, 1 );

      double da = 360.0/SIDESFORCIRCLE;
      double angle, cos, sin;

      gl.glBegin( GL.GL_POLYGON );
        for( int k=0; k<SIDESFORCIRCLE; ++k )
        {
          angle = Math.toRadians(k*da);  cos = Math.cos( angle );  sin = Math.sin( angle );
          gl.glTexCoord2d( 0.5 + 0.5*cos, 0.5 + 0.5*sin );
          gl.glVertex3d( circlePoints[k][0], circlePoints[k][1], 0 );
        }
      gl.glEnd();
    gl.glPopMatrix();

    // deactivate texture mapping
    Picture.turnOff( gl );

    return true;
  }

  private static int nextPictureCode = 0;
  private static ArrayList<Picture> pictures = new ArrayList<Picture>();

  // construct a new Picture from given fileName and add to list
  //  with specified picture name
  public static void addPicture( String fileName, String picName )
  {
    nextPictureCode++;
    pictures.add( new Picture( fileName, picName, nextPictureCode, 0, gl ) );   
  }

  // if find picture with given name in list, activate it and return true,
  // otherwise return false
  public static boolean usePicture( String picName )
  {
    int pos = -1;
    for( int k=0; k<pictures.size() && pos==-1; k++ )
    {
      if( pictures.get(k).getName().equals( picName ) )
      {
        pos = k;
      }
    }
 
    if( pos == -1 )
      return false;
    else
    {
      pictures.get( pos ).activate( gl );
      return true;
    }
 
  }

  //****************** midi stuff ***************************************

  private static Synthesizer synth = initMidi();
  private static MidiChannel[] channels;

  private static Synthesizer initMidi()
  {
    Synthesizer syn = null;

    try{
      syn = MidiSystem.getSynthesizer();
      syn.open();
      channels = syn.getChannels();
    }
    catch( Exception e )
    {
      System.out.println("AALib could not initialize MIDI");
      e.printStackTrace();
      System.exit(1);
    }

    return syn;
  }
    
  public static void setInstrument( int ch, int inst )
  {
    channels[ch].programChange( inst );
  }

  public static void playNote( int ch, int freq, int dur )
  {
    channels[ch].noteOn( freq, dur );
    try{ Thread.sleep( 5000 ); }catch(Exception e){}
    channels[ch].noteOff( freq, dur );
  }

  public static void main(String[] args)
  {
    setInstrument( 0, 124 );
    playNote( 0, 440, 1000 );
    playNote( 0, 880, 1000);
    playNote( 0, 110, 1000);
    synth.close();
  }

}
