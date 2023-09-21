package io.github.braully.graph.operation;

import io.github.braully.graph.UndirectedSparseGraphTO;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

public abstract class GraphHullNumberAbstract implements IGraphOperation {

    static final String type = "P3-Convexity";

    static final Logger log = Logger.getLogger(GraphHullNumberAbstract.class);
    //
    public static final int NEIGHBOOR_COUNT_INCLUDED = 1;
    public static final int INCLUDED = 2;
    public static final int PROCESSED = 3;

    public static final String PARAM_NAME_HULL_NUMBER = "number";
    public static final String PARAM_NAME_HULL_SET = "set";
    public static final String PARAM_NAME_SERIAL_TIME = "serial";
    public static final String PARAM_NAME_PARALLEL_TIME = "parallel";

    @Override
    public Map<String, Object> doOperation(UndirectedSparseGraphTO<Integer, Integer> graph) {
        Integer hullNumber = -1;
        Set<Integer> minHullSet = null;

        try {
            minHullSet = findMinHullSetGraph(graph);
            if (minHullSet != null && !minHullSet.isEmpty()) {
                hullNumber = minHullSet.size();
            }
        } catch (Exception ex) {
            log.error(null, ex);
        }

        /* Processar a buscar pelo hullset e hullnumber */
        Map<String, Object> response = new HashMap<>();
        response.put(PARAM_NAME_HULL_NUMBER, hullNumber);
        response.put(PARAM_NAME_HULL_SET, minHullSet);
        response.put(IGraphOperation.DEFAULT_PARAM_NAME_RESULT, hullNumber);
        return response;
    }

    public String getTypeProblem() {
        return type;
    }

    public abstract Set<Integer> findMinHullSetGraph(UndirectedSparseGraphTO<Integer, Integer> graph);

}
