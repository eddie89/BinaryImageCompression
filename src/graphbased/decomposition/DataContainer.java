package graphbased.decomposition;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.princeton.cs.algs4.Graph;

/**
 * 
 * @author daniel tremmel
 *
 *         This class serves as a container class to store all kinds of data,
 *         that is generated in the course of the compression. Therefore, it
 *         contains several getters and setters for retrieving and setting the
 *         data during runtime.
 */
public class DataContainer {

	private Pixel[][] pixelArray;
	private boolean[][] blackAndwhite;
	private List<Pixel> blackPixelList;
	private List<Pixel> concavePixelList;
	private List<Pixel> concaveGapPixelList;
	private Map<Angle, Map<Integer, List<List<Pixel>>>> allCogridEdgesMap;
	private Map<Angle, List<Edge>> angleOrderedEdgeMap;
	private Graph graph;
	private Map<Integer, Edge> adjacencyMatrix;
	private List<Edge> edgeList;
	private List<Integer> allEdgesList;
	private Map<Integer, List<List<Pixel>>> horizontalRowSegmentsMap;
	private Map<Integer, List<List<Pixel>>> verticalRowSegmentsMap;
	private Map<List<Integer>, Set<Integer>> blockSetMap;

	public DataContainer() {
	}

	public DataContainer(Pixel[][] pixelArray) {
		this.pixelArray = pixelArray;
	}

	public Pixel[][] getPixelArray() {
		return pixelArray;
	}

	public void setPixelArray(Pixel[][] pixelArray) {
		this.pixelArray = pixelArray;
	}

	public boolean[][] getBlackAndwhite() {
		return blackAndwhite;
	}

	public void setBlackAndwhite(boolean[][] blackAndwhite) {
		this.blackAndwhite = blackAndwhite;
	}

	public List<Pixel> getBlackPixelList() {
		return blackPixelList;
	}

	public void setBlackPixelList(List<Pixel> blackPixelList) {
		this.blackPixelList = blackPixelList;
	}

	public List<Pixel> getConcavePixelList() {
		return concavePixelList;
	}

	public void setConcavePixelList(List<Pixel> concavePixelList) {
		this.concavePixelList = concavePixelList;
	}

	public List<Pixel> getConcaveGapPixelList() {
		return concaveGapPixelList;
	}

	public void setConcaveGapPixelList(List<Pixel> concaveGapPixelList) {
		this.concaveGapPixelList = concaveGapPixelList;
	}

	public Map<Angle, Map<Integer, List<List<Pixel>>>> getAllCogridEdgesMap() {
		return allCogridEdgesMap;
	}

	public void setAllCogridVerticesMap(Map<Angle, Map<Integer, List<List<Pixel>>>> allCogridEdgesMap) {
		this.allCogridEdgesMap = allCogridEdgesMap;
	}

	public Map<Angle, List<Edge>> getAngleOrderedEdgeMap() {
		return angleOrderedEdgeMap;
	}

	public void setAngleOrderedEdgeMap(Map<Angle, List<Edge>> angleOrderedEdgeMap) {
		this.angleOrderedEdgeMap = angleOrderedEdgeMap;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public Map<Integer, Edge> getAdjacencyMatrix() {
		return adjacencyMatrix;
	}

	public void setAdjacencyMatrix(Map<Integer, Edge> adjacencyMatrix) {
		this.adjacencyMatrix = adjacencyMatrix;
	}

	public List<Edge> getEdgeList() {
		return edgeList;
	}

	public void setEdgeList(List<Edge> edgeList) {
		this.edgeList = edgeList;
	}

	public List<Integer> getAllEdgesList() {
		return allEdgesList;
	}

	public void setAllEdgesList(List<Integer> allEdgesList) {
		this.allEdgesList = allEdgesList;
	}

	public Map<Integer, List<List<Pixel>>> getHorizontalRowSegmentsMap() {
		return horizontalRowSegmentsMap;
	}

	public void setHorizontalRowSegmentsMap(Map<Integer, List<List<Pixel>>> horizontalRowSegmentsMap) {
		this.horizontalRowSegmentsMap = horizontalRowSegmentsMap;
	}

	public Map<Integer, List<List<Pixel>>> getVerticalRowSegmentsMap() {
		return verticalRowSegmentsMap;
	}

	public void setVerticalRowSegmentsMap(Map<Integer, List<List<Pixel>>> verticalRowSegmentsMap) {
		this.verticalRowSegmentsMap = verticalRowSegmentsMap;
	}

	public Map<List<Integer>, Set<Integer>> getBlockMap() {
		return blockSetMap;
	}

	public void setBlockMap(Map<List<Integer>, Set<Integer>> blockSetMap) {
		this.blockSetMap = blockSetMap;
	}

}
