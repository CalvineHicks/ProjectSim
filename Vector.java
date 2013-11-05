import java.lang.Object;

public class Vector
{
//Instance Variables
   double i, j; //becomes problamatic when expressed as a function of t

//Constructor
   public Vector (double iIn, double jIn)
   {
      i = iIn;
      j = jIn;
   }

   public double magnatude()
   {
      double magnatude = Math.sqrt(Math.pow(i,2) + Math.pow(j,2)); //distance equation
      return magnatude;
   }

   public double direction()
   {
      double direction = Math.atan(j/i);  //arctan of y/x gives angle of direction in radians
   
      if (i > 0) //angle is in first or forth quadrint
      {
         return direction; 
      }
      else     //(i < 0) angle of direction is outside domain of arctan function (-90 to 90 degrees)
               //pi must be added to rezult
      {
         direction = direction + Math.PI;
         return direction;
      }
   }

   public Vector add(Vector vectorToAdd)
   {
      this.i += vectorToAdd.i;
      this.j += vectorToAdd.j;
      return this;
   }
   
   public String toString()
   {
   String s;
   s = "i is: " + i + " j is: " + j;
   return s;
   }
}