package kr.goldenmine.models;

import kr.goldenmine.points.Point;
import kr.theterroronline.util.physics.Vector3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Figure {
	// 좌표들
	private List<Vector3d> coordinates;

	// 어떤 좌표를 연결시킬 건가
	private List<Point> connects = new ArrayList<>();

	// 색깔은 어떻게 할건가
	private Color color = Color.BLACK;
	
	protected Figure(List<Vector3d> coordinates) {
		this.coordinates = coordinates;
	}

	protected Figure(List<Vector3d> coordinates, List<Point> connects) {
		this(coordinates);
		this.connects = connects;
	}

	protected Figure(List<Vector3d> coordinates, List<Point> connects, Color color) {
		this(coordinates, connects);
		this.color = color;
	}

	public List<Vector3d> getCoordinates() {
		return coordinates;
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
