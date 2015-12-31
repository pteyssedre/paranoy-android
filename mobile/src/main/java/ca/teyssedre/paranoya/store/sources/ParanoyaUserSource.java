/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015. Pierre Teyssedre
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

package ca.teyssedre.paranoya.store.sources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ca.teyssedre.paranoya.messaging.data.KeyRelation;
import ca.teyssedre.paranoya.messaging.data.User;
import ca.teyssedre.paranoya.store.ParanoyaDBHelper;

public class ParanoyaUserSource extends DBSource {

    private static final String TAG = "ParanoyaUserSource";
    private static final String USERS_STORE_DB_NAME = "users.db";

    //<editor-fold desc="USER TABLE">
    private static final String USERS_TABLE_NAME = "paranoyaUser";
    private static final String USER_ID = "userId";
    private static final String USER_HASH = "hashKey";
    private static final String USER_AVATAR = "avatar";
    private static final String USER_PSEUDO = "pseudo";
    private static final String USER_DESCRIPTION = "description";
    private static final String USER_TYPE = "type";
    private static final String USER_RELAY = "relayTo";
    private static final String[] ALL_USER_COLUMNS = {USER_ID, USER_HASH, USER_AVATAR, USER_PSEUDO, USER_DESCRIPTION, USER_TYPE, USER_RELAY};

    private static final String CREATE_USER_TABLE = "CREATE TABLE "
            + USERS_TABLE_NAME + " (" + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + USER_HASH + " TEXT, "
            + USER_AVATAR + " TEXT, "
            + USER_PSEUDO + " TEXT, "
            + USER_DESCRIPTION + " TEXT, "
            + USER_TYPE + " INTEGER, "
            + USER_RELAY + " INTEGER "
            + ");";
    //</editor-fold>

    //<editor-fold desc="RELATION TABLE">
    private static final String RELATION_KEY_TABLE_NAME = "relatedKey";
    private static final String RELATION_ID = "relayId";
    private static final String RELATION_KEY_LINK = "keyId";
    private static final String RELATION_DESCRIPTION = "description";
    private static final String RELATION_TYPE = "type";
    private static final String[] ALL_RELATION_COLUMNS = {RELATION_ID, USER_ID, RELATION_KEY_LINK, RELATION_DESCRIPTION, RELATION_TYPE};

    private static final String CREATE_RELATION_TABLE = "CREATE TABLE "
            + RELATION_KEY_TABLE_NAME + " (" + RELATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + USER_ID + " INTEGER, "
            + RELATION_KEY_LINK + " INTEGER, "
            + RELATION_TYPE + " INTEGER, "
            + RELATION_DESCRIPTION + " TEXT "
            + ");";
    //</editor-fold>

    private final ParanoyaDBHelper dbHelper;
    private SQLiteDatabase database;

    //<editor-fold desc="Singleton Instance">
    private static ParanoyaUserSource instance;

    private ParanoyaUserSource(Context context) {
        dbHelper = new ParanoyaDBHelper(context, USERS_STORE_DB_NAME, 1, this);
    }

