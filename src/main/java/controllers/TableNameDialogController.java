package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.DialogManager;

import java.net.URL;
import java.util.ResourceBundle;

public class TableNameDialogController implements Initializable {
    public static String tableName = null;
    @FXML
    private Label labelTN;
    @FXML
    private TextField tableNameEdit;
    private ResourceBundle resourceBundle;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        labelTN.setText(resourceBundle.getString("name_of_table"));
    }

    private boolean checkValues(){
        if (tableNameEdit.getText().trim().length()!=0&&tableNameEdit.getText().matches("^[a-zA-Z]+\\w+")){
            return true;
        } else {
            DialogManager.showErrorDialog(resourceBundle.getString("error"),
                    resourceBundle.getString("fill_field"));
            return false;
        }
    }
    public void actionSaveTN(ActionEvent actionEvent) {
        if (!checkValues()){
            DialogManager.showErrorDialog(resourceBundle.getString("error"),
                    resourceBundle.getString("fill_field"));
        } else{
            tableName = tableNameEdit.getText().trim().toUpperCase();
            actionClose(actionEvent);
        }

    }
    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node)actionEvent.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.hide();
    }

    public void changeLang(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        labelTN.setText(resourceBundle.getString("name_of_table"));
    }
}
