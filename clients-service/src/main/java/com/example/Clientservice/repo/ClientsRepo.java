package com.example.Clientservice.repo;

import com.example.Clientservice.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface ClientsRepo extends JpaRepository<Client,Long> {

    boolean existsByUsername(String username);

    Optional<Client> findOneByUsername(String username);
}
