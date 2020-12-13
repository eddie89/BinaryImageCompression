package graphbased.decomposition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.HopcroftKarp;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

/**
 * 
 * @author daniel tremmel
 * 
 *         This class contains most of the logic of the compression technique.
 *
 */
public class Segmentator {

	private DataContainer container = new DataContainer();

	/**
	 * This method reads the pixels of the the image. If the argb-value is 0, a
	 * boolean value false is saved. If it is not 0 or -1 the value true is saved.
	 * In the end, a boolean array is returned containing the boolean values false
	 * for the color white and true for the color black.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public boolean[][] readPixels(Image image) {
		PixelReader pixelReader = image.getPixelReader();
		boolean[][] blackAndwhite = new boolean[(int) image.getWidth()][(int) image.getHeight()];
		for (int j = 0; j < image.getHeight(); j++) {
			for (int i = 0; i < image.getWidth(); i++) {
				int argb = pixelReader.getArgb(i, j);
				if (argb == -1 || argb == 0) {
					blackAndwhite[i][j] = false;
				} else {
					blackAndwhite[i][j] = true;
				}
			}
		}
		container.setBlackAndwhite(blackAndwhite);
		return blackAndwhite;
	}

	/**
	 * This method has the purpose to compute the raster in which the picture object
	 * is drawn into later. The purpose of the raster itself is to provide a schema
	 * in which the picture object can be drawn and in which edges between concave
	 * corners can be drawn without overwriting part of the object. Therefore, the
	 * pixels that are put into the raster are given the variable isGap, which is
	 * set true for each pixel that is part of the raster.
	 */
	public void computeRaster() {
		boolean[][] blackAndwhite = container.getBlackAndwhite();
		int rasterWidth = blackAndwhite[0].length * 2 + 1;
		int rasterHeight = blackAndwhite.length * 2 + 1;
		Pixel[][] pixelArray = new Pixel[rasterWidth][rasterHeight];
		for (int y = 0; y < rasterHeight; y++) {
			if (y % 2 == 0) {
				int x = 0;
				while (x < rasterWidth) {
					pixelArray[x][y] = new Pixel(new Coordinates(x, y), false, true);
					x++;
				}
			} else if (y % 2 == 1) {
				int x = 0;
				while (x < rasterWidth) {
					if (x % 2 == 0) {
						pixelArray[x][y] = new Pixel(new Coordinates(x, y), false, true);
					}
					x++;
				}
			}
		}
		container.setPixelArray(pixelArray);
	}

	/**
	 * This method computes the shape of the picture Object. The shape is generated
	 * out of the Array blackAndwhite. It is than transfered into the raster. For
	 * that purpose, it is necessary that the object is only written on pixels that
	 * are not gap pixels. In order to do so, the iteration always takes 2 steps and
	 * starts with 1. At the end of the method the raster is saved into the
	 * dataConainer with the variable pixelArray. The dataContainer itself is then
	 * returned.
	 * 
	 * @return
	 */
	public void computeShape() {
		Pixel[][] pixelArray = container.getPixelArray();
		boolean[][] blackAndwhite = container.getBlackAndwhite();
		int y = 0;
		for (int i = 1; i < pixelArray.length; i = i + 2) {
			int x = 0;
			for (int j = 1; j < pixelArray[i].length; j = j + 2) {
				if (blackAndwhite[x][y] == true) {
					Pixel pixel = new Pixel(new Coordinates(j, i), true);
					pixel.setInsideFigure(true);
					pixelArray[j][i] = pixel;
				} else {
					pixelArray[j][i] = new Pixel(new Coordinates(j, i), false);
				}
				x++;
			}
			y++;
		}
		container.setPixelArray(pixelArray);
	}

	/**
	 * This method has the purpose to return the direction of the concave direction.
	 * Based on the position that is given to the method as a parameter, the
	 * direction is determined and returned at the end of the method.
	 * 
	 * @param position
	 * @return
	 */
	public Direction determineConcaveDirection(int position) {
		Direction concaveDirection = null;
		switch (position) {
		case 0:
			concaveDirection = Direction.LOWER;
			break;
		case 1:
			concaveDirection = Direction.LOWERRIGHT;
			break;
		case 2:
			concaveDirection = Direction.RIGHT;
			break;
		case 3:
			concaveDirection = Direction.UPPERRIGHT;
			break;
		case 4:
			concaveDirection = Direction.UPPER;
			break;
		case 5:
			concaveDirection = Direction.UPPERLEFT;
			break;
		case 6:
			concaveDirection = Direction.LEFT;
			break;
		case 7:
			concaveDirection = Direction.LOWERLEFT;
			break;
		}
		return concaveDirection;
	}

