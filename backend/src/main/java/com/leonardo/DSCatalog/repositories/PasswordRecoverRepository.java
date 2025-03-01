package com.leonardo.DSCatalog.repositories;

import com.leonardo.DSCatalog.entities.PasswordRecover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordRecoverRepository extends JpaRepository<PasswordRecover, Long> {
}
