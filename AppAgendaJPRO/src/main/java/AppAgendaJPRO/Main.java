

package AppAgendaJPRO;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author crist
 */
public class Main extends Application {
    
    private EntityManagerFactory emf;
    private EntityManager em;
    
    @Override
    public void start(Stage primaryStage)throws IOException
    {
         /*Nuevo contenedor de tipo StacKPane que se almacenara en la variable llamada rooMain
        aqui se permite apilar paneles o vistas*/
        StackPane rootMain = new StackPane(); 
        //Hace referencia al archivo FXML cuyo contenido sera cargado como elemento raiz de la ventana
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AgendaView.fxml"));
        
        Pane rootAgendaView=fxmlLoader.load(); 
        rootMain.getChildren().add(rootAgendaView);
        
        /*la llamada al metodo load de fxmlLoader controla la IOEXception
        que se generaria si no existiera el archivo FXML
        en este caso o bien se encierra todo en un bloque try-catch
        o se añade la sentencia throws wn la cabecera*/
        //Parent root=fxmlLoader.load();
        
        /*Conexión a la BD creando los objetos EntityManager y
        EntityManagerFactory*/
        emf=Persistence.createEntityManagerFactory("DI_AppAgendaPU");
        em=emf.createEntityManager();
        
        //Se crea objeto asociado a la clase controlador
        AgendaViewController agendaViewController = (AgendaViewController)fxmlLoader.getController();
        
        // Después de obtener el objeto del controlador y del EntityManager:
        agendaViewController.setEntityManager(em);
        
        //Lamada al metodo 
        agendaViewController.cargarTodasPersonas();
       
        
        Scene scene = new Scene(rootMain, 600, 400);
        primaryStage.setTitle("App Agenda");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception 
    {
        em.close();
        emf.close();
        
        try{
            DriverManager.getConnection("jdbc:hasqldb:hsql;shutdown=true");
            } catch (SQLException ex){
        }
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
