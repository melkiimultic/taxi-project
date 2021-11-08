package com.example.Driversservice.repo;

import com.example.Driversservice.domain.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriversRepo extends JpaRepository<Driver,Long> {

    boolean existsByUsername(String username);
}
