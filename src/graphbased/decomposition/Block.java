package graphbased.decomposition;

/**
 * 
 * @author daniel tremmel
 * 
 *         This class is the base class for the blocks that are built at the end
 *         of the picture compression. It contains all necessary block
 *         attributes, as well as the corresponding getters and setters.
 *
 */
public class Block {

	private int x;
	private int y;
	private int width;
	private int height;

	public Block(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
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

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
