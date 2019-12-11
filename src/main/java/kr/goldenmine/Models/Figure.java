package kr.goldenmine.Models;

import kr.goldenmine.points.Point;
import kr.goldenmine.points.Point3D;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Figure {
	// 좌표들
	List<Point3D> points;

	// 어떤 좌표를 연결시킬 건가
	List<Point> connects = new ArrayList<>();

	// 색깔은 어떻게 할건가
	Color color = Color.BLACK;
	
	public Figure(List<Point3D> points) {
		this.points = points;
	}
	
	public Figure(List<Point3D> points, List<Point> connects) {
		this(points);
		this.connects = connects;
	}
	
	public Figure(List<Point3D> points, List<Point> connects, Color color) {
		this(points, connects);
		this.color = color;
	}

	public List<Point3D> getCoordinates() {
		return points;
	}
	
	public List<Point> getConnects() {
		return connects;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
