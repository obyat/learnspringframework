package com.in28minutes.spring.learn_spring_framework.enterprise.example.web;

import com.in28minutes.spring.learn_spring_framework.enterprise.example.business.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A Spring-managed web-layer bean.
 *
 * <p>{@code @Component} makes this class discoverable during component scanning.
 * Spring creates the object and provides its required dependencies.</p>
 *
 * <p>This class uses constructor injection, which is generally preferred over
 * field injection: the dependency is explicit, required, and can be immutable.</p>
 *
 * <p><b>Pros:</b></p>
 * <ul>
 *   <li>Dependencies are visible in the constructor.</li>
 *   <li>Required dependencies can be {@code final}.</li>
 *   <li>The class is easy to create and unit test without Spring.</li>
 * </ul>
 *
 * <p><b>Cons:</b></p>
 * <ul>
 *   <li>Constructors become longer when a class has many dependencies.</li>
 *   <li>A long parameter list can reveal that the class has too many responsibilities.</li>
 * </ul>
 */
@Component
public class MyWebController {

    /**
     * A required dependency. {@code final} ensures it cannot change after the
     * controller has been created.
     */
    private final BusinessService businessService;

    /**
     * Spring finds a managed {@link BusinessService} bean and passes it here.
     *
     * <p>{@code @Autowired} is optional because this is the class's only
     * constructor. It is included here to make the injection explicit.</p>
     */
    @Autowired
    public MyWebController(BusinessService businessService) {
        this.businessService = businessService;
    }

    public long returnValueFromBusinessService() {
        return businessService.calculateSum();
    }
}


