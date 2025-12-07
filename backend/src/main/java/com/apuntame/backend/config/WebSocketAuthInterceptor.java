package com.apuntame.backend.config;

import com.apuntame.backend.constant.JwtConstants;
import com.apuntame.backend.security.JwtUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader(JwtConstants.AUTHORIZATION_HEADER);

            if (token == null || !token.startsWith(JwtConstants.BEARER_PREFIX)) {
                System.err.println("Conexión WebSocket rechazada: No se proporcionó token de autenticación");
                throw new IllegalArgumentException("Token de autenticación requerido");
            }

            token = token.substring(JwtConstants.BEARER_PREFIX.length());

            try {
                String username = jwtUtil.extractUsername(token);

                if (username == null || !jwtUtil.isTokenValid(token, username)) {
                    System.err.println("Conexión WebSocket rechazada: Token inválido o expirado");
                    throw new IllegalArgumentException("Token de autenticación inválido");
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                accessor.setUser(authentication);
                System.out.println("Conexión WebSocket autenticada para usuario: " + username);

            } catch (Exception e) {
                System.err.println("Error validando token WebSocket: " + e.getMessage());
                throw new IllegalArgumentException("Error al validar token de autenticación");
            }
        }

        return message;
    }
}
