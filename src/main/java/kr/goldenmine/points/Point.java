package kr.goldenmine.points;

public class Point {
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point toPosition(double midx, double midy, int onesize) {
        return new Point(midx + x * onesize, midy - y * onesize);
    }

    public Point getRotatePoint(double yaw, double pitch) {
        // x1 = b * cos(pitch) - a * sin(pitch)
        // y1 = b * sin(pitch) + a * cos(pitch)

        double a = this.x;
        double b = this.y;

        double d = degree(pitch);

        double x1 = b * Math.cos(d) - a * Math.sin(d);

        double y1 = b * Math.sin(d) + a * Math.cos(d);

        return new Point(x1, y1);
    }

    public static double degree(double ra) {
        return ra * Math.PI / 180;
    }

    public String toString() {
        return x + ", " + y;
    }

    //public Point getNearPoint(int xsize, int ysize) {


    //}
}
