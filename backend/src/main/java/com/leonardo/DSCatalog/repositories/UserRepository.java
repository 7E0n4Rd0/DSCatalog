package com.leonardo.DSCatalog.repositories;

import com.leonardo.DSCatalog.entities.User;
import com.leonardo.DSCatalog.projections.UserDetailsProjection;
import com.leonardo.DSCatalog.projections.UserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM(
				SELECT DISTINCT tb_user.id AS id, tb_user.email AS email, tb_user.first_name AS firstName,
				tb_user.last_name AS lastName FROM TB_USER
					INNER JOIN TB_USER_ROLE ON tb_user_role.user_id = tb_user.id
					INNER JOIN TB_ROLE ON tb_user_role.role_id = tb_role.id
					WHERE :roleIds IS NULL OR tb_user_role.role_id IN :roleIds
			) AS tb_result
			""" ,
			countQuery =
			"""
			SELECT COUNT(*) FROM(
				SELECT DISTINCT tb_user.id AS id, tb_user.email AS email, tb_user.first_name AS firstName,
				tb_user.last_name AS lastName FROM TB_USER
					INNER JOIN TB_USER_ROLE ON tb_user_role.user_id = tb_user.id
					INNER JOIN TB_ROLE ON tb_user_role.role_id = tb_role.id
					WHERE :roleIds IS NULL OR tb_user_role.role_id IN :roleIds
			) AS tb_result
			""")
    Page<UserProjection> searchUsers(List<Long> roleIds, Pageable pageable);

    @Query(value = """ 
			SELECT obj FROM User obj JOIN FETCH obj.roles 
				WHERE obj.id IN :userIds""")
    List<User> searchUserWithRoles(List<Long> userIds);

    @Query(nativeQuery = true, value = """
			SELECT tb_user.email AS username, tb_user.password, tb_role.id AS roleId, tb_role.authority
			FROM tb_user
			INNER JOIN tb_user_role ON tb_user.id = tb_user_role.user_id
			INNER JOIN tb_role ON tb_role.id = tb_user_role.role_id
			WHERE tb_user.email = :email
		""")
    List<UserDetailsProjection> searchUserAndRolesByEmail(String email);

    Optional<User> findByEmail(String email);

}
