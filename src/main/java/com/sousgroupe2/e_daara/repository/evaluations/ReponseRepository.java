package com.sousgroupe2.e_daara.repository.evaluations;

import com.sousgroupe2.e_daara.entities.evaluations.Reponse;
import org.apache.catalina.connector.Response;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReponseRepository extends JpaRepository<Reponse, Long> {
}
