package pl.lodz.pas.librarianwebapp;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class LocalDateProvider implements DateProvider {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
