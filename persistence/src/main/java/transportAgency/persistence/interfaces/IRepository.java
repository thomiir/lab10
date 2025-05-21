package transportAgency.persistence.interfaces;


import transportAgency.model.Entity;

public interface IRepository<ID, E extends Entity<ID>> {
    E findOne(ID id);

    Iterable<E> findAll();

    E save(E entity);

    void delete(ID id);

    E update(ID id, E entity);
}
