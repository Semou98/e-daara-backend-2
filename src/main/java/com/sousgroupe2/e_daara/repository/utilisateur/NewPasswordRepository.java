package com.sousgroupe2.e_daara.repository.utilisateur;

import com.sousgroupe2.e_daara.entities.utilisateur.NewPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewPasswordRepository extends JpaRepository<NewPassword, Long> {
    NewPassword findByToken(String key);
}