	/**
	 * This method has the purpose to determine if a pixel is concave. In order to
	 * proof that, the neighbors of the pixel are examined. If the neighbors, that
	 * are building a circle around the pixel contain the scheme true, false, true,
	 * it means that the pixel is lying at a concave angle, as the scheme stands for
	 * black, white, black. At the end of the method either true or false is
	 * returned based on whether it is concave or not.
	 * 
	 * @param pixel
	 * @return
	 */
	public boolean determineIfIsConcave(Pixel pixel) {
		List<Pixel> neighborList = pixel.getNeighbors();
		List<Boolean> concavityList = new ArrayList<>();
		List<Boolean> concavityNeighbors = new ArrayList<>();
		concavityList.add(true);
		concavityList.add(false);
		concavityList.add(true);
		for (int i = 0; i < neighborList.size(); i++) {
			concavityNeighbors.add(neighborList.get(i).isBlack());
		}
		int position = Collections.indexOfSubList(concavityNeighbors, concavityList);
		if (position != -1) {
			Direction concaveDirection = determineConcaveDirection(position);
			pixel.setConcaveDirection(concaveDirection);
			pixel.setConcave(true);
			return true;
		} else {
			pixel.setConcave(false);
			return false;
		}
	}

	/**
	 * This method has the purpose to compute all compute all concave Pixels of an
	 * object. For that, the shape is iterated over and when a pixel is black, the
	 * neighbors are collected and then it is determined if it is concave or not via
	 * the method determineIfIsConcave. At the end, the concave pixels are saved in
	 * a list and saved into the dataContainer.
	 * 
	 * @return
	 */
	public void computeConcavePixels() {
		Pixel[][] pixelArray = container.getPixelArray();
		List<Pixel> concavePixelList = new ArrayList<>();
		for (int j = 0; j < pixelArray.length; j++) {
			for (int i = 0; i < pixelArray[j].length; i++) {
				if (pixelArray[i][j].isBlack()) {
					Pixel pixel = pixelArray[i][j];
					List<Pixel> neighbors = Pixel.collectNeighbors(i, j, pixelArray.length, pixelArray[j].length,
							pixelArray);
					pixel.setNeighbors(neighbors);
					pixel.setInsideFigure(true);
					if (determineIfIsConcave(pixel)) {
						concavePixelList.add(pixel);
					}
				}
			}
		}
		container.setConcavePixelList(concavePixelList);
	}

	/**
	 * This method has the purpose to determine the coordinates of the gap pixel,
	 * which will be part of the edge that is connecting two concave angles. For
	 * that purpose, the direction and the coordinates of the object pixel that is
	 * lying at the concave angle is taken as parameters and based upon both new
	 * Coordinates are set and returned at the end of the method.
	 * 
	 * @param coordinates
	 * @param direction
	 * @return
	 */
	public Coordinates determineGapCoordinates(Coordinates coordinates, Direction direction) {
		int x = coordinates.getX();
		int y = coordinates.getY();
		if (direction != null) {
			switch (direction) {
			case LOWER:
				y++;
				coordinates = new Coordinates(x, y);
				break;
			case LOWERLEFT:
				x--;
				y++;
				coordinates = new Coordinates(x, y);
				break;
			case LEFT:
				x--;
				coordinates = new Coordinates(x, y);
				break;
			case UPPERLEFT:
				x--;
				y--;
				coordinates = new Coordinates(x, y);
				break;
			case UPPER:
				y--;
				coordinates = new Coordinates(x, y);
				break;
			case UPPERRIGHT:
				y--;
				x++;
				coordinates = new Coordinates(x, y);
				break;
			case RIGHT:
				x++;
				coordinates = new Coordinates(x, y);
				break;
			case LOWERRIGHT:
				x++;
				y++;
				coordinates = new Coordinates(x, y);
				break;
			}
		}
		return coordinates;
	}

	/**
	 * This method has the purpose to compute all concave Gap Pixels. For that
	 * purpose, each pixel from the list with all concave pixels is iterated over,
	 * the direction and coordinates of the current pixel extracted and the new
	 * pixel created and add to a list based upon the generated coordinates of
	 * determineGapCoordinates. At the end the list is saved to the container.
	 * 
	 */
	public void computeConcaveGapPixel() {
		List<Pixel> concavePixelList = container.getConcavePixelList();
		Pixel[][] pixelArray = container.getPixelArray();
		List<Pixel> concaveGapPixelList = new ArrayList<>();
		for (Pixel pixel : concavePixelList) {
			Direction direction = pixel.getConcaveDirection();
			Coordinates coordinates = determineGapCoordinates(pixel.getCoordinates(), direction);
			Pixel gapPixel = pixelArray[coordinates.getX()][coordinates.getY()];
			gapPixel.setConcaveDirection(direction);
			concaveGapPixelList.add(gapPixel);
		}
		container.setConcaveGapPixelList(concaveGapPixelList);
	}

