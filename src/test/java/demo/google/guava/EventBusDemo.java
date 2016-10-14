package demo.google.guava;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class EventBusDemo {

    public static void main(String[] args) {
        final EventBus eventBus = new EventBus();
        eventBus.register(new Object() {
            @Subscribe
            public void listen(Integer integer) {
                System.out.printf("%s from Integer \n", integer);
            }

            @Subscribe
            public void listen(Number integer) {
                System.out.printf("%s from Number \n", integer);
            }

            @Subscribe
            public void listen(Long lng) {
                System.out.printf("%s from Long \n", lng);
            }
        });
        eventBus.post(1);
        eventBus.post(1L);
    }
}
