package pl.poznan.put.bsr.bank.utils;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 * @author Kamil Walkowiak
 */
public class DataStoreHandlerUtil {
    private static DataStoreHandlerUtil Instance = new DataStoreHandlerUtil();
    private Datastore dataStore;

    public void initializeDataStore() {
        final Morphia morphia = new Morphia();
        dataStore = morphia.createDatastore(new MongoClient("localhost", 8004), "bank");
        morphia.mapPackage("pl.poznan.put.bsr.bank.models");
        dataStore.ensureIndexes();
    }

    public static DataStoreHandlerUtil getInstance() {
        return Instance;
    }

    public Datastore getDataStore() {
        return dataStore;
    }
}
