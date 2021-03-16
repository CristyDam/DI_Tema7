package AppAgendaJPRO;


import entidades.Provincia;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;


/**
 *
 * @author crist
 */
public class ConsultaProvincias {

    
    public static void main(String[] args) {
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("DI_AppAgendaPU");
        EntityManager em = emf.createEntityManager();
        
        //Lista completa de la s consultas que se han almacenado en la tabla
        Query queryProvincias = em.createNamedQuery("Provincia.findAll");
        
        //Se ejecuta la consulta llamando a getResultList, que retorna un objeto List
        List<Provincia> listProvincias = queryProvincias.getResultList();
        
        //Metodo size retorna el numero de objetos contenidos en la lista
        for(int i=0;i<listProvincias.size();i++)
        { 
            Provincia provincia=listProvincias.get(i); //get retorna retorna el objeto que se encuentra en la posicion que indique el parametro
            System.out.println(provincia.getNombre()); //getNombre retorna el valor almacenado en esa propiedad
        }
       
    }

    
    
}
