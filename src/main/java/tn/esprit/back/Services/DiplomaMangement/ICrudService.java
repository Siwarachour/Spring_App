package tn.esprit.back.Services.DiplomaMangement;

import java.util.List;

public interface ICrudService<T,S> {
    T add (T data);
    T update (T data);
    void delete (S id);
    List<T> getAll ();
    T get  (S id) ;
}

