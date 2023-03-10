/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package io.atlantisframework.vortex.metric.api;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.time.DateUtils;

/**
 * 
 * GenericUserMetricSequencer
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public abstract class GenericUserMetricSequencer<I, V> extends SimpleMetricSequencer<I, UserMetric<V>>
		implements UserMetricSequencer<I, V> {

	public GenericUserMetricSequencer(int span, TimeWindowUnit timeWindowUnit, int bufferSize,
			MetricEvictionHandler<I, UserMetric<V>> evictionHandler) {
		super(span, timeWindowUnit, bufferSize, evictionHandler);
	}

	@Override
	public Map<String, Map<String, Object>> sequenceLatest(I identifier, String[] metrics, String datePattern) {
		DateTimeFormatter df = StringUtils.isNotBlank(datePattern) ? DateTimeFormatter.ofPattern(datePattern)
				: DateTimeFormatter.ofPattern("HH:mm:ss");
		Map<String, Map<String, Object>> renderer = new LinkedHashMap<String, Map<String, Object>>();
		for (String metric : metrics) {
			Map<Instant, UserMetric<V>> sequence = super.sequence(identifier, metric);
			Map.Entry<Instant, UserMetric<V>> lastEntry = MapUtils.getLastEntry(sequence);
			if (lastEntry != null) {
				String time = lastEntry.getKey().atZone(ZoneId.systemDefault()).toLocalDateTime().format(df);
				Map<String, Object> data = MapUtils.get(renderer, time, () -> {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(metric, renderNull(lastEntry.getValue().getTimestamp()));
					return map;
				});
				data.put(metric, render(metric, time, lastEntry.getValue()));
			}
		}
		return renderer;
	}

	public Map<String, Map<String, Object>> sequence(I identifier, String[] metrics, boolean asc, String datePattern) {
		DateTimeFormatter df = StringUtils.isNotBlank(datePattern) ? DateTimeFormatter.ofPattern(datePattern)
				: DateTimeFormatter.ofPattern("HH:mm:ss");
		long timestamp = System.currentTimeMillis();
		Map<String, Map<String, Object>> renderer = new LinkedHashMap<String, Map<String, Object>>();
		String time;
		for (String metric : metrics) {
			Map<Instant, UserMetric<V>> sequence = super.sequence(identifier, metric);
			for (Map.Entry<Instant, UserMetric<V>> entry : sequence.entrySet()) {
				time = entry.getKey().atZone(ZoneId.systemDefault()).toLocalDateTime().format(df);
				Map<String, Object> data = MapUtils.get(renderer, time, () -> {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(metric, renderNull(entry.getValue().getTimestamp()));
					return map;
				});
				data.put(metric, render(metric, time, entry.getValue()));
				timestamp = timestamp > 0 ? Math.min(entry.getValue().getTimestamp(), timestamp) : entry.getValue().getTimestamp();
			}
		}
		return render(metrics, renderer, timestamp, asc, df);
	}

	protected final Map<String, Map<String, Object>> render(String[] metrics, Map<String, Map<String, Object>> renderer, long timestamp,
			boolean asc, DateTimeFormatter df) {
		int span = getSpan();
		int bufferSize = getBufferSize();
		TimeWindowUnit timeWindow = getTimeWindowUnit();
		Date startTime;
		if (asc) {
			Date date = new Date(timestamp);
			int amount = span * bufferSize;
			Date endTime = DateUtils.addField(date, timeWindow.getCalendarField(), amount);
			if (endTime.compareTo(new Date()) <= 0) {
				asc = false;
				startTime = new Date();
			} else {
				startTime = date;
			}
		} else {
			startTime = new Date();
		}
		Map<String, Map<String, Object>> sequentialMap = asc
				? timeWindow.ascendingMap(startTime, span, bufferSize, metrics, df, timeInMs -> {
					return renderNull(timeInMs);
				})
				: timeWindow.descendingMap(startTime, span, bufferSize, metrics, df, timeInMs -> {
					return renderNull(timeInMs);
				});
		String datetime;
		for (Map.Entry<String, Map<String, Object>> entry : renderer.entrySet()) {
			datetime = entry.getKey();
			if (sequentialMap.containsKey(datetime)) {
				sequentialMap.put(datetime, entry.getValue());
			}
		}
		return sequentialMap;
	}

	protected Map<String, Object> render(String metric, String time, UserMetric<V> metricUnit) {
		return metricUnit.toEntries();
	}

	protected abstract Map<String, Object> renderNull(long timeInMs);

}
