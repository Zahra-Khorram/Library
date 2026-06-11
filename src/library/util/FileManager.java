package library.util;

import library.model.Book;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    public static final String BOOKS_TEXT_DIR = "data/books_text/";
    public static final String BOOK_LIST_CSV = "data/Book_List.txt";

    public static List<Book> loadBooksFromCSV() {
        List<Book> books = new ArrayList<>();
        File file = new File(BOOK_LIST_CSV);

        if (!file.exists()) {
            System.err.println("The Book File doesn't exist: " + BOOK_LIST_CSV);
            return books;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            int id = 1;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String title = parts[0].trim();
                    String author = parts[1].trim();
                    String publisher = parts[2].trim();
                    int year = Integer.parseInt(parts[3].trim());
                    String fileName = parts[4].trim();

                    String fullPath = BOOKS_TEXT_DIR + fileName;

                    Book book = new Book(id++, title, fullPath, author, publisher, year);
                    books.add(book);
                }
            }
        } catch (IOException e) {
            System.err.println("An Error occurred while reading file! " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("An Error occurred (Book Year): " + e.getMessage());
        }

        return books;
    }

    public static void saveBooksToCSV(List<Book> books) {
        File file = new File(BOOK_LIST_CSV);

        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Title,Author,Publisher,Publication Year,Path");
            writer.newLine();

            for (Book book : books) {
                String fileName = book.getTextFilePath().replace(BOOKS_TEXT_DIR, "");

                writer.write(String.format("%s,%s,%s,%d,%s",
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPublisher(),
                        book.getPublicationYear(),
                        fileName));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error in saving the book files! " + e.getMessage());
        }
    }
    public static List<String> readBookPages(String filePath, int linesPerPage) {
        List<String> pages = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            for (int i = 0; i < lines.size(); i += linesPerPage) {
                StringBuilder newPages = new StringBuilder();
                for (int j = i; j < i + linesPerPage && j < lines.size(); j++) 
                    newPages.append(allLines.get(j) + "\n");
                pages.add(newPages.toString());
            }
            if (pages.size() == 0) 
                pages.add("It is empty.");
        } catch (FileNotFoundException e) {
            pages.add("Not found " + filePath + " this file.");
        } catch (IOException e) {
            pages.add("Error: " + e.getMessage());
        }
        return pages;
    }
    
    public static int countLines(String filePath) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.readLine() != null) 
                count++;
        } catch (IOException e) {
            return 0;
        }
        return count;
    }

    public static void writeBookText(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        }
    }

    public static String readFullText(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            while (line != null) {
                content.append(line + "\n");
                line = reader.readLine()
            }
        } catch (IOException e) {
            return "";
        }
        return content.toString();
    }
}
