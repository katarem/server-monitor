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
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import pgv.proyectofinal.App;
import pgv.proyectofinal.beans.LogBean;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public void procesarClient(String data){

        var id = Integer.parseInt(data.split(";")[0]);
        var existsCliente = clientes.stream().filter(c -> c.getId() == id).findAny();
        var tipoMensaje = data.split(";")[1];
        if(existsCliente.isPresent()) updateClient(existsCliente.get(),data);
        else addClient(data);
    }


    private void updateLogs(){
        logsView.setItems(FXCollections.observableList(logs));
    }



    private void addClient(String data){
       try {
           ServerComponentController cliente = new ServerComponentController();
           var tipoMensaje = data.split(";")[1];
           cliente.setId(Integer.parseInt(data.split(";")[0]));
           cliente.setNumeroCliente(clientes.size()+1);
           cliente.setData(data);
           clientes.add(cliente);
           if(tipoMensaje.equals("ALERTA")){
               var mensajeAlerta = data.split(";")[2];
               Platform.runLater(() -> {
                   logs.add("[ALERTA] " + formatter.format(LocalDateTime.now()) + " Servidor " + cliente.getNumeroCliente() + ": " + data);
                   updateLogs();
                   App.showAlerta("ALERTA EN SERVIDOR " + cliente.getNumeroCliente() + ": ", mensajeAlerta);
               });
           }
           else{
               Platform.runLater(() -> {
                   logs.add("[AGREGADO] "  + formatter.format(LocalDateTime.now()) + " Servidor " + cliente.getNumeroCliente() + ": " + data);
                   updateLogs();
                   this.servidoresContainer.getPanes().add(cliente.getView());
               });
           }
       }catch (Exception e){
           log.error(e.getLocalizedMessage());
       }

    }
    private void updateClient(ServerComponentController client, String data){
        var tipoMensaje = data.split(";")[1];
        if(tipoMensaje.equals("ALERTA")){
            var mensajeAlerta = data.split(";")[2];
            Platform.runLater(() -> {
                logs.add("[ALERTA] " + formatter.format(LocalDateTime.now()) + " Servidor " + client.getNumeroCliente() + ": " + data);
                updateLogs();
                App.showAlerta("ALERTA EN SERVIDOR " + client.getNumeroCliente() + ": ", mensajeAlerta);
            });
        }
        else {
            Platform.runLater(() -> {
                logs.add("[ACTUALIZADO] " + formatter.format(LocalDateTime.now()) + " Servidor " + client.getNumeroCliente() + ": " + data);
                updateLogs();
                client.setData(data);
            });
        }
    }



    @FXML
    private void generarInforme() {
        try {
            JasperReport report = JasperCompileManager.compileReport(getClass().getResourceAsStream("/logsReport.jrxml"));
            Map<String, Object> parameters = new HashMap<>();

            var logsHastaInforme = logs;

            ArrayList<LogBean> beans = new ArrayList<>();
            logsHastaInforme.forEach(l -> beans.add(new LogBean(l)));

            var timestamp = formatter.format(LocalDateTime.now());
            var numeroServidores = clientes.size();
            parameters.put("today.date",timestamp);
            parameters.put("server.number",numeroServidores);

            JasperPrint print = JasperFillManager.fillReport(report, parameters, new JRBeanCollectionDataSource(beans));

            File pdfPath = new File("pdf");
            if(!pdfPath.exists()) pdfPath.mkdir();

            var reportPDF = "pdf/logs_" + System.currentTimeMillis() + ".pdf";

            JasperExportManager.exportReportToPdfFile(print,reportPDF);

            Desktop.getDesktop().open(new File(reportPDF));

        } catch(JRException | IOException e){
            log.error(e.getLocalizedMessage());
        }


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
