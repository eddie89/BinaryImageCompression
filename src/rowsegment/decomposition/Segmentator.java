package rowsegment.decomposition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

/**
 * The Segmentator class contains the logic of the decomposition algorithm. It
 * contains the methods to dissect the object into rows and the saves it to a
 * map with all rows.
 * 
 * @author daniel tremmel
 *
 */
public class Segmentator {

	/**
	 * This method computes the rows of which the picture is made up in the end. It
	 * saves each row in a hashmap in which a list of the object rows is mapped to
	 * the corresponding row, which is the key. It examines every pixel in every row
	 * if it is black or white. As soon as the algorithm encounters a pixel which is
	 * not white, x is raised and the next pixel is examined. This is repeated until
	 * a pixel either is white, or that last position of the row is encountered.
	 * After the while-loop, a row is created with the start and the stop point. If
	 * the hashmap already contains the row as key the row is added to the
	 * corresponding key, otherwise the row is added as a new key including the list
	 * with the row
	 * 
	 * @return
	 */
	public Map<Integer, List<Row>> computeRows(Image image) {
		PixelReader pixelReader = image.getPixelReader();
		Map<Integer, List<Row>> rowMap = new HashMap<>();
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				if (pixelReader.getArgb(x, y) != 0 && pixelReader.getArgb(x, y) != -1) {
					int start = x;
					while (pixelReader.getArgb(x, y) != 0 && pixelReader.getArgb(x, y) != -1
							&& x < image.getWidth() - 1) {
						x++;
					}
					int stop = x - 1;
					Row row = new Row(y, start, stop);
					if (rowMap.containsKey(y)) {
						rowMap.get(y).add(row);
					} else {
						List<Row> rows = new ArrayList<>();
						rows.add(row);
						rowMap.put(y, rows);
					}
				}
			}
		}
		return rowMap;
	}

	/**
	 * This method sorts the rows to blocks. For that purpose, all rows that contain
	 * the same range in terms of the row length are put into a map, where the range
	 * serves as a key. At the end, the map with the different ranges of rows as
	 * keys and the rows as values are returned
	 * 
	 */
	public Map<List<Integer>, Set<Integer>> computeBlocks(Map<Integer, List<Row>> rowMap) {
		Set<Integer> keys = rowMap.keySet();
		Map<List<Integer>, Set<Integer>> blockMap = new HashMap<>();
		for (Integer key : keys) {
			if (rowMap.containsKey(key)) {
				List<Row> segments = rowMap.get(key);
				for (Row segment : segments) {
					int startX = segment.getStart();
					int stopX = segment.getStop();
					List<Integer> range = new ArrayList<>();
					range.add(startX);
					range.add(stopX);
					if (blockMap.containsKey(range)) {
						blockMap.get(range).add(key);
					} else {
						Set<Integer> rows = new TreeSet<>();
						rows.add(key);
						blockMap.put(range, rows);
					}
				}
			}
		}
		return blockMap;
	}

	/**
	 * This method builds the blocks out of the blockMap. For that purpose, it
	 * iterates over the blockMap and pushes each row of a certain range on a stack.
	 * As long as the stack is not empty, the element on top of the stack is popped
	 * from it, depending on its distance to the last row that was taken from the
	 * stack. If the distance is 2, it means that both rows are part of the same
	 * block. If it is more then 2, it means that both rows have the same range, but
	 * they are not part of the same block which means that a new block can be
	 * created from the y coordinate and the height coordinate of the current row.
	 * As x and width the range can be taken. Then y has to set new to the currently
	 * selected row. At the end of the method, the list with blocks is returned.
	 * 
	 * @return
	 */
	public List<Block> buildBlocks(Map<List<Integer>, Set<Integer>> blockMap) {
		Set<Entry<List<Integer>, Set<Integer>>> entrySet = blockMap.entrySet();
		List<Block> blockList = new ArrayList<>();
		for (Entry<List<Integer>, Set<Integer>> entry : entrySet) {
			List<Integer> segment = entry.getKey();
			int x = segment.get(0);
			int width = segment.get(1);
			Set<Integer> rows = entry.getValue();
			List<Integer> rowList = new ArrayList<>(rows);
			Stack<Integer> stack = new Stack<>();
			for (Integer row : rowList) {
				stack.push(row);
			}
			int counter = 0;
			int height = stack.pop();
			int y = height;
			if (!stack.isEmpty()) {
				while (!stack.isEmpty()) {
					if (counter == 0) {
						if (height - stack.peek() == 1) {
							y = stack.pop();
							counter++;
							if (stack.isEmpty()) {
								blockList.add(new Block(x, y, width, height));
							}
						} else {
							blockList.add(new Block(x, y, width, height));
							height = stack.pop();
							y = height;
							if (stack.isEmpty()) {
								blockList.add(new Block(x, y, width, height));
							}
							counter++;
						}
					} else {
						if (y - stack.peek() == 1) {
							y = stack.pop();
							if (stack.isEmpty()) {
								blockList.add(new Block(x, y, width, height));
							}
						} else {
							blockList.add(new Block(x, y, width, height));
							height = stack.pop();
							y = height;
							if (stack.isEmpty()) {
								blockList.add(new Block(x, y, width, height));
							}
						}
					}
				}
			} else {
				blockList.add(new Block(x, height, width, height));
			}
		}
		return blockList;
	}

}
