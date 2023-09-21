package io.github.braully.graph.operation;

import io.github.braully.graph.UndirectedSparseGraphTO;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

public class GraphHullNumberHeuristicV1
        extends GraphHullNumber implements IGraphOperation {

    private static final Logger log = Logger.getLogger(GraphHullNumberHeuristicV1.class);

    static final String description = "Hull Number Heuristic";

    @Override
    public String getName() {
        return description;
    }

    public GraphHullNumberHeuristicV1() {
    }

    @Override
    public Set<Integer> findMinHullSetGraph(UndirectedSparseGraphTO<Integer, Integer> graph) {
        return buildOptimizedHullSet(graph);
    }

//    @Override
    public Set<Integer> buildOptimizedHullSet(UndirectedSparseGraphTO<Integer, Integer> graphRead) {
        Set<Integer> hullSet = null;
        Collection<Integer> vertices = graphRead.getVertices();

        Set<Integer> s = new HashSet<>();
        int vertexCount = graphRead.getVertexCount();
        int[] aux = new int[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            aux[i] = 0;
        }
        int sizeHs = 0;
        for (Integer v : vertices) {
            if (graphRead.degree(v) <= 1) {
                sizeHs = sizeHs + addVertToS(v, s, graphRead, aux);
            }
        }

        for (Integer v : vertices) {
            if (s.contains(v)) {
                continue;
            }
            Set<Integer> tmp = buildOptimizedHullSetFromStartVertice(graphRead, v, s, aux, sizeHs);
            if (hullSet == null || tmp.size() < hullSet.size()) {
                hullSet = tmp;
            }
        }
        return hullSet;
    }

    private Set<Integer> buildOptimizedHullSetFromStartVertice(UndirectedSparseGraphTO<Integer, Integer> graph,
            Integer v, Set<Integer> sini, int[] auxini, int sizeHsini) {
        Set<Integer> s = new HashSet<>(sini);
        int vertexCount = graph.getVertexCount();
        int[] aux = auxini.clone();
        int sizeHs = addVertToS(v, s, graph, aux) + sizeHsini;
        int bestVertice;
        do {
            bestVertice = -1;
            int maiorGrau = 0;
            int maiorDeltaHs = 0;
            int maiorContaminado = 0;

            for (int i = 0; i < vertexCount; i++) {
                //Se vertice já foi adicionado, ignorar
                if (aux[i] >= INCLUDED) {
                    continue;
                }
                int[] auxb = aux.clone();
                int deltaHsi = addVertToS(i, null, graph, auxb);

                int neighborCount = 0;
                int contaminado = 0;
                //Contabilizar quantos vertices foram adicionados
                for (int j = 0; j < vertexCount; j++) {
                    if (auxb[j] == INCLUDED) {
                        neighborCount++;
                    }
                    if (auxb[j] == NEIGHBOOR_COUNT_INCLUDED) {
                        contaminado++;
                    }
                }

                if (bestVertice == -1) {
                    maiorDeltaHs = deltaHsi;
                    maiorGrau = neighborCount;
                    maiorContaminado = contaminado;
                    bestVertice = i;
                } else if (deltaHsi > maiorDeltaHs) {
                    maiorDeltaHs = deltaHsi;
                    maiorGrau = neighborCount;
                    maiorContaminado = contaminado;
                    bestVertice = i;
                } else if (deltaHsi == maiorDeltaHs) {
                    if (neighborCount > maiorGrau) {
                        maiorDeltaHs = deltaHsi;
                        maiorGrau = neighborCount;
                        maiorContaminado = contaminado;
                        bestVertice = i;
                    } else if (neighborCount == maiorGrau) {
                        if (contaminado > maiorContaminado) {
                            maiorDeltaHs = deltaHsi;
                            maiorGrau = neighborCount;
                            maiorContaminado = contaminado;
                            bestVertice = i;
                        }
                    }
                }
            }
            if (bestVertice == -1) {
                break;
            }
            sizeHs = sizeHs + addVertToS(bestVertice, s, graph, aux);
        } while (sizeHs < vertexCount);
        return s;
    }
}
