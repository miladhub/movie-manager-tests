package movie.it;

import junit.framework.TestCase;
import movie.j2ee.ejb.entity.Movie;
import movie.j2ee.interfaces.IMovieManager;
import movie.j2ee.util.ServiceLocator;

import org.apache.log4j.Logger;
import org.dbunit.IDatabaseTester;
import org.dbunit.JndiDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;

/**
 * Test-case del movie manager di tipo {@link IMovieManager}.
 * 
 * @author daneel
 */
public class MovieManagerTest extends TestCase {

    /**
     * Logger.
     */
	Logger logger = Logger.getLogger(MovieManagerTest.class);
	
	/**
	 * Movie manager (da testare).
	 */
	IMovieManager movieManager;
	
	/**
	 * Tester.
	 */
	IDatabaseTester dbTester;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		// Creo il tester reperendo il datasource via JNDI 
		dbTester = new JndiDatabaseTester("java:movieds");
		
		// Creo il dataset, precondizione di ognuno dei test e lo imposto sul tester
		IDataSet dataSet = 
			new XmlDataSet(getClass().getClassLoader().getResourceAsStream("dataset.xml"));
		dbTester.setDataSet(dataSet);		
		
		// Operazione di setup del tester - default: CLEAN_INSERT
		dbTester.onSetup();
		
		// Creo il movie manager
		movieManager = ServiceLocator.getMovieManager();
	}
	
	/**
	 * Testa il comportamento in assenza di un film con un dato nome.
	 */
    public void testNonExistentMovie() {
        Movie movie = movieManager.findByTitle("random");
        assertNull("movie should not exist", movie);
    }

    /**
     * Testa il comportamento con un film esistente.
     */
	public void testScannerDarkly() {
	    Movie movie = movieManager.findByTitle("a scanner darkly");
	    assertNotNull("movie not found!", movie);
	    assertEquals("language should be english", "english", movie.getLanguage());
	    assertEquals("title should be 'a scanner darkly'", "a scanner darkly", movie.getTitle());
	    assertEquals("category should be 'science fiction'", "science fiction", movie.getCategory().getName());
	}	
	
	protected void tearDown() throws Exception {
	    dbTester.onTearDown();
	}
	
}
