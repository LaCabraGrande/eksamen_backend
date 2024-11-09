package dat.daos;

import java.util.List;


public interface IDAO<T> {
    //List<T> getAll();
    //T getById(int i);
    T update(int i, T t);
    T delete(int i);
}

