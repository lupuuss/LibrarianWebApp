package pl.lodz.pas.librarianwebapp;

import java.time.LocalDateTime;

public class LocalDateProvider implements DateProvider {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
