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

import java.util.Map;

/**
 * 
 * UserMetricSequencer
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public interface UserMetricSequencer<I, V> extends MetricSequencer<I, UserMetric<V>> {

	default Map<String, Map<String, Object>> sequence(I identifier, String metric, boolean asc, String datePattern) {
		return sequence(identifier, new String[] { metric }, asc, datePattern);
	}

	Map<String, Map<String, Object>> sequence(I identifier, String[] metrics, boolean asc, String datePattern);

	Map<String, Map<String, Object>> sequenceLatest(I identifier, String[] metrics, String datePattern);

}