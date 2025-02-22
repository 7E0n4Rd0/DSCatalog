package com.leonardo.DSCatalog.services;

import com.leonardo.DSCatalog.DTO.UserDTO;
import com.leonardo.DSCatalog.DTO.UserInsertDTO;
import com.leonardo.DSCatalog.DTO.UserUpdateDTO;
import com.leonardo.DSCatalog.entities.Role;
import com.leonardo.DSCatalog.entities.User;
import com.leonardo.DSCatalog.projections.UserDetailsProjection;
import com.leonardo.DSCatalog.projections.UserProjection;
import com.leonardo.DSCatalog.repositories.RoleRepository;
import com.leonardo.DSCatalog.repositories.UserRepository;
import com.leonardo.DSCatalog.services.exceptions.DatabaseException;
import com.leonardo.DSCatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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


    @Transactional(readOnly = true)
    public Page<UserDTO> findAllWithRolesJPQL(Pageable pageable) {
        Page<User> listUser = repository.findAllUserWithRolesPagedJPQL(pageable);
        Page<UserDTO> page = new PageImpl<UserDTO>(listUser.stream().map(UserDTO::new).toList(), pageable, listUser.getSize());
        return page;
    }

    /*@Transactional(readOnly = true)
    public Page<UserDTO> findAllWithRolesSQL(Integer page, Integer size, String sort, String direction, Pageable pageable) {
        Page<UserDTO> pageDto = listUserProjectionToPageUser(page, size, sort, direction, pageable);
        return pageDto;
    }*/

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        Optional<User> obj = repository.findById(id);
        User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        User entity = new User();
        copyDtoToEntity(dto, entity);
        entity.setPassword(dto.getPassword());
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

    private void copyProjectionToEntity(UserProjection projection, User entity) {
        entity.setId(projection.getId());
        entity.setFirstName(projection.getFirstName());
        entity.setLastName(projection.getLastName());
        entity.setEmail(projection.getEmail());

        entity.addRoles(new Role(projection.getRoleId(), projection.getAuthority()));
    }


    /*private Page<UserDTO> listUserProjectionToPageUser(Integer page, Integer size, String sort, String direction, Pageable pageable) {
        List<UserProjection> result = repository.findAllUsersWithRolesPagedSQL(pageable);
        Set<User> users = new HashSet<>();
        User entity = null;
        for (UserProjection projection : result) {
            entity = new User();
            copyProjectionToEntity(projection, entity);
            if (!users.isEmpty()) {
                if (users.contains(entity)) {
                    int index = users.stream().toList().indexOf(entity);
                    users.stream().toList().get(index)
                            .addRoles(new Role(projection.getRoleId(), projection.getAuthority()));
                }
            }
            users.add(entity);
        }
        System.out.println("Sort: "+ Sort.by(sort));
        List<UserDTO> list = users.stream().sorted(getComparatorByName(Sort.by(sort))).map(UserDTO::new).toList();
        //int start = Math.min((int) pageable.getOffset(), list.size());
        //int end = Math.min((start + pageable.getPageSize()), list.size());
        System.out.println(pageable.getPageNumber() +" "+ pageable.getPageSize() + " " + pageable.getSort().toString());
        configurePage(sort, direction, list);
        Page<UserDTO> userDTOPage = new PageImpl<UserDTO>(list);
        return userDTOPage;
    }

    private void configurePage(String sort, String direction, List<UserDTO> list) {
        MutableSortDefinition mutableSortDefinition = new MutableSortDefinition();
        mutableSortDefinition.setProperty(sort);
        PagedListHolder<UserDTO> pagedListHolder = new PagedListHolder<>(list);
        pagedListHolder.setSort(mutableSortDefinition);
        mutableSortDefinition.setAscending(false);
    }

    private Comparator<User> getComparatorByName(Sort sort) {
        Comparator<User> firstName = Comparator.comparing(User::getFirstName);
        Comparator<User> lastName = Comparator.comparing(User::getLastName);
        Comparator<User> email = Comparator.comparing(User::getEmail);
        Comparator<User> id = Comparator.comparing(User::getId);

        List<Comparator<User>> comparatorList = new ArrayList<>(
                List.of(firstName, lastName, email, id));
        for (Comparator<User> comparator : comparatorList) {
            if (comparator.toString().equals(sort.toString())) {
                return comparator;
            }
        }
        return firstName;
    }*/
}
