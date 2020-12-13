package constantbitlengthquadtree.decomposition;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.sun.pkg.util.BitArray;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * 
 * @author daniel tremmel
 * 
 *         This class starts the compression and contains all necessary class
 *         instances and the corresponding methods for the compression.
 *
 */
public class PictureLauncher extends Application {

	final int width = 400;
	final int height = 400;
	private Segmentator segmentator = new Segmentator();

	/**
	 * This method starts the view. It loads the input picture and the picture drawn
	 * by the method drawImage, in order to see if the method drawImage works
	 * correctly and draws the same image as the input image. The input image is
	 * shown on the left side and the modified image is on the right side.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
	}

	/**
	 * This method reads the pixels of the the image. If the argb-value is 0, a
	 * boolean value false is saved. If it is not 0 the value true is saved. In the
	 * end, a boolean array is returned containing the boolean values false for the
	 * color white and true for the color black.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public boolean[] readPixels(int x, int y, int width, int height, Image image) {
		PixelReader pixelReader = image.getPixelReader();
		boolean[] blackAndwhite = new boolean[(width - x) * (height - y)];
		int count = 0;
		for (int j = y; j < height; j++) {
			for (int i = x; i < width; i++) {
				int argb = pixelReader.getArgb(i, j);
				if (argb == 0 || argb == -1) {
					blackAndwhite[count] = false;
				} else {
					blackAndwhite[count] = true;
				}
				count++;
			}
		}
		return blackAndwhite;
	}

	/**
	 * This method transfers every value of the bitSequence to a binary
	 * representation and then transforms the binaryString to 2 boolean values. The
	 * resultList is then transferred to a result Array, which is finally
	 * transferred to a bitArary. The bitArray serves demonstration purposes, to
	 * print the final bit Sequence in which the picture is translated. At the end,
	 * the size of the bitArary is printed.
	 * 
	 * @param bitSequence
	 */
	public void transferToBitSequence(String bitSequence) {
		List<Boolean> resultList = new ArrayList<>();
		for (int i = 0; i < bitSequence.length(); i++) {
			String binaryString = Integer.toBinaryString(Character.getNumericValue(bitSequence.charAt(i)));
			switch (binaryString) {
			case "1":
				resultList.add(false);
				resultList.add(true);
				break;
			case "0":
				resultList.add(false);
				resultList.add(false);
				break;
			case "10":
				resultList.add(true);
				resultList.add(false);
				break;
			case "11":
				resultList.add(true);
				resultList.add(true);
				break;
			}
		}
		boolean[] resultArray = new boolean[resultList.size()];
		int counter = 0;
		for (Boolean booleanObject : resultList) {
			resultArray[counter] = booleanObject;
			counter++;
		}
		BitArray bitArray = new BitArray(resultArray);
		System.out.println("Bytes: " + bitArray.length() / 8);
	}

	/**
	 * This methods reads in all pictures from a given directory and calls the
	 * decomposition method and saves each picture to a new file, given the
	 * segmentation.
	 * 
	 * @param args
	 */
	public void saveImages(String[] args) {
		Path directory = Paths.get(args[0]);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
			for (Path file : stream) {
				System.out.println(file.getFileName());
				Image image = new Image("file:pictures/" + file.getFileName().toString(), width, height, false, false);
				long start = new Timestamp(System.currentTimeMillis()).getTime();
				String bitSequence = segmentator.computeBitSequence(image);
				long stop = new Timestamp(System.currentTimeMillis()).getTime();
				List<Sector> sectorList = segmentator.rebuildPicture(bitSequence);
				WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
				transferToBitSequence(bitSequence);
				long time = stop - start;
				System.out.println("Time: " + time + " milliseconds");
				drawImage(sectorList, file.getFileName().toString(), writableImage);
			}
		} catch (IOException | DirectoryIteratorException x) {
			System.err.println(x);
		}
	}

	/**
	 * This method draws the image. Its input is a blocklist containing all blocks
	 * of which the image is consisting.
	 * 
	 * @param blockList
	 */
	public void drawImage(List<Sector> sectorList, String fileName, WritableImage writableImage) {
		List<Color> colorList = new ArrayList<>();
		colorList.add(Color.BLUE);
		colorList.add(Color.RED);
		colorList.add(Color.YELLOW);
		colorList.add(Color.GREEN);
		colorList.add(Color.BROWN);
		colorList.add(Color.CHARTREUSE);
		colorList.add(Color.BLACK);
		colorList.add(Color.DARKSLATEGRAY);
		PixelWriter pixelWriter = writableImage.getPixelWriter();
		int colorCounter = 0;
		for (Sector sector : sectorList) {
			for (int i = sector.getY(); i < sector.getDepth(); i++) {
				for (int j = sector.getX(); j < sector.getWidth(); j++) {
					pixelWriter.setColor(j, i, colorList.get(colorCounter));
				}
			}
			if (colorCounter < 7) {
				colorCounter++;
			} else {
				colorCounter = 0;
			}
		}
		File file = new File("resultPictures/ConstantBitlengthQuadtree_" + fileName);
		RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
		try {
			ImageIO.write(renderedImage, "png", file);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		PictureLauncher pictureLauncher = new PictureLauncher();
		pictureLauncher.saveImages(args);
		System.exit(0);
	}
}
