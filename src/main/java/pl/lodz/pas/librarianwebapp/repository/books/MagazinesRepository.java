package pl.lodz.pas.librarianwebapp.repository.books;

import pl.lodz.pas.librarianwebapp.repository.books.data.Magazine;
import pl.lodz.pas.librarianwebapp.repository.books.data.MagazineCopy;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectNotFoundException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.RepositoryException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MagazinesRepository {

    List<Magazine> findAllMagazines();

    Integer getNextCopyNumberByIssnAndIssue(String issn, int issue);

    Optional<Magazine> findMagazineByUuid(UUID uuid);

    Optional<Magazine> findMagazineByIssnAndIssue(String issn, int issue);

    List<MagazineCopy> findMagazineCopiesByIssnAndIssue(String issn, int issue);

    Optional<MagazineCopy> findMagazineCopyByIssnAndIssueAndNumber(String issn, int issue , int number);

    List<MagazineCopy> findMagazineCopiesByIssnAndIssueAndNotDamaged(String issn, int issue);

    List<MagazineCopy> findMagazineCopiesByIssnAndIssueAndState(String issn, int issue, MagazineCopy.State state);

    void addMagazine(Magazine magazine) throws ObjectAlreadyExistsException;

    void addMagazineCopy(MagazineCopy magazineCopy) throws RepositoryException;

    void updateMagazine(Magazine magazine) throws ObjectNotFoundException;

    void updateMagazineCopy(MagazineCopy magazineCopy) throws RepositoryException;

    void deleteMagazine(Magazine magazine) throws ObjectNotFoundException;

    void deleteMagazineCopy(MagazineCopy magazineCopy) throws ObjectNotFoundException;

    Optional<MagazineCopy> findMagazineCopyByUuid(UUID uuid);

    List<MagazineCopy> findMagazineCopiesByIssnContains(String query);
}
