package library.service;

import library.model.Book;
import library.util.FileManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibraryService {
    private List<Book> books;

    public LibraryService() {
        this.books = FileManager.loadBooksFromCSV();

        if (!this.books.isEmpty()) {
            FileManager.saveBooksToCSV(this.books);
        } else {
            this.books = new ArrayList<>();
        }
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    public Optional<Book> findBookById(int id) {
        return books.stream()
                .filter(book -> book.getId() == id)
                .findFirst();
    }

    public void saveBooks() {
        FileManager.saveBooksToCSV(books);
    }

    public boolean editBookMetadata(int bookId, String newTitle, String newAuthor,
                                    String newPublisher, Integer newYear) {
        Optional<Book> bookOpt = findBookById(bookId);

        if (bookOpt.isEmpty()) {
            return false;
        }

        Book book = bookOpt.get();

        if (newTitle != null && !newTitle.trim().isEmpty()) {
            book.setTitle(newTitle.trim());
        }

        if (newAuthor != null && !newAuthor.trim().isEmpty()) {
            book.setAuthor(newAuthor.trim());
        }

        if (newPublisher != null && !newPublisher.trim().isEmpty()) {
            book.setPublisher(newPublisher.trim());
        }

        if (newYear != null && newYear > 0) {
            book.setPublicationYear(newYear);
        }

        saveBooks();
        return true;
    }

    public List<String> getBookPages(Book book, int linesPerPage) {
        if (book == null || book.getTextFilePath() == null) 
            return List.of("ٍError: not found or book is emty.");
        return FileManager.readBookPages(book.getTextFilePath(), linesPerPage);
    }

    public String readFullText(int bookId) {
        Book book = findBookById(bookId);
        if (book == null) 
            return "";
        return FileManager.readFullText(book.getTextFilePath());
    }

    public boolean editBookContent(int bookId, String newContent) throws IOException {
        Book book = findBookById(bookId);
        if (book == null) 
            return false;
        FileManager.writeBookText(book.getTextFilePath(), newContent);
        return true;
    }

    public int countLines(Book book) {
        if (book == null || book.getTextFilePath() == null) 
            return 0;
        return FileManager.countLines(book.getTextFilePath());
    }
}
