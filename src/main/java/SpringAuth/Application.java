package SpringAuth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.google.api.client.auth.oauth2.Credential;
import java.util.concurrent.*;

class Storage {
	public static ConcurrentHashMap<String, Credential> data = new ConcurrentHashMap<String, Credential>();
}

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
