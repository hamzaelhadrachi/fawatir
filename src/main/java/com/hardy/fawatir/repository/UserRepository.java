package com.hardy.fawatir.repository;

import com.hardy.fawatir.model.User;

import java.util.Collection;

public interface UserRepository <T extends User> {
    /* Basic CURD Operations */
    T create(T data);
    Collection<T> list(int page, int pageSize);
    T get(int id);
    T update(int id, T data);
    Boolean delete(int id);

    /* More Complex Operations */
}
