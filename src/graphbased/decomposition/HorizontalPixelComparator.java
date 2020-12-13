package graphbased.decomposition;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 
 * @author daniel tremmel
 * 
 *         This class is a comparator class in order to compare all pixels in a
 *         collection. The comparator orders the pixels in the collection
 *         according to their horizontal order.
 *
 */
public class HorizontalPixelComparator implements Comparator<Pixel>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1994396935563407231L;

	@Override
	public int compare(Pixel o1, Pixel o2) {
		if (o1.getCoordinates().getX() > o2.getCoordinates().getX()) {
			return 1;
		} else if (o1.getCoordinates().getX() == o2.getCoordinates().getX()) {
			return 0;
		} else {
			return -1;
		}
	}
}
