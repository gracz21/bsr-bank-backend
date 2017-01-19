package pl.poznan.put.bsr.bank.utils;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import pl.poznan.put.bsr.bank.models.Counter;

/**
 * Util class responsible for handling connection to database server
 * @author Kamil Walkowiak
 */
public class DataStoreHandlerUtil {
    private static DataStoreHandlerUtil Instance = new DataStoreHandlerUtil();
    private Datastore dataStore;

    /**
     * Initializes database Datastore object
     */
    public void initializeDataStore() {
        final Morphia morphia = new Morphia();
        dataStore = morphia.createDatastore(new MongoClient("localhost", ConstantsUtil.MONGODB_PORT), "bank");
        morphia.mapPackage("pl.poznan.put.bsr.bank.models");
        dataStore.ensureIndexes();

        if (dataStore.getCount(Counter.class) == 0) {
            dataStore.save(new Counter("accountNoCounter"));
        }
    }

    /**
     * Get instance of datastore handler class
     * @return instance of datastore handler class
     */
    public static DataStoreHandlerUtil getInstance() {
        return Instance;
    }

    /**
     * Get database Datastore object
     * @return database Datastore object
     */
    public Datastore getDataStore() {
        return dataStore;
    }
}
