/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Pierre Teyssedre
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ca.teyssedre.crypto.store.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CryptoStorageHelper extends SQLiteOpenHelper {

    public static final String COLUMN_ID = "_id";
    public static final String STORAGE_NAME = "crypto_storage.db";
    public static final int STORAGE_VERSION = 1;

    private List<BaseDataSource> dataSources;

    private final Object lock;

    //region Singleton
    public static CryptoStorageHelper instance;

    /**
     * Getter of the singleton {@code instance}, if it's the first call the instance will be
     * instantiate with the private constructor using the {@code context} parameter.
     *
     * @param context {@link Context} use to open or create the database.
     * @return {@link CryptoStorageHelper} singleton instance.
     */
    public static CryptoStorageHelper getInstance(Context context) {
        if (instance == null) {
            instance = new CryptoStorageHelper(context);
        }
        return instance;
    }

    /**
     * Shorter instance fetcher.
     *
     * @return current {@code instance} of {@link CryptoStorageHelper}.
     */
    public static CryptoStorageHelper getInstance() {
        if (instance == null) {
            throw new RuntimeException("No valid Context has been pass to CryptoStorageHelper");
        }
        return instance;
    }

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     */
    private CryptoStorageHelper(Context context) {
        super(context, STORAGE_NAME, null, STORAGE_VERSION);
        dataSources = new ArrayList<>();
        lock = new Object();
    }
    //endregion

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        synchronized (lock) {
            for (BaseDataSource ds : dataSources) {
                db.execSQL(ds.CreateTableQuery());
            }
        }
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        synchronized (lock) {
            for (BaseDataSource ds : dataSources) {
                db.execSQL(ds.UpgradeTableQuery(oldVersion, newVersion));
            }
        }
    }

    /**
     * Registration of {@link BaseDataSource} instance.
     *
     * @param dataSource {@link BaseDataSource} instance to add to the {@code dataSources} list.
     */
    public void RegisterDataSource(BaseDataSource dataSource) {
        synchronized (lock) {
            dataSources.add(dataSource);
        }
    }
}
