package com.example.demo.filter;

import com.example.demo.service.Ipv4Service;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class IpFilter extends OncePerRequestFilter {

    private final Ipv4Service ipv4Service;
    private final String message = """
            {"error":"Access denied for IP %s (%s, %s)"}
            """;

    public IpFilter(Ipv4Service ipv4Service) {
        this.ipv4Service = ipv4Service;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();
        var ipInformation = ipv4Service.getInformationByIp(clientIp);

        if (ipv4Service.isIpBlocked(ipInformation)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(message.formatted(
                    ipInformation.query(),
                    ipInformation.country(),
                    ipInformation.isp()
            ));
            return;
        }

        filterChain.doFilter(request, response);
    }
}