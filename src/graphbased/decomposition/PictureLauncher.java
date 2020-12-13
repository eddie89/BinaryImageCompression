package graphbased.decomposition;

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

	private int width = 400, height = 400;
	private Segmentator segmentator = new Segmentator();

	public static void main(String[] args) {
		PictureLauncher pictureLauncher = new PictureLauncher();
		pictureLauncher.saveImages(args);
		System.exit(0);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

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
	 * This method draws the image. Its input is a blocklist containing all blocks
	 * of which the image is consisting. The purpose of the method is to demonstrate
	 * the partitioning of the pictures after the compression.
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
		int colorCounter = 0;
		for (Block block : blockList) {
			for (int i = block.getY(); i <= block.getHeight(); i++) {
				for (int j = block.getX(); j <= block.getWidth(); j++) {
					pixelWriter.setColor(j, i, colorList.get(colorCounter));
				}
			}
			if (colorCounter < 7) {
				colorCounter++;
			} else {
				colorCounter = 0;
			}
		}
		File file = new File("resultPictures/GraphBased_" + fileName);
		RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
		try {
			ImageIO.write(renderedImage, "png", file);

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("GraphBased Blocks: " + blockList.size());
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
				segmentator.readPixels(image);
				segmentator.computeRaster();
				segmentator.computeShape();
				segmentator.computeConcavePixels();
				segmentator.computeConcaveGapPixel();
				segmentator.computeHorizontalRowSegments();
				segmentator.computeVerticalRowSegments();
				segmentator.computeInsideFigurePixels();
				segmentator.computeCogridVertices();
				segmentator.computeAllEdges();
				segmentator.computeIntersectionGraph();
				segmentator.computeMaximumMatching();
				segmentator.computeMinimumDissection();
				segmentator.computeSegmentSkeleton();
				segmentator.computeDividedSegments();
				segmentator.computeBlocks();
				List<Block> blockList = segmentator.buildBlocks();
				long stop = new Timestamp(System.currentTimeMillis()).getTime();
				long time = stop - start;
				System.out.println("Time: " + time + " milliseconds");
				WritableImage writableImage = new WritableImage((int) width, height);
				drawImage(blockList, file.getFileName().toString(), writableImage);
				calculateBytes(blockList, file.getFileName().toString());
			}
		} catch (IOException | DirectoryIteratorException x) {
			System.err.println(x);
		}
	}

}
