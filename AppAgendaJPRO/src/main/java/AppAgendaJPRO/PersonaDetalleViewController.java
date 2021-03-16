
package AppAgendaJPRO;

import entidades.Persona;
import entidades.Provincia;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.RollbackException;

/**
 * FXML Controller class
 *
 * @author crist
 */
public class PersonaDetalleViewController implements Initializable {
    
    /*Añadimos la propiedad de tipo Pane, 
    para pasar el objeto de una clase a otra*/
    private Pane rootAgendaView;
    
    //Propiedades
    private TableView tableViewPrevio; 
    private Persona persona; //La persona que haya seleccionado el usuario
    private EntityManager entityManager;//Gestiona las operaciones con la base de dato 
    private boolean nuevaPersona;//para saber si es nuevo(boton nuevo)o un objeto que existia y se va a editar(boton editar)
    //declaramos las constantes para asociar cada estado con su caracter
    public static final char CASADO='C'; 
    public static final char SOLTERO='S'; 
    public static final char VIUDO='V';
    //constante que almacene el nombre de la carpeta donde estaran las imagenes
    public static final String CARPETA_FOTOS="src/appagenda/Fotos";
    
    /*Metodo set de rootAgendaView
    para poder asignarle desde la otra clase controladora el objeto correspondiente*/
    public void setRootAgendaView(Pane rootAgendaView){ 
        this.rootAgendaView = rootAgendaView; 
    }
    
    /*Metodo set de TableViewPrevio para que cualquier otra clase 
    pueda asignal el TableView a esta clase de detalle*/
    public void setTableViewPrevio(TableView tableViewPrevio){ 
        this.tableViewPrevio=tableViewPrevio; 
    }
    
    //Metodo para asignar el objeto persona, el EntityManager y la propiedad para saber si es una nueva persona
    public void setPersona(EntityManager entityManager, Persona persona, Boolean nuevaPersona){ 
        this.entityManager = entityManager;
        
        //Se inicia la transaccion con la base de datos en el momento q se use el metodo
        entityManager.getTransaction().begin();
        /*si la persona existe se toma desde la base de datos se toma entityManager.find()
        por si el usuario cancela la edicion se pueda recuperar los datos originales con un rollback*/
        if (!nuevaPersona){ 
            this.persona=entityManager.find(Persona.class,persona.getId());
   } else { 
            this.persona=persona;
   } 
   this.nuevaPersona=nuevaPersona; 
}
    
    
    private AnchorPane rootPersonaDetalleView;
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellidos;
    @FXML
    private TextField textFieldTelefono;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldNumHijos;
    @FXML
    private TextField textFieldSalario;
    @FXML
    private CheckBox checkBoxJubilado;
    @FXML
    private ComboBox<Provincia> comboBoxProvincia;
    @FXML
    private RadioButton radioButtonSoltero;
    @FXML
    private RadioButton radioButtonCasado;
    @FXML
    private RadioButton radioButtonViudo;
    @FXML
    private DatePicker datePickerFechaNacimiento;
    @FXML
    private ImageView imageViewFoto;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        
        //Variable booleana que almacenara si los datos no son correctos
        boolean errorFormato = false;
        
        //StackPane rootMain = (StackPane) rootPersonaDetalleView.getScene().getRoot(); 
        //rootMain.getChildren().remove(rootPersonaDetalleView);
        
        //rootAgendaView.setVisible(true);
        
        /*sentencias para actualizar las propiedades del Objeto persona obteniendo la informacion de los controles de la ventana
        y se almacena en la base de datos si se esta creando una nueva persona se inserta 
        y si existía se actualiza*/
        persona.setNombre(textFieldNombre.getText()); 
        persona.setApellidos(textFieldApellidos.getText()); 
        persona.setTelefono(textFieldTelefono.getText()); 
        persona.setEmail(textFieldEmail.getText());
        