	/**
	 * This method computes every horizontal row segment. Once it encounters a black
	 * pixel, an inner while-loop iterates over every following pixel as long as it
	 * is black and the length of the array wasn't reached. Then, the end pixel of
	 * the segment is saved and the whole segment is saved as list. Every segment is
	 * saved in a list that is itself saved in a map with the current row as key. At
	 * the end of the method, the map is saved to the dataContainer.
	 * 
	 */
	public void computeHorizontalRowSegments() {
		Pixel[][] pixelArray = container.getPixelArray();
		boolean isBlack = false;
		Map<Integer, List<List<Pixel>>> horizontallyOrderedRowSegments = new HashMap<>();
		for (int y = 1; y < pixelArray.length; y += 2) {
			for (int x = 1; x < pixelArray[y].length; x += 2) {
				isBlack = pixelArray[x][y].isBlack();
				if (isBlack) {
					List<Pixel> row = new ArrayList<>();
					Pixel startPixel = new Pixel(pixelArray[x][y].getCoordinates(), isBlack);
					row.add(startPixel);
					while (isBlack && x < pixelArray[y].length) {
						isBlack = pixelArray[x][y].isBlack();
						x += 2;
					}
					Coordinates stopCoordinates = new Coordinates(x - 2, y);
					Pixel stopPixel = new Pixel(stopCoordinates);
					row.add(stopPixel);
					if (horizontallyOrderedRowSegments.containsKey(y)) {
						horizontallyOrderedRowSegments.get(y).add(row);
					} else {
						List<List<Pixel>> edges = new ArrayList<>();
						edges.add(row);
						horizontallyOrderedRowSegments.put(y, edges);
					}
				}
			}
		}
		container.setHorizontalRowSegmentsMap(horizontallyOrderedRowSegments);
	}

	/**
	 * This method computes every vertical row segment. Once it encounters a black
	 * pixel, an inner while-loop iterates over every following pixel as long as it
	 * is black and the length of the array wasn't reached. Then, the end pixel of
	 * the segment is saved and the whole segment is saved as list. Every segment is
	 * saved in a list that is itself saved in a map with the current row as key. At
	 * the end of the method, the map is saved to the dataContainer.
	 * 
	 */
	public void computeVerticalRowSegments() {
		Pixel[][] pixelArray = container.getPixelArray();
		boolean isBlack = false;
		Map<Integer, List<List<Pixel>>> verticallyOrderedRowSegments = new HashMap<>();
		for (int x = 1; x < pixelArray.length; x += 2) {
			for (int y = 1; y < pixelArray[x].length; y += 2) {
				isBlack = pixelArray[x][y].isBlack();
				if (isBlack) {
					List<Pixel> row = new ArrayList<>();
					Pixel startPixel = new Pixel(pixelArray[x][y].getCoordinates(), isBlack);
					row.add(startPixel);
					while (isBlack && y < pixelArray[x].length) {
						isBlack = pixelArray[x][y].isBlack();
						y += 2;
					}
					Coordinates stopCoordinates = new Coordinates(x, y - 2);
					Pixel stopPixel = new Pixel(stopCoordinates);
					row.add(stopPixel);
					if (verticallyOrderedRowSegments.containsKey(x)) {
						verticallyOrderedRowSegments.get(x).add(row);
					} else {
						List<List<Pixel>> edges = new ArrayList<>();
						edges.add(row);
						verticallyOrderedRowSegments.put(x, edges);
					}
				}
			}
		}
		container.setVerticalRowSegmentsMap(verticallyOrderedRowSegments);
	}

