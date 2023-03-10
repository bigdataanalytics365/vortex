package io.atlantisframework.vortex.metric;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.atlantisframework.vortex.metric.api.BigInt;
import io.atlantisframework.vortex.metric.api.Bool;
import io.atlantisframework.vortex.metric.api.Numeric;

/**
 * 
 * MetricSequencerAutoConfiguration
 *
 * @author Fred Feng
 * @since 2.0.1
 */
@Import({ UserSequencerController.class })
@Configuration
public class MetricSequencerAutoConfiguration {

	@ConditionalOnMissingBean
	@Bean
	public MetricSequencerFactory defaultMetricSequencerFactory() {
		return new DefaultMetricSequencerFactory();
	}

	@Bean
	public UserSequencer sequencer() {
		return new UserSequencer();
	}

	@Bean
	public UserMetricRegistrarScanner userMetricRegistrarScanner() {
		return new UserMetricRegistrarScanner();
	}

	@Bean
	public UserMetricRegistrar<BigInt> bigIntMetricRegistrar(MetricSequencerFactory metricSequencerFactory) {
		return new GenericUserMetricRegistration<BigInt>(metricSequencerFactory.getBigIntMetricSequencer(), new BigIntTypeHandler());
	}

	@Bean
	public UserMetricRegistrar<Numeric> numericMetricRegistrar(MetricSequencerFactory metricSequencerFactory) {
		return new GenericUserMetricRegistration<Numeric>(metricSequencerFactory.getNumericMetricSequencer(), new NumericTypeHandler());
	}

	@Bean
	public UserMetricRegistrar<Bool> boolMetricRegistrar(MetricSequencerFactory metricSequencerFactory) {
		return new GenericUserMetricRegistration<Bool>(metricSequencerFactory.getBoolMetricSequencer(), new BoolTypeHandler());
	}

	@Bean
	public SynchronizationExecutor incrementalSynchronizationExecutor() {
		return new IncrementalSynchronizationExecutor();
	}

	@Bean
	public SynchronizationExecutor fullSynchronizationExecutor() {
		return new FullSynchronizationExecutor();
	}

}
