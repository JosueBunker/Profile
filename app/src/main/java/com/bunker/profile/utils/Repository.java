package com.bunker.profile.utils;

import java.util.List;

public interface Repository<T> {

    void add(T item);

    void update(T item);
    void updateList(List<T> items);

    void delete(T item);

    List<T> getAll();
    List<T> getSpecific(T item);
}
