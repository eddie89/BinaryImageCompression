package constantbitlengthquadtree.decomposition;

/**
 * 
 * @author daniel tremmel
 * 
 *         This class is the base class for the sectors. It contains all
 *         necessary block attributes, as well as the corresponding getters and
 *         setters.
 *
 */
public class Sector {

	private int x, y, width, depth;

	public Sector(int x, int y, int width, int depth) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.depth = depth;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

}
