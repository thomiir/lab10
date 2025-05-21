package transportAgency.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class Entity<E> {
    @Id
    @GeneratedValue(generator = "increment")
    private E id;

    public E getId() {
        return id;
    }

    public void setId(E id) {
        this.id = id;
    }
}
