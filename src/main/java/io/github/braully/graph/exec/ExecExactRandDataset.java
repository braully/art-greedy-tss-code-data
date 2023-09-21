package io.github.braully.graph.exec;

import io.github.braully.graph.operation.*;
import io.github.braully.graph.UndirectedSparseGraphTO;
import static io.github.braully.graph.exec.ExecBigDataSets.operations;
import static io.github.braully.graph.operation.IGraphOperation.DEFAULT_PARAM_NAME_SET;
import io.github.braully.graph.util.MapCountOpt;
import io.github.braully.graph.util.UtilDatabase;
import io.github.braully.graph.util.UtilGraph;
import io.github.braully.graph.util.UtilProccess;
import static io.github.braully.graph.util.UtilProccess.printTimeFormated;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Exec Exact Algorithm for Rand
 *
 * @author Braully Rocha da Silva
 */
public class ExecExactRandDataset {

    private static int OFFSET = 100;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        TSSBruteForceOptm opf = new TSSBruteForceOptm();
        TIPDecomp tip = new TIPDecomp();
        TSSCordasco tss = new TSSCordasco();

        CCMPanizi ccm = new CCMPanizi();
        ccm.setRefine(true);
        ccm.setRefine2(true);

        GreedyDegree gd = new GreedyDegree();
        gd.setRefine(true);
        gd.setRefine2(true);

        GreedyDeltaTss gdt = new GreedyDeltaTss();
        gdt.setRefine2(true);

        GreedyDeltaXDifTotal gdxd = new GreedyDeltaXDifTotal();

        GreedyDistAndDifDelta gdd = new GreedyDistAndDifDelta();
        gdd.setRefine(true);
        gdd.setRefine2(true);

        UndirectedSparseGraphTO<Integer, Integer> graph = null;

        String strFile = "data/rand/grafos-rand-densall-n5-100.txt";

        AbstractHeuristic[] operations = new AbstractHeuristic[]{
            opf, tip, tss, ccm, gd, 
//            gdt, 
            gdxd, gdd
        };

        Map<String, Boolean> piorou = new HashMap<>();
        Integer[] delta = new Integer[operations.length];
        int[] contMelhorGlobal = new int[operations.length];
        int[] contPiorGlobal = new int[operations.length];
        int[] contIgualGlobal = new int[operations.length];
        int[] contMelhor = new int[operations.length];
        int[] contPior = new int[operations.length];
        int[] contIgual = new int[operations.length];
        int maxDelta = 0;

        MapCountOpt mapCount = new MapCountOpt((operations.length + 1) * OFFSET * 10);

        for (int i = 0;
                i < operations.length;
                i++) {
            contMelhorGlobal[i] = contPiorGlobal[i] = contIgualGlobal[i] = contMelhor[i] = contPior[i] = contIgual[i] = 0;
        }

