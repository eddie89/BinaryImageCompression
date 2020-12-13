package constantbitlengthquadtree.decomposition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import javafx.scene.image.Image;

/**
 * 
 * @author daniel tremmel
 *
 *         This class contains the logic of the constant bit length
 *         decomposition. It transfers the given picture into a bitsequence and
 *         also recreates the picture from the bitsequence.
 */
public class Segmentator {

	final int width = 400;
	final int height = 400;
	private PictureLauncher launcher;

	/**
	 * This method has the purpose to rebuild the picture out of the bitsequence
	 * that was passed over as parameter. At first, a Stack is filled with the first
	 * sector, with the size of the complete picture. If the given indicator of the
	 * sequence is 2 or 3, the given sector is again passed to the method
	 * fillSectorStack. If the indicator is, the given sector is popped from the
	 * stack and passed to a list, otherwise the sector is just popped from the
	 * stack.
	 * 
	 * 
	 * @param bitSequence
	 * @return
	 */
	public List<Sector> rebuildPicture(String bitSequence) {
		int x = 0, y = 0;
		Stack<Sector> sectorStack = new Stack<>();
		Sector sector = new Sector(x, y, width, height);
		List<Sector> sectorList = new ArrayList<>();
		fillSectorStack(sectorStack, sector);
		for (int i = 0; i < bitSequence.length(); i++) {
			int indicator = Character.getNumericValue(bitSequence.charAt(i));
			if (indicator == 2 || indicator == 3) {
				fillSectorStack(sectorStack, sectorStack.pop());
			} else if (indicator == 1) {
				sectorList.add(sectorStack.pop());
			} else {
				sectorStack.pop();
			}
		}
		return sectorList;
	}

	/**
	 * This method computes the bitsequence out of the picture. At first it fills
	 * the sectorStack. While the sectorStack is not empty, it takes the top element
	 * from the stack and examines if it contains either white or black colors. If
	 * the sector is black and white, if examines its externality by the method
	 * computeExternality. Based on the result it is either 2 added to the
	 * bitsequence, which means that the given sector contains further sectors that
	 * need to be divided, or 3 if the given sector contains 4 sectors that are
	 * either black or white and none of them needs to be divided further. In both
	 * cases the sectorStack is filled again. If the sector is only black, 1 is
	 * added to the bitsequence, if it is only white 0 is added. At the end the
	 * bitsequence is returned.
	 * 
	 * 
	 * @param image
	 * @return
	 */
	public String computeBitSequence(Image image) {
		int x = 0, y = 0;
		Stack<Sector> sectorStack = new Stack<>();
		boolean[] blackAndwhite;
		Sector firstSector = new Sector(x, y, width, height);
		fillSectorStack(sectorStack, firstSector);
		String bitSequence = "";
		launcher = new PictureLauncher();
		while (!sectorStack.isEmpty()) {
			Sector sector = sectorStack.pop();
			blackAndwhite = new boolean[(int) sector.getWidth() * (int) sector.getDepth()];
			blackAndwhite = launcher.readPixels(sector.getX(), sector.getY(), sector.getWidth(), sector.getDepth(),
					image);
			if (Arrays.toString(blackAndwhite).contains("true")) {
				if (Arrays.toString(blackAndwhite).contains("false")) {
					fillSectorStack(sectorStack, sector);
					boolean isBlackORWhite = computeExternality(sector, image);
					if (isBlackORWhite) {
						bitSequence += 3;
					} else {
						bitSequence += 2;
					}
				} else {
					bitSequence += 1;
				}
			} else {
				bitSequence += 0;
			}
		}
		return bitSequence;
	}

	/**
	 * This method takes the given sectorStack and the given sector as parameters,
	 * divides the sector into 4 parts, with 1 for each direction and adds them to
	 * the bottom of the sectorStack.
	 * 
	 * @param sectorStack
	 * @param sector
	 */
	private void fillSectorStack(Stack<Sector> sectorStack, Sector sector) {
		Sector southeast = new Sector((sector.getX() + (sector.getWidth() - sector.getX()) / 2),
				(sector.getY() + ((sector.getDepth() - sector.getY()) / 2)), sector.getWidth(), sector.getDepth());
		sectorStack.add(0, southeast);
		Sector southwest = new Sector(sector.getX(), (sector.getY() + ((sector.getDepth() - sector.getY()) / 2)),
				(sector.getX() + (sector.getWidth() - sector.getX()) / 2), sector.getDepth());
		sectorStack.add(0, southwest);
		Sector northeast = new Sector((sector.getX() + (sector.getWidth() - sector.getX()) / 2), sector.getY(),
				sector.getWidth(), (sector.getY() + ((sector.getDepth() - sector.getY()) / 2)));
		sectorStack.add(0, northeast);
		Sector northwest = new Sector(sector.getX(), sector.getY(),
				(sector.getX() + (sector.getWidth() - sector.getX()) / 2),
				(sector.getY() + ((sector.getDepth() - sector.getY()) / 2)));
		sectorStack.add(0, northwest);
	}

