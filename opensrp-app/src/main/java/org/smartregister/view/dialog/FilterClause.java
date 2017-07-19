package org.smartregister.view.dialog;

public interface FilterClause<T> {
    public boolean filter(T object);
}