        Integer[] result = new Integer[operations.length];
        long totalTime[] = new long[operations.length];
        List<String> ops = Arrays.asList(new String[]{
            "k", //                        "r",
        //            "m"
        });
        for (int k = 1;
                k <= 10; k++) {
            String op = "k";
            for (AbstractHeuristic ab : operations) {
                ab.setK(k);
            }
            System.out.println("-------------\n\nk: " + k);
            BufferedReader files = new BufferedReader(new FileReader(strFile));
            String line = null;
            int contgraph = 0;
            int density = 1;

            while (null != (line = files.readLine())) {
                graph = UtilGraph.loadGraphES(line);
                graph.setName("rand-n5-100-dens0" + density + "-cont-" + contgraph);
                String gname = graph.getName();
                contgraph++;
                if ((contgraph % 20) == 0) {
                    density++;
                }
                String id = strFile + "-" + density + "-" + contgraph;

                for (int i = 0; i < operations.length; i++) {
                    Map<String, Object> doOperation = null;

                    String arquivadoStr = operations[i].getName() + "-" + op + k + "-" + gname;
                    int[] get = UtilDatabase.getResultCache(arquivadoStr);
                    if (get != null) {
                        result[i] = get[0];
                        totalTime[i] = get[1];
                    } else {
                        UtilProccess.startTime();
                        doOperation = operations[i].doOperation(graph);
                        totalTime[i] += UtilProccess.endTime();
                        result[i] = (Integer) doOperation.get(IGraphOperation.DEFAULT_PARAM_NAME_RESULT);
                    }

                    String out = "Rand\t" + gname + "\t" + graph.getVertexCount() + "\t"
                            + graph.getEdgeCount()
                            + "\t" + op + "\t" + k + "\t"
                            //                                + grupo[i]
                            + "grupo"
                            + "\t" + operations[i].getName()
                            + "\t" + result[i] + "\t" + totalTime[i] + "\n";

//                        System.out.print("xls: " + out);
                    System.out.print(out);
                    if (doOperation != null) {
                        boolean checkIfHullSet = operations[i].checkIfHullSet(graph, ((Set<Integer>) doOperation.get(DEFAULT_PARAM_NAME_SET)));
                        if (!checkIfHullSet) {
                            System.out.println("ALERT: ----- RESULTADO ANTERIOR IS NOT HULL SET");
                            System.out.println(line);
                        }
                    }
                    if (i == 0) {
                        delta[i] = 0;
                    } else {
                        delta[i] = result[0] - result[i];

                    }
                    if (delta[i] == 0) {
                        contIgual[i]++;
                        contIgualGlobal[i]++;
                    } else if (delta[i] > 0) {
                        contMelhor[i]++;
                        contMelhorGlobal[i]++;
                        if (k == 2) {
                            piorou.put(id, true);
                        }
                    } else {
                        int absde = Math.abs(delta[i]);
                        mapCount.inc(i * OFFSET + absde);
                        if (absde > maxDelta) {
                            maxDelta = absde;
                        }
                        contPior[i]++;
                        contPiorGlobal[i]++;
                        Boolean get1 = piorou.get(id);
                        if (get1 != null && get1) {
//                                    System.out.println("grafo piorou: " + id + " em k: " + k);
//                                    System.out.println(graphES.getEdgeString());
                            piorou.remove(id);
                        }
                    }
                }
            }

            if (true) {
                System.out.println("Resumo parcial: " + k);
                for (int i = 1; i < operations.length; i++) {
                    int total = contMelhor[i] + contPior[i] + contIgual[i];
                    System.out.println("==========");
                    System.out.println("Operacao: " + operations[i].getName());
                    System.out.println("Melhor: " + contMelhor[i]);
                    System.out.println("Pior: " + contPior[i]);
                    System.out.println("Igual: " + contIgual[i]);
                    System.out.println("Total: " + total);
                    System.out.println("------------");
                    System.out.println("Melhor: " + (contMelhor[i] * 100 / total) + "pct");
                    System.out.println("Igual: " + ((total - (contMelhor[i] + contPior[i]))
                            * 100 / total) + "pct"
                    );
                    System.out.println("Pior: " + (contPior[i] * 100 / total) + "pct");
                }
                for (int i = 0; i < operations.length; i++) {
                    contMelhor[i] = contPior[i] = contIgual[i] = 0;
                }
            }

        }

        System.out.println(
                "\n\nResumo Global");
        System.out.println("OPs:" + ops);
        for (int i = 1;
                i < operations.length;
                i++) {
            System.out.println("***************");
            System.out.println("Operacao: " + operations[i].getName());
            if (contMelhorGlobal[i] > contPiorGlobal[i]) {
                System.out.println("GOOD " + operations[0].getName());
            } else {
                System.out.println("WORST " + operations[0].getName());
            }

            System.out.println("Melhor: " + contMelhorGlobal[i]);
            System.out.println("Pior: " + contPiorGlobal[i]);
            System.out.println("Igual: " + contIgualGlobal[i]);

            System.out.println("------------");
            for (int x = 1; x <= maxDelta; x++) {
                Integer cont = mapCount.getCount(i * OFFSET + x);
                if (cont > 0) {
                    System.out.println("Delta: " + x + " cont: " + cont);
                }
            }
            System.out.println("------------");
            int total = contMelhorGlobal[i] + contPiorGlobal[i] + contIgualGlobal[i];
            if (total > 0) {
                System.out.println("Melhor: " + (contMelhorGlobal[i] * 100 / total) + "pct");
                System.out.println("Igual: " + ((total - (contMelhorGlobal[i] + contPiorGlobal[i]))
                        * 100 / total) + "pct"
                );
                System.out.println("Pior: " + (contPiorGlobal[i] * 100 / total) + "pct");
            }

        }
        for (int i = 0;
                i < operations.length;
                i++) {
            System.out.println(operations[i].getName());
            System.out.print("time: ");
            printTimeFormated(totalTime[i]);
//            System.out.println();

        }

    }
}