    public void initialization() {
        try {
            open();
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ParanoyaUserSource getInstance(Context context) {
        if (instance == null) {
            instance = new ParanoyaUserSource(context);
            instance.initialization();
        }
        return instance;
    }

    public static ParanoyaUserSource getInstance() {
        if (instance == null) {
            throw new IllegalStateException("No Context provide");
        }
        return instance;
    }
    //</editor-fold>

    //<editor-fold desc="DBSource Implementation">
    @Override
    public String dbName() {
        return USERS_STORE_DB_NAME;
    }

    @Override
    public int dbVersion() {
        return 1;
    }
    //</editor-fold>

    //<editor-fold desc="IDBSource Implementation">
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_RELATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    //</editor-fold>

    //<editor-fold desc="Private Methods">
    private void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    private void close() {
        if (database != null) {
            database.close();
            database = null;
        }
        dbHelper.close();
    }

    //<editor-fold desc="Converters">
    private ContentValues keyRelationToContentValues(KeyRelation keyRelation) {
        ContentValues values = new ContentValues();
        values.put(USER_ID, keyRelation.getUserId());
        values.put(RELATION_KEY_LINK, keyRelation.getKeyId());
        values.put(RELATION_DESCRIPTION, keyRelation.getDescription());
        values.put(RELATION_TYPE, keyRelation.getType());
        return values;
    }

    private ContentValues userToContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(USER_HASH, DatabaseUtils.sqlEscapeString(user.getHash()));
        values.put(USER_AVATAR, user.getAvatarUrl());
        values.put(USER_PSEUDO, user.getPseudo());
        values.put(USER_DESCRIPTION, user.getMessage());
        values.put(USER_TYPE, user.getType());
        values.put(USER_RELAY, user.getRelayId());
        return values;
    }

    private User cursorToUser(Cursor cursor) {
        if (cursor == null || cursor.isNull(0)) {
            return null;
        }
        User user = null;
        try {
            user = new User(cursor.getInt(0),
                    cursor.getString(1).replaceAll("'", ""),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getLong(6));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    private KeyRelation cursorToKeyRelation(Cursor cursor) {
        if (cursor == null || cursor.isNull(0)) {
            return null;
        }
        KeyRelation keyRelation = null;
        try {
            keyRelation = new KeyRelation(cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyRelation;
    }
    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="Public Methods">

    /**
     * @param userId {@link Long} unique identifier of the user.
     */
    public void getContactsList(long userId) {
        try {
            open();
            String query = "SELECT * FROM " + USERS_TABLE_NAME + " u INNER JOIN " + RELATION_KEY_TABLE_NAME + " r " +
                    "ON u." + USER_ID + "=" + "r." + USER_ID + " WHERE u." + USER_TYPE + " !=? AND u." + USER_ID + " !=?";
            Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(1), String.valueOf(userId)});
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                System.out.println(cursor);
                cursor.moveToNext();
            }
            cursor.close();
            close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method may not need to be public ... maybe it could be use internally only.
     *
     * @param userId {@link Long} unique Id of the user. (Source Paranoya-Android)
     * @return
     */
    public List<Long> getKeysByUserId(long userId) {
        List<Long> ids = new ArrayList<>();
        try {
            open();
            Cursor cursor = database.query(RELATION_KEY_TABLE_NAME, ALL_RELATION_COLUMNS, USER_ID + " = " + userId, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                KeyRelation keyRelation = cursorToKeyRelation(cursor);
                ids.add(keyRelation.getKeyId());
                cursor.moveToNext();
            }
            cursor.close();
            close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ids;
    }

    /**
     * Accessor to get all the {@link User} instance in the database.
     *
     * @return a {@link List} of {@link User} instances. If a error happen the list will be empty.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            open();
            Cursor cursor = database.query(USERS_TABLE_NAME, ALL_USER_COLUMNS, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                User user = cursorToUser(cursor);
                if (user != null) {
                    users.add(user);
                }
                cursor.moveToNext();
            }
            cursor.close();
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Accessor to get all the {@link KeyRelation} instance in the database.
     *
     * @return a {@link List} of {@link KeyRelation} instances. If a error happen the list will be empty.
     */
    public List<KeyRelation> getAllRelations() {
        List<KeyRelation> keyRelations = new ArrayList<>();
        try {
            open();
            Cursor cursor = database.query(RELATION_KEY_TABLE_NAME, ALL_RELATION_COLUMNS, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                KeyRelation keyRelation = cursorToKeyRelation(cursor);
                if (keyRelation != null) {
                    keyRelations.add(keyRelation);
                }
                cursor.moveToNext();
            }
            cursor.close();
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyRelations;
    }

    /**
     * Accessor to get all the {@link KeyRelation} instance in the database relay to a specific {@link User}
     * base on the {@code userId} parameter.
     *
     * @param userId {@link Long} unique identifier from {@link ParanoyaUserSource}.
     * @return a {@link List} of {@link KeyRelation} instances. If a error happen the list will be empty.
     */
    public List<KeyRelation> getAllKeyRelationByUserId(long userId) {
        List<KeyRelation> keyRelations = new ArrayList<>();
        try {
            open();
            Cursor cursor = database.query(RELATION_KEY_TABLE_NAME, ALL_RELATION_COLUMNS, USER_ID + " = " + userId, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                KeyRelation keyRelation = cursorToKeyRelation(cursor);
                if (keyRelation != null) {
                    keyRelations.add(keyRelation);
                }
                cursor.moveToNext();
            }
            cursor.close();
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyRelations;
    }

    /**
     * Accessor to add a new {@link User} in the storage. The existence of the user will be check
     * before add process to validate the {@link User#getHash()} value is unique in the storage.
     *
     * @param user {@link User} instance to insert.
     * @return The same instance is return in all cases, if the insert was successful the instance
     * will have a value {@link User#getId()} superior to -1.
     */
    public User addUser(User user) {
        if (user != null) {
            if (user.getHash() != null && user.getHash().length() > 0) {
                User u = getUserByHash(user.getHash());
                if (u != null) {
                    Log.e(TAG, "User already exist");
                    return user;
                }
            }
            try {
                open();
                long insertId = database.insert(USERS_TABLE_NAME, null, userToContentValues(user));
                user.setId(insertId);
                close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    /**
     * @param id
     * @return
     */
    public User getUserById(long id) {
        List<User> users = new ArrayList<>();
        try {
            open();
            Cursor cursor = database.query(USERS_TABLE_NAME, ALL_USER_COLUMNS, USER_ID + " = " + id, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                User user = cursorToUser(cursor);
                if (user != null) {
                    users.add(user);
                }
                cursor.moveToNext();
            }
            cursor.close();
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users.size() > 0 ? users.get(0) : null;
    }

    /**
     * Accessor to retrieve an {@link User} instance using the {@link User#getHash()} value.
     *
     * @param hash unique value to identify an user.
     * @return {@link User} instance parse from the storage.
     */
    public User getUserByHash(String hash) {
        List<User> users = new ArrayList<>();
        try {
            open();
            Cursor cursor = database.query(USERS_TABLE_NAME, ALL_USER_COLUMNS, USER_HASH + " = " + DatabaseUtils.sqlEscapeString(hash), null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                User user = cursorToUser(cursor);
                if (user != null) {
                    users.add(user);
                }
                cursor.moveToNext();
            }
            cursor.close();
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users.size() > 0 ? users.get(0) : null;
    }

    /**
     * Accessor to retrieve an {@link User} instance using the {@link User#getType()} value.
     *
     * @param type value to identify all match users.
     * @return {@link List<User>} instance parse from the storage.
     */
    public List<User> getUsersByType(int type) {
        List<User> users = new ArrayList<>();
        try {
            open();
            Cursor cursor = database.query(USERS_TABLE_NAME, ALL_USER_COLUMNS, USER_TYPE + " = " + type, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                User user = cursorToUser(cursor);
                if (user != null) {
                    users.add(user);
                }
                cursor.moveToNext();
            }
            cursor.close();
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Delete an {@link User} from the storage using the
     *
     * @param user
     */
    public void deleteUser(User user) {
        if (user != null && user.getId() > -1) {
            try {
                open();
                database.delete(USERS_TABLE_NAME, USER_ID + " = " + user.getId(), null);
                close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param keyRelation
     * @return
     */
    public KeyRelation addRelation(KeyRelation keyRelation) {
        if (keyRelation == null) {
            return null;
        }
        try {
            open();
            long insertId = database.insert(RELATION_KEY_TABLE_NAME, null, keyRelationToContentValues(keyRelation));
            keyRelation.setId(insertId);
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyRelation;
    }
    //</editor-fold>
}
