package pl.lodz.pas.librarianwebapp.producer.annotations;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;


@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, TYPE, METHOD})
public @interface UsersRepositoryInitializer {
}
