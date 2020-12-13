package graphbased.decomposition;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author daniel tremmel
 *
 *         This class contains all pixel attributes, as well as the methods to
 *         collect the neighbors around it and getters and setters.
 */
public class Pixel {

	private Coordinates coordinates;
	private boolean isBlack;
	private boolean isConcave;
	private boolean isGap;
	private boolean isInsideFigure;
	private boolean isBoundary;
	private boolean isBorder;
	private List<Pixel> neighbors;
	private Direction direction;

	public Pixel(Coordinates coordinates, boolean isBlack) {
		this.coordinates = coordinates;
		this.isBlack = isBlack;
	}

	public Pixel(Coordinates coordinates, boolean isBlack, boolean isGap) {
		this.coordinates = coordinates;
		this.isGap = isGap;
	}

	public Pixel(Direction direction, Coordinates coordinates, boolean isBlack) {
		this.direction = direction;
		this.coordinates = coordinates;
		this.isBlack = isBlack;
	}

	public Pixel(Coordinates coordinates, boolean isBlack, List<Pixel> neighbors) {
		this.coordinates = coordinates;
		this.isBlack = isBlack;
		this.neighbors = neighbors;
	}

	public Pixel(Direction direction, Coordinates coordinates, List<Pixel> neighbors) {
		this.direction = direction;
		this.coordinates = coordinates;
		this.neighbors = neighbors;
	}

