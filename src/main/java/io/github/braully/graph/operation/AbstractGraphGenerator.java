package io.github.braully.graph.operation;

import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author braully
 */
public abstract class AbstractGraphGenerator {

    static final Logger log = Logger.getLogger(AbstractGraphGenerator.class.getName());

    /*
     * 
     */
    protected String getStringParameter(Map<String, Object> parameters, String paramName) {
        String ret = null;
        try {
            ret = parameters.get(paramName).toString();
        } catch (Exception e) {
//            log.log(Level.SEVERE, "Fail parse", e);
        }
        return ret;
    }

    protected Integer getIntegerParameter(Map parameters, String paramName) {
        Integer ret = null;
        try {
            ret = Integer.parseInt(parameters.get(paramName).toString());
        } catch (Exception e) {
//            log.log(Level.SEVERE, "Fail parse integer", e);
        }
        return ret;
    }

    protected Double getDoubleParameter(Map<String, Object> parameters, String paramName) {
        Double ret = null;
        try {
            ret = Double.parseDouble(parameters.get(paramName).toString());
        } catch (Exception e) {
//            log.log(Level.SEVERE, "Fail parse double", e);
        }
        return ret;
    }
}
