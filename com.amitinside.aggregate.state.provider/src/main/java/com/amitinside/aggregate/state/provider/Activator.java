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
import static org.osgi.framework.Constants.BUNDLE_ACTIVATOR;

import org.osgi.annotation.bundle.Header;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.util.tracker.ServiceTracker;

@Header(name = BUNDLE_ACTIVATOR, value = "${@class}")
public final class Activator implements BundleActivator {

	private ServiceTracker<Object, Object> aggregateServiceTracker;
	private AggregateStatesTracker customizer;

	@Override
	public void start(final BundleContext bundleContext) throws Exception {
		final String ldapFilter = "(" + PROPERTY + "=*)";
		final Filter filter = bundleContext.createFilter(ldapFilter);
		customizer = new AggregateStatesTracker(bundleContext);
		aggregateServiceTracker = new ServiceTracker<>(bundleContext, filter, customizer);
		aggregateServiceTracker.open();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		if (aggregateServiceTracker != null) {
			customizer.deregisterAggregateServiceRegistration();
			aggregateServiceTracker.close();
		}
	}

}
