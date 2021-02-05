package org.springtribe.framework.gearless.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * SequentialMetricsCollector
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface SequentialMetricsCollector<T extends Metric<T>> extends MetricsCollector<T> {

	default T set(String metric, T metricUnit) {
		return set(metric, Long.min(System.currentTimeMillis(), metricUnit.getTimestamp()), metricUnit);
	}

	T set(String metric, long timestamp, T metricUnit);

	default T get(String metric) {
		Map<String, T> data = sequence(metric);
		Map.Entry<String, T> lastEntry = MapUtils.getLastEntry(data);
		return lastEntry != null ? lastEntry.getValue() : null;
	}

	default Map<String, T> fetch() {
		Map<String, T> data = new LinkedHashMap<String, T>();
		for (String metric : metrics()) {
			data.put(metric, get(metric));
		}
		return data;
	}

	Map<String, T> sequence(String metric);

}
