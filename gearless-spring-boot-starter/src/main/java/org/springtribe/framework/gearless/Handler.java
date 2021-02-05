package org.springtribe.framework.gearless;

import org.springtribe.framework.gearless.common.Tuple;

/**
 * 
 * Handler
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface Handler {

	void onData(Tuple tuple);

	default String getTopic() {
		return Tuple.DEFAULT_TOPIC;
	}

}