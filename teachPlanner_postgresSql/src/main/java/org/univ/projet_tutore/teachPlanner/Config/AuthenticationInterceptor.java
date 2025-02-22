package org.univ.projet_tutore.teachPlanner.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();

        // Liste des URLs publiques (login, ressources statiques, etc.)
        String[] publicUrls = {
                "/login",
                "/css/",
                "/js/",
                "/images/",
                "/favicon.ico"
        };

        // Vérifier si l'URL est publique
        String requestUri = request.getRequestURI();
        for (String publicUrl : publicUrls) {
            if (requestUri.startsWith(publicUrl)) {
                return true;
            }
        }

        // Vérifier si l'utilisateur est connecté
        if (session.getAttribute("userLoggedIn") == null) {
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}
