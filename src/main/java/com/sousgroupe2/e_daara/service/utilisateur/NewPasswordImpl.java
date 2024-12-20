package com.sousgroupe2.e_daara.service.utilisateur;

import com.sousgroupe2.e_daara.DTO.UtilisateurDTO;
import com.sousgroupe2.e_daara.entities.utilisateur.NewPassword;
import com.sousgroupe2.e_daara.entities.utilisateur.Utilisateur;
import com.sousgroupe2.e_daara.mapper.UtilisateurMapper;
import com.sousgroupe2.e_daara.repository.utilisateur.NewPasswordRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class NewPasswordImpl implements INewPassword {

    @Autowired
    NewPasswordRepository newPasswordRepository;

    @Autowired
    UtilisateurServiceImpl utilisateurService;

    @Autowired
    UtilisateurMapper utilisateurMapper;

    JavaMailSenderImpl mailSender;

    @Override
    public String getNewPassword(String email) {
        Utilisateur user = this.utilisateurService.loadUserByEmail(email);
        String token = null;
        if (user.getEmail().equals(email)) {
            NewPassword newPassword = new NewPassword();
            token = UUID.randomUUID().toString();
            newPassword.setToken(token);
            newPassword.setDate(new Date());
            newPassword.setUtilisateur(user);
            newPassword.setValid(true);
            this.newPasswordRepository.save(newPassword);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Edaara Nouveau mot de passe");
            message.setText("<p>" +
                    "Nous avons reçu une demande de récupération de mot de passe pour votre compte. " +
                    "Pour réinitialiser votre mot de passe, veuillez cliquer sur le bouton ci-dessous :" +
                    "<a href=http://localhost:4200/new-password/" + token + ">Réinitialiser mon mot de passe</a> " +
                    "pour changer votre mot de passe </p>");
            try {
                mailSender.send(message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return token;
    }


    @Override
    public boolean reset(String key, String password) {
         if(this.verifKey(key)){
             NewPassword _new =this.newPasswordRepository.findByToken(key);
             Utilisateur user = _new.getUtilisateur();
             user.setPassword(password);
             UtilisateurDTO userDTO = this.utilisateurMapper.toDTO(user);
             _new.setExpirationDate(new Date());
             _new.setValid(false);
             this.utilisateurService.updateUtilisateur(user.getId(), userDTO);
             this.newPasswordRepository.save(_new);
             return true;
         }
         return false;
    }

    @Override
    public boolean verifKey(String key){
        NewPassword _new =this.newPasswordRepository.findByToken(key);
        if(_new == null){
            return false;
        }else if(_new.getExpirationDate().before(new Date()) || !_new.isValid()){
            return false;
        }
        return true;
    }
}
