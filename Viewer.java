public class Viewer
{
  private int x, y, w, h;  // rectangle with lower left corner (x,y),
                           // width w, height h in pixel grid that
                           // this viewer draws in

  private double left, right, bottom, top; // front of view volume in 3D space

  private RGB backgroundColor;  // color of background

  private boolean view2D;

  // these are only used for 3D viewing-----------------------

  private double near, far;  // distance to near, far clipping plane in z
                             // direction of view volume

  private Triple eye, center, up;  // info to specify 3D camera

  // construct 2D viewer with permanent rectangle within screen pixel grid,
  // and changeable limits of visible rectangle on the graph paper
  public Viewer( int llcX, int llcY, int width, int height,
                 double l, double r, double b, double t,
                 RGB color )
  {
    view2D = true;

    x = llcX;  y = llcY;  w = width;  h = height;
    left = l;  right = r;  bottom = b;  top = t;
    backgroundColor = color;
  }

  // construct 3D viewer with specified pixel grid region in window
  // and view volume info, with default camera setup
  // and changeable limits of visible rectangle on the graph paper
  public Viewer( int llcX, int llcY, int width, int height,
                 double l, double r, double b, double t, double n, double f,
                 RGB color )
  {
    view2D = false;

    x = llcX;  y = llcY;  w = width;  h = height;
    left = l;  right = r;  bottom = b;  top = t; near = n;  far = f;
    backgroundColor = color;

    // default camera has eye at origin, looking along negative z axis
    // with positive y axis the up direction
    eye = new Triple(0,0,0);
    center = new Triple( 0, 0, -1 );
    up = new Triple( 0, 1, 0 );
  }

  public void setLimits( double l, double r,
                                double b, double t )
  {
    left = l;  right = r;  bottom = b;  top = t;
  }

  // prepare the viewer for displaying to---must be done
  // each time want to display what the viewer sees
  public void activate()
  {
    if( view2D )
    {// activate viewer for 2D viewing
      AALib.setLimits( left, right, bottom, top );
      AALib.setViewport( x, y, w, h );
    }
    else
    {// activate viewer for 3D viewing
      AALib.setViewport( x, y, w, h );
      AALib.setCamera( left, right, bottom, top, near, far,
                       eye, center, up );
    }
  }

  public void setBackColor( RGB color )
  {
    backgroundColor = color;    
  }

  public void drawBackground()
  {
    AALib.setBackColor( backgroundColor );
    AALib.clearBackground();
  }

  public boolean hits( int mx, int my )
  {
    return x<=mx && mx<=x+w && y<=my && my<=y+h;
  }

  public double getMouseX( int mx )
  {
    return left + (mx-x)*(right-left)/w;
  }

  public double getMouseY( int my )
  {
    return bottom + (my-y)*(top-bottom)/h;
  }

  public double getLeft()
  {
    return left;
  }

  public double getRight()
  {
    return right;
  }

  public double getBottom()
  {
    return bottom;
  }

  public double getTop()
  {
    return top;
  }

  public void shift( double dx, double dy )
	{
		left += dx;  right += dx;
		bottom += dy;  top += dy; 
	}
	
  // position eye point at given radius and angle (in degrees) on circle about
  // the current center, shifted up/down to altitude
  public void positionCamera( double radius, double azimuth, double altitude )
  {
    eye = new Triple( center.x + 
                       radius*Math.cos(Math.toRadians(altitude))*
                                Math.cos( Math.toRadians( azimuth )), 
                      center.y + radius*Math.sin(Math.toRadians(altitude)),
                      center.z - radius*Math.cos(Math.toRadians(altitude))*
                                Math.sin( Math.toRadians( azimuth ))
                    );
  }

  public void shiftCamera( double dx, double dy, double dz )
  {
    center.x += dx;
    center.y += dy;
    center.z += dz;
  }

}
