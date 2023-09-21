package io.github.braully.graph.operation;

import io.github.braully.graph.UndirectedSparseGraphTO;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import org.apache.commons.math3.util.CombinatoricsUtils;

public class GraphHullNumber extends GraphHullNumberAbstract implements IGraphOperation {

    static final String description = "Hull Number Brute";

    @Override
    public Set<Integer> findMinHullSetGraph(UndirectedSparseGraphTO<Integer, Integer> graph) {
        Set<Integer> ceilling = calcCeillingHullNumberGraph(graph);
        Set<Integer> hullSet = ceilling;
        if (graph == null || graph.getVertices().isEmpty()) {
            return ceilling;
        }
        int maxSizeSet = ceilling.size();
        int currentSize = 2;
        int countOneNeigh = 0;

        Collection<Integer> vertices = graph.getVertices();

        for (Integer i : vertices) {
            if (graph.degree(i) == 1) {
                countOneNeigh++;
            }
        }
        currentSize = Math.max(currentSize, countOneNeigh);
//        System.out.println("Find hull number");
        while (currentSize < maxSizeSet) {
//            System.out.println("trying : " + currentSize);
            Set<Integer> hs = findHullSetBruteForce(graph, currentSize);
            if (hs != null && !hs.isEmpty()) {
                hullSet = hs;
//                System.out.println("ok");
                break;
            } else {
//                System.out.println("not found");
            }
            currentSize++;
        }
        return hullSet;
    }

    public int addVertToS(Integer verti, Set<Integer> s,
            UndirectedSparseGraphTO<Integer, Integer> graph,
            int[] aux) {
        int countIncluded = 0;
        if (verti == null || aux[verti] >= INCLUDED) {
            return countIncluded;
        }

        aux[verti] = aux[verti] + INCLUDED;
        if (s != null) {
            s.add(verti);
        }

        Queue<Integer> mustBeIncluded = new ArrayDeque<>();
        mustBeIncluded.add(verti);
        while (!mustBeIncluded.isEmpty()) {
            verti = mustBeIncluded.remove();
            Collection<Integer> neighbors = graph.getNeighborsUnprotected(verti);
            for (int vertn : neighbors) {
                if (vertn == verti) {
                    continue;
                }
                if (vertn != verti && ++aux[vertn] == INCLUDED) {
                    mustBeIncluded.add(vertn);
                }
            }
            countIncluded++;
        }
        return countIncluded;
    }

    private Set<Integer> calcCeillingHullNumberGraph(UndirectedSparseGraphTO<Integer, Integer> graph) {
        Set<Integer> ceilling = new HashSet<>();
        if (graph != null) {
            Collection<Integer> vertices = graph.getVertices();

            if (vertices != null) {
                ceilling.addAll(vertices);
            }
        }
        return ceilling;
    }

    public Set<Integer> findHullSetBruteForce(UndirectedSparseGraphTO<Integer, Integer> graph, int currentSetSize) {
        Set<Integer> hullSet = null;
        if (graph == null || graph.getVertexCount() <= 0) {
            return hullSet;
        }
        Collection vertices = graph.getVertices();
        Iterator<int[]> combinationsIterator = CombinatoricsUtils.combinationsIterator(graph.getVertexCount(), currentSetSize);
        while (combinationsIterator.hasNext()) {
            int[] currentSet = combinationsIterator.next();
            if (checkIfHullSet(graph, currentSet)) {
                hullSet = new HashSet<>(currentSetSize);
                for (int i : currentSet) {
                    hullSet.add(i);
                }
                break;
            }
        }
        return hullSet;
    }

    public boolean checkIfHullSet(UndirectedSparseGraphTO<Integer, Integer> graph,
            int[] currentSet) {
        if (currentSet == null || currentSet.length == 0) {
            return false;
        }
        Set<Integer> fecho = new HashSet<>();
        Collection vertices = graph.getVertices();
        int[] aux = new int[graph.getVertexCount()];
        for (int i = 0; i < aux.length; i++) {
            aux[i] = 0;
        }

        Queue<Integer> mustBeIncluded = new ArrayDeque<>();
        for (Integer v : currentSet) {
            mustBeIncluded.add(v);
            aux[v] = INCLUDED;
        }
        while (!mustBeIncluded.isEmpty()) {
            Integer verti = mustBeIncluded.remove();
            fecho.add(verti);
            Collection<Integer> neighbors = graph.getNeighbors(verti);
            for (int vertn : neighbors) {
                if (vertn == verti) {
                    continue;
                }
                if (vertn != verti && aux[vertn] < INCLUDED) {
                    aux[vertn] = aux[vertn] + NEIGHBOOR_COUNT_INCLUDED;
                    if (aux[vertn] == INCLUDED) {
                        mustBeIncluded.add(vertn);
                    }
                }
            }
            aux[verti] = PROCESSED;
        }
        return fecho.size() == graph.getVertexCount();
    }

    public void includeVertex(UndirectedSparseGraphTO<Integer, Integer> graph, Set<Integer> fecho, int[] aux, int i) {
        fecho.add(i);
        aux[i] = INCLUDED;
        Collection<Integer> neighbors = graph.getNeighbors(i);
        for (int vert : neighbors) {
            if (vert != i) {
                int previousValue = aux[vert];
                aux[vert] = aux[vert] + NEIGHBOOR_COUNT_INCLUDED;
                if (previousValue < INCLUDED && aux[vert] >= INCLUDED) {
                    includeVertex(graph, fecho, aux, vert);
                }
            }
        }
    }

    public String getTypeProblem() {
        return type;
    }

    public String getName() {
        return description;
    }

}
