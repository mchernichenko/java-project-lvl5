package hexlet.code.service;

import java.util.Map;

public interface TokenService {
    String expiring(Map<String, Object> attributes);

    Map<String, Object> verify(String token);
}
