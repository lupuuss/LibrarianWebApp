package pl.lodz.pas.librarianwebapp.services;

import pl.lodz.pas.librarianwebapp.repository.books.BooksRepository;
import pl.lodz.pas.librarianwebapp.repository.books.MagazinesRepository;
import pl.lodz.pas.librarianwebapp.repository.books.data.Book;
import pl.lodz.pas.librarianwebapp.repository.books.data.BookCopy;
import pl.lodz.pas.librarianwebapp.repository.books.data.Magazine;
import pl.lodz.pas.librarianwebapp.repository.books.data.MagazineCopy;
import pl.lodz.pas.librarianwebapp.repository.events.EventsRepository;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.ObjectNotFoundException;
import pl.lodz.pas.librarianwebapp.repository.exceptions.RepositoryException;
import pl.lodz.pas.librarianwebapp.services.dto.BookDto;
import pl.lodz.pas.librarianwebapp.services.dto.ElementCopyDto;
import pl.lodz.pas.librarianwebapp.services.dto.ElementDto;
import pl.lodz.pas.librarianwebapp.services.dto.MagazineDto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@RequestScoped
public class ElementsService {

    @Inject
    private BooksRepository booksRepository;

    @Inject
    private MagazinesRepository magazinesRepository;

    @Inject
    private EventsRepository eventsRepository;

    public List<ElementCopyDto> getAllCopies() {

        var books = booksRepository.findAllBooks();
        var magazines = magazinesRepository.findAllMagazines();

        var copies = new ArrayList<ElementCopyDto>();

        for (var book : books) {

            var bookDto = new BookDto(
                    book.getTitle(),
                    book.getPublisher(),
                    book.getIsbn(),
                    book.getAuthor()
            );

            var copiesForBook = booksRepository.findBookCopiesByIsbn(book.getIsbn())
                    .stream()
                    .map(copy -> new ElementCopyDto(copy.getNumber(), bookDto, StateUtils.mapState(copy.getState())))
                    .collect(Collectors.toList());

            copies.addAll(copiesForBook);
        }

        for (var magazine : magazines) {
            var magazineDto = new MagazineDto(
                    magazine.getTitle(),
                    magazine.getPublisher(),
                    magazine.getIssn(),
                    magazine.getIssue()
            );

            var copiesForMagazine =
                    magazinesRepository.findMagazineCopiesByIssnAndIssue(magazine.getIssn(), magazine.getIssue())
                            .stream()
                    .map(copy -> new ElementCopyDto(copy.getNumber(), magazineDto, StateUtils.mapState(copy.getState())))
                    .collect(Collectors.toList());

            copies.addAll(copiesForMagazine);
        }

        return copies;
    }

    public boolean addBookCopy(String isbn, ElementCopyDto.State state) {
        try {

            var number = booksRepository.getNextCopyNumberByIsbn(isbn);

            var book = booksRepository.findBookByIsbn(isbn);

            if (book.isEmpty()) {
                return false;
            }

            booksRepository.addBookCopy(new BookCopy(
                    book.get().getUuid(),
                    number,
                    StateUtils.mapState(state)
            ));

            return true;
        } catch (RepositoryException e) {

            e.printStackTrace();
            return false;
        }
    }

    public boolean addMagazineCopy(String issn, int issue, ElementCopyDto.State state) {
        try {

            var number = magazinesRepository.getNextCopyNumberByIssnAndIssue(issn,issue);

            var magazine = magazinesRepository.findMagazineByIssnAndIssue(issn,issue);

            if (magazine.isEmpty()) {
                return false;
            }

            magazinesRepository.addMagazineCopy(new MagazineCopy(
                    magazine.get().getUuid(),
                    number,
                    StateUtils.mapState(state)
            ));

            return true;
        } catch (RepositoryException e) {

            e.printStackTrace();
            return false;
        }

    }

    public List<String> getAllIsbns() {
        return booksRepository.findAllBooks()
                .stream()
                .map(Book::getIsbn)
                .collect(Collectors.toList());
    }

    public List<MagazineDto> getAllMagazines() {
        return magazinesRepository.findAllMagazines()
                .stream()
                .map(magazine -> new MagazineDto(
                        magazine.getTitle(),
                        magazine.getPublisher(),
                        magazine.getIssn(),
                        magazine.getIssue()
                        ))
                .collect(Collectors.toList());
    }

    private void degradeBookCopy(BookDto bookDto, ElementCopyDto copy) {

        var toUpdate = booksRepository.findBookCopyByIsbnAndNumber(
                bookDto.getIsbn(),
                copy.getNumber()
        );

        if (toUpdate.isEmpty()) {
            return;
        }

        var bookCopy = toUpdate.get();

        var currentState = bookCopy.getState();
        bookCopy.setState(BookCopy.State.degrade(currentState));

        try {
            booksRepository.updateBookCopy(bookCopy);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    private void degradeMagazineCopy(MagazineDto magazineDto, ElementCopyDto copy) {
        var toUpdate = magazinesRepository.findMagazineCopyByIssnAndIssueAndNumber(
                magazineDto.getIssn(),
                magazineDto.getIssue(),
                copy.getNumber()
        );

        if (toUpdate.isEmpty()) {
            return;
        }

        var magazineCopy = toUpdate.get();

        var currentState = magazineCopy.getState();
        magazineCopy.setState(MagazineCopy.State.degrade(currentState));

        try {
            magazinesRepository.updateMagazineCopy(magazineCopy);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    public void degradeCopies(List<ElementCopyDto> copies) {

        for (var copy : copies) {

            if (copy.getElement() instanceof BookDto) {

                var book = (BookDto) copy.getElement();

                degradeBookCopy(book, copy);

            } else if(copy.getElement() instanceof MagazineDto) {

                var magazine = (MagazineDto) copy.getElement();

                degradeMagazineCopy(magazine, copy);

            } else {
                throw new IllegalStateException("Unsupported element type!");
            }
        }

    }

    private void deleteBookCopy(ElementCopyDto copy) {
        var book = (BookDto) copy.getElement();
        var toRemove = booksRepository.findBookCopyByIsbnAndNumber(
                book.getIsbn(),
                copy.getNumber()
        );

        if (toRemove.isEmpty()) {
            return;
        }

        try {
            booksRepository.deleteBookCopy(toRemove.get());
            eventsRepository.clearDanglingReferencesFor(toRemove.get().getUuid());
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    private void deleteMagazineCopy(ElementCopyDto copy) {
        var magazine = (MagazineDto) copy.getElement();
        var toRemove = magazinesRepository.findMagazineCopyByIssnAndIssueAndNumber(
                magazine.getIssn(),
                magazine.getIssue(),
                copy.getNumber()
        );

        if (toRemove.isEmpty()) {
            return;
        }

        try {
            magazinesRepository.deleteMagazineCopy(toRemove.get());
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    public void deleteCopies(List<ElementCopyDto> copies) {

        for (var copy : copies) {

            if (copy.getElement() instanceof BookDto) {

                deleteBookCopy(copy);

            } else if(copy.getElement() instanceof MagazineDto) {

                deleteMagazineCopy(copy);

            } else {
                throw new IllegalStateException("Unsupported element type!");
            }
        }


    }

    public Map<ElementDto, Long> getAvailableCopiesCount() {

        var books = booksRepository.findAllBooks();
        var booksMap = new TreeMap<ElementDto, Long>();

        for (var book : books) {
            var copies = booksRepository.findBookCopiesByIsbnAndNotDamaged(book.getIsbn());

            var amount = copies.stream()
                    .filter(copy -> eventsRepository.isElementAvailable(copy.getUuid()))
                    .count();

            booksMap.put(
                    new BookDto(book.getTitle(), book.getPublisher(), book.getIsbn(), book.getAuthor()),
                    amount
            );
        }

        var magazines = magazinesRepository.findAllMagazines();

        for (var magazine : magazines) {
            var copies =
                    magazinesRepository.findMagazineCopiesByIssnAndIssueAndNotDamaged(magazine.getIssn(), magazine.getIssue());

            var amount = copies.stream()
                    .filter(copy -> eventsRepository.isElementAvailable(copy.getUuid()))
                    .count();

            booksMap.put(
                    new MagazineDto(magazine.getTitle(), magazine.getPublisher(), magazine.getIssn(), magazine.getIssue()),
                    amount
            );
        }

        return booksMap;

    }

    public Optional<BookDto> getBook(String isbn) {
        return booksRepository.findBookByIsbn(isbn).map(book -> new BookDto(
                book.getTitle(),
                book.getPublisher(),
                book.getIsbn(),
                book.getAuthor()
        ));
    }

    public List<ElementCopyDto.State> getAvailableStatesForBook(String isbn) {

        var states = Arrays.stream(BookCopy.State.values())
                .filter(state -> state.getLevel() > BookCopy.State.NEED_REPLACEMENT.getLevel())
                .collect(Collectors.toList());

        var availableStates = new ArrayList<ElementCopyDto.State>();

        for (var state : states) {

            var copies = booksRepository.findBookCopiesByIsbnAndState(isbn, state);

            if (copies.stream().anyMatch(copy -> eventsRepository.isElementAvailable(copy.getUuid()))) {
                availableStates.add(StateUtils.mapState(state));
            }
        }

        return availableStates;
    }

    public List<ElementCopyDto.State> getAvailableStatesForMagazine(String issn, Integer issue){
        var states = Arrays.stream(MagazineCopy.State.values())
                .filter(state -> state.getLevel()>MagazineCopy.State.NEED_REPLACEMENT.getLevel())
                .collect(Collectors.toList());

        var availableStates = new ArrayList<ElementCopyDto.State>();

        for (var state : states){
            var copies = magazinesRepository.findMagazineCopiesByIssnAndIssueAndState(issn,issue,state);

            if (copies.stream().anyMatch(copy -> eventsRepository.isElementAvailable(copy.getUuid()))){
                availableStates.add(StateUtils.mapState(state));
            }
        }
        return availableStates;
    }

    public Optional<MagazineDto> getMagazine(String ref, Integer issue) {
        return magazinesRepository.findMagazineByIssnAndIssue(ref, issue).map(magazine -> new MagazineDto(
                magazine.getTitle(),
                magazine.getPublisher(),
                magazine.getIssn(),
                magazine.getIssue()
        ));
    }

    public boolean addBook(BookDto book) {

        try{
            booksRepository.addBook(new Book(
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher()
            ));
            return true;
        } catch (ObjectAlreadyExistsException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addMagazine(MagazineDto magazine){
        try {
            magazinesRepository.addMagazine(new Magazine(
                    magazine.getPublisher(),
                    magazine.getTitle(),
                    magazine.getIssn(),
                    magazine.getIssue()
            ));
            return true;
        } catch (ObjectAlreadyExistsException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addElement(ElementDto elementDto) {

        if (elementDto instanceof BookDto) {

            var book = (BookDto) elementDto;
            return addBook(book);

        } else if (elementDto instanceof MagazineDto) {

            var magazine = (MagazineDto) elementDto;
            return addMagazine(magazine);

        }
        return false;

    }

    public List<ElementCopyDto> getCopiesByIssnIsbnContains(String query) {

        if(query == null || query.isBlank()){
            return getAllCopies();
        }

        List<ElementCopyDto> copies = new ArrayList<>();

        var booksByIsbn =  booksRepository.findBookCopiesByIsbnContains(query);

        for (var x : booksByIsbn) {

            var book = booksRepository.findBookByUuid(x.getElementUuid()).orElseThrow();

            var bookDto = new BookDto( book.getTitle(), book.getPublisher(), book.getIsbn(), book.getAuthor());

            copies.add(new ElementCopyDto(x.getNumber(), bookDto, StateUtils.mapState(x.getState())));
        }

        var magazinesByIssn = magazinesRepository.findMagazineCopiesByIssnContains(query);

        for (var x : magazinesByIssn) {
            var magazine = magazinesRepository.findMagazineByUuid(x.getElementUuid()).orElseThrow();
            var magazineDto = new MagazineDto(
                    magazine.getTitle(),
                    magazine.getPublisher(),
                    magazine.getIssn(),
                    magazine.getIssue()
            );

            copies.add(new ElementCopyDto(x.getNumber(), magazineDto, StateUtils.mapState(x.getState())));
        }

        return copies;
    }

    public boolean updateElement(ElementDto elementDto) {

        if (elementDto instanceof BookDto) {
            return updateBook((BookDto) elementDto);
        } else if (elementDto instanceof MagazineDto) {
            return updateMagazine((MagazineDto) elementDto);
        } else {
            throw new IllegalStateException("Unsupported element type!");
        }
    }

    private boolean updateMagazine(MagazineDto elementDto) {

        var magazineOpt = magazinesRepository.findMagazineByIssnAndIssue(
                elementDto.getIssn(),
                elementDto.getIssue()
        );

        if (magazineOpt.isEmpty()) {
            return false;
        }

        var updatedMagazine = magazineOpt.get();

        updatedMagazine.setPublisher(elementDto.getPublisher());
        updatedMagazine.setTitle(elementDto.getTitle());

        try {
            magazinesRepository.updateMagazine(updatedMagazine);
            return true;
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateBook(BookDto elementDto) {
        var bookOpt = booksRepository.findBookByIsbn(elementDto.getIsbn());

        if (bookOpt.isEmpty()){
            return false;
        }

        var updatedBook = bookOpt.get();

        updatedBook.setAuthor(elementDto.getAuthor());
        updatedBook.setTitle(elementDto.getTitle());
        updatedBook.setPublisher(elementDto.getPublisher());

        try {
            booksRepository.updateBook(updatedBook);
            return true;
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
