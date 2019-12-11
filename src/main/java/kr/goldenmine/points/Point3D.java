package kr.goldenmine.points;

public class Point3D extends Point {
    public double z;

    public Point3D(double x, double y, double z) {
        super(x, y);
        this.z = z;
    }

    public Point get2DPoint(double eye, double mul) {
        double x = this.x / (z + eye) * eye * mul;

        double y = this.y / (z + eye) * eye * mul;

        return new Point(x, y);
    }

    public Point3D out3D(Point3D p3d) {
        return new Point3D(y * p3d.z - z * p3d.y, z * p3d.y - x * p3d.z, x * p3d.y - y * p3d.x);
    }

    public Point3D getRotatePoint(double yaw, double pitch) {
        // x1 = b * cos(pitch) - a * sin(pitch)
        // y1 = b * sin(pitch) + a * cos(pitch)

        double a = this.x;
        double b = this.y;
        double c = this.z;
        //double z = this.z;

        double d = degree(pitch);
        double d2 = degree(yaw);

        double x1 = b * Math.cos(d) - a * Math.sin(d);

        double y1 = b * Math.sin(d) + a * Math.cos(d);

        double x2 = x1;
        double y2 = c * Math.cos(d2) - y1 * Math.sin(d2);
        double z2 = c * Math.sin(d2) + y1 * Math.cos(d2);

        return new Point3D(x2, y2, z2);
    }

    public static Point3D getDirection(double yaw, double pitch, double multiply) {
        double xz = Math.cos(Math.toRadians(pitch));
        return new Point3D(-xz * Math.sin(Math.toRadians(yaw)) * multiply, Math.sin(Math.toRadians(pitch)) * multiply, xz * Math.cos(Math.toRadians(yaw)) * multiply);
    }

    public String toString() {
        return x + ", " + y + ", " + z;
    }
}
