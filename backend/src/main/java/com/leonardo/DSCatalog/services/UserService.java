package com.leonardo.DSCatalog.services;

import com.leonardo.DSCatalog.DTO.UserDTO;
import com.leonardo.DSCatalog.DTO.UserInsertDTO;
import com.leonardo.DSCatalog.DTO.UserUpdateDTO;
import com.leonardo.DSCatalog.config.AuthorizationServerConfig;
import com.leonardo.DSCatalog.entities.Role;
import com.leonardo.DSCatalog.entities.User;
import com.leonardo.DSCatalog.projections.UserDetailsProjection;
import com.leonardo.DSCatalog.projections.UserProjection;
import com.leonardo.DSCatalog.repositories.RoleRepository;
import com.leonardo.DSCatalog.repositories.UserRepository;
import com.leonardo.DSCatalog.services.exceptions.DatabaseException;
import com.leonardo.DSCatalog.services.exceptions.ResourceNotFoundException;
import com.leonardo.DSCatalog.util.Utils;
import jakarta.persistence.EntityNotFoundException;
import jdk.jshell.execution.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(String roleId, Pageable pageable) {
        List<Long> roleIds = ("0".equals(roleId)) ? Arrays.asList() :
                Arrays.stream(roleId.split(",")).map(Long::parseLong).toList();
        Page<UserProjection> page = repository.searchUsers(roleIds, pageable);
        List<Long> userIds = page.map(UserProjection::getId).toList();
        List<User> entities = repository.searchUserWithRoles(userIds);
        entities = (List<User>) Utils.replace(page.getContent(), entities);
        List<UserDTO> dtos = entities.stream().map(UserDTO::new).toList();
        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        Optional<User> obj = repository.findById(id);
        User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        return new UserDTO(entity);
    }

    @Transactional(readOnly = true)
    public UserDTO findMe(){
        Optional<User> entity = authService.authenticated();
        return new UserDTO(entity.get());
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        User entity = new User();
        copyDtoToEntity(dto, entity);
        entity.getRoles().clear();
        entity.getRoles().add(roleRepository.findByAuthority("ROLE_OPERATOR"));
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity = repository.save(entity);
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto) {
        try {
            User entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new UserDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Resource not found");
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found");
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity Violation");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);

        if (result.isEmpty()){
            throw new UsernameNotFoundException("User not found");
        }

        User user = new User();

        user.setEmail(username);
        user.setPassword(result.get(0).getPassword());
        for (UserDetailsProjection projection : result){
            user.addRoles(new Role(projection.getRoleId(), projection.getAuthority()));
        }
        return user;
    }

    private void copyDtoToEntity(UserDTO dto, User entity) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.getRoles().clear();

        entity.getRoles()
                .addAll(dto.getRoles().stream()
                        .map(x -> new Role(
                                roleRepository.findById(x.getId()).get().getId(),
                                roleRepository.findById(x.getId()).get().getAuthority()))
                        .collect(Collectors.toSet()));
    }
}
