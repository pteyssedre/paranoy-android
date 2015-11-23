/**
 * ********************************************************************************************************
 * <p/>
 * All rights reserved © 2015  -  Innovative Imaging Technologies  -  www.iitreacts.com
 * This computer program may not be used, copied, distributed, corrected, modified, translated,
 * transmitted or assigned without Innovative Imaging Technologies's prior written authorization.
 * <p/>
 * Created by  :  pteyssedre
 * Date        :  15-11-05
 * Application :  Paranoya .
 * Package     :  ca.teyssedre.paranoya.store .
 * <p/>
 * ********************************************************************************************************
 */
package ca.teyssedre.paranoya.store.sources;

import android.database.sqlite.SQLiteDatabase;

public interface IDBSource {

    void onCreate(SQLiteDatabase db);

    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
