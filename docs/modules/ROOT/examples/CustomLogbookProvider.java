import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import org.zalando.logbook.Logbook;

@ApplicationScoped
public class CustomLogbookProvider {

    @Produces
    public Logbook customLogbook() {
        return Logbook.builder()
                // your custom configuration
                .build();
    }
}
