package com.soaprestadapter.Repository;

import com.soaprestadapter.entity.GeneratedWsdlClassEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link GeneratedWsdlClassEntity} entities.
 */
@Repository
@Profile("!sqlite")
public interface GeneratedWsdlClassRepository extends JpaRepository<GeneratedWsdlClassEntity, Long> {
}
