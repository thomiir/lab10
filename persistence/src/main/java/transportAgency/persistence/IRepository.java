package transportAgency.persistence;


import transportAgency.model.Entity;

public interface IRepository<ID, E extends Entity<ID>> {
    E findOne(ID id);

    Iterable<E> findAll();

    void save(E entity);

    void delete(ID id);

    void update(ID id, E entity);
}
