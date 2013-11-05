import java.lang.Object;


public class Projectile
{
//Instance Variables
   public double xLocation, yLocation, xInitial, yInitial;//Location variables
   Vector velocity;     //Direction and Magnatude(speed) 
                        //(assumes i and j are constant)
   Vector acceleration; //Direction and Magnatude 
                        //(assumes i and j are constant)
   public RGB color;    //color of projectile when drawn  
                      
//public double friction; //friction of surface
//public double xCM, yCM; //x and y center of mass
//public double torque;


//Constructor
   public Projectile (double xIn, double yIn, Vector velocityIn, Vector accelerationIn)
   {
      xLocation = xIn; //current location
      yLocation = yIn; //current location
      xInitial = xIn; //saves initial location
      yInitial = yIn; //saves initial location
      velocity = velocityIn;
      acceleration = accelerationIn;
      color = RGB.BLACK;
   }

   public double getX()
   {
      return xLocation;
   }

   public double getY()
   {
      return yLocation;
   }

   public void move(double time) 
                    //move projectile with respect to Velocity, Acceleration, and Gravity
                   //time based
                   //displacement = vt + 1/2 at^2 (gravity not assumed)
   {
      xLocation = this.xInitial + (velocity.i * time)   
            + (acceleration.i * Math.pow(time,2) )/2;
      yLocation = this.yInitial + (velocity.j * time)   
            + (acceleration.j * Math.pow(time,2) )/2;
   }

   public void addVelocity(Vector vectorToAdd)
                    //adds an additional vector to what is currently acting upon object
                    //to be used for collisions and such
   {
      this.velocity = this.velocity.add(vectorToAdd); //in Vector Class
   }

   public void addAcceleration(Vector vectorToAdd)
                    //adds an additional vector to what is currently acting upon object
                    //to be used for gravity and such
   {
      this.acceleration = this.acceleration.add(vectorToAdd); //in Vector Class
   }
   public void draw() //projectile drawn with default color Black and width/height 2
   {
         AALib.setColor(color);
         AALib.setDepth(2);
         AALib.drawRect(this.xLocation, this.yLocation, 2.0, 2.0);
   }
   
   
public static void main(String[] args)
{
Vector velocity = new Vector(1,3);
Vector acceleration = new Vector(0,0);
Vector velocity2 = new Vector(1,2);
Projectile projectile = new Projectile(1,1, velocity, acceleration);
System.out.println("x = " + projectile.getX());
System.out.println("y = " + projectile.getY());
System.out.println("vector " + projectile.velocity);
projectile.move(1);
System.out.println("x = " + projectile.getX());
System.out.println("y = " + projectile.getY());
System.out.println("vector " + projectile.velocity);
projectile.addVelocity(velocity2);
   System.out.println("x = " + projectile.getX());
System.out.println("y = " + projectile.getY());
System.out.println("vector " + projectile.velocity);
projectile.draw();
}
}
