/*******************************************************************************
 * Copyright (c) 2018 Amit Kumar Mondal
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 *******************************************************************************/
package com.amitinside.aggregate.state.provider;

import static com.amitinside.aggregate.state.AggregateState.PROPERTY;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.amitinside.aggregate.state.AggregateState;
import com.amitinside.aggregate.state.AggregateStateException;

public final class AggregateStatesTracker implements ServiceTrackerCustomizer<Object, Object> {

	private static final String UNIQUE_STATES_KEY_PREFIX = "%";
	private static final String NON_UNIQUE_STATES_KEY_PREFIX = "#";

	private final Lock monitor;
	private final BundleContext bundleContext;
	private final List<AggregateStateInfo> aggregateStateInfos;
	private volatile ServiceRegistration<AggregateState> serviceRegistration;

	public AggregateStatesTracker(BundleContext bundleContext) {
		requireNonNull(bundleContext, "BundleContext cannot be null");
		this.bundleContext = bundleContext;
		monitor = new ReentrantLock();
		aggregateStateInfos = new CopyOnWriteArrayList<>();
	}

	@Override
	public Object addingService(ServiceReference<Object> reference) {
		addAggregateStateInfo(reference);
		registerOrUpdateAggregateStateService();
		return bundleContext.getService(reference);
	}

	@Override
	public void modifiedService(ServiceReference<Object> reference, Object service) {
		removeAggregateStateInfo(reference);
		addAggregateStateInfo(reference);
		registerOrUpdateAggregateStateService();
	}

	@Override
	public void removedService(ServiceReference<Object> reference, Object service) {
		removeAggregateStateInfo(reference);
		registerOrUpdateAggregateStateService();
	}

	/**
	 * Deregisters already registered {@link AggregateState} service
	 */
	public void deregisterAggregateServiceRegistration() {
		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	private enum AggregateStateProvider implements AggregateState {
		INSTANCE;
	}

	private void addAggregateStateInfo(ServiceReference<Object> reference) {
		final AggregateStateInfo stateTrackingInfo = getAggregateStateInfo(reference);
		aggregateStateInfos.add(stateTrackingInfo);
	}

	private void removeAggregateStateInfo(ServiceReference<Object> reference) {
		final AggregateStateInfo stateTrackingInfo = getAggregateStateInfo(reference);
		aggregateStateInfos.remove(stateTrackingInfo);
	}

	private AggregateStateInfo getAggregateStateInfo(ServiceReference<Object> reference) {
		final Object property = reference.getProperty(PROPERTY);
		final Map<String, String> properties = new HashMap<>();

		List<String> states = null;
		if (property instanceof String) {
			states = Arrays.asList(String.valueOf(property));
		} else if (property instanceof String[]) {
			final String[] props = (String[]) property;
			states = Arrays.asList(props);
		} else {
			states = Collections.emptyList();
		}

		for (final String state : states) {
			final Object prop = reference.getProperty(state);
			if (prop == null) {
				final String message = String.format(
						"Aggregate State cannot be processed since the specified state(s) cannot be mapped to an existing property - [%s] in ServiceReference [%s]",
						prop, reference);
				throw new AggregateStateException(message);
			}
			properties.put(state, String.valueOf(prop));
		}

		final AggregateStateInfo info = new AggregateStateInfo();
		info.stateProperties = properties;
		info.serviceReference = reference;

		return info;
	}

	private void registerOrUpdateAggregateStateService() {
		monitor.lock();
		try {
			if (serviceRegistration == null) {
				serviceRegistration = bundleContext.registerService(AggregateState.class,
						AggregateStateProvider.INSTANCE, calculateProperties());
			} else {
				serviceRegistration.setProperties(calculateProperties());
			}
		} finally {
			monitor.unlock();
		}
	}

	private Dictionary<String, ?> calculateProperties() {
		final Map<String, Object> properties = new HashMap<>();
		for (final AggregateStateInfo stateInfo : aggregateStateInfos) {
			final Map<String, String> stateProperties = stateInfo.stateProperties;
			for (final Entry<String, String> entry : stateProperties.entrySet()) {
				final String key = entry.getKey();
				final String value = entry.getValue();

				if (properties.containsKey(key)) {
					final String[] newValue = append((String[]) properties.get(key), value);
					properties.put(key, newValue);
				} else {
					properties.put(key, new String[] { value });
				}
				final String[] keyValue = (String[]) properties.get(key);

				final String totalNoOfStatesPropertyKey = NON_UNIQUE_STATES_KEY_PREFIX + key;
				final String totalNoOfUniqueStatesPropertyKey = UNIQUE_STATES_KEY_PREFIX + key;

				properties.put(totalNoOfStatesPropertyKey, keyValue.length);
				properties.put(totalNoOfUniqueStatesPropertyKey, Arrays.stream(keyValue).collect(toSet()).size());
			}
		}
		return new Hashtable<>(properties);
	}

	private static <T> T[] append(T[] arr, T element) {
		final int N = arr.length;
		arr = Arrays.copyOf(arr, N + 1);
		arr[N] = element;
		return arr;
	}

}
