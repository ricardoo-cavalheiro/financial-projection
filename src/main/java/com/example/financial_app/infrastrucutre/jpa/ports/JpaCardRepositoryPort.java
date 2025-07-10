package com.example.financial_app.infrastrucutre.jpa.ports;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import com.example.financial_app.infrastrucutre.jpa.entities.JpaCardEntity;

@Repository
public interface JpaCardRepositoryPort extends CrudRepository<JpaCardEntity, Long> {
  public Optional<JpaCardEntity> findByName(String name);

  @NonNull List<JpaCardEntity> findAll();
}
