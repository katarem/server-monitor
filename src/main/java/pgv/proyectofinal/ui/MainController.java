package pgv.proyectofinal.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

@Slf4j
public class MainController implements Initializable {

    @FXML
    private BorderPane view;
    @FXML
    private Accordion servidoresContainer;

    @FXML
    private ListView<String> logsView;

    private ArrayList<String> logs;

    private ArrayList<ServerComponentController> clientes;


    public void procesarClient(String data){
        var id = Integer.parseInt(data.split(";")[0]);
        var existsCliente = clientes.stream().filter(c -> c.getId() == id).findAny();
        if(existsCliente.isPresent()) updateClient(existsCliente.get(),data);
        else addClient(data);
    }


    private void updateLogs(){
        logsView.setItems(FXCollections.observableList(logs));
    }



    private void addClient(String data){
       try {
           ServerComponentController cliente = new ServerComponentController();
           cliente.setId(Integer.parseInt(data.split(";")[0]));
           cliente.setNumeroCliente(clientes.size()+1);
           cliente.setData(data);
           clientes.add(cliente);
           Platform.runLater(() -> {
               logs.add("[AGREGADO] Servidor " + cliente.getNumeroCliente() + ": " + data);
               updateLogs();
               this.servidoresContainer.getPanes().add(cliente.getView());
           });
       }catch (Exception e){
           log.error(e.getLocalizedMessage());
       }

    }

    private void updateClient(ServerComponentController client, String data){
        Platform.runLater(() -> {
            logs.add("[ACTUALIZADO] Servidor " + client.getNumeroCliente() + ": " + data);
            updateLogs();
            client.setData(data);
        });
    }



    public MainController(){
        try{
            FXMLLoader l = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            l.setController(this);
            l.load();
        } catch(IOException e){
            log.error(e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clientes = new ArrayList<>();
        logs = new ArrayList<>();
        logsView.setItems(FXCollections.observableList(logs));


    }
    public BorderPane getView() {
        return view;
    }
}
