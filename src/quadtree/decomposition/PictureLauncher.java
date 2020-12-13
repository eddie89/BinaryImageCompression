package quadtree.decomposition;

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

	final int width = 400, height = 400;

	private Segmentator segmentator = new Segmentator();

	@Override
	public void start(Stage stage) {
	}

	/**
	 * This method calculates bytes out of the blocks. As the pictures can have a
	 * higher range than 256 each parameter is stored in 2 bytes. If a parameter is
	 * smaller then 2 bytes(16 bit) the rest of the 16 bit is filled. Based upon the
	 * values a resultlist is filled with boolean values which is later transferred
	 * into a bitArray. At the end the bitArray is divided by 8 to have the number
	 * of bytes of the compressed picture. The purpose of the bitArray is mainly for
	 * demonstration purposes, as the boolean array itself could also be measured on
	 * its length. At the end the number of bytes is printed.
	 * 
	 * 
	 * @param blockList
	 * @param fileName
	 */
	public void calculateBytes(List<Block> blockList, String fileName) {
		List<Boolean> resultList = new ArrayList<>();
		for (Block block : blockList) {
			List<String> blockParameters = new ArrayList<>();
			blockParameters.add(Integer.toBinaryString(block.getX()));
			blockParameters.add(Integer.toBinaryString(block.getY()));
			blockParameters.add(Integer.toBinaryString(block.getWidth()));
			blockParameters.add(Integer.toBinaryString(block.getHeight()));
			for (String parameter : blockParameters) {
				int gap = 16 - parameter.length();
				while (gap > 0) {
					resultList.add(false);
					gap--;
				}
				for (int i = 0; i < parameter.length(); i++) {
					if (parameter.charAt(i) == 0) {
						resultList.add(false);
					} else {
						resultList.add(true);
					}
				}
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
				List<Block> blockList = segmentator.computeBlocks((image));
				long stop = new Timestamp(System.currentTimeMillis()).getTime();
				long time = stop - start;
				System.out.println("Time: " + time + " milliseconds");
				WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
				calculateBytes(blockList, file.getFileName().toString());
				drawImage(blockList, file.getFileName().toString(), writableImage);
			}
		} catch (IOException | DirectoryIteratorException x) {
			// IOException can never be thrown by the iteration.
			// In this snippet, it can only be thrown by newDirectoryStream.
			System.err.println(x);
		}
	}

	public static void main(String[] args) {
		PictureLauncher pictureLauncher = new PictureLauncher();
		pictureLauncher.saveImages(args);
		System.exit(0);
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
				if (argb == -1 || argb == 0) {
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
	 * This method draws the image. Its input is a blocklist containing all blocks
	 * of which the image is consisting.
	 * 
	 * @param blockList
	 */
	public void drawImage(List<Block> blockList, String fileName, WritableImage writableImage) {
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
		int counter = 0;
		int colorCounter = 0;
		for (Block block : blockList) {
			counter++;
			for (int i = block.getY(); i < block.getHeight(); i++) {
				for (int j = block.getX(); j < block.getWidth(); j++) {
					pixelWriter.setColor(j, i, colorList.get(colorCounter));
				}
			}
			if (colorCounter < 7) {
				colorCounter++;
			} else {
				colorCounter = 0;
			}
		}
		File file = new File("resultPictures/Quadtree_" + fileName);
		RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
		try {
			ImageIO.write(renderedImage, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Quadtree Blocks: " + counter);
	}

}
