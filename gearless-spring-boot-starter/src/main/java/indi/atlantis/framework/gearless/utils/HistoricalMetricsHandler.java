package indi.atlantis.framework.gearless.utils;

/**
 * 
 * HistoricalMetricsHandler
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface HistoricalMetricsHandler<T extends Metric<T>> {

	void handleHistoricalMetrics(String metric, T metricUnit);

}