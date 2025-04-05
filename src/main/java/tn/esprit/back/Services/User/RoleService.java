package tn.esprit.back.Services.User;

import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RoleService {
    public List<String> getAllRoles() {
        // Retourner les rôles disponibles (vous pouvez les récupérer d'une base de données si nécessaire)
        return List.of("ROLE_ADMIN", "ROLE_CCLIENT", "ROLE_HR","ROLE_PROVIDER");  // Exemple statique
    }
}
