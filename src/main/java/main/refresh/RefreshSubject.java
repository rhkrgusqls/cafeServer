package main.refresh;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class RefreshSubject {
    private Set<RefreshObserver> observers = new CopyOnWriteArraySet<>();

    public void registerObserver(RefreshObserver observer) {
        observers.add(observer);
    }

    public void unregisterObserver(RefreshObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(RefreshEvent event) {
        for (RefreshObserver o : observers) {
            o.onRefresh(event);
        }
    }
}