	/**
	 * This method has the purpose to mark every pixel that is inside the picture
	 * object as inside. This will be needed when the method computeClosingEdges is
	 * called.
	 * 
	 */
	public void computeInsideFigurePixels() {
		Pixel[][] pixelArray = container.getPixelArray();
		Map<Integer, List<List<Pixel>>> horizontallyOrderedRowSegments = container.getHorizontalRowSegmentsMap();
		Map<Integer, List<List<Pixel>>> verticallyOrderedRowSegments = container.getVerticalRowSegmentsMap();
		for (Entry<Integer, List<List<Pixel>>> entry : horizontallyOrderedRowSegments.entrySet()) {
			int y = entry.getKey();
			List<List<Pixel>> segments = entry.getValue();
			for (List<Pixel> segment : segments) {
				int counterX = segment.get(0).getCoordinates().getX();
				int stopX = segment.get(1).getCoordinates().getX();
				while (counterX < stopX) {
					pixelArray[counterX][y].setInsideFigure(true);
					pixelArray[counterX][y + 1].setInsideFigure(true);
					counterX++;
				}
			}
		}
		for (Entry<Integer, List<List<Pixel>>> entry : verticallyOrderedRowSegments.entrySet()) {
			int x = entry.getKey();
			List<List<Pixel>> segments = entry.getValue();
			for (List<Pixel> segment : segments) {
				int counterY = segment.get(0).getCoordinates().getY();
				int stopY = segment.get(1).getCoordinates().getY();
				while (counterY < stopY) {
					pixelArray[x][counterY].setInsideFigure(true);
					pixelArray[x + 1][counterY].setInsideFigure(true);

					counterY++;
				}
			}
		}
		container.setPixelArray(pixelArray);
	}

	/**
	 * This method finds all cogrid vertices and saves them as lists. The methods
	 * computeHorizontalGapWhiteSpaces and computeVerticalGapWhiteSpaces remove all
	 * pixel pairs that are not completely lying in the object. Based on the x or
	 * y-Coordinate the pairs are saved in the map with the coordinate as key. The
	 * maps are then saved in a map based on their orientation. This map is then
	 * saved in the dataContainer.
	 * 
	 * @param container
	 */
	public void computeCogridVertices() {
		List<Pixel> concavePixels = container.getConcaveGapPixelList();
		List<Pixel> comparableList = concavePixels;
		Map<Angle, Map<Integer, List<List<Pixel>>>> allCogridVerticesMap = new HashMap<>();
		Map<Integer, Set<Pixel>> verticalCogridPixelsMap = new HashMap<>();
		Map<Integer, Set<Pixel>> horizontalCogridPixelMap = new HashMap<>();
		for (int i = 0; i < concavePixels.size(); i++) {
			for (int j = i + 1; j < comparableList.size(); j++) {
				Coordinates concavecoordinates = concavePixels.get(i).getCoordinates();
				Coordinates comparableCoordinates = comparableList.get(j).getCoordinates();
				if (concavecoordinates.getX() == comparableCoordinates.getX()) {
					if (verticalCogridPixelsMap.containsKey(concavecoordinates.getX())) {
						verticalCogridPixelsMap.get(concavecoordinates.getX()).add(comparableList.get(j));
					} else {
						Set<Pixel> verticalpixels = new TreeSet<>(new VerticalPixelComparator());
						verticalpixels.add(concavePixels.get(i));
						verticalpixels.add(comparableList.get(j));
						verticalCogridPixelsMap.put(concavecoordinates.getX(), verticalpixels);
					}
				}
				if (concavecoordinates.getY() == comparableCoordinates.getY()) {
					if (horizontalCogridPixelMap.containsKey(concavecoordinates.getY())) {
						horizontalCogridPixelMap.get(concavecoordinates.getY()).add(comparableList.get(j));
					} else {
						Set<Pixel> horizontalpixels = new TreeSet<>(new HorizontalPixelComparator());
						horizontalpixels.add(concavePixels.get(i));
						horizontalpixels.add(comparableList.get(j));
						horizontalCogridPixelMap.put(concavecoordinates.getY(), horizontalpixels);
					}
				}
			}
		}
		Map<Integer, List<List<Pixel>>> blackHorizontalEdgesMap = computeHorizontalGapWhiteSpaces(
				horizontalCogridPixelMap);
		Map<Integer, List<List<Pixel>>> blackVerticalEdgesMap = computeVerticalGapWhiteSpaces(verticalCogridPixelsMap);
		allCogridVerticesMap.put(Angle.HORIZONTAL, blackHorizontalEdgesMap);
		allCogridVerticesMap.put(Angle.VERTICAL, blackVerticalEdgesMap);
		container.setAllCogridVerticesMap(allCogridVerticesMap);
	}

