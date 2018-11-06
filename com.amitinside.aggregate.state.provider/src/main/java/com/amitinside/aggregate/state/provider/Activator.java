package com.amitinside.aggregate.state.provider;

import static com.amitinside.aggregate.state.api.AggregateState.PROPERTY;
import static org.osgi.framework.Constants.OBJECTCLASS;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public final class Activator implements BundleActivator {

	private ServiceTracker<Object, Object> tracker;

	@Override
	public void start(BundleContext context) throws Exception {
		final AggregateStatesTracker trackerCustomizer = new AggregateStatesTracker(context);
		final String filter = "&(" + OBJECTCLASS + "=*)(" + PROPERTY + "=*)";
		tracker = new ServiceTracker<>(context, context.createFilter(filter), trackerCustomizer);
		tracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (tracker != null) {
			tracker.close();
		}
	}

}
