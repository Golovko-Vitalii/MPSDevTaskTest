package Interfaces.Impl;

import Interfaces.NamesOfColumns;
import Objects.NameOfColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CollectionNamesOfColumns implements NamesOfColumns{
    private ObservableList<NameOfColumn>namesOfColumns = FXCollections.observableArrayList();
    public void add(NameOfColumn nameOfColumn) {
        namesOfColumns.add(nameOfColumn);
    }

    public void edit(NameOfColumn nameOfColumn) {

    }

    public ObservableList<NameOfColumn> getNamesOfColumnsList() {
        return namesOfColumns;
    }

}