	/**
	 * This method determines if the horizontal edges lies at any point outside the
	 * object. For that it examines whether the gap Pixel always has contact to the
	 * object and is inside the object. This is the case when the surrounding
	 * neighbors of the gap Pixel contain at least 2 black pixels. If thats the case
	 * the variable hasBlackNeighbor is set to true, otherwise to false. If at any
	 * time any pixel of doesn't have 2 black neighbors the iteration is stopped. If
	 * after the iteration hasBlackNeighbor is true a pair is created out of the
	 * start and stop pixel and added to map.
	 * 
	 * @param pixelMap
	 * @return
	 */
	public Map<Integer, List<List<Pixel>>> computeHorizontalGapWhiteSpaces(Map<Integer, Set<Pixel>> pixelMap) {
		Map<Integer, List<List<Pixel>>> lineMap = new HashMap<>();
		Pixel[][] pixelArray = container.getPixelArray();
		for (Integer key : pixelMap.keySet()) {
			Set<Pixel> horizontalPixelSet = pixelMap.get(key);
			List<Pixel> horizontalPixelList = new ArrayList<>(horizontalPixelSet);
			List<List<Pixel>> parallelLinePairs = new ArrayList<>();
			for (int i = 0; i < horizontalPixelList.size() - 1; i++) {
				Pixel firstPixel = horizontalPixelList.get(i);
				Pixel secondPixel = horizontalPixelList.get(i + 1);
				boolean hasBlackNeighbor = true;
				for (int j = firstPixel.getCoordinates().getX(); j < secondPixel.getCoordinates().getX(); j++) {
					List<Pixel> neighbors = Pixel.collectGapNeighbors(j, key, pixelArray.length, pixelArray[0].length,
							pixelArray);
					int counter = 0;
					for (Pixel neighbor : neighbors) {
						boolean isBlack = neighbor.isBlack();
						if (isBlack) {
							counter++;
						}
					}
					if (counter >= 2) {
						hasBlackNeighbor = true;
					} else {
						hasBlackNeighbor = false;
					}
					if (!hasBlackNeighbor) {
						break;
					}
				}
				if (hasBlackNeighbor) {
					List<Pixel> horizontalPair = new ArrayList<>();
					horizontalPair.add(firstPixel);
					horizontalPair.add(secondPixel);
					parallelLinePairs.add(horizontalPair);
					lineMap.put(key, parallelLinePairs);
				}
			}
		}
		return lineMap;
	}

	/**
	 * This method determines if the vertical edges lies at any point outside the
	 * object. For that it examines whether the gap Pixel always has contact to the
	 * object and is inside the object. This is the case when the surrounding
	 * neighbors of the gap Pixel contain at least 2 black pixels. If thats the case
	 * the variable hasBlackNeighbor is set to true, otherwise to false. If at any
	 * time any pixel of doesn't have 2 black neighbors the iteration is stopped. If
	 * after the iteration hasBlackNeighbor is true a pair is created out of the
	 * start and stop pixel and added to map.
	 * 
	 * @param pixelMap
	 * @return
	 */
	public Map<Integer, List<List<Pixel>>> computeVerticalGapWhiteSpaces(Map<Integer, Set<Pixel>> pixelMap) {
		Map<Integer, List<List<Pixel>>> lineMap = new HashMap<>();
		Pixel[][] pixelArray = container.getPixelArray();
		for (Integer key : pixelMap.keySet()) {
			Set<Pixel> horizontalPixelSet = pixelMap.get(key);
			List<Pixel> horizontalPixelList = new ArrayList<>(horizontalPixelSet);
			List<List<Pixel>> parallelLinePairs = new ArrayList<>();
			for (int i = 0; i < horizontalPixelList.size() - 1; i++) {
				Pixel firstPixel = horizontalPixelList.get(i);
				Pixel secondPixel = horizontalPixelList.get(i + 1);
				boolean hasBlackNeighbor = true;
				for (int j = firstPixel.getCoordinates().getY(); j < secondPixel.getCoordinates().getY(); j++) {
					List<Pixel> neighbors = Pixel.collectGapNeighbors(key, j, pixelArray.length, pixelArray[0].length,
							pixelArray);
					int counter = 0;
					for (Pixel neighbor : neighbors) {
						boolean isBlack = neighbor.isBlack();
						if (isBlack) {
							counter++;
						}
					}
					if (counter >= 2) {
						hasBlackNeighbor = true;
					} else {
						hasBlackNeighbor = false;
					}
					if (!hasBlackNeighbor) {
						break;
					}
				}
				if (hasBlackNeighbor) {
					List<Pixel> verticalPair = new ArrayList<>();
					verticalPair.add(firstPixel);
					verticalPair.add(secondPixel);
					parallelLinePairs.add(verticalPair);
					lineMap.put(key, parallelLinePairs);
				}
			}
		}
		return lineMap;
	}

