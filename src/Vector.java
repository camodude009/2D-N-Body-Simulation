public class Vector {
	public double x, y;

	public Vector() {
	}

	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector add(Vector v) {
		return new Vector(x + v.x, y + v.y);
	}

	public void addS(Vector v) {
		x += v.x;
		y += v.y;
	}

	public Vector subtract(Vector v) {
		return new Vector(x - v.x, y - v.y);
	}

	public void subtractS(Vector v) {
		x -= v.x;
		y -= v.y;
	}

	public Vector multiply(double s) {
		return new Vector(x * s, y * s);
	}

	public void multiplyS(double s) {
		x *= s;
		y *= s;
	}

	public Vector divide(double s) {
		return new Vector(x / s, y / s);
	}

	public void divideS(double s) {
		x /= s;
		y /= s;
	}

	public Vector square() {
		return new Vector(x * x, y * y);
	}

	public void squareS() {
		x *= x;
		y *= y;
	}

	public double length2() {
		return (x * x + y * y);
	}

	public double length() {
		return Math.sqrt(length2());
	}

	public double length3() {
		return (length2() * length());
	}
}
