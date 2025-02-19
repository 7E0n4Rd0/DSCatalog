package com.leonardo.DSCatalog.repositories;

import com.leonardo.DSCatalog.entities.User;
import com.leonardo.DSCatalog.projections.UserProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value =
        "SELECT tb_user.id, tb_user.email, tb_user.first_name, tb_user.last_name, " +
            "tb_role.id AS roleId, tb_role.authority FROM TB_USER " +
                "INNER JOIN TB_USER_ROLE ON tb_user_role.user_id = tb_user.id " +
                "INNER JOIN TB_ROLE ON tb_user_role.role_id = tb_role.id ")
    List<UserProjection> findAllUserWithRoles();

}