	/**
	 * This method computes the edges out of all cogrid pairs. In order to connect
	 * crossing edges in a graph later, every edge is given a unique id. In the end
	 * a map and a list with alle edges is saved to the dataContainer.
	 */
	public void computeAllEdges() {
		Map<Angle, Map<Integer, List<List<Pixel>>>> lineMap = container.getAllCogridEdgesMap();
		int id = 0;
		List<Edge> edgeList;
		List<Integer> allEdgesList = new ArrayList<>();
		Map<Angle, List<Edge>> edgeMap = new HashMap<>();
		for (Angle key : lineMap.keySet()) {
			edgeList = new LinkedList<>();
			Map<Integer, List<List<Pixel>>> edges = lineMap.get(key);
			for (Integer row : edges.keySet()) {
				List<List<Pixel>> values = edges.get(row);
				for (int i = 0; i < values.size(); i++) {
					for (List<Pixel> list : values) {
						id++;
						Edge edge = new Edge(id, list, key, row);
						edgeList.add(edge);
						allEdgesList.add(id);
					}
				}
			}
			edgeMap.put(key, edgeList);
		}
		container.setAllEdgesList(allEdgesList);
		container.setAngleOrderedEdgeMap(edgeMap);
	}

	/**
	 * This method computes the intersectionGraph and an adjacencyMatrix. If 2 edges
	 * are crossing at any point, they are connected via their IDs in the graph,
	 * which is saved to the dataContainer in the end, as well as the
	 * adjacencyMatrix containing every edge and the given id as a key.
	 * 
	 */
	public void computeIntersectionGraph() {
		Map<Angle, List<Edge>> graphMap = container.getAngleOrderedEdgeMap();
		int vertices = graphMap.get(Angle.HORIZONTAL).size();
		vertices += graphMap.get(Angle.VERTICAL).size();
		vertices = vertices * 2;
		Graph graph = new Graph(vertices);
		Map<Integer, Edge> adjacencyMatrix = new HashMap<>();
		List<Edge> horizontalEdgeList = graphMap.get(Angle.HORIZONTAL);
		List<Edge> verticalEdgeList = graphMap.get(Angle.VERTICAL);
		for (Edge horizontalEdge : horizontalEdgeList) {
			adjacencyMatrix.put(horizontalEdge.getId(), horizontalEdge);
			List<Pixel> horizontalverticeList = horizontalEdge.getVertices();
			Pixel horizontalstartPoint = horizontalverticeList.get(0);
			Pixel horizontalendPoint = horizontalverticeList.get(1);
			for (Edge verticalEdge : verticalEdgeList) {
				adjacencyMatrix.put(verticalEdge.getId(), verticalEdge);
				List<Pixel> verticalverticeList = verticalEdge.getVertices();
				Pixel verticalstartPoint = verticalverticeList.get(0);
				Pixel verticalendPoint = verticalverticeList.get(1);
				if (verticalendPoint.getCoordinates().getX() <= horizontalendPoint.getCoordinates().getX()
						&& verticalendPoint.getCoordinates().getX() >= horizontalstartPoint.getCoordinates().getX()
						&& verticalstartPoint.getCoordinates().getY() <= horizontalstartPoint.getCoordinates().getY()
						&& verticalendPoint.getCoordinates().getY() >= horizontalstartPoint.getCoordinates().getY()) {
					graph.addEdge(horizontalEdge.getId(), verticalEdge.getId());
				}
			}
		}
		container.setGraph(graph);
		container.setAdjacencyMatrix(adjacencyMatrix);
	}

	/**
	 * This method computes the Maximum Matching Algorithm Hopcroft-Karp, then
	 * removes all IDs from the allEdgesIDList, that are in the MinimumVertexCover.
	 * At the end the remaining IDs are used to add the corresponding edges to the
	 * resultEdgesList, which is saved in the dataContainer.
	 * 
	 */
	public void computeMaximumMatching() {
		Map<Integer, Edge> adjacencyMatrix = container.getAdjacencyMatrix();
		List<Integer> allEdgesIDList = container.getAllEdgesList();
		List<Edge> resultEdgesList = new ArrayList<>();
		Graph graph = container.getGraph();
		HopcroftKarp hopcroftKarp = new HopcroftKarp(graph);
		for (int i = 0; i < graph.V(); i++) {
			if (hopcroftKarp.inMinVertexCover(i)) {
				allEdgesIDList.remove(Integer.valueOf(i));
			}
		}
		for (Integer i : allEdgesIDList) {
			resultEdgesList.add(adjacencyMatrix.get(i));
		}
		container.setEdgeList(resultEdgesList);
	}

