package com.leonardo.DSCatalog.services;

import com.leonardo.DSCatalog.DTO.RoleDTO;
import com.leonardo.DSCatalog.DTO.UserDTO;
import com.leonardo.DSCatalog.DTO.UserInsertDTO;
import com.leonardo.DSCatalog.entities.Role;
import com.leonardo.DSCatalog.entities.User;
import com.leonardo.DSCatalog.projections.UserProjection;
import com.leonardo.DSCatalog.repositories.RoleRepository;
import com.leonardo.DSCatalog.repositories.UserRepository;
import com.leonardo.DSCatalog.services.exceptions.DatabaseException;
import com.leonardo.DSCatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllWithRoles(Pageable pageable){
        List<UserProjection> list = repository.findAllUserWithRoles();
        Set<User> users = new HashSet<>();
        User entity = null;
        for (UserProjection projection : list){
            entity = new User();
            copyProjectionToEntity(projection, entity);
            if(!users.isEmpty()){
                if(users.contains(entity)) {
                    int index = users.stream().toList().indexOf(entity);
                    users.stream().toList().get(index)
                            .addRoles(new Role(projection.getRoleId(), projection.getAuthority()));
                }
            }
            users.add(entity);
        }
        Page<UserDTO> page = new PageImpl<UserDTO>(users.stream().map(UserDTO::new).toList(), pageable,
                users.stream().map(UserDTO::new).toList().size());
        return page;
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id){
        Optional<User> obj = repository.findById(id);
        User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto){
        User entity = new User();
        copyDtoToEntity(dto, entity);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity = repository.save(entity);
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto){
        try{
            User entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new UserDTO(entity);
        }catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Resource not found");
        }
    }

    @Transactional
    public void delete(Long id){
        if(!repository.existsById(id)){
            throw new ResourceNotFoundException("Resource not found");
        }
        try{
            repository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new DatabaseException("Integrity Violation");
        }
    }

    private void copyDtoToEntity(UserDTO dto, User entity){
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

    private void copyProjectionToEntity(UserProjection projection, User entity){
        entity.setId(projection.getId());
        entity.setFirstName(projection.getFirstName());
        entity.setLastName(projection.getLastName());
        entity.setEmail(projection.getEmail());

        entity.addRoles(new Role(projection.getRoleId(), projection.getAuthority()));
    }

}
