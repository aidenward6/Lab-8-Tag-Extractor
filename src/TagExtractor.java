import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

class TagExtractor extends JFrame {
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JButton textFileButton;
    private JButton stopWordsButton;
    private JButton extractKeywordsButton;
    private JButton saveKeywordButton;

    private File selectedTextFile;
    private Set<String> stopWords;
    private Map<String, Integer> keywordFrequency;

    public TagExtractor() {

        textArea = new JTextArea(20, 40);
        scrollPane = new JScrollPane(textArea);
        textFileButton = new JButton("Select Text File");
        stopWordsButton = new JButton("Select Stop Words File");
        extractKeywordsButton = new JButton("Extract Tags");
        saveKeywordButton = new JButton("Save Tags");

        textFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectTextFile();
            }
        });

        stopWordsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectStopWordsFile();
            }
        });

        extractKeywordsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                extractTags();
            }
        });

        saveKeywordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                saveTags();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(textFileButton);
        buttonPanel.add(stopWordsButton);
        buttonPanel.add(saveKeywordButton);
        buttonPanel.add(extractKeywordsButton);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(scrollPane, BorderLayout.NORTH);
        contentPane.add(buttonPanel, BorderLayout.CENTER);


        setTitle("Tag Extractor");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

    }



    private void selectTextFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedTextFile = fileChooser.getSelectedFile();
            textArea.setText("Selected Text File: " + selectedTextFile.getName() + "\n");
        }
    }

    private void selectStopWordsFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File stopWordsFile = fileChooser.getSelectedFile();
            stopWords = loadStopWords(stopWordsFile);
            textArea.append("Selected Stop Words File: " + stopWordsFile.getName() + "\n");
        }
    }

    private Set<String> loadStopWords(File stopWordsFile) {
        Set<String> NoiseWords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(stopWordsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                NoiseWords.add(line.toLowerCase());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Cannot Load Noise Words File.");
            e.printStackTrace();
        }
        return NoiseWords;
    }




    private void extractTags() {
        if (selectedTextFile == null) {
            JOptionPane.showMessageDialog(this, "You Must Select A Text File");
            return;
        }

        if (stopWords == null) {
            JOptionPane.showMessageDialog(this, "You Must Select A Noise Word File");
            return;
        }




        keywordFrequency = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(selectedTextFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
                    if (!stopWords.contains(word)) {
                        keywordFrequency.put(word, keywordFrequency.getOrDefault(word, 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error processing the text file.");
            e.printStackTrace();
        }

        displayTags();
    }

    private void displayTags() {
        textArea.append("\nTags and Frequencies:\n");

        for (Entry<String, Integer> entry : keywordFrequency.entrySet()) {
            textArea.append(entry.getKey() + " : " + entry.getValue() + "\n");
        }
    }

    private void saveTags() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(outputFile)) {
                for (Entry<String, Integer> entry : keywordFrequency.entrySet()) {
                    writer.println(entry.getKey() + " : " + entry.getValue());
                }
                textArea.append("\nTags saved to: " + outputFile.getName() + "\n");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Cannot Save Tags");
                e.printStackTrace();
            }
        }
    }


}
