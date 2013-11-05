#include <math.h>
class Vector
{
	//Instance Variables
	double i, j; //becomes problamatic when expressed as a function of t
	const double PI = 3.141592653589793238462; //Math.h has no pi constant, so number must be used

	//Constructor
public:	Vector(double iIn, double jIn)

{
			i = iIn;
			j = jIn;
}

public: double magnitude()
{
			double magnitude = sqrt(pow(i, 2) + pow(j, 2)); //distance equation
			return magnitude;
}

public: double direction()
{
			double direction = atan(j / i);  //arctan of y/x gives angle of direction in radians

			if (i > 0) //angle is in first or forth quadrant
			{
				return direction;
			}
			else
				//(i < 0) angle of direction is outside domain of arctan function (-90 to 90 degrees)
				//pi must be added to rezult
			{
				direction = direction + PI;
				return direction;
			}
}

public: Vector add(Vector vectorToAdd)
{
			this->i += vectorToAdd.i;
			this->j += vectorToAdd.j;

			return *this;
}

};

//C++ has no toString, so other functions must be used
//public: String toString()
//{
//	String s;
//	s = "i is: " + i + " j is: " + j;
//	return s;
//}
