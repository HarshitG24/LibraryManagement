package com.example.distributedsystems.distributed.systems.repository;

import com.example.distributedsystems.distributed.systems.model.Paxos;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface (Repository) for managing Paxos entities in the system.
 */
@Repository
public interface PxRepository extends CrudRepository<Paxos, Long> {
    /**
     * Retrieves a Paxos instance by its ID.
     *
     * @param id The ID of the Paxos instance.
     * @return The Paxos instance with the specified ID.
     */
    Paxos getById(int id);
}
