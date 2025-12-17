package org.example.utils.Events;

public class EntityChangeEvent<E> implements Event {
    private final EntityChangeEventType type;
    private final E data;
    private E oldData;

    public EntityChangeEvent(EntityChangeEventType type, E data) {
        this.type = type;
        this.data = data;
    }

    public EntityChangeEventType getType() {
        return type;
    }

    public E getData() {
        return data;
    }
}
