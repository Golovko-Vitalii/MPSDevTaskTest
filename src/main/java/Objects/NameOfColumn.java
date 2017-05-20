package Objects;

import javafx.beans.property.SimpleStringProperty;

public class NameOfColumn {

    private int id;
    private SimpleStringProperty columnNameInExcel = new SimpleStringProperty("");
    private SimpleStringProperty columnNameInDB = new SimpleStringProperty("");

    public NameOfColumn() {
    }

    public NameOfColumn(int id, String columnNameInExcel, String columnNameInDB) {
        this.id = id;
        this.columnNameInExcel = new SimpleStringProperty(columnNameInExcel);
        this.columnNameInDB = new SimpleStringProperty(columnNameInDB);
    }

    public String getColumnNameInExcel() {
        return columnNameInExcel.get();
    }

    public SimpleStringProperty columnNameInExcelProperty() {
        return columnNameInExcel;
    }

    public void setColumnNameInExcel(String columnNameInExcel) {
        this.columnNameInExcel.set(columnNameInExcel);
    }

    public String getColumnNameInDB() {
        return columnNameInDB.get();
    }

    public SimpleStringProperty columnNameInDBProperty() {
        return columnNameInDB;
    }

    public void setColumnNameInDB(String columnNameInDB) {
        this.columnNameInDB.set(columnNameInDB);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
