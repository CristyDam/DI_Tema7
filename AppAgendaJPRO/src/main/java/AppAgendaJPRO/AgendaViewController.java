
package AppAgendaJPRO;

import entidades.Persona;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 * FXML Controller class
 *
 * @author crist
 */
public class AgendaViewController implements Initializable 
{
    /*para q la ventana muestre los contenidos en la base de datos
    el controlador debe tener acceso al objeto EntityManager que permite el acceso a los datos
    para ello pasamos el objeto EntityManager a traves de un metodo set en esta clase controladora*/
    private EntityManager entityManager; //propiedad
    private Persona personaSeleccionada;
    
    //Estas propiedades nos indicara que informacion de la base de datos se debe mostrar en cada columna
    @FXML
    private TableView<Persona> tableViewAgenda;
    @FXML
    private TableColumn<Persona,String> columnNombre;
    @FXML
    private TableColumn<Persona,String> columnApellidos;
    @FXML
    private TableColumn<Persona,String> columnEmail;
    @FXML
    private TableColumn<Persona,String> columnProvincia;    //Identificador de la columna para provincia
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellidos;
    @FXML
    private AnchorPane rootAgendaView;
    
    public void setEntityManager(EntityManager entityManager){//metodo
        
        this.entityManager=entityManager;
    }
    
    
    /*Asociar las columnas a propiedades de la clase entidad
    la propiedad de la clase persona se mostrara em la columna columnNombre*/
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        /*con la llamada a cellData.getValue() obtiene el objeto persona de una determinada fila de TableView
        sguido del metodo getProvincia()para obtener el objeto provincia relacionado con el objeto persona
        el metodo getNombre para obtener como String el nombre de la provincia
        comprobacion por si una persona no tiene asociada ninguna provincia (if)
        */
        columnProvincia.setCellValueFactory( cellData->{ SimpleStringProperty property=new SimpleStringProperty(); 
        if (cellData.getValue().getProvincia()!=null){ property.setValue(cellData.getValue().getProvincia().getNombre());
        } 
        return property;
        });
        
