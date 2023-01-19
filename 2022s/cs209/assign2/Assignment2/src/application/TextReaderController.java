package application;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static javafx.scene.control.SelectionMode.SINGLE;


public class TextReaderController {
    @FXML
    private VBox vBoxPane;
    @FXML
    private ListView<FileCell> fileListView;
    @FXML
    private WebView webView;
    @FXML
    private Label filePathLabel;
    @FXML
    private Label wordsNumberLabel;

    @FXML
    private MenuItem openFileBtn;
    @FXML
    private MenuItem closeFileBtn;
    @FXML
    private MenuItem saveFileBtn;
    @FXML
    private MenuItem saveAsFileBtn;
    @FXML
    private MenuItem quitBtn;

    @FXML
    private MenuItem findBtn;
    @FXML
    private MenuItem replaceAllBtn;

    private String defaultStyle;
    private String markStyle;

    private final Set<FileCell> filesInList = new HashSet<>();

    /**
     * The method fileListOnDragDropped(DragEvent event) is the callback function
     * when the mouse drag a file (files) to the ListView component and then drop.
     *
     * @param event: DragEvent object
     */
    @FXML
    public void fileListOnDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        ObservableList<FileCell> items = fileListView.getItems();
        if (dragboard.hasFiles()) {
            List<File> files = dragboard.getFiles();
            FileCell last = null;
            for (File file : files) {
                if (!file.getName().endsWith(".txt")) continue;
                FileCell fc = new FileCell(file);
                if (!filesInList.contains(fc)) {
                    items.add(fc);
                    filesInList.add(fc);
                }
                last = fc;
            }
            displayFile(last);
        }
    }

    /**
     * The method fileListOnDragOver(DragEvent event) is the callback function
     * when the mouse drag a file (files) over the ListView Component.
     *
     * @param event: DragEvent object
     */
    @FXML
    public void fileListOnDragOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.ANY);
    }

    /**
     * The method openFile() is to open a file using FileChooser API,
     * then add the file object to the fileListView Component.
     * <p>
     * You should restrict the fileChooser to only select *.txt files.
     * Hint: FileChooser.ExtensionFilter()
     * <p>
     * Useful FileChooser API Refer:
     * https://www.w3cschool.cn/java/javafx-filechooser.html
     */
    @FXML
    public void openFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose a txt file to be loaded");
        fc.setInitialDirectory(new File("."));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plain text file", "*.txt"));

        File file = fc.showOpenDialog(vBoxPane.getScene().getWindow());
        if (file == null) return;
        FileCell cell = new FileCell(file);

        if (!filesInList.contains(cell)) {
            fileListView.getItems().add(cell);
            fileListView.refresh();
            filesInList.add(cell);
        }
        displayFile(cell);
    }

    /**
     * The method saveFile() is to save the selected file to the disk.
     * 1. The "selected file" is the selected item in fileListView.
     * 2. If selected file is null, just do nothing,
     * else, save the textContent in the reader to disk.
     * <p>
     * Hint:
     * The selected item is a FileCell object, which encapsulates the File object (getFile()).
     */
    @FXML
    public void saveFile() {
        FileCell selected = fileListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        selected.setFileContent(getReaderContent());
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(selected.file));
            writer.write(selected.fileContent);
            writer.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * The method saveAsFile() is to save the selected file to another path in the disk.<p>
     * 1. The "selected file" is the selected item in fileListView.<br>
     * 2. If selected file is null, just do nothing,
     * else, use FileChooser to choose the new path to save.
     * <p>
     * You should restrict the fileChooser to only save *.txt files.
     * Hint: FileChooser.ExtensionFilter()
     * <p>
     * Attention:
     * When you have saved the file successfully,
     * the old file name in fileListView for this file should be changed to the new file name,
     * and update the attributes in the FileCell object.
     * <p>
     * Hint:
     * After updating the attributes in the FileCell object,
     * call fileListView.refresh() to refresh the UI Component.
     */
    @FXML
    public void saveAsFile() {
        FileCell selected = fileListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        FileChooser fc = new FileChooser();
        fc.setTitle("Save the file to...");
        fc.setInitialDirectory(new File("."));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plain text file", "*.txt"));

        File dst = fc.showSaveDialog(vBoxPane.getScene().getWindow());
        if (dst == null) return;
        selected.setFile(dst);
        selected.setAbsoluteFilePath(dst.getAbsolutePath());
        selected.setFileName(dst.getName());
        saveFile();
        fileListView.refresh();
        displayFile(selected);
    }

    /**
     * The method closeFile() is to close the selected file.
     * 1. The "selected file" is the selected item in fileListView.
     * 2. If selected file is null, just do nothing,
     * else, use Alert to ask user whether to save the file.
     * 1. If yes, save the file.
     * 2. If no, do nothing.
     * 3. Then, remove the item in fileListView, clear the selection, and clear the reader content.
     * <p>
     * Don't forget to refresh the fileListView.
     */
    @FXML
    public void closeFile() {
        FileCell selected = fileListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.WARNING,
                "Save the file before closing?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            saveFile();
        }
        filesInList.remove(selected);
        fileListView.getItems().remove(selected);
        fileListView.refresh();
        displayFile(null);
    }

    @FXML
    public void quit() {
        System.exit(0);
    }

    /**
     * The method updateWordsNumber() is to update the words number in bottom-right label.
     * 1. Statistic the words number for the textContent in reader.
     * 2. Set the words number in format "Words number: 123" for wordsNumberLabel object.
     */
    @FXML
    public void updateWordsNumber() {
        String content = getReaderContent().replaceAll("\r?\n", " ");
        Matcher m = Pattern.compile("\\b[\\w'-]+\\b").matcher(content);
        int num = 0;
        while (m.find()) {
            num++;
        }
        wordsNumberLabel.setText("Words number: " + num);
    }

    /**
     * The method find() is to find the user-input word in the reader.
     * 1. Use TextInputDialog API for user to input a word.
     * 2. Highlight the word in the reader.
     * <p>
     * Hint:
     * To highlight a word in the reader, for simplicity,
     * you can just replace the word with String
     * "<span class=\"mark\">" + word + "</span>",
     * then set the replaced string to the reader.
     * <p>
     * Example:
     * To highlight "is" in "He is boy.",
     * "He <span class="mark">is</span> boy." is enough.
     * <p>
     * TextInputDialog API Refer:
     * https://blog.csdn.net/qq_26954773/article/details/78215554
     */
    @FXML
    public void find() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Word Finder");
        dialog.setHeaderText("Only the whole word will be found");
        Optional<String> result = dialog.showAndWait();
        AtomicReference<String> word = new AtomicReference<>("");
        result.ifPresent(word::set);

        String content = getReaderContent();
        StringBuilder highlighted = new StringBuilder();
        Matcher wm = Pattern.compile(String.format("\\b(?<![-'])(?i)%s(?![-'])\\b", word)).matcher(content);

        int pos = 0;
        while (wm.find()) {
            MatchResult mr = wm.toMatchResult();
            highlighted.append(content, pos, mr.start());
            highlighted.append("<span class = \"mark\">")
                    .append(mr.group())
                    .append("</span>");
            pos = mr.end();
        }
        highlighted.append(content.substring(pos));
        setReaderContent(highlighted.toString());
    }


    /**
     * The method replaceAll() is to replaceAll the user-input string in the reader and replace it.
     * 1. Use TextInputDialog API for user to input the old string and the new string.
     * 2. Replace the old string with the new string in the reader.
     * 3. Highlight the replaced string in the reader.
     * 4. Then update the word number label.
     */
    @FXML
    public void replaceAll() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Word Replacer");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        TextField from = new TextField();
        from.setPromptText("replace");
        TextField to = new TextField();
        to.setPromptText("to");

        gridPane.add(from, 0, 0);
        gridPane.add(to, 1, 0);
        dialog.getDialogPane().setContent(gridPane);

        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.APPLY)
                return null;
            return new Pair<>(from.getText(), to.getText());
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        if (!result.isPresent()) return;
        String original = result.get().getKey();
        String toRaw = result.get().getValue();
        if ("".equals(original)) return;

        String content = getReaderContent();
        StringBuilder highlighted = new StringBuilder();
        StringBuilder newContent = new StringBuilder();
        Matcher wm = Pattern.compile(String.format("\\b(?i)%s\\b", original)).matcher(content);

        int pos = 0;
        while (wm.find()) {
            MatchResult mr = wm.toMatchResult();
            highlighted.append(content, pos, mr.start());
            highlighted.append("<span class = \"mark\">")
                    .append(toRaw)
                    .append("</span>");
            newContent.append(content, pos, mr.start());
            newContent.append(toRaw);
            pos = mr.end();
        }
        highlighted.append(content.substring(pos));
        newContent.append(content.substring(pos));

        setReaderContent(highlighted.toString());
        FileCell selected = fileListView.getSelectionModel().getSelectedItem();
        if (selected != null) selected.setFileContent(newContent.toString());
    }

    /**
     * The method checkGrammar() is to check the correctness of the first letter of the English sentence.
     * 1. Find all the incorrect sentences with lower case letter as the first letter.
     * 2. Highlight the whole sentence in the reader.
     */
    @FXML
    public void checkGrammar() {
        String str = getReaderContent();
        StringBuilder sb = new StringBuilder();

        int pos = 0, sentTo = 0;
        while (sentTo < str.length() - 1) {
            while (str.charAt(sentTo) != '.' &&
                    str.charAt(sentTo) != '!' &&
                    str.charAt(sentTo) != '?') {
                if (sentTo == str.length() - 1) break;
                sentTo++;
            }
            sentTo++;
            String sent = str.substring(pos, sentTo);
            if (!sent.trim().replaceAll("\r?\n", " ").matches("^[^\\w]*[a-z].*")) sb.append(sent);
            else {
                while (!(str.charAt(pos) >= 'a' && str.charAt(pos) <= 'z')) {
                    sb.append(str.charAt(pos));
                    pos++;
                }
                sb.append("<span class = \"mark\">").append(str, pos, sentTo).append("</span>");
            }
            pos = sentTo;
        }
        setReaderContent(sb.toString());
    }

    /**
     * The method fixGrammar() is to fix the correctness of the first letter of the English sentence.
     * 1. Find all the incorrect sentences with lower case letter as the first letter.
     * 2. Fix the first letter of them.
     * 3. Highlight the whole fixed sentence in the reader.
     */
    @FXML
    public void fixGrammar() {
        String str = getReaderContent();
        StringBuilder sb = new StringBuilder();

        int pos = 0, sentTo = 0;
        while (sentTo < str.length() - 1) {
            while (str.charAt(sentTo) != '.' &&
                    str.charAt(sentTo) != '!' &&
                    str.charAt(sentTo) != '?') {
                if (sentTo == str.length() - 1) break;
                sentTo++;
            }
            sentTo++;
            String sent = str.substring(pos, sentTo);
            if (!sent.trim().replaceAll("\r?\n", " ").matches("^[^\\w]*[a-z].*")) sb.append(sent);
            else {
                while (!(str.charAt(pos) >= 'a' && str.charAt(pos) <= 'z')) {
                    sb.append(str.charAt(pos));
                    pos++;
                }
                sb.append("<span class = \"mark\">")
                        .append(Character.toUpperCase(str.charAt(pos)))
                        .append(str, pos + 1, sentTo)
                        .append("</span>");
            }
            pos = sentTo;
        }
        setReaderContent(sb.toString());
    }

    /**
     * The method statisticWordFrequency() is to statistic the top-10 words frequency in the reader.
     * <p>
     * Requirements:
     * 1. Use BarChart.
     * 2. The chart should have title, x-label, y-label and category for the top-10 words.
     * 3. The chart should be in order from left to right (in descending).
     * 4. The chart should be in a new stage.
     * <p>
     * Hint:
     * If there are not 10 different words in the reader, just top-n (n<10) is also ok.
     */
    @FXML
    public void statisticWordFrequency() {
        Matcher words = Pattern.compile("\\b[\\w-']+\\b").matcher(getReaderContent().replaceAll("\r?\n", " ").toLowerCase());
        Map<String, Integer> wordsFreq = regexResults(words)
                .map(MatchResult::group)
                .collect(Collectors.toMap(Function.identity(), _v -> 1, Integer::sum));
        List<XYChart.Data<String, Number>> top10 = wordsFreq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>
                                comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(10)
                .map(e -> new XYChart.Data<>(e.getKey(), (Number) e.getValue()))
                .collect(Collectors.toList());

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.observableArrayList(top10.stream().map(XYChart.Data::getXValue).collect(Collectors.toList())));
        xAxis.setLabel("Word");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Frequency");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Top 10 Word Frequency Bar Chart");

        XYChart.Series<String, Number> data = new XYChart.Series<>();
        data.setName("");
        data.getData().addAll(top10);
        barChart.getData().add(data);

        Group group = new Group(barChart);
        Scene scene = new Scene(group, 500, 420);
        Stage stage = new Stage();
        stage.setTitle("");
        stage.setScene(scene);

        KeyCombination closeFreq = System.getProperty("os.name").toLowerCase().startsWith("win") ?
                KeyCombination.keyCombination("CTRL+W") :
                KeyCombination.keyCombination("META+W");
        scene.getAccelerators().put(closeFreq, stage::close);
        stage.show();
    }

    private Stream<MatchResult> regexResults(Matcher matcher) {
        Spliterator<MatchResult> spliterator = new Spliterators.AbstractSpliterator<MatchResult>(
                Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.NONNULL) {
            @Override
            public boolean tryAdvance(Consumer<? super MatchResult> action) {
                if (!matcher.find()) return false;
                action.accept(matcher.toMatchResult());
                return true;
            }
        };
        return StreamSupport.stream(spliterator, false);
    }

    /**
     * The method clearMark() is to clear the mark (highlight) in the reader.
     */
    @FXML
    public void clearMark() {
        setReaderContent(getReaderContent());
    }

    /**
     * The method setMarkStyle() is to prompt user to input a mark style string with format ".mark{...;...;}"
     * and set the new mark style to the reader.
     */
    @FXML
    public void setMarkStyle() {
        TextInputDialog dialog = new TextInputDialog("Set the mark style in css format");
        dialog.setHeaderText("Input the mark style in css format (.mark{...;...;})");
        Optional<String> result = dialog.showAndWait();
        this.markStyle = "";
        result.ifPresent(style -> this.markStyle = style);
        reload();
    }

    /**
     * The method setDefaultStyle() is to prompt user to input a default style string with format ".default{...;...;}"
     * and set the new default style to the reader.
     */
    @FXML
    public void setDefaultStyle() {
        TextInputDialog dialog = new TextInputDialog("Set the default style in css format");
        dialog.setHeaderText("Input the default style in css format (.default{...;...;})");
        Optional<String> result = dialog.showAndWait();
        this.defaultStyle = "";
        result.ifPresent(style -> this.defaultStyle = style);
        reload();
    }

    /**
     * The method fileListItemOnSelected() is the callback function when user select an item in ListView Component.
     */
    public void fileListItemOnSelected() {
        FileCell selectedItem = fileListView.getSelectionModel().getSelectedItem();
        setReaderContent(selectedItem.getFileContent());
        wordsNumberLabel.setText("Words number: " + selectedItem.getWordsNumber());
        filePathLabel.setText("File Path: " + selectedItem.getAbsoluteFilePath());
    }

    private void moveFocusTo(FileCell cell) {
        if (cell == null) {
            fileListView.getSelectionModel().select(null);
            return;
        }
        int idx = -1;
        ObservableList<FileCell> fl = fileListView.getItems();
        for (int i = 0; i < fl.size(); i++) {
            if (fl.get(i).equals(cell)) {
                idx = i;
                break;
            }
        }
        if (idx == -1) return;
        fileListView.scrollTo(idx);
        fileListView.getSelectionModel().select(idx);
    }

    private void displayFile(FileCell cell) {
        moveFocusTo(cell);
        if (cell == null) {
            setReaderContent("");
            wordsNumberLabel.setText("Words number: 0");
            filePathLabel.setText("File Path:");
            return;
        }
        setReaderContent(cell.getFileContent());
        wordsNumberLabel.setText("Words number: " + cell.getWordsNumber());
        filePathLabel.setText("File Path: " + cell.getAbsoluteFilePath());
    }

    /**
     * The method preprocessHTMLDocument(String txtDocument) is to wrap the content string with
     * the standard HTML labels (such as <html></html>, <style></style>, etc.), to get the standard HTML document string.
     *
     * @param txtDocument: the content for reader (perhaps include some mark labels)
     * @return The html document string
     */
    public String preprocessHTMLDocument(String txtDocument) {
        return "<html>" +
                "<head>" + "<style>" + defaultStyle + markStyle + "</style>" + "</head>" +
                "<body contenteditable=\"false\">" +
                "<span class=\"default\">" + txtDocument.trim().replaceAll("\n", "<br>") + "</span>" +
                "</body>" +
                "</html>";
    }

    /**
     * The method postprocessHTMLDocument(String HTMLDocument) is to parse the html document string,
     * filtrate the html mark labels, return the pure content.
     *
     * @param HTMLDocument: the html document string (which includes <html></html>, <style></style>, etc.)
     * @return The content without any html mark labels
     */
    public String postprocessHTMLDocument(String HTMLDocument) {
        String postprocessDocument = HTMLDocument.replaceAll("<br>", "\n");

        String regExStyle = "]*?>[\\s\\S]*?</style>";
        Pattern patternStyle = Pattern.compile(regExStyle, Pattern.CASE_INSENSITIVE);
        Matcher matcherStyle = patternStyle.matcher(postprocessDocument);
        postprocessDocument = matcherStyle.replaceAll("");

        String regExHTML = "<[^>]+>";
        Pattern patternHTML = Pattern.compile(regExHTML, Pattern.CASE_INSENSITIVE);
        Matcher matcherHTML = patternHTML.matcher(postprocessDocument);
        postprocessDocument = matcherHTML.replaceAll("");
        return postprocessDocument.trim();
    }

    /**
     * The method getHtmlText() is to get the html string in the reader.
     *
     * @return The HTML string in the current reader.
     */
    public String getHtmlText() {
        return (String) webView.getEngine().executeScript("document.documentElement.outerHTML");
    }

    /**
     * The method setHtmlText() is to set the html string to the reader.
     *
     * @param htmlText The HTML string to set.
     */
    public void setHtmlText(String htmlText) {
        webView.getEngine().loadContent(htmlText);
    }

    /**
     * The method setReaderContent() is to set the content string to the reader.
     *
     * @param content The reader content to be set (perhaps including <span class="..."></span> labels).
     */
    public void setReaderContent(String content) {
        setHtmlText(preprocessHTMLDocument(content));
    }

    /**
     * The method getReaderContent() is to get the content string in the reader.
     *
     * @return content string without any html mark labels.
     */
    public String getReaderContent() {
        return postprocessHTMLDocument(getHtmlText());
    }

    /**
     * The method reload() is to reload the content in the reader.
     * It can clear the mark (highlight) in the reader.
     */
    public void reload() {
        setReaderContent(getReaderContent());
    }

    @FXML
    public void about() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About TextReader");
        alert.setHeaderText("CS209A 22F Assignment2");
        alert.setContentText("License under GNU General Public License v3.0\n" +
                "Made with \uFE0F by Chris");
        alert.showAndWait();
    }

    /**
     * The method initialize() is a default method called by Java FX Loader (after constructor method is called).
     * More details: https://newbedev.com/javafx-fxml-controller-constructor-vs-initialize-method
     */
    public void initialize() {
        fileListView.getSelectionModel().setSelectionMode(SINGLE);
        fileListView.setEditable(false);
        fileListView.setCellFactory((ListView<FileCell> fileCellListView) -> new ListCell<FileCell>() {
            @Override
            protected void updateItem(FileCell item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(item.getFileName());
                    this.setOnMouseClicked(mouseEvent -> fileListItemOnSelected());
                    this.setGraphic(label);
                }
            }
        });

        webView.addEventFilter(KeyEvent.KEY_TYPED, Event::consume);
        webView.setOnInputMethodTextChanged(Event::consume);
        webView.getEngine().getLoadWorker().stateProperty().
                addListener((observableValue, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        updateWordsNumber();
                    }
                });

        markStyle = ".mark{background-color:yellow;}";
        defaultStyle = ".default{color:black;}";

        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            openFileBtn.setText("Open...  [ctrl + O]");
            closeFileBtn.setText("Close  [ctrl + W]");
            saveFileBtn.setText("Save  [ctrl + S]");
            saveAsFileBtn.setText("Save As...  [ctrl + shift + S]");
            quitBtn.setText("Quit  [alt + F4]");
            findBtn.setText("Find  [ctrl + F]");
            replaceAllBtn.setText("Replace All  [ctrl + shift + F]");
        } else {
            openFileBtn.setText("Open...  [⌘ + O]");
            closeFileBtn.setText("Close  [⌘ + W/⌫]");
            saveFileBtn.setText("Save  [⌘ + S]");
            saveAsFileBtn.setText("Save As...  [⌘ + ⇧ + S]");
            quitBtn.setText("Quit  [⌘ + Q]");
            findBtn.setText("Find  [⌘ + F]");
            replaceAllBtn.setText("Replace All  [⌘ + ⇧ + F]");
        }
    }

    /**
     * Class FileCell is an abstract of a file item.
     */
    private static class FileCell {
        private File file;
        private String absoluteFilePath;
        private String fileName;
        private Integer wordsNumber;
        private String fileContent;

        public FileCell(String filePath) {
            this.file = new File(filePath);
            this.initialize();
        }

        public FileCell(File file) {
            this.file = file;
            this.initialize();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FileCell fileCell = (FileCell) o;
            return Objects.equals(file, fileCell.file) && Objects.equals(absoluteFilePath, fileCell.absoluteFilePath);
        }

        @Override
        public int hashCode() {
            return Objects.hash(absoluteFilePath);
        }

        /**
         * Initialize the attributes
         */
        private void initialize() {
            this.absoluteFilePath = this.file.getAbsolutePath();
            this.fileName = this.file.getName();

            if (!file.exists()) {
                this.wordsNumber = 0;
                this.fileContent = "";
            } else {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    this.fileContent = stringBuilder.toString();

                    Matcher m = Pattern.compile("\\b[\\w'-]+\\b").matcher(this.fileContent);
                    int num = 0;
                    while (m.find()) {
                        num++;
                    }
                    this.wordsNumber = num;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public File getFile() {
            return file;
        }

        public String getAbsoluteFilePath() {
            return absoluteFilePath;
        }

        public String getFileName() {
            return fileName;
        }

        public Integer getWordsNumber() {
            return wordsNumber;
        }

        public String getFileContent() {
            return fileContent;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public void setAbsoluteFilePath(String absoluteFilePath) {
            this.absoluteFilePath = absoluteFilePath;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public void setWordsNumber(Integer wordsNumber) {
            this.wordsNumber = wordsNumber;
        }

        public void setFileContent(String fileContent) {
            this.fileContent = fileContent;
            Matcher m = Pattern.compile("\\b[\\w'-]+\\b").matcher(this.fileContent);
            int num = 0;
            while (m.find()) {
                num++;
            }
            this.wordsNumber = num;
        }
    }
}