	/**
	 * This method computes the boundaries up to which the closing edges can shift
	 * in the method computeClosingEdges. For that every pixel of the horizontal
	 * edge is marked with the variable boundary as true and at the end the method
	 * saves current pixelArray as well as the concaveGapPixelList to the
	 * dataContainer
	 * 
	 */
	public void computeBoundaries() {
		Pixel[][] pixelArray = container.getPixelArray();
		List<Edge> edgeList = container.getEdgeList();
		List<Pixel> concavePixelList = container.getConcaveGapPixelList();
		for (Edge edge : edgeList) {
			int row = edge.getRow();
			List<Pixel> vertices = edge.getVertices();
			Pixel startPixel = vertices.get(0);
			Pixel stopPixel = vertices.get(1);
			concavePixelList.remove(startPixel);
			concavePixelList.remove(stopPixel);
			int counter;
			int stop;
			if (edge.getAngle().equals(Angle.HORIZONTAL)) {
				counter = vertices.get(0).getCoordinates().getX();
				stop = vertices.get(1).getCoordinates().getX();
				while (counter <= stop) {
					pixelArray[counter][row].setBoundary(true);
					counter++;
				}
			}
		}
		container.setPixelArray(pixelArray);
		container.setConcaveGapPixelList(concavePixelList);
	}

	/**
	 * This method has the purpose to compute all closing edges from all concave
	 * pixels that are not part of the maximum independent set of edges, which was
	 * found after the Maximum Matching was called. Each edge shifts either up or
	 * down until it reaches a boundary or the object border.
	 * 
	 * @param concavePixel
	 * @param edgeList
	 * @param verticalEdgeList
	 */
	public void computeClosingEdges(Pixel concavePixel, List<Edge> edgeList, List<Edge> verticalEdgeList) {
		int yCounter = concavePixel.getCoordinates().getY();
		int xCounter = concavePixel.getCoordinates().getX();
		Pixel[][] pixelArray = container.getPixelArray();
		if (concavePixel.getConcaveDirection().equals(Direction.UPPER)
				|| concavePixel.getConcaveDirection().equals(Direction.UPPERRIGHT)
				|| concavePixel.getConcaveDirection().equals(Direction.UPPERLEFT)) {
			yCounter++;
			while (pixelArray[xCounter][yCounter].isInsideFigure() && xCounter < pixelArray[0].length - 1
					&& yCounter < pixelArray.length - 1 && !pixelArray[xCounter][yCounter].isBoundary()) {
				yCounter++;
			}
			Coordinates coordinates = new Coordinates(xCounter, yCounter);
			Pixel stopPixel = new Pixel(coordinates);
			List<Pixel> vertices = new ArrayList<>();
			vertices.add(concavePixel);
			vertices.add(stopPixel);
			Edge edge = new Edge(vertices, Angle.VERTICAL);
			verticalEdgeList.add(edge);
			edgeList.add(edge);
		} else {
			yCounter--;
			while (yCounter > 0 && pixelArray[xCounter][yCounter].isInsideFigure()
					&& !pixelArray[xCounter][yCounter].isBoundary()) {
				yCounter--;
			}
			Coordinates coordinates = new Coordinates(xCounter, yCounter);
			Pixel stopPixel = new Pixel(coordinates);
			List<Pixel> vertices = new ArrayList<>();
			vertices.add(stopPixel);
			vertices.add(concavePixel);
			Edge edge = new Edge(vertices, Angle.VERTICAL);
			verticalEdgeList.add(edge);
			edgeList.add(edge);
		}
	}

	/**
	 * This method creates the minimumDissection of the Object by calling
	 * computeClosingEdges on every concavePixel from the concavePixelList, which is
	 * consisting only of the remaining pixels, that aren't already part of the
	 * already existing edges. In the end the current edgeList is saved to the
	 * dataContainer.
	 * 
	 */
	public void computeMinimumDissection() {
		List<Edge> edgeList = container.getEdgeList();
		List<Edge> verticalEdgeList = new ArrayList<>();
		computeBoundaries();
		List<Pixel> concavePixelList = container.getConcaveGapPixelList();
		for (Pixel concavePixel : concavePixelList) {
			computeClosingEdges(concavePixel, edgeList, verticalEdgeList);
		}
		container.setEdgeList(edgeList);
	}

