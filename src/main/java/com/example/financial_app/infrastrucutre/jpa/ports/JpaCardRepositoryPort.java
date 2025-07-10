package com.example.financial_app.infrastrucutre.jpa.ports;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.financial_app.infrastrucutre.jpa.entities.JpaCardEntity;

@Repository
public interface JpaCardRepositoryPort extends JpaRepository<JpaCardEntity, Long> {
  public Optional<JpaCardEntity> findByName(String name);
}