        //Datos numericos
        /*el usuario inserta el numero de hijos en un TextField,
        se debe transformar el valor recogido de tipo String a Short(Short.valueOf()
        si el usuario introduce caracteres no numericos se produce la excepcion(NumberFormatExeption) al convertir
        mostrando el mensaje  y asignando true a la vble errorFormato para q no se almacene,
        se mantendra el foco en el mismo TextField con el metodo requestFocus*/
        if (!textFieldNumHijos.getText().isEmpty()) {
            try {
                persona.setNumHijos(Short.valueOf(textFieldNumHijos.getText()));
                
            } catch (NumberFormatException e) {errorFormato = true;
            
                Alert alert = new Alert(AlertType.INFORMATION, "Número de hijos no válido"); 
                alert.showAndWait(); 
                textFieldNumHijos.requestFocus();
            }
        }
        
        /*Asigna valores numericos con decimales obtenidos en el TextField
        usando BigDecimal.valueOf()para la conversion ya que la propiedad salario es de tipo BigDecimal
        y en la BD es decimal*/
        if (!textFieldSalario.getText().isEmpty()){ 
            try { 
                persona.setSalario(BigDecimal.valueOf(Double.valueOf(textFieldSalario.getText()).doubleValue()));
                
            } catch(NumberFormatException ex) { errorFormato = true; 
            
            Alert alert = new Alert(AlertType.INFORMATION, "Salario no válido"); 
            alert.showAndWait(); textFieldSalario.requestFocus(); } 
        }
        
        //Datos booleanos
        /*control de tipo CheckBox en la vista, con el metodo isSelected que retorna un booleano
        true-->seleccionado
        false-->sin seleccionar
        se usa directamente ese valor retornado para asignarselo propiedad jubilado de persona*/
        persona.setJubilado(checkBoxJubilado.isSelected());
        
        //Valores de opcion Multiple
        /*Estado civil almacena un caracter que indica el valor q corresponde a las constantes
        incluidas en el radioButton para cada estado, hay q comprobar cual esta seleccionada*/
        if (radioButtonCasado.isSelected()){ 
            persona.setEstadoCivil(CASADO); 
        } else if (radioButtonSoltero.isSelected()){ 
            persona.setEstadoCivil(SOLTERO); 
        } else if (radioButtonViudo.isSelected()){ 
            persona.setEstadoCivil(VIUDO); }
        
