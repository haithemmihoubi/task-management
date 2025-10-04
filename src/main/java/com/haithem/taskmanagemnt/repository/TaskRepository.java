package com.haithem.taskmanagemnt.repository;

import com.haithem.taskmanagemnt.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Task entity
 */
@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
}

