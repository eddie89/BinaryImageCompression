package quadtree.decomposition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import javafx.scene.image.Image;

/**
 * 
 * @author daniel tremmel
 * 
 *         This class contains most of the logic of the compression technique.
 *
 */
public class Segmentator {

	/**
	 * This method divides the picture into several segments, that are either black
	 * or white. At first a node with the size of the image is pushed on a stack.
	 * Then a while loop is running in which the pixels of the area specified by the
	 * node coordinates are read and saved in the boolean array blackAndwhite. If
	 * blackAndWhite contains true and false, the node is divided into four equal
	 * nodes, which are all pushed to the stack. Then, the top node is again popped
	 * of the stack, and proofed again. Once the node contains only true or false,
	 * it is saved as block with its coordinates and the boolean value. In the end a
	 * list with all blocks is returned
	 * 
	 * @param image
	 * @return
	 */
	public List<Block> computeBlocks(Image image) {
		int x = 0, y = 0;
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		PictureLauncher launcher = new PictureLauncher();
		Stack<Block> nodeStack = new Stack<>();
		boolean[] blackAndwhite;
		Block node = new Block(x, y, width, height);
		nodeStack.push(node);
		List<Block> blockList = new ArrayList<>();
		while (!nodeStack.isEmpty()) {
			Block temporaryBlock = nodeStack.pop();
			blackAndwhite = new boolean[(int) temporaryBlock.getWidth() * (int) temporaryBlock.getHeight()];
			blackAndwhite = launcher.readPixels(temporaryBlock.getX(), temporaryBlock.getY(), temporaryBlock.getWidth(),
					temporaryBlock.getHeight(), image);
			if (Arrays.toString(blackAndwhite).contains("true")) {
				if (Arrays.toString(blackAndwhite).contains("false")) {
					fillStack(nodeStack, temporaryBlock);
				} else {
					Block resultBlock = new Block(temporaryBlock.getX(), temporaryBlock.getY(),
							temporaryBlock.getWidth(), temporaryBlock.getHeight());
					blockList.add(resultBlock);
				}
			}
		}
		return blockList;
	}

	/**
	 * 
	 * This method fills the stack by dividing the passed block into 4 equal sized
	 * blocks and pushing them back to the passed stack.
	 * 
	 * @param nodeStack
	 * @param temporaryBlock
	 */
	private void fillStack(Stack<Block> nodeStack, Block temporaryBlock) {
		nodeStack.push(new Block(temporaryBlock.getX(), temporaryBlock.getY(),
				(temporaryBlock.getX() + ((temporaryBlock.getWidth() - temporaryBlock.getX()) / 2)),
				(temporaryBlock.getY() + ((temporaryBlock.getHeight() - temporaryBlock.getY()) / 2))));
		nodeStack.push(new Block(temporaryBlock.getX() + ((temporaryBlock.getWidth() - temporaryBlock.getX()) / 2),
				temporaryBlock.getY(), temporaryBlock.getWidth(),
				(temporaryBlock.getY() + ((temporaryBlock.getHeight() - temporaryBlock.getY()) / 2))));
		nodeStack.push(new Block(temporaryBlock.getX() + ((temporaryBlock.getWidth() - temporaryBlock.getX()) / 2),
				(temporaryBlock.getY() + ((temporaryBlock.getHeight() - temporaryBlock.getY()) / 2)),
				temporaryBlock.getWidth(), temporaryBlock.getHeight()));
		nodeStack.push(new Block(temporaryBlock.getX(),
				(temporaryBlock.getY() + ((temporaryBlock.getHeight() - temporaryBlock.getY()) / 2)),
				(temporaryBlock.getX() + ((temporaryBlock.getWidth() - temporaryBlock.getX()) / 2)),
				temporaryBlock.getHeight()));
	}
}
