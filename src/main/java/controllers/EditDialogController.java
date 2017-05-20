package controllers;

import Objects.NameOfColumn;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.DialogManager;

import java.net.URL;
import java.util.ResourceBundle;

public class EditDialogController implements Initializable {
    @FXML
    private Label labelNE;
    @FXML
    private Label labelNT;
    @FXML
    private Button btnCancel;
    @FXML
    private TextField txtNameInExcel;
    @FXML
    private TextField txtNameInDB;
    private NameOfColumn nameOfColumn;
    private ResourceBundle resourceBundle;

    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
    }
    public void changeLang (ResourceBundle resourceBundle){
        this.resourceBundle = resourceBundle;
        labelNE.setText(resourceBundle.getString("name_in_excel"));
        labelNT.setText(resourceBundle.getString("name_in_table"));
        btnCancel.setText(resourceBundle.getString("cancel"));
    }

    public void setNameOfColumn(NameOfColumn nameOfColumn){
        if (nameOfColumn == null){
            return;
        }
        this.nameOfColumn = nameOfColumn;
        txtNameInExcel.setText(nameOfColumn.getColumnNameInExcel());
        txtNameInDB.setText(nameOfColumn.getColumnNameInDB());
    }
    public NameOfColumn getNameOfColumn(){return nameOfColumn;}

    private boolean checkValues(){
        if (txtNameInDB.getText().trim().length()==0){
            DialogManager.showErrorDialog(resourceBundle.getString("error"),
                    resourceBundle.getString("fill_field"));
            return false;
        }
        return true;
    }

    public void actionSave(ActionEvent actionEvent) {
        if (!checkValues()){
            return;
        }
        nameOfColumn.setColumnNameInDB(txtNameInDB.getText());
        actionClose(actionEvent);
    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node)actionEvent.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.hide();
    }
}