        //Fecha
        /*Convertir desde DatePicker a Date*/
        if (datePickerFechaNacimiento.getValue() != null){ 
            LocalDate localDate = datePickerFechaNacimiento.getValue(); 
            ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault()); 
            Instant instant = zonedDateTime.toInstant(); 
            Date date = Date.from(instant); 
            persona.setFechaNacimiento(date); 
        } else { 
            persona.setFechaNacimiento(null); 
        }
        
        //Objetos de tabla relacionada
        /*El objeto seleccionado en el ComboBox se asigna directamente
        getValue() retorna dicho objetoque se asigna a la propiedad provincia*/
        if (comboBoxProvincia.getValue() != null){ 
            persona.setProvincia(comboBoxProvincia.getValue());
        } else { 
            Alert alert = new Alert(AlertType.INFORMATION,"Debe indicar una provincia"); 
            alert.showAndWait(); 
            errorFormato = true; 
        }
        
        
         //pasamos los datos que recogemos por pantalla a la BD
        if (!errorFormato) {    //Los datos introducidos son correctos
            try {
                //guardar el objeto en la BD
                if (nuevaPersona) { 
                    entityManager.persist(persona);
                }else{
                    entityManager.merge(persona);
                }
                entityManager.getTransaction().commit();
            } catch (RollbackException e) { //Los datos introducidos no cumplen los requisitos de la base de datos
                Alert alert = new Alert(AlertType.INFORMATION, "No se han podido guardar los datos en la Base de Datos. Compruebe que los datos cumplen los requisitos");
                alert.setContentText(e.getLocalizedMessage());
                alert.showAndWait();
            }
            
        }
        
        /*Actualiza los nuevos datos en el TableView
        si es una nueva persona se selecciona la ultima fila del TableView
        y si se estaba editando se debe volver a seleccionea la linea en la que se encontraba dentro del TableView*/
        int numFilaSeleccionada; 
        if (nuevaPersona){ 
            tableViewPrevio.getItems().add(persona); 
            numFilaSeleccionada = tableViewPrevio.getItems().size()- 1; 
            tableViewPrevio.getSelectionModel().select(numFilaSeleccionada); 
            tableViewPrevio.scrollTo(numFilaSeleccionada); 
        } else { 
            numFilaSeleccionada= tableViewPrevio.getSelectionModel().getSelectedIndex(); 
            tableViewPrevio.getItems().set(numFilaSeleccionada,persona); 
        } 
        TablePosition pos = new TablePosition(tableViewPrevio, numFilaSeleccionada,null); 
        tableViewPrevio.getFocusModel().focus(pos);
    }
    
    @FXML
    private void onActionButtonCancelar(ActionEvent event) {
        
       StackPane rootMain = (StackPane) rootPersonaDetalleView.getScene().getRoot(); 
       rootMain.getChildren().remove(rootPersonaDetalleView);
       
       rootAgendaView.setVisible(true);  
       
       entityManager.getTransaction().rollback(); 
       
       int numFilaSeleccionada = tableViewPrevio.getSelectionModel().getSelectedIndex(); 
       TablePosition pos = new TablePosition(tableViewPrevio, numFilaSeleccionada,null); 
       tableViewPrevio.getFocusModel().focus(pos); tableViewPrevio.requestFocus();
    }
    
    //Imagen
    /*Para cambiar la imagen asociada a la propiedad foto de persona con el boton examinar se puede 
    buscar una imagen en el equipo
    */
    @FXML private void onActionButtonExaminar(ActionEvent event){
        
        File carpetaFotos = new File(CARPETA_FOTOS);
        if (!carpetaFotos.exists()){ 
        carpetaFotos.mkdir();
        }
        FileChooser fileChooser = new FileChooser(); 
        fileChooser.setTitle("Seleccionar imagen"); 
        fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("Imágenes (jpg, png)", "*.jpg", "*.png"), 
            new FileChooser.ExtensionFilter("Todos los archivos","*.*")); 
        File file = fileChooser.showOpenDialog( rootPersonaDetalleView.getScene().getWindow()); 
        if (file != null){ 
        try {
            Files.copy(file.toPath(),new File(CARPETA_FOTOS+ "/"+file.getName()).toPath()); 
            persona.setFoto(file.getName()); 
            Image image = new Image(file.toURI().toString()); 
            imageViewFoto.setImage(image);
            
        } catch (FileAlreadyExistsException ex){ Alert alert = new Alert(AlertType.WARNING,"Nombre de archivo duplicado"); 
            alert.showAndWait(); 
        } catch (IOException ex){ Alert alert = new Alert(AlertType.WARNING,"No se ha podido guardar la imagen"); 
            alert.showAndWait();
        } 
    } 
}
    

    void mostrarDatos() {
        
       
        textFieldNombre.setText(persona.getNombre()); 
        textFieldApellidos.setText(persona.getApellidos()); 
        textFieldTelefono.setText(persona.getTelefono()); 
        textFieldEmail.setText(persona.getEmail()); 

        // Implementacion del codigo para los controles
        if (persona.getNumHijos() != null){ 
            textFieldNumHijos.setText(persona.getNumHijos().toString()); //parametro tipo String
        } 
        if (persona.getSalario() != null){ 
            textFieldSalario.setText(persona.getSalario().toString()); 
        }
        
        if (persona.getJubilado() != null){ 
            checkBoxJubilado.setSelected(persona.getJubilado()); //setSelected lleva un parametro de tipo boolean
            
        if (persona.getEstadoCivil() != null)
        { 
            //comprobar cual de los valores coincide con el almacenado en la propiedad estadoCivil del objeto persona
            switch(persona.getEstadoCivil())    //opcion múltiple
            { 
                case CASADO: 
                    radioButtonCasado.setSelected(true);
                    break;
                case SOLTERO: 
                    radioButtonSoltero.setSelected(true);
                    break;
                case VIUDO: 
                    radioButtonViudo.setSelected(true);
                    break;
            }
        }
        
        if (persona.getFechaNacimiento() != null)
        { 
            //convertir de Date a LocalDate
            Date date=persona.getFechaNacimiento(); 
            Instant instant=date.toInstant(); 
            ZonedDateTime zdt=instant.atZone(ZoneId.systemDefault()); 
            LocalDate localDate=zdt.toLocalDate(); 
            datePickerFechaNacimiento.setValue(localDate); 
        }
        
        /*para que la lista contenga todas las provincias almacenadas en la base de datos
        se hace una consulta y se asigna a la lista desplegable*/
        
        Query queryProvinciaFindAll= entityManager.createNamedQuery("Provincia.findAll"); 
        List listProvincia = queryProvinciaFindAll.getResultList(); 
        comboBoxProvincia.setItems(FXCollections.observableList(listProvincia));
        
        //si el objeto persona tiene asignada una provincia se tiene que mostrar en el comboBoxProvincia
        if (persona.getProvincia() != null)
        { 
            comboBoxProvincia.setValue(persona.getProvincia()); 
        }
        /*Asignar al ComboBox un CellFactory en cuyo metodo updateItem 
        se debe asignar con un setText()el String q tiene q aparecer por cada objeto de la lista ComboBox*/  
        comboBoxProvincia.setCellFactory( (ListView<Provincia> l)-> new ListCell<Provincia>()
        {
            protected void updateItem(Provincia provincia, Boolean empty)
            { 
                super.updateItem(provincia, empty); 
                if (provincia == null || empty){ setText(""); 
                } 
                else 
                { setText(provincia.getCodigo()+"-"+provincia.getNombre()); 
                } 
            } 
        });
        
        comboBoxProvincia.setConverter(new StringConverter<Provincia>(){ 
                @Override
                public String toString(Provincia provincia){ 
                    if (provincia == null){
                 return null;
                } else {
            return provincia.getCodigo()+"-"+provincia.getNombre();
            } 
                }
            @Override
                public Provincia fromString(String userId){
                return null;
                    }
                });
        
        if (persona.getFoto() != null)
        { 
            String imageFileName=persona.getFoto(); 
            File file = new File(CARPETA_FOTOS+"/"+imageFileName); 
            if (file.exists())
            { 
                Image image = new Image(file.toURI().toString()); 
                imageViewFoto.setImage(image); 
            } else { 
                Alert alert=new Alert(AlertType.INFORMATION,"No se encuentra la imagen en "+file.toURI().toString()); 
                alert.showAndWait(); 
            } 
        }
        }  
        
    }
    
    @FXML 
    private void onActionSuprimirFoto(ActionEvent event){ 
        Alert alert = new Alert(AlertType.CONFIRMATION); 
        alert.setTitle("Confirmar supresión de imagen"); 
        alert.setHeaderText("¿Desea SUPRIMIR el archivo asociado a la imagen, \n"+ "quitar la foto pero MANTENER el archivo, \no CANCELAR la operación?"); alert.setContentText("Elija la opción deseada:"); 
        ButtonType buttonTypeEliminar = new ButtonType("Suprimir"); 
        ButtonType buttonTypeMantener = new ButtonType("Mantener"); 
        ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE); 
        alert.getButtonTypes().setAll(buttonTypeEliminar, buttonTypeMantener, buttonTypeCancel); 
        Optional<ButtonType> result = alert.showAndWait(); 
        if (result.get() == buttonTypeEliminar){ 
            String imageFileName = persona.getFoto(); 
            File file = new File(CARPETA_FOTOS + "/" + imageFileName); 
            if (file.exists()) { 
                file.delete(); 
            } 
            persona.setFoto(null); 
            imageViewFoto.setImage(null); 
        } else if (result.get() == buttonTypeMantener) {
            persona.setFoto(null); 
            imageViewFoto.setImage(null); 
        } 
    }
    
}