	public Pixel(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	public Pixel(Direction direction, Coordinates coordinates) {
		this.direction = direction;
		this.coordinates = coordinates;
	}

	public Direction getConcaveDirection() {
		return direction;
	}

	public void setConcaveDirection(Direction direction) {
		this.direction = direction;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	public boolean isBlack() {
		return isBlack;
	}

	public void setBlack(boolean isBlack) {
		this.isBlack = isBlack;
	}

	public boolean isConcave() {
		return isConcave;
	}

	public void setConcave(boolean isConcave) {
		this.isConcave = isConcave;
	}

	public boolean isGap() {
		return isGap;
	}

	public void setGap(boolean isGap) {
		this.isGap = isGap;
	}

	public boolean isInsideFigure() {
		return isInsideFigure;
	}

	public void setInsideFigure(boolean isInsideFigure) {
		this.isInsideFigure = isInsideFigure;
	}

	public boolean isBoundary() {
		return isBoundary;
	}

	public void setBoundary(boolean isBoundary) {
		this.isBoundary = isBoundary;
	}

	public boolean isBorder() {
		return isBorder;
	}

	public void setBorder(boolean isBorder) {
		this.isBorder = isBorder;
	}

	public List<Pixel> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(List<Pixel> neighbors) {
		this.neighbors = neighbors;
	}

	/**
	 * 
	 * This method collects all neighbors surrounding a pixel. At the end the list
	 * with all pixel neighbors is returned.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param pixelArray
	 * @return
	 */
	public static List<Pixel> collectNeighbors(int x, int y, int width, int height, Pixel[][] pixelArray) {
		List<Pixel> neighbors = new ArrayList<>();
		Pixel lowerLeftPixel;
		Pixel lowerPixel;
		Pixel lowerRightPixel;
		Pixel rightPixel;
		Pixel upperRightPixel;
		Pixel upperPixel;
		Pixel upperLeftPixel;
		Pixel leftPixel;
		if (y + 2 >= height || x - 2 == -1) {
			lowerLeftPixel = new Pixel(new Coordinates(x - 2, y + 2), true);
			neighbors.add(lowerLeftPixel);
		} else {
			if (!pixelArray[x - 2][y + 2].isBlack()) {
				lowerLeftPixel = new Pixel(new Coordinates(x - 2, y + 2), false);
			} else {
				lowerLeftPixel = new Pixel(new Coordinates(x - 2, y + 2), true);
			}
			neighbors.add(lowerLeftPixel);
		}
		if (y + 2 >= height) {
			lowerPixel = new Pixel(new Coordinates(x, y + 2), true);
			neighbors.add(lowerPixel);
		} else {
			if (!pixelArray[x][y + 2].isBlack()) {
				lowerPixel = new Pixel(new Coordinates(x, y + 2), false);
			} else {
				lowerPixel = new Pixel(new Coordinates(x, y + 2), true);
			}
			neighbors.add(lowerPixel);
		}
		if (y + 2 >= height || x + 2 >= width) {
			lowerRightPixel = new Pixel(new Coordinates(x + 2, y + 2), true);
			neighbors.add(lowerRightPixel);
		} else {
			if (!pixelArray[x + 2][y + 2].isBlack()) {
				lowerRightPixel = new Pixel(new Coordinates(x + 2, y + 2), false);
			} else {
				lowerRightPixel = new Pixel(new Coordinates(x + 2, y + 2), true);
			}
			neighbors.add(lowerRightPixel);
		}
		if (x + 2 >= width) {
			rightPixel = new Pixel(new Coordinates(x + 2, y), true);
			neighbors.add(rightPixel);
		} else {
			if (!pixelArray[x + 2][y].isBlack()) {
				rightPixel = new Pixel(new Coordinates(x + 2, y), false);
			} else {
				rightPixel = new Pixel(new Coordinates(x + 2, y), true);
			}
			neighbors.add(rightPixel);
		}
		if (y - 2 == -1 || x + 2 >= width) {
			upperRightPixel = new Pixel(new Coordinates(x + 2, y - 2), true);
			neighbors.add(upperRightPixel);
		} else {
			if (!pixelArray[x + 2][y - 2].isBlack()) {
				upperRightPixel = new Pixel(new Coordinates(x + 2, y - 2), false);
			} else {
				upperRightPixel = new Pixel(new Coordinates(x + 2, y - 2), true);
			}
			neighbors.add(upperRightPixel);
		}
		if (y - 2 == -1) {
			upperPixel = new Pixel(new Coordinates(x, y - 2), true);
			neighbors.add(upperPixel);
		} else {
			if (!pixelArray[x][y - 2].isBlack()) {
				upperPixel = new Pixel(new Coordinates(x, y - 2), false);
			} else {
				upperPixel = new Pixel(new Coordinates(x, y - 2), true);
			}
			neighbors.add(upperPixel);
		}
		if (y - 2 == -1 || x - 2 == -1) {
			upperLeftPixel = new Pixel(new Coordinates(x - 2, y - 2), true);
			neighbors.add(upperLeftPixel);
		} else {
			if (!pixelArray[x - 2][y - 2].isBlack()) {
				upperLeftPixel = new Pixel(new Coordinates(x - 2, y - 2), false);
			} else {
				upperLeftPixel = new Pixel(new Coordinates(x - 2, y - 2), true);
			}
			neighbors.add(upperLeftPixel);
		}
		if (x - 2 == -1) {
			leftPixel = new Pixel(new Coordinates(x - 2, y), true);
			neighbors.add(leftPixel);
		} else {
			if (!pixelArray[x - 2][y].isBlack()) {
				leftPixel = new Pixel(new Coordinates(x - 2, y), false);
			} else {
				leftPixel = new Pixel(new Coordinates(x - 2, y), true);
			}
			neighbors.add(leftPixel);
		}
		// the following 2 pixels must be added again so the
		// Collections.indexOfSubList
		// method can be called upon the list with 3 values
		// so the list can be treated like a circle
		neighbors.add(lowerLeftPixel);
		neighbors.add(lowerPixel);
		return neighbors;
	}

	/**
	 * This method collects all neighbors of the gapPixels. At the end a list with
	 * all neighbor pixels is returned.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param pixelArray
	 * @return
	 */
	public static List<Pixel> collectGapNeighbors(int x, int y, int width, int height, Pixel[][] pixelArray) {
		List<Pixel> neighbors = new ArrayList<>();
		Pixel lowerLeftPixel;
		Pixel lowerPixel;
		Pixel lowerRightPixel;
		Pixel rightPixel;
		Pixel upperRightPixel;
		Pixel upperPixel;
		Pixel upperLeftPixel;
		Pixel leftPixel;
		List<Boolean> blackAndwhiteList = new ArrayList<>();
		if (y + 1 >= height || x - 1 == -1) {
		} else {
			if (!pixelArray[x - 1][y + 1].isBlack()) {
				blackAndwhiteList.add(pixelArray[x - 1][y + 1].isBlack());
				lowerLeftPixel = new Pixel(new Coordinates(x - 1, y + 1), false);
			} else {
				blackAndwhiteList.add(pixelArray[x - 1][y + 1].isBlack());
				lowerLeftPixel = new Pixel(new Coordinates(x - 1, y + 1), true);
			}
			neighbors.add(lowerLeftPixel);
		}
		if (y + 1 >= height) {
		} else {
			if (!pixelArray[x][y + 1].isBlack()) {
				blackAndwhiteList.add(pixelArray[x][y + 1].isBlack());
				lowerPixel = new Pixel(new Coordinates(x, y + 1), false);
			} else {
				blackAndwhiteList.add(pixelArray[x][y + 1].isBlack());
				lowerPixel = new Pixel(new Coordinates(x, y + 1), true);
			}
			neighbors.add(lowerPixel);
		}
		if (y + 1 >= height || x + 1 >= width) {
		} else {
			if (!pixelArray[x + 1][y + 1].isBlack()) {
				blackAndwhiteList.add(pixelArray[x + 1][y + 1].isBlack());
				lowerRightPixel = new Pixel(new Coordinates(x + 1, y + 1), false);
			} else {
				blackAndwhiteList.add(pixelArray[x + 1][y + 1].isBlack());
				lowerRightPixel = new Pixel(new Coordinates(x + 1, y + 1), true);
			}
			neighbors.add(lowerRightPixel);
		}
		if (x + 1 >= width) {
		} else {
			if (!pixelArray[x + 1][y].isBlack()) {
				blackAndwhiteList.add(pixelArray[x + 1][y].isBlack());
				rightPixel = new Pixel(new Coordinates(x + 1, y), false);
			} else {
				blackAndwhiteList.add(pixelArray[x + 1][y].isBlack());
				rightPixel = new Pixel(new Coordinates(x + 1, y), true);
			}
			neighbors.add(rightPixel);
		}
		if (y - 1 == -1 || x + 1 >= width) {
		} else {
			if (!pixelArray[x + 1][y - 1].isBlack()) {
				blackAndwhiteList.add(pixelArray[x + 1][y - 1].isBlack());
				upperRightPixel = new Pixel(new Coordinates(x + 1, y - 1), false);
			} else {
				blackAndwhiteList.add(pixelArray[x + 1][y - 1].isBlack());
				upperRightPixel = new Pixel(new Coordinates(x + 1, y - 1), true);
			}
			neighbors.add(upperRightPixel);
		}
		if (y - 1 == -1) {
		} else {
			if (!pixelArray[x][y - 1].isBlack()) {
				blackAndwhiteList.add(pixelArray[x][y - 1].isBlack());
				upperPixel = new Pixel(new Coordinates(x, y - 1), false);
			} else {
				blackAndwhiteList.add(pixelArray[x][y - 1].isBlack());
				upperPixel = new Pixel(new Coordinates(x, y - 1), true);
			}
			neighbors.add(upperPixel);
		}
		if (y - 1 == -1 || x - 1 == -1) {
		} else {
			if (!pixelArray[x - 1][y - 1].isBlack()) {
				blackAndwhiteList.add(pixelArray[x - 1][y - 1].isBlack());
				upperLeftPixel = new Pixel(new Coordinates(x - 1, y - 1), false);
			} else {
				blackAndwhiteList.add(pixelArray[x - 1][y - 1].isBlack());
				upperLeftPixel = new Pixel(new Coordinates(x - 1, y - 1), true);
			}
			neighbors.add(upperLeftPixel);
		}
		if (x - 1 == -1) {
		} else {
			if (!pixelArray[x - 1][y].isBlack()) {
				blackAndwhiteList.add(pixelArray[x - 1][y].isBlack());
				leftPixel = new Pixel(new Coordinates(x - 1, y), false);
			} else {
				blackAndwhiteList.add(pixelArray[x - 1][y].isBlack());
				leftPixel = new Pixel(new Coordinates(x - 1, y), true);
			}
			neighbors.add(leftPixel);
		}
		return neighbors;
	}

}
