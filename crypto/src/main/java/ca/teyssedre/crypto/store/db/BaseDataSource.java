/**
 * ********************************************************************************************************
 * <p/>
 * All rights reserved © 2015  -  Innovative Imaging Technologies  -  www.iitreacts.com
 * This computer program may not be used, copied, distributed, corrected, modified, translated,
 * transmitted or assigned without Innovative Imaging Technologies's prior written authorization.
 * <p/>
 * Created by  :  pteyssedre
 * Date        :  15-10-18
 * Application :  Paranoya .
 * Package     :  ca.teyssedre.crypto.store.db .
 * <p/>
 * ********************************************************************************************************
 */
package ca.teyssedre.crypto.store.db;

public abstract class BaseDataSource {

    public abstract String CreateTableQuery();

    public abstract String UpgradeTableQuery(int oldVersion, int newVersion);
}