	/**
	 * This method computes the externality of all 4 sectors. If each of the 4
	 * sectors is either completely black or completely white, it is returned true,
	 * otherwise it is returned false.
	 * 
	 * @param sector
	 * @return
	 */
	public boolean computeExternality(Sector sector, Image image) {
		boolean northWestIsBlackORWhite = computeNorthWestExternality(sector, image);
		boolean northEastIsBlackORWhite = computeNorthEastExternality(sector, image);
		boolean southWestIsBlackORWhite = computeSouthWestExternality(sector, image);
		boolean southEastIsBlackORWhite = computeSouthEastExternality(sector, image);
		if (northWestIsBlackORWhite && northEastIsBlackORWhite && southWestIsBlackORWhite && southEastIsBlackORWhite) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method computes the externality of the northwestSector. If it is neither
	 * black or white and not both, it returns true, otherwise it returns false
	 * 
	 * @param sector
	 * @return
	 */
	public boolean computeNorthWestExternality(Sector sector, Image image) {
		boolean[] northWest = new boolean[((int) sector.getWidth() / 2) * ((int) sector.getDepth() / 2)];
		boolean northWestIsBlackOrWhite = false;
		northWest = launcher.readPixels(sector.getX(), sector.getY(),
				(sector.getX() + (sector.getWidth() - sector.getX()) / 2),
				(sector.getY() + ((sector.getDepth() - sector.getY()) / 2)), image);
		if ((Arrays.toString(northWest).contains("true") && !Arrays.toString(northWest).contains("false")
				|| (!Arrays.toString(northWest).contains("true") && Arrays.toString(northWest).contains("false")))) {
			northWestIsBlackOrWhite = true;
		}
		return northWestIsBlackOrWhite;
	}

	/**
	 * This method computes the externality of the northeastSector. If it is neither
	 * black or white and not both, it returns true, otherwise it returns false
	 * 
	 * @param sector
	 * @return
	 */
	public boolean computeNorthEastExternality(Sector sector, Image image) {
		boolean[] northEast = new boolean[((int) sector.getWidth() / 2) * ((int) sector.getDepth() / 2)];
		boolean northEastIsBlackOrWhite = false;
		northEast = launcher.readPixels((sector.getX() + (sector.getWidth() - sector.getX()) / 2), sector.getY(),
				sector.getWidth(), (sector.getY() + ((sector.getDepth() - sector.getY()) / 2)), image);
		if ((Arrays.toString(northEast).contains("true") && !Arrays.toString(northEast).contains("false"))
				|| (!Arrays.toString(northEast).contains("true") && Arrays.toString(northEast).contains("false"))) {
			northEastIsBlackOrWhite = true;
		}
		return northEastIsBlackOrWhite;
	}

	/**
	 * This method computes the externality of the southwestSector. If it is neither
	 * black or white and not both, it returns true, otherwise it returns false
	 * 
	 * @param sector
	 * @return
	 */
	public boolean computeSouthWestExternality(Sector sector, Image image) {
		boolean[] southWest = new boolean[((int) sector.getWidth() / 2) * ((int) sector.getDepth() / 2)];
		boolean southWestIsBlackOrWhite = false;
		southWest = launcher.readPixels((sector.getX() + (sector.getWidth() - sector.getX()) / 2),
				(sector.getY() + ((sector.getDepth() - sector.getY()) / 2)), sector.getWidth(), sector.getDepth(),
				image);
		if ((Arrays.toString(southWest).contains("true") && !Arrays.toString(southWest).contains("false"))
				|| (!Arrays.toString(southWest).contains("true") && Arrays.toString(southWest).contains("false"))) {
			southWestIsBlackOrWhite = true;
		}
		return southWestIsBlackOrWhite;
	}

	/**
	 * This method computes the externality of the southeastSector. If it is neither
	 * black or white and not both, it returns true, otherwise it returns false
	 * 
	 * @param sector
	 * @return
	 */
	public boolean computeSouthEastExternality(Sector sector, Image image) {
		boolean[] southEast = new boolean[((int) sector.getWidth() / 2) * ((int) sector.getDepth() / 2)];
		boolean southEastIsBlackOrWhite = false;
		southEast = launcher.readPixels(sector.getX(), (sector.getY() + ((sector.getDepth() - sector.getY()) / 2)),
				(sector.getX() + (sector.getWidth() - sector.getX()) / 2), sector.getDepth(), image);
		if ((Arrays.toString(southEast).contains("true") && !Arrays.toString(southEast).contains("false"))
				|| (!Arrays.toString(southEast).contains("true") && Arrays.toString(southEast).contains("false"))) {
			southEastIsBlackOrWhite = true;
		}
		return southEastIsBlackOrWhite;
	}

}
