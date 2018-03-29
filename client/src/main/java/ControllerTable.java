import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ControllerTable {
    private ObservableList<Data> data = FXCollections.observableArrayList();

    @FXML
    private TableView<Data> tableData;
    @FXML
    private TableColumn<Data, String> nameColumn;
    @FXML
    private TableColumn<Data, String> sizeColumn;
    @FXML
    private TableColumn<Data, String> dateColumn;

    @FXML
    private void initialize()   {
        initData();

        nameColumn.setCellValueFactory(new PropertyValueFactory<Data, String>("name"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<Data, String>("size"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<Data, String>("date"));

        tableData.setItems(data);
    }

    private void initData() {
        data.add(new Data("file1", "150KBytes", "19.10.2018"));
        data.add(new Data("file2", "250KBytes", "19.09.2018"));
        data.add(new Data("file3", "350KBytes", "19.08.2018"));
    }
}
