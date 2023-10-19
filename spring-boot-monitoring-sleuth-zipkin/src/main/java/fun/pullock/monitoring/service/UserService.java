package fun.pullock.monitoring.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    public String query(Long id) {
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return "user: " + id;
    }
}
