package pl.lodz.pas.librarianwebapp.repository.books;

import pl.lodz.pas.librarianwebapp.producer.annotations.MagazinesRepositoryInitializer;
import pl.lodz.pas.librarianwebapp.repository.books.data.Book;
import pl.lodz.pas.librarianwebapp.repository.books.data.ElementCopy;
import pl.lodz.pas.librarianwebapp.repository.books.data.Magazine;
import pl.lodz.pas.librarianwebapp.repository.books.data.MagazineCopy;
import pl.lodz.pas.librarianwebapp.repository.exceptions.InconsistencyFoundException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectNotFoundException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.RepositoryException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;


@ApplicationScoped
public class LocalMagazinesRepository implements MagazinesRepository{

    @Inject
    @MagazinesRepositoryInitializer
    private BiConsumer<Set<Magazine>, Set<MagazineCopy>> magazinesInitializer;

    private final Set<Magazine> magazines = new HashSet<>();
    private final Set<MagazineCopy> magazineCopies = new HashSet<>();



    @PostConstruct
    private void initializeMagazines(){
        if(magazinesInitializer != null){
            magazinesInitializer.accept(magazines,magazineCopies);
        }
    }

    @Override
    public synchronized Integer getNextCopyNumberByIssnAndIssue(String issn, int issue) {
        return findMagazineCopiesByIssnAndIssue(issn,issue)
                .stream()
                .mapToInt(copy -> copy.getNumber() + 1)
                .max()
                .orElse(0);
    }

    @Override
    public synchronized List<Magazine> findAllMagazines() {
        return magazines.stream()
                .map(Magazine::copy)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized Optional<Magazine> findMagazineByUuid(UUID uuid) {
        return magazines.stream()
                .filter(magazine -> magazine.getUuid().equals(uuid))
                .findFirst()
                .map(Magazine::copy);
    }

    @Override
    public synchronized Optional<Magazine> findMagazineByIssnAndIssue(String issn, int issue) {
        return magazines.stream()
                .filter(magazine -> magazine.getIssn().equals(issn) && magazine.getIssue() == issue)
                .findFirst()
                .map(Magazine::copy);
    }

    @Override
    public synchronized List<MagazineCopy> findMagazineCopiesByIssnAndIssue(String issn, int issue) {
        return magazineCopies.stream()
                .filter(magazineCopy -> findMagazineByUuid(magazineCopy.getElementUuid()).equals(findMagazineByIssnAndIssue(issn, issue)))
                .map(MagazineCopy::copy)
                .collect(Collectors.toList());
    }



    @Override
    public synchronized Optional<MagazineCopy> findMagazineCopyByIssnAndIssueAndNumber(String issn, int issue, int number) {
        return findMagazineCopiesByIssnAndIssue(issn,issue)
                .stream()
                .filter(copy -> copy.getNumber() == number)
                .findFirst()
                .map(MagazineCopy::copy);
    }

    @Override
    public synchronized List<MagazineCopy> findMagazineCopiesByIssnAndIssueAndNotDamaged(String issn, int issue) {
        return findMagazineCopiesByIssnAndIssue(issn,issue)
                .stream()
                .filter(copy ->copy.getState() != MagazineCopy.State.DAMAGED &&
                               copy.getState() != MagazineCopy.State.NEED_REPLACEMENT)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized void addMagazine(Magazine magazine) throws ObjectAlreadyExistsException {
        if(magazines.contains(magazine)){
            throw new ObjectAlreadyExistsException(Magazine.class.getSimpleName(), magazine.getIssn() +"  " + magazine.getIssue());
        }
        magazines.add(magazine.copy());
    }

    private synchronized void checkMagazineCopyConsistency(MagazineCopy magazineCopy) throws InconsistencyFoundException {
        var magazine = magazines.stream()
                .filter(x -> x.getUuid().equals(magazineCopy.getElementUuid()))
                .findFirst();

        if(magazine.isEmpty()){
            throw new InconsistencyFoundException(
                    "Passed MagazineCopy doesn't match any Magazine! UUID '" + magazineCopy.getElementUuid() + "' not found!"
            );
        }
    }

    @Override
    public synchronized void addMagazineCopy(MagazineCopy magazineCopy) throws RepositoryException {

        var optCopy = magazineCopies
                .stream()
                .filter(copy -> copy.equals(magazineCopy))
                .findAny();

        if(optCopy.isPresent()){
            var copy = optCopy.get();
            throw new ObjectAlreadyExistsException(MagazineCopy.class.getSimpleName(), copy.getUuid().toString());
        }

        checkMagazineCopyConsistency(magazineCopy);

        magazineCopies.add(magazineCopy.copy());

    }


    @Override
    public synchronized void updateMagazine(Magazine magazine) throws ObjectNotFoundException {
        if(!magazines.contains(magazine)){
            throw new ObjectNotFoundException(Magazine.class.getSimpleName(), magazine.getIssn() +"  " + magazine.getIssue());
        }

        magazines.remove(magazine);
        magazines.add(magazine.copy());
    }

    @Override
    public synchronized void updateMagazineCopy(MagazineCopy magazineCopy) throws RepositoryException {
        if(!magazineCopies.contains(magazineCopy)){
            throw new ObjectNotFoundException(MagazineCopy.class.getSimpleName(), magazineCopy.getUuid().toString());
        }

        magazineCopies.remove(magazineCopy);
        magazineCopies.add(magazineCopy.copy());
    }

    @Override
    public synchronized void deleteMagazine(Magazine magazine) throws ObjectNotFoundException {
        if(!magazines.contains(magazine)){
            throw new ObjectNotFoundException(Magazine.class.getSimpleName(), magazine.getIssn() +"  " + magazine.getIssue());
        }

        magazines.remove(magazine);
    }

    @Override
    public synchronized void deleteMagazineCopy(MagazineCopy magazineCopy) throws ObjectNotFoundException {
        if(!magazineCopies.contains(magazineCopy)){
            throw new ObjectNotFoundException(MagazineCopy.class.getSimpleName(), magazineCopy.getUuid().toString());
        }

        magazineCopies.remove(magazineCopy);
    }




    @Override
    public synchronized List<MagazineCopy> findMagazineCopiesByIssnAndIssueAndState(String issn, int issue, MagazineCopy.State state) {
        return findMagazineCopiesByIssnAndIssue(issn,issue)
                .stream()
                .filter(copy -> copy.getState() == state)
                .collect(Collectors.toList());
    }
}