	/**
	 * This method computes a skeleton out of the vertical edges and marks every
	 * pixel that is part of the edge as a border. These pixels serve as markers for
	 * the method computeDividedSegments, which is called later. At the end of this
	 * method the pixelArray is saved to the dataContainer
	 * 
	 */
	public void computeSegmentSkeleton() {
		List<Edge> edgeList = container.getEdgeList();
		Pixel[][] pixelArray = container.getPixelArray();
		for (Edge edge : edgeList) {
			List<Pixel> vertices = edge.getVertices();
			Pixel start = vertices.get(0);
			Pixel stop = vertices.get(1);
			if (edge.getAngle().equals(Angle.VERTICAL)) {
				int x = start.getCoordinates().getX();
				int startY = start.getCoordinates().getY();
				int stopY = stop.getCoordinates().getY();
				for (int i = startY + 1; i < stopY; i++) {
					pixelArray[x][i].setBorder(true);
				}
			}
		}
		container.setPixelArray(pixelArray);
	}

	/**
	 * This method computes the divided segments that are seperated by the markers
	 * isBorder. The method iterates over the map containing the horizontal rows and
	 * extracts new rows based on the markers. The new segments are than saved in
	 * lists that are mapped to the horizontal row, which marks the key of the map.
	 * 
	 */
	public void computeDividedSegments() {
		Pixel[][] pixelArray = container.getPixelArray();
		Map<Integer, List<List<Pixel>>> rowOrderedSegments = container.getHorizontalRowSegmentsMap();
		for (Entry<Integer, List<List<Pixel>>> entry : rowOrderedSegments.entrySet()) {
			int key = entry.getKey();
			List<List<Pixel>> segments = entry.getValue();
			List<List<Pixel>> resultSegments = new ArrayList<>();
			for (List<Pixel> segment : segments) {
				Pixel start = segment.get(0);
				Pixel stop = segment.get(1);
				for (int i = start.getCoordinates().getX(); i < stop.getCoordinates().getX(); i++) {
					boolean isBorder = pixelArray[i][key].isBorder();
					if (isBorder || i == stop.getCoordinates().getX() - 1) {
						Pixel pixel = new Pixel(new Coordinates(i - 1, key));
						List<Pixel> edge = new ArrayList<>();
						edge.add(start);
						edge.add(pixel);
						resultSegments.add(edge);
						start = new Pixel(new Coordinates(i + 1, key));
					}
				}
			}
			rowOrderedSegments.put(key, resultSegments);
		}
		container.setHorizontalRowSegmentsMap(rowOrderedSegments);
	}

	/**
	 * This method sorts the rows to blocks. For that purpose, all rows that contain
	 * the same range in terms of the row length are put into a map, where the range
	 * serves as a key. At the end, the map with the different ranges of rows as
	 * keys and the rows as values are saved to the dataContainer.
	 * 
	 */
	public void computeBlocks() {
		Map<Integer, List<List<Pixel>>> rowOrderedSegments = container.getHorizontalRowSegmentsMap();
		Set<Integer> keys = rowOrderedSegments.keySet();
		Map<List<Integer>, Set<Integer>> blockMap = new HashMap<>();
		for (Integer key : keys) {
			if (rowOrderedSegments.containsKey(key)) {
				List<List<Pixel>> segments = rowOrderedSegments.get(key);
				for (List<Pixel> segment : segments) {
					int startX = segment.get(0).getCoordinates().getX();
					int stopX = segment.get(1).getCoordinates().getX();
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
		container.setBlockMap(blockMap);
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
	public List<Block> buildBlocks() {
		Map<List<Integer>, Set<Integer>> blockSetMap = container.getBlockMap();
		Set<Entry<List<Integer>, Set<Integer>>> entrySet = blockSetMap.entrySet();
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
						if (height - stack.peek() == 2) {
							y = stack.pop();
							counter++;
							if (stack.isEmpty()) {
								blockList.add(new Block(x / 2, y / 2, width / 2, height / 2));
							}
						} else {
							blockList.add(new Block(x / 2, y / 2, width / 2, height / 2));
							height = stack.pop();
							y = height;
							if (stack.isEmpty()) {
								blockList.add(new Block(x / 2, y / 2, width / 2, height / 2));
							}
							counter++;
						}
					} else {
						if (y - stack.peek() == 2) {
							y = stack.pop();
							if (stack.isEmpty()) {
								blockList.add(new Block(x / 2, y / 2, width / 2, height / 2));
							}
						} else {
							blockList.add(new Block(x / 2, y / 2, width / 2, height / 2));
							height = stack.pop();
							y = height;
							if (stack.isEmpty()) {
								blockList.add(new Block(x / 2, y / 2, width / 2, height / 2));
							}
						}
					}
				}
			} else {
				blockList.add(new Block(x / 2, height / 2, width / 2, height / 2));
			}
		}
		return blockList;
	}

}
