package com.epitech.epitrello.Security.Logout;
import org.springframework.data.repository.CrudRepository;

public interface LogoutRepository extends CrudRepository<Logout, Integer> {
    boolean existsByToken(String token);
}