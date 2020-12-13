package rowsegment.decomposition;

/**
 * 
 * @author daniel tremmel
 * 
 *         This class contains all row attributes, as well as their
 *         corresponding getters and setters.
 *
 */
public class Row {

	private int y, start, stop;

	public Row(int y, int start, int stop) {
		this.y = y;
		this.start = start;
		this.stop = stop;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getStop() {
		return stop;
	}

	public void setStop(int stop) {
		this.stop = stop;
	}

}
