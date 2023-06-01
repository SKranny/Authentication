package AuthService.config;

import AuthService.security.service.JwtService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeignRequestInterceptor implements RequestInterceptor {
    private final JwtService jwtService;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        final String authorization = HttpHeaders.AUTHORIZATION;
        requestTemplate.header(authorization,"Bearer " + jwtService.generateDefaultToken());
    }
}
