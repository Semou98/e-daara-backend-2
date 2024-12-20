package com.sousgroupe2.e_daara.service.utilisateur;

import com.sousgroupe2.e_daara.entities.utilisateur.NewPassword;

public interface INewPassword {
    String getNewPassword(String email);

    boolean reset(String key, String password);

    boolean verifKey(String key);
}
