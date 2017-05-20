package controllers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import DAO.DaoMain;
import Interfaces.Impl.CollectionNamesOfColumns;
import Objects.NameOfColumn;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;
import start.MainApp;
import utils.DialogManager;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainController implements Initializable {

    private CollectionNamesOfColumns namesOfColumnsImpl = new CollectionNamesOfColumns();
    private Map <Integer, String> columnMap = new TreeMap<>();
    @FXML
    private TextField tableNameEdit;
    @FXML
    private Button btnSearch;
    @FXML
    private CustomTextField txtSearch;
    @FXML
    private Button btnOpen;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnLoad;
    @FXML
    private TableView tableNamesOfColumns;
    @FXML
    private TableColumn<NameOfColumn, String> columnNameInExcel;
    @FXML
    private  TableColumn<NameOfColumn, String> columnNameInTable;
    @FXML
    private ChoiceBox choiceLang;
    private EditDialogController editDialogController;
    private TableNameDialogController tableNameDialogController;
    private FXMLLoader fxmlLoader1 = new FXMLLoader();
    private FXMLLoader fxmlLoader2 = new FXMLLoader();
    private Stage mainStage;
    private Stage editDialogStage;
    private Stage tableNameDialogStage;
    private Stage openDialogStage;
    private ResourceBundle resourceBundle;
    private Parent rootEdit;
    private Parent rootEditTN;
    private File file;

    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        tableNamesOfColumns.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        columnNameInExcel.setCellValueFactory(new PropertyValueFactory<NameOfColumn, String>("columnNameInExcel"));
        columnNameInTable.setCellValueFactory(new PropertyValueFactory<NameOfColumn, String>("columnNameInDB"));
        initLoaderEditDialog();
        initLoaderTableNameDialog();
        initListener();
        setupClearButtonField(txtSearch);
        initChoiceBox();
    }
    private void initChoiceBox() {
        choiceLang.setItems(FXCollections.observableArrayList("Русский", "English"));
        choiceLang.setValue("Русский");
        //choiceLang.setDisable(true);
        choiceLang.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue ov, Number oldValue, Number newValue) {
                switch (newValue.intValue()){
                    case 0:
                        Locale.setDefault(new Locale("ru"));
                        break;
                    case 1:
                        Locale.setDefault(new Locale("en"));
                        break;
                }
                resourceBundle = ResourceBundle.getBundle("bundles.Locale", Locale.getDefault());
                changeLang(resourceBundle);
                editDialogController.changeLang(resourceBundle);
                tableNameDialogController.changeLang(resourceBundle);
            }
        });
    }
    private void changeLang(ResourceBundle resourceBundle){
        this.resourceBundle = resourceBundle;
        btnOpen.setText(resourceBundle.getString("open_excel"));
        btnEdit.setText(resourceBundle.getString("edit_line"));
        btnLoad.setText(resourceBundle.getString("load_into_db"));
        btnSearch.setText(resourceBundle.getString("search"));
        columnNameInExcel.setText(resourceBundle.getString("name_in_excel"));
        columnNameInTable.setText(resourceBundle.getString("name_in_table"));
    }

    private void initSearch() {
        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<NameOfColumn> filteredData = new FilteredList<>(namesOfColumnsImpl.getNamesOfColumnsList(), p -> true);
        // 2. Set the filter Predicate whenever the filter changes.
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(name -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare fields with filter text.
                String lowerCaseFilter = newValue.toLowerCase();
                if (name.getColumnNameInExcel().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches field in Excel.
                } else if (name.getColumnNameInDB().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches field in DB.
                }
                return false; // Does not match.
            });
        });
        // 3. Wrap the FilteredList in a SortedList.
        SortedList<NameOfColumn> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tableNamesOfColumns.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        tableNamesOfColumns.setItems(sortedData);
    }

    private void initLoaderEditDialog() {
        try {
            fxmlLoader1.setLocation(getClass().getResource("../fxml/edit.fxml"));
            fxmlLoader1.setResources(resourceBundle);
            rootEdit = fxmlLoader1.load();
            editDialogController = fxmlLoader1.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initLoaderTableNameDialog() {
        try {
            fxmlLoader2.setLocation(getClass().getResource("../fxml/tableName.fxml"));
            fxmlLoader2.setResources(resourceBundle);
            rootEditTN = fxmlLoader2.load();
            tableNameDialogController = fxmlLoader2.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //listener on table clicked
    private void initListener() {
        tableNamesOfColumns.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (event.getClickCount()==2){
                    NameOfColumn selectedNameOfColumn = (NameOfColumn)tableNamesOfColumns.getSelectionModel().getSelectedItem();
                    if (!nameOfColumnIsSelected(selectedNameOfColumn)){
                        return;
                    }
                    editDialogController.setNameOfColumn(selectedNameOfColumn);
                    showEditDialog();
                }
            }
        });
    }
    private void showEditDialog() {
        if (editDialogStage == null) {
            editDialogStage = new Stage();
            editDialogStage.setMinHeight(100);
            editDialogStage.setMaxHeight(100);
            editDialogStage.setMinWidth(500);
            editDialogStage.setMaxHeight(500);
            editDialogStage.setResizable(false);
            editDialogStage.setScene(new Scene(rootEdit));
            editDialogStage.initModality(Modality.WINDOW_MODAL);
            editDialogStage.initOwner(mainStage);
        }
        editDialogStage.setTitle(resourceBundle.getString("edit_line"));
        editDialogStage.showAndWait();
    }
    private void showTableNameDialog() {
        if (tableNameDialogStage == null) {
            tableNameDialogStage = new Stage();
            tableNameDialogStage.setMinHeight(50);
            tableNameDialogStage.setMaxHeight(50);
            tableNameDialogStage.setMinWidth(500);
            tableNameDialogStage.setMaxHeight(500);
            tableNameDialogStage.setResizable(false);
            tableNameDialogStage.setScene(new Scene(rootEditTN));
            tableNameDialogStage.initModality(Modality.WINDOW_MODAL);
            tableNameDialogStage.initOwner(mainStage);
        }
        tableNameDialogStage.setTitle(resourceBundle.getString("name_of_table"));
        tableNameDialogStage.showAndWait();
    }
    private void showOpenFileDialog(){
        if (openDialogStage == null){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(resourceBundle.getString("open_file_excel"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel *.xls","*.xls"));
            file = fileChooser.showOpenDialog(mainStage);
        }

    }

    private void setupClearButtonField(CustomTextField customTextField) {
        try {
            Method m = TextFields.class.getDeclaredMethod("setupClearButtonField", TextField.class, ObjectProperty.class);
            m.setAccessible(true);
            m.invoke(null, customTextField, customTextField.rightProperty());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void actionDialogPressed(ActionEvent actionEvent) throws IOException{
        Object source = actionEvent.getSource();
        if (!(source instanceof Button)){
            return;
        }
        Button clickedButton = (Button)source;
        NameOfColumn selectedNameOfColumn = (NameOfColumn)tableNamesOfColumns.getSelectionModel().getSelectedItem();
        Window parentWindow = ((Node)actionEvent.getSource()).getScene().getWindow();
        editDialogController.setNameOfColumn(selectedNameOfColumn);
        switch (clickedButton.getId()){
            case "btnOpen":
                showOpenFileDialog();
                if (!(file==null)){
                    if (!fillTableView(file)){
                        file=null;
                    }
                }
                break;
            case "btnEdit":
                if (!nameOfColumnIsSelected(selectedNameOfColumn)){
                    return;
                }
                editDialogController.setNameOfColumn((NameOfColumn)tableNamesOfColumns.getSelectionModel().getSelectedItem());
                showEditDialog();
                break;
            case "btnLoad":
                if (!(file==null)){
                    if (validateTableNames()) {
                        do {
                            showTableNameDialog();
                        }while (TableNameDialogController.tableName == null);
                        for (NameOfColumn nameOfColumn : namesOfColumnsImpl.getNamesOfColumnsList()) {
                            columnMap.put(nameOfColumn.getId(), nameOfColumn.getColumnNameInDB());
                        }
                        File csvFile = loadXlsToCsv(file, columnMap);
                        DaoMain.loadIntoDB(TableNameDialogController.tableName, csvFile);
                        String jsonFileName = file.getName();
                        int index = jsonFileName.lastIndexOf('.');
                        if(index > 0) jsonFileName = jsonFileName.substring(0, index);
                        jsonFileName =  jsonFileName +"-"+ Math.abs(new Random().nextInt()) + ".json";
                        String gsonResult = saveGSONSchema(jsonFileName, TableNameDialogController.tableName , namesOfColumnsImpl.getNamesOfColumnsList());
                        if (gsonResult.isEmpty()){
                            DialogManager.showErrorDialog(resourceBundle.getString("error"),resourceBundle.getString("json_file_error"));
                        } else {
                            DialogManager.showInfoDialog(resourceBundle.getString("info"),resourceBundle.getString("json_file_ok")+"\n"+gsonResult);
                        }
                        DialogManager.showInfoDialog(resourceBundle.getString("info"),resourceBundle.getString("success_load"));
                        btnOpen.setDisable(false);
                        //tableNameDialogController.tableName = "";
                        file = null;
                        //tableNameDialogStage = null;
                        namesOfColumnsImpl.getNamesOfColumnsList().clear();
                    } else {
                        DialogManager.showErrorDialog(resourceBundle.getString("error"),resourceBundle.getString("error_names"));
                    }
                } else {
                    DialogManager.showErrorDialog(resourceBundle.getString("error"),resourceBundle.getString("choose_file"));
                }
                break;
        }

    }

    private String saveGSONSchema(String jsonFileName, String tableName, ObservableList<NameOfColumn> namesOfColumnsList) {
        String result = "";
        ObjectMapper objectMapper = new ObjectMapper(){{
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addSerializer(StringProperty.class, new JsonSerializer<StringProperty>() {
                @Override
                public void serialize(StringProperty stringProperty, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
                    jsonGenerator.writeObject(stringProperty.get());
                }
            });
            registerModule(simpleModule);
        }};
        try(
                StringWriter sw = new StringWriter();
        )
        {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(sw, namesOfColumnsList);
            StringBuilder sb = new StringBuilder();
            sb.append("{\n\t\""+tableName.toUpperCase()+"\": ").append(sw.toString()).append("\n}");
            Path path = Paths.get(jsonFileName);
            Files.write(path,sb.toString().getBytes());
            result = path.toFile().getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean validateTableNames() {
        Set<NameOfColumn> set = new HashSet<>();
        set.addAll(namesOfColumnsImpl.getNamesOfColumnsList());
        for (NameOfColumn nameOfColumn : set) {
            if (!nameOfColumn.getColumnNameInDB().matches("^[a-zA-Z]+\\w+")){
                return false;
            }
        }
        return true;
    }

    public void actionSearch(ActionEvent actionEvent) {
        txtSearch.clear();
    }

    private boolean fillTableView (File file) throws IOException{
        FileInputStream fis = new FileInputStream(file);
        Workbook wb = new HSSFWorkbook(fis);
        String str;
        Row row0 = getFirstRowData(wb.getSheetAt(0));
        if (!(row0==null)){
            for (Cell cell: row0){
                str = getCellText(cell);
                if (!(str.trim().length()==0)){
                    namesOfColumnsImpl.add(new NameOfColumn(cell.getColumnIndex(),str,str));
                }
            }
            tableNamesOfColumns.setItems(namesOfColumnsImpl.getNamesOfColumnsList());
            btnOpen.setDisable(true);
        } else {
            fis.close();
            wb.close();
            DialogManager.showErrorDialog(resourceBundle.getString("error"),resourceBundle.getString("file_empty"));
            return false;
        }
        fis.close();
        wb.close();
        initSearch();
        return true;
    }
    public static Row getFirstRowData (Sheet sheet){
        Row row0 = null;
        for (Row row : sheet){
            if (row.getFirstCellNum()!=-1){
                row0 = row;
                break;
            }
        }
        return row0;
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
    private static String getCellText(Cell cell){
        String result = "";
        switch (cell.getCellTypeEnum()) {
            case STRING:
                result = cell.getRichStringCellValue().getString();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    result = sdf.format(cell.getDateCellValue());
                } else {
                    result =Double.toString(cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                result =Boolean.toString(cell.getBooleanCellValue());
                break;
            case FORMULA:
                result =cell.getCellFormula();
                break;
            case BLANK:
                break;
            default:
                break;
        }
        return result;
    }


    private boolean nameOfColumnIsSelected(NameOfColumn selectedNameOfColumn){
        if (selectedNameOfColumn == null){
            DialogManager.showErrorDialog(resourceBundle.getString("error"),resourceBundle.getString("select_field"));
            return false;
        }
        return true;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    private static File loadXlsToCsv (File file, Map<Integer, String> columnMap){
        File result = null;
        try {
            result = File.createTempFile("tmp", ".csv");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try(InputStream in = new FileInputStream(file);
            HSSFWorkbook wb = new HSSFWorkbook(in);
            BufferedWriter out = new BufferedWriter(new FileWriter(result));){
            StringBuilder rowForWrite = new StringBuilder();
            for (Map.Entry<Integer, String> entry : columnMap.entrySet()){
                if (!rowForWrite.toString().equals("")) {
                    rowForWrite.append(',');
                }
                rowForWrite.append('"').append(entry.getValue()).append('"');
            }
            out.write(rowForWrite.toString());
            out.newLine();

            Row row = getFirstRowData(wb.getSheetAt(0));
            Sheet sheet = row.getSheet();
            Iterator<Row> iterator = sheet.iterator();
            for (iterator.next(); iterator.hasNext(); ) {
                rowForWrite = new StringBuilder();
                Row currentRow = iterator.next();
                boolean first = true;
                for (Map.Entry<Integer, String> entry : columnMap.entrySet()) {
                    if (!first) {
                        rowForWrite.append(',');
                    }
                    first = false;
                    Cell cell = currentRow.getCell(entry.getKey());
                    if (cell == null) {
                        rowForWrite.append("");
                        continue;
                    }
                    //only txt
                    //rowForWrite.append('"').append(getCellText(cell)).append('"');
                    //-other types
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING: {
                            rowForWrite.append('"').append(cell.getStringCellValue()).append('"');
                            break;
                        }
                        case Cell.CELL_TYPE_NUMERIC: {
                            rowForWrite.append(cell.getNumericCellValue());
                            break;
                        }
                        case Cell.CELL_TYPE_BOOLEAN: {
                            rowForWrite.append('"').append(cell.getBooleanCellValue()).append('"');
                            break;
                        }
                    }
                    //-
                }
                out.write(rowForWrite.toString());
                out.newLine();
            }
            out.flush();
        }catch (IOException e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

}