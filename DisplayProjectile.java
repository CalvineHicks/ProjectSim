import java.util.*;
import java.awt.event.*;
import java.io.*;

public class DisplayProjectile extends AnimApp
{
//static variables*********************************************************************
private final static int HSIZE=1000, VSIZE=1000;
   
      public static void main(String[] args)
      {
         DisplayProjectile app = new DisplayProjectile();
      }
//Instance Variables*********************************************************************
private Viewer viewer;
public  Vector velocity = new Vector(4, 9); //velocity of projectile
public  Vector acceleration = new Vector(0,-.98); //acceleration of projectile
public  Projectile projectile = new Projectile(1,1, velocity, acceleration);
public double time = 0; 
public double timeIncrement = .1;
public int groundHeight = 10;
      
public DisplayProjectile()
      {
         super( "shoot that thing!", 
             HSIZE, VSIZE, 33333333 );
      }
public void setup()
      {
      addKeyListener(this);
      viewer = new Viewer( 0, 0, HSIZE, VSIZE,
                         0.0, 100.0, 0.0, 100.0,
                         new RGB( 150, 150, 150 ) );
      
      }
public void step()
      {
         viewer.activate();
         viewer.drawBackground();
         AALib.setColor(RGB.RED);
         AALib.drawRect(50, 0, 100, groundHeight);
         projectile.draw();
         projectile.move(time);
         time = time + timeIncrement;
      }
}
