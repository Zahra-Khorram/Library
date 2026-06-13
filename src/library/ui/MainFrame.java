package library.ui;

import library.model.Book;
import library.service.LibraryService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;

public class MainFrame extends JFrame {
    private String s;
    private JPanel readerPanel;
    private JTextArea pageArea;
    private List<String> pages;
    private int currentPage;
    private LibraryService service;
    private JPanel booksPanel;
    private JPanel menuPanel;
    private Book selectedBook;

    private JTextField titleField;
    private JTextField authorField;
    private JTextField publisherField;
    private JTextField yearField;

    public MainFrame() {
        service = new LibraryService();
        setTitle("Personal Library System");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createBooksPanel();
        add(booksPanel);

        setVisible(true);
    }

     private void createBooksPanel() {
        booksPanel = new JPanel(new BorderLayout());
        readerPanel = new JPanel();

        JPanel bookButtonsPanel = new JPanel();
        bookButtonsPanel.setLayout(new BoxLayout(bookButtonsPanel, BoxLayout.Y_AXIS));

        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.addActionListener(e -> refreshBooksPanel());
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookButtonsPanel.add(refreshButton);
        bookButtonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        for (Book book : service.getAllBooks()) {
            JButton bookButton = new JButton(book.getTitle());
            bookButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            bookButton.addActionListener(e -> {
                selectedBook = book;
                showBookMenu();
            });
            bookButtonsPanel.add(bookButton);
            bookButtonsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        JScrollPane scrollPane = new JScrollPane(bookButtonsPanel);
        booksPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void refreshBooksPanel() {
        remove(booksPanel);
        createBooksPanel();
        revalidate();
        repaint();
    }

    private void showBookMenu() {
        menuPanel = new JPanel(new BorderLayout());

        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(selectedBook.getTitle(), 25);
        infoPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Writer:"), gbc);
        gbc.gridx = 1;
        authorField = new JTextField(selectedBook.getAuthor(), 25);
        infoPanel.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Publisher:"), gbc);
        gbc.gridx = 1;
        publisherField = new JTextField(selectedBook.getPublisher(), 25);
        infoPanel.add(publisherField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("Publish Year:"), gbc);
        gbc.gridx = 1;
        yearField = new JTextField(String.valueOf(selectedBook.getPublicationYear()), 25);
        infoPanel.add(yearField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton saveMetadataBtn = new JButton("Save the book's info");
        saveMetadataBtn.addActionListener(this::saveMetadata);
        buttonPanel.add(saveMetadataBtn);

         JButton backBtn = new JButton("🔙 Return");
        backBtn.addActionListener(e -> {
            remove(menuPanel);
            add(booksPanel);
            revalidate();
            repaint();
        });
        buttonPanel.add(backBtn);

        JButton readBookBtn = new JButton("Read Book");
        readBookBtn.addActionListener(e -> {
            openBook(false);  
        });
        readBookBtn.setEnabled(true);
        buttonPanel.add(readBookBtn);

        JButton editBookBtn = new JButton("Edit Book");
        editBookBtn.addActionListener(e -> {
            openBook(true);   
        });
        editBookBtn.setEnabled(true);
        buttonPanel.add(editBookBtn);

        int lines = service.countLines(selectedBook);
        JLabel lineCountLabel = new JLabel("Number of line: " + lines);
        buttonPanel.add(lineCountLabel);

        menuPanel.add(infoPanel, BorderLayout.CENTER);
        menuPanel.add(buttonPanel, BorderLayout.SOUTH);

        remove(booksPanel);
        add(menuPanel);
        revalidate();
        repaint();
    }

    private void saveMetadata(ActionEvent e) {
        try {
            String newTitle = titleField.getText();
            String newAuthor = authorField.getText();
            String newPublisher = publisherField.getText();
            int newYear = Integer.parseInt(yearField.getText());

            boolean success = service.editBookMetadata(
                    selectedBook.getId(),
                    newTitle,
                    newAuthor,
                    newPublisher,
                    newYear
            );

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "The book info saved successfully!",
                        "Successful",
                        JOptionPane.INFORMATION_MESSAGE);

                selectedBook = service.findBookById(selectedBook.getId());
            } else {
                JOptionPane.showMessageDialog(this,
                        "Av Error occourred in saving book info!",
                        "Error!",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid publish year!",
                    "Error!",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

     private void openBook(boolean editable) {
        readerPanel.removeAll();
        readerPanel.setLayout(new BorderLayout());

        pageArea = new JTextArea();
        pageArea.setLineWrap(true);
        pageArea.setWrapStyleWord(true);
        pageArea.setEditable(editable);
        JScrollPane scrollPane = new JScrollPane(pageArea);
        readerPanel.add(scrollPane, BorderLayout.CENTER);

        int linesPerPage = 20; 
        pages = service.getBookPages(selectedBook, linesPerPage);
        currentPage = 0;

        if (pages != null && !pages.isEmpty()) 
            pageArea.setText(pages.get(currentPage));
        else 
            pageArea.setText("Error get book page.");

        JPanel buttonPanel = new JPanel();
        JButton prevButton = new JButton("Previous Page");
        JButton nextButton = new JButton("Next Page");
         JButton backButton = new JButton("Back");
        JButton applyButton = new JButton("Apply");

        prevButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                pageArea.setText(pages.get(currentPage));
            } else 
                JOptionPane.showMessageDialog(this, "This is first page cant Previous Page");
        });

         backButton.addActionListener(e -> {
             remove(readerPanel);
             showBookMenu();
         });

        nextButton.addActionListener(e -> {
            if (currentPage < pages.size() - 1) {
                currentPage++;
                pageArea.setText(pages.get(currentPage));
            } else 
                JOptionPane.showMessageDialog(this, "This is last page cant Next Page");
        });

        applyButton.addActionListener(e -> {
            if (editable) {
                try {
                    String newContent = pageArea.getText();
                    boolean success = service.editBookContent(selectedBook.getId(), newContent);
                    if (success == true) {
                        JOptionPane.showMessageDialog(this, "success.");
                        pages = service.getBookPages(selectedBook, linesPerPage);
                        currentPage = 0;
                        if (pages.isEmpty() == false) 
                            pageArea.setText(pages.get(currentPage));
                    } else 
                        JOptionPane.showMessageDialog(this, "Error cant finde.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(backButton);
        
        if (editable == true) 
            buttonPanel.add(applyButton);

        readerPanel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().removeAll();
        getContentPane().add(readerPanel);
        revalidate();
        repaint();
    }    
}
