package com.in28minutes.spring.learn_spring_framework.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A Spring-managed bean that uses a {@link Game}.
 *
 * <p>This class demonstrates field injection. Spring creates this bean, finds the
 * registered {@code Game} implementation ({@link MarioGame}), and assigns it to the
 * annotated field after construction.</p>
 */
@Component
public class GameRunner {

    /**
     * Field injection.
     *
     * <p><b>Pros:</b></p>
     * <ul>
     *   <li>No constructor or setter is needed.</li>
     *   <li>Less boilerplate for small examples.</li>
     * </ul>
     *
     * <p><b>Cons:</b></p>
     * <ul>
     *   <li>Dependencies are hidden rather than declared in the constructor.</li>
     *   <li>Harder to unit test without Spring or reflection.</li>
     *   <li>The field may be {@code null} if the class is created outside Spring.</li>
     *   <li>Cannot be used with {@code final} fields or an immutable design.</li>
     *   <li>Makes it easier for a class to accumulate too many dependencies.</li>
     * </ul>
     *
     * <p>Prefer constructor injection: required dependencies are explicit and
     * available as soon as the object is created.</p>
     */
    @Autowired
    private Game game;

    /**
     * Uses the injected dependency through its interface.
     */
    public void run() {
        game.up();
        game.down();
        game.left();
        game.right();
    }
}
