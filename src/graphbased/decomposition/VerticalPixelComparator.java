package graphbased.decomposition;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 
 * @author daniel tremmel
 * 
 *         This class is a comparator class in order to compare all pixels in a
 *         collection. The comparator orders the pixels in the collection
 *         according to their vertical order.
 *
 */
public class VerticalPixelComparator implements Comparator<Pixel>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 745041295210547483L;

	@Override
	public int compare(Pixel o1, Pixel o2) {
		if (o1.getCoordinates().getY() > o2.getCoordinates().getY()) {
			return 1;
		} else if (o1.getCoordinates().getY() == o2.getCoordinates().getY()) {
			return 0;
		} else {
			return -1;
		}
	}
}
