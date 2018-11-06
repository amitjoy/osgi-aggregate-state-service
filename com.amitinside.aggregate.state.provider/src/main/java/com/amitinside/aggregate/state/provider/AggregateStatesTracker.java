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
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.amitinside.aggregate.state.api.AggregateState;

@Component(name = "AggregateStatesTracker", immediate = true)
public final class AggregateStatesTracker {

	private static final String UNIQUE_STATES_KEY_PREFIX = "%";
	private static final String NON_UNIQUE_STATES_KEY_PREFIX = "#";

	private final Lock monitor;
	private final BundleContext bundleContext;
	private final List<AggregateStateInfo> aggregateStateInfos;
	private final AtomicReference<ServiceRegistration<AggregateState>> aggregateStateServiceReg;

	@Activate
	public AggregateStatesTracker(BundleContext bundleContext) {
		requireNonNull(bundleContext, "BundleContext cannot be null");
		this.bundleContext = bundleContext;
		monitor = new ReentrantLock();
		aggregateStateInfos = new CopyOnWriteArrayList<>();
		aggregateStateServiceReg = new AtomicReference<>(null);
	}

	@Reference(target = "(" + PROPERTY + "=*)")
	protected void bindListener(Object service, ServiceReference<Object> reference) {
		addAggregateStateInfo(reference);
		registerOrUpdateAggregateStateService();
	}

	protected void unbindListener(Object service, ServiceReference<Object> reference) {
		removeAggregateStateInfo(reference);
		registerOrUpdateAggregateStateService();
	}

	protected void updatedListener(Object service, ServiceReference<Object> reference) {
		removeAggregateStateInfo(reference);
		addAggregateStateInfo(reference);
		registerOrUpdateAggregateStateService();
	}

	@Deactivate
	protected void deactivate() {
		final ServiceRegistration<AggregateState> serviceRegistration = aggregateStateServiceReg.get();
		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	private class AggregateStateProvider implements AggregateState {
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

		states.forEach(s -> {
			final Object prop = reference.getProperty(s);
			if (prop == null) {
				throw new RuntimeException(String.format(
						"Aggregate State cannot be processed since the specified state cannot be mapped to an existing property - %s",
						s));
			}
			properties.put(s, String.valueOf(prop));
		});

		final AggregateStateInfo info = new AggregateStateInfo();
		info.stateProperties = properties;
		info.serviceReference = reference;

		return info;
	}

	private void registerOrUpdateAggregateStateService() {
		if (!aggregateStateServiceReg.compareAndSet(null, bundleContext.registerService(AggregateState.class,
				new AggregateStateProvider(), calculateProperties()))) {
			final ServiceRegistration<AggregateState> registration = aggregateStateServiceReg.get();
			registration.setProperties(calculateProperties());
		}
	}

	private Dictionary<String, ?> calculateProperties() {
		monitor.lock();
		try {
			final Map<String, Object> properties = new HashMap<>();
			aggregateStateInfos.stream().forEach(t -> {
				final Map<String, String> map = t.stateProperties;
				map.forEach((k, v) -> {
					if (properties.containsKey(k)) {
						append((String[]) properties.get(k), v);
					} else {
						properties.put(k, new String[] { v });
					}
					final String[] value = (String[]) properties.get(k);

					final String totalNoOfStatesPropertyKey = NON_UNIQUE_STATES_KEY_PREFIX + k;
					final String totalNoOfUniqueStatesPropertyKey = UNIQUE_STATES_KEY_PREFIX + k;

					properties.put(totalNoOfStatesPropertyKey, value.length);
					properties.put(totalNoOfUniqueStatesPropertyKey, Arrays.stream(value).collect(toSet()).size());
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
