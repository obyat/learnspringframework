package com.in28minutes.spring.learn_spring_framework.enterprise.example.business;

import com.in28minutes.spring.learn_spring_framework.enterprise.example.data.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class BusinessService {

    private DataService dataService;

    /**
     * Setter injection.
     *
     * <p>After Spring creates this {@code BusinessService}, it finds the managed
     * {@link DataService} bean and calls this method to provide the dependency.</p>
     *
     * <p><b>Pros:</b></p>
     * <ul>
     *   <li>Useful for optional dependencies or dependencies that may change.</li>
     *   <li>Easy to replace the dependency in a unit test.</li>
     * </ul>
     *
     * <p><b>Cons:</b></p>
     * <ul>
     *   <li>The object can exist temporarily without its dependency.</li>
     *   <li>The dependency cannot be {@code final}, so the class is mutable.</li>
     *   <li>A manually created object can fail with a {@code NullPointerException}
     *       if this setter is not called.</li>
     * </ul>
     *
     * <p>Prefer constructor injection for required dependencies. Setter injection
     * is most appropriate when a dependency is genuinely optional.</p>
     */
    @Autowired
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    public long calculateSum() {
        List<Integer> data = dataService.getData();

        return data.stream()
                .reduce(0, Integer::sum);
    }
}
