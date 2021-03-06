# OSGi Aggregate State Service

 In OSGi, there is no start ordering. That is, any requirement like (start) ordering must be translated to (service) dependencies. Once something is a service dependency, a (DS) component can defer its activation until the dependency is available. Since this is a proper dependency, an deregistration will automatically deactivate any components that depend on this service. Once something is mapped to a service it leverages the DS runtime to handle the highly complex ordering issues between different components.
 
 The **AggregateState** now actively tracks any service that has the **aggregate.state** service property. It uses the learned information to modify its own service properties.
 
 The **AggregateState** also registers the cardinality that it detected for each state. The cardinality is the number of values that were registered for a state. It will prefix the state id with a hash (**#**) and register the total number of values that it found in other services. The **%** prefix registers the total number of unique values.

For further information, have a look at [http://aqute.biz/2017/04/24/aggregate-state.html](http://aqute.biz/2017/04/24/aggregate-state.html)

-------------------------------------------------------------------------------------------------------------------

### Requirements

1. Java 8+
2. OSGi R6+

--------------------------------------------------------------------------------------------------------------------

### Dependencies

This project comprises four bundles - 

1. `com.amitinside.aggregate.state.api`- The core Aggregate State Service API
2. `com.amitinside.aggregate.state.provider` - The core Aggregate State Service implementation
3. `com.amitinside.aggregate.state.listener` - Example Project with services taking part in Aggregate State Service mechanism
4. `com.amitinside.aggregate.state.consumer` - Example Consumer Project showing how to use Aggregate State Service in clients

--------------------------------------------------------------------------------------------------------------------