        /*para almacenar en pesonaSeleccionada el objeto q se seleccione en el table view
        se ejecuta cada vez que se cambie la seleccion en la lista a traves del metodo addListener
        los listener se ejecutan cuando se produce un evento
        el parametro newValue contiene el nuevo elemento que se almacena en la propiedad personaSeleccionada
        para mostrar los valores de nombre y apellidos en los TexField usamos setText que permite cambiar el texto mostrado en ellos
        los metodos getNombre y getApellidos de la clase persona retornaran los valores y se pasan como texto a los TextField*/
        tableViewAgenda.getSelectionModel().selectedItemProperty().addListener( (observable,oldValue,newValue)->
        { personaSeleccionada=newValue;
        if (personaSeleccionada != null){ textFieldNombre.setText(personaSeleccionada.getNombre()); 
        textFieldApellidos.setText(personaSeleccionada.getApellidos()); 
        } else 
        { textFieldNombre.setText(""); 
        textFieldApellidos.setText(""); 
        }
        });
        
//columnProvincia.setCellValueFactory(new PropertyValueFactory<>("provincia")); //muestra la propiedad provincia del objeto persona que se muestre en cada fila
    } 
    
    /*Pasa la lista de resultados de la base de datos al TableView con el metodo setItems
    se convierte la lista de todo List a tipo ObservableArrayLis, se hace con el metodo FXCollections.observableArrayLis*/
    public void cargarTodasPersonas(){
        
        Query queryPersonaFindAll= entityManager.createNamedQuery("Persona.findAll"); 
        List<Persona> listPersona=queryPersonaFindAll.getResultList(); 
        tableViewAgenda.setItems(FXCollections.observableArrayList(listPersona)); }

    /*Metodo para que se ejecute su contenido cuando el usuario lo accione*/
    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        
        /*Comprueba que haya un registro seleccionado en el TableView
        Se detecta comprobando si en la variable personaSeleccionada se encuentra almacenada
        algun objeto (no se null*/
        if (personaSeleccionada != null){
            
            /*Se le asignan los valores de las ventanas con los metodos set
            se indica como parametros los valores recogidos en los TextField*/
            personaSeleccionada.setNombre(textFieldNombre.getText()); 
            personaSeleccionada.setApellidos(textFieldApellidos.getText());
            
            /*actualizar los valores de la base de datos*/
            entityManager.getTransaction().begin(); 
            entityManager.merge(personaSeleccionada); 
            entityManager.getTransaction().commit();
            
            /*actualizar en TableView los nuevos valores del objeto
            se obtiene el numero de fila seleccionada
            se le vuelve a asignar el mismo objeto a esa fila
            para mostrar los nuevos valores del odjeto*/
            int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex(); 
            tableViewAgenda.getItems().set(numFilaSeleccionada,personaSeleccionada);
            
            /*para que el foco de la ventana vuelva al TableView
            y que el usuario pueda moverse con el teclado
            */
            TablePosition pos = new TablePosition(tableViewAgenda,numFilaSeleccionada,null); 
            tableViewAgenda.getFocusModel().focus(pos); 
            tableViewAgenda.requestFocus();
        }  
       
    }

    @FXML
    private void onActionButtonNuevo(ActionEvent event) {
        try{ 
            // Cargar la vista de detalle 
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PersonaDetalleView.fxml")); 
            Parent rootDetalleView = fxmlLoader.load();
            
            PersonaDetalleViewController personaDetalleViewController =(PersonaDetalleViewController) fxmlLoader.getController();
            personaDetalleViewController.setRootAgendaView(rootAgendaView);
            
            //Intercambio de datos funcionales con el detalle 
            personaDetalleViewController.setTableViewPrevio(tableViewAgenda);
            
            //Para pasar el objeto Persona y EntityManager, pasando un nuevo objeto persona y si es nuevo o no
            personaSeleccionada = new Persona(); 
            personaDetalleViewController.setPersona(entityManager, personaSeleccionada,true);
            
            // Ocultar la vista de la lista 
            rootAgendaView.setVisible(false);
            
            //Añadir la vista detalle al StackPane principal para que se muestre 
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot(); 
            rootMain.getChildren().add(rootDetalleView);
            
            /*para que muestre en la vista de detalle los datos del objeto persona que estemos tratando
            invovamos al metodo mostrarDatos*/
            personaDetalleViewController.mostrarDatos();
            
        } catch (IOException ex){ Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE,null,ex); }
    }

    @FXML
    private void onActionButtonEditar(ActionEvent event) {
         try{ 
            // Cargar la vista de detalle 
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PersonaDetalleView.fxml")); 
            Parent rootDetalleView = fxmlLoader.load();
            
            PersonaDetalleViewController personaDetalleViewController =(PersonaDetalleViewController) fxmlLoader.getController();
            personaDetalleViewController.setRootAgendaView(rootAgendaView);
            
            //Intercambio de datos funcionales con el detalle 
            personaDetalleViewController.setTableViewPrevio(tableViewAgenda);
            
            //Para pasar el objeto EntityManager, el objeto seleccionado y si es nuevo o no
            personaDetalleViewController.setPersona(entityManager, personaSeleccionada,false);
            
            // Ocultar la vista de la lista 
            rootAgendaView.setVisible(false);
            
            //Añadir la vista detalle al StackPane principal para que se muestre 
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot(); 
            rootMain.getChildren().add(rootDetalleView);
            
            /*para que muestre en la vista de detalle los datos del objeto persona que estemos tratando
            invocamos al metodo mostrarDatos*/
            personaDetalleViewController.mostrarDatos();
            
        } catch (IOException ex){ Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE,null,ex); }
        
    }

    @FXML
    private void onActionButtonSuprimir(ActionEvent event) {
        //Ventanas emergentes
        /*Al objeto Alert se le asignan varias propiedades
        titulo, cabecera y mensaje*/
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar"); 
        alert.setHeaderText("¿Desea suprimir el siguiente registro?"); 
        alert.setContentText(personaSeleccionada.getNombre() + " " + personaSeleccionada.getApellidos()); //Incluye nombre y apellidos de la persona seleccionada
        Optional<ButtonType> result = alert.showAndWait();//metodo showAndWait muestra la venatana cuando el usuario pulsa el boton 
        if (result.get() == ButtonType.OK)//si pulsa Aceptar se procede al borrado del objeto personaSeleccionada
        { 
        // Acciones a realizar si el usuario acepta
        entityManager.getTransaction().begin(); 
        entityManager.merge(personaSeleccionada); //si es nuevo el objeto se hace antes de llamar a remove para asegurarse q el EntityManager este gestionando el objeto a eliminar
        entityManager.remove(personaSeleccionada); 
        entityManager.getTransaction().commit(); 
        tableViewAgenda.getItems().remove(personaSeleccionada); 
        tableViewAgenda.getFocusModel().focus(null); 
        tableViewAgenda.requestFocus();//Que la TableView vuelva a tener el foco seleccionando uno de los registros
        
        } else { //si pulsa cancelar hay q dejar seleccionada en el TableView la misma fila
            
        // Acciones a realizar si el usuario cancela 
        int numFilaSeleccionada= tableViewAgenda.getSelectionModel().getSelectedIndex(); 
        tableViewAgenda.getItems().set(numFilaSeleccionada,personaSeleccionada); 
        TablePosition pos = new TablePosition(tableViewAgenda, numFilaSeleccionada,null); 
        tableViewAgenda.getFocusModel().focus(pos); //para q no se quede el foco en el boton suprimir
        tableViewAgenda.requestFocus();
        }
    }

  
    
}
