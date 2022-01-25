package com.srb2kart.server.servermanager.repositories;

import com.srb2kart.server.servermanager.models.ConfigEntry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigEntryRepository extends JpaRepository<ConfigEntry, Long>{
    
}
