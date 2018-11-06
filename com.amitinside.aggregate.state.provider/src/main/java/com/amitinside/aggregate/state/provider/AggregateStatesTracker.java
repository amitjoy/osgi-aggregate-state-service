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

import static com.amitinside.aggregate.state.api.AggregateState.PROPERTY;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.amitinside.aggregate.state.api.AggregateState;

public final class AggregateStatesTracker implements ServiceTrackerCustomizer<Object, Object> {

	private final BundleContext bundleContext;
	private final Lock monitor = new ReentrantLock();
	private ServiceRegistration<AggregateState> aggregateStateServiceReg;
	private final List<AggregateStateTrackingInfo> trackingInfos = new CopyOnWriteArrayList<>();

	public AggregateStatesTracker(BundleContext bundleContext) {
		requireNonNull(bundleContext, "BundleContext cannot be null");
		this.bundleContext = bundleContext;
	}

	@Override
	public Object addingService(ServiceReference<Object> reference) {
		final AggregateStateTrackingInfo stateTrackingInfo = getAggregateStateTrackingInfo(reference);
		trackingInfos.add(stateTrackingInfo);
		registerOrUpdateAggregateStateService();
		return bundleContext.getService(reference);
	}

	private void registerOrUpdateAggregateStateService() {
		if (aggregateStateServiceReg == null) {
			aggregateStateServiceReg = bundleContext.registerService(AggregateState.class, new AggregateStateProvider(),
					calculateProperties());
		} else {
			aggregateStateServiceReg.setProperties(calculateProperties());
		}
	}

	@Override
	public void modifiedService(ServiceReference<Object> reference, Object service) {
		removedService(reference, service);
		addingService(reference);
	}

	@Override
	public void removedService(ServiceReference<Object> reference, Object service) {
		removeTrackingInfo(reference);
		registerOrUpdateAggregateStateService();
	}

	private void removeTrackingInfo(ServiceReference<Object> reference) {
		final AggregateStateTrackingInfo stateTrackingInfo = getAggregateStateTrackingInfo(reference);
		trackingInfos.remove(stateTrackingInfo);
	}

	private AggregateStateTrackingInfo getAggregateStateTrackingInfo(ServiceReference<Object> reference) {
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

		states.forEach(s -> {
			final Object prop = reference.getProperty(s);
			if (prop == null) {
				throw new RuntimeException(String.format(
						"Aggregate State cannot be processed since the specified state cannot be mapped to an existing proeprty - %s",
						s));
			}
			properties.put(s, String.valueOf(prop));
		});

		final AggregateStateTrackingInfo info = new AggregateStateTrackingInfo();
		info.stateProperties = properties;
		info.serviceReference = reference;

		return info;
	}

	private Dictionary<String, ?> calculateProperties() {
		monitor.lock();
		try {
			final Map<String, Object> properties = new HashMap<>();
			trackingInfos.stream().forEach(t -> {
				final Map<String, String> map = t.stateProperties;
				map.forEach((k, v) -> {
					if (properties.containsKey(k)) {
						append((String[]) properties.get(k), v);
					} else {
						properties.put(k, new String[] { v });
					}
					final String totalNoOfStatesKey = "#" + k;
					final String totalNoOfUniqueStatesKey = "%" + k;
					properties.put(totalNoOfStatesKey, ((String[]) properties.get(k)).length);
					properties.put(totalNoOfUniqueStatesKey,
							new HashSet<>(Arrays.asList((String[]) properties.get(k))).size());
				});
			});
			return new Hashtable<>(properties);
		} finally {
			monitor.unlock();
		}

	}

	private static <T> T[] append(T[] arr, T element) {
		final int N = arr.length;
		arr = Arrays.copyOf(arr, N + 1);
		arr[N] = element;
		return arr;
	}

}
