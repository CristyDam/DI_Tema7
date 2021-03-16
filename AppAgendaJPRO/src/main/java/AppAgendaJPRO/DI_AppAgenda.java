
package AppAgendaJPRO;

import entidades.Provincia;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author crist
 */
public class DI_AppAgenda {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //Iniciamos la conexion
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("DI_AppAgendaPU");
        EntityManager em = emf.createEntityManager();
        
        //Creamos la clase provincia para agregar una nueva a la base de datos
        Provincia provinciaSevilla=new Provincia();
        provinciaSevilla.setNombre("Sevilla");
        
        //para iniciar las transacciones
        em.getTransaction().begin();
        em.persist(provinciaSevilla);
        em.getTransaction().commit();
        
        Provincia provinciaCadiz=new Provincia();
        provinciaCadiz.setNombre("Cadiz");

        em.getTransaction().begin();
        em.persist(provinciaCadiz);
        em.getTransaction().commit(); //commit para guardar los cambios
        
        //cerramos las conexiones
        em.close();
        emf.close();
        
        try{
            DriverManager.getConnection("jdbc:derby:BDAgenda;shutdown=true");
            } catch (SQLException ex){
        }
    }
    
}
