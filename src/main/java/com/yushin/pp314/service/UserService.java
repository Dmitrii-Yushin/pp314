package com.yushin.pp314.service;

import com.yushin.pp314.modeles.Role;
import com.yushin.pp314.modeles.User;
import com.yushin.pp314.repository.RoleRepository;
import com.yushin.pp314.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {
    @PersistenceContext
    private EntityManager entityManager;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public User getUserById(long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(new User());
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void addNewUser(User user, Set<Role> roles) {
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Transactional
    public void addNewUser(User user) {
        Set<Role> roles = getRoles(user.getRolesId());
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void updateUser(User user) {
        Set<Role> roles = getRoles(user.getRolesId());
        user.setRoles(roles);
        entityManager.merge(user);
    }

    @Transactional(readOnly = true)
    public Set<Role> getRoles(List<Long> roles) {
        TypedQuery<Role> query = entityManager.createQuery("select r from Role r where r.id in :role", Role.class);
        query.setParameter("role", roles);
        return new HashSet<>(query.getResultList());
    }
}
