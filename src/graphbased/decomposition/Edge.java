package graphbased.decomposition;

import java.util.List;

/**
 * 
 * @author daniel tremmel
 * 
 *         This class is a base class for the edge, containing all corresponding
 *         attributes, as well as their getters and setters.
 *
 */
public class Edge {

	private int id;
	private List<Pixel> vertices;
	private Angle angle;
	private int row;

	public Edge(int id, List<Pixel> vertices, Angle angle, int row) {
		this.id = id;
		this.vertices = vertices;
		this.angle = angle;
		this.row = row;
	}

	public Edge(List<Pixel> vertices, Angle angle) {
		this.vertices = vertices;
		this.angle = angle;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Pixel> getVertices() {
		return vertices;
	}

	public void setVertices(List<Pixel> edges) {
		this.vertices = edges;
	}

	public Angle getAngle() {
		return angle;
	}

	public void setAngle(Angle angle) {
		this.angle = angle;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

}
