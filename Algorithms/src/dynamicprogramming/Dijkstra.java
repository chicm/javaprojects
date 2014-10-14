package dynamicprogramming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class Vertex {
	private String name;
	private int value;
	public Vertex(String s, int v) {
		name = s;
		value =v;
	}
	public String getName() { return name; }
	public int getValue() { return value; }
}

class Graph {
	public HashMap <String, List<Vertex>> graph;
	public ArrayList <String> vertex;
	
	public String getVertex(int index) {
		return vertex.get(index); 
	}
	
	public int getDistance(String a, String b) {
		int ret = Integer.MAX_VALUE/2;
		
		if(a.equals(b))
			return 0;
		
		List<Vertex> list = graph.get(a);
		if(list == null || list.isEmpty())
			return ret;
		
		for (Vertex v : list) {
			if(v.getName().equals(b))
				return v.getValue();
		}
		return ret;
	}
	public int getDistance (String a, int i) {
		String b = vertex.get(i);
		return getDistance(a, b);
	}
	
	public Graph() {
		init();
	}
	public void init() {
		
		vertex = new ArrayList<>();
		vertex.add("A");
		vertex.add("B");
		vertex.add("C");
		vertex.add("D");
		vertex.add("E");
		vertex.add("F");
		
		graph = new HashMap<> ();
		
		List<Vertex> lista = new LinkedList<Vertex>();
		lista.add(new Vertex("B", 100));
		lista.add(new Vertex("C", 5));
		lista.add(new Vertex("D", 500));
		lista.add(new Vertex("E", 100));
		lista.add(new Vertex("F", 200));
		
		List<Vertex> listb = new LinkedList<Vertex>();
		listb.add(new Vertex("A", 10));
		listb.add(new Vertex("C", 5));
		listb.add(new Vertex("D", 5));
		
		List<Vertex> listc = new LinkedList<Vertex>();
		listc.add(new Vertex("A", 10));
		listc.add(new Vertex("B", 20));
		listc.add(new Vertex("D", 5));
		listc.add(new Vertex("E", 15));
		listc.add(new Vertex("F", 10));
		
		List<Vertex> listd = new LinkedList<Vertex>();
		listd.add(new Vertex("A", 10));
		listd.add(new Vertex("B", 3));
		listd.add(new Vertex("C", 5));
		listd.add(new Vertex("E", 8));
		listd.add(new Vertex("F", 10));
		
		List<Vertex> liste = new LinkedList<Vertex>();
		liste.add(new Vertex("A", 10));
		liste.add(new Vertex("B", 13));
		liste.add(new Vertex("F", 10));
		
		List<Vertex> listf = new LinkedList<Vertex>();
		listf.add(new Vertex("C", 15));
		listf.add(new Vertex("D", 18));
		listf.add(new Vertex("E", 10));
		
		graph.put("A", lista);
		graph.put("B", listb);
		graph.put("C", listc);
		graph.put("D", listd);
		graph.put("E", liste);
		graph.put("F", listf);
		
	}
}

public class Dijkstra {

	private Graph graph = new Graph();
	
	public void shortestPath() {
		int numVertex = graph.graph.size();
		int[][] m = new int[numVertex+1][numVertex];
		
		String current = graph.getVertex(0);
		int min = Integer.MAX_VALUE/2;
		String nextCurrent = current;
		HashSet<String> S = new HashSet<>();
		
		for(int i = 0; i < numVertex+1; i++ ) {
			current = nextCurrent;
			S.add(current);
			min = Integer.MAX_VALUE/2;
			for (int j = 0; j <numVertex; j++) {
								
				if(i ==0) {
					m[i][j] = graph.getDistance(current, j);
					
				} else {
					m[i][j] = m[i-1][j];
					int curDist = m[i-1][graph.vertex.indexOf(current)];
					if(curDist + graph.getDistance(current, j) < m[i-1][j])
						m[i][j] = curDist + graph.getDistance(current, j);
				}
				if(m[i][j]!=0 && m[i][j] < min && !S.contains(graph.getVertex(j)) ) {
					min = m[i][j];
					nextCurrent = graph.getVertex(j);
				}
				
			}
			
			for(int j = 0; j < numVertex; j++) {
				System.out.print(m[i][j] + " ");
			}
			System.out.println("");
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Dijkstra d = new Dijkstra();
		d.shortestPath();
	}

}
