package com.amitinside.aggregate.state.listener;

import org.osgi.service.component.annotations.Component;

@Component(service = Example2.class, property = { "aggregate.state=sample2", "sample2=B" })
public class Example2 {

}
