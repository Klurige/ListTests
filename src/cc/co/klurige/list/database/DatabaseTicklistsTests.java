package cc.co.klurige.list.database;

import java.sql.SQLException;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import cc.co.klurige.list.database.Table.Key;
import cc.co.klurige.list.database.Table.Status;

public class DatabaseTicklistsTests extends AndroidTestCase {

  private DatabaseAdapter mDbAdapter;
  private Context         mCtx;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mCtx = new IsolatedContext(null, this.getContext());
    setContext(mCtx);
    mDbAdapter = DatabaseAdapter.getDatabaseAdapter(mCtx);

    try {
      mDbAdapter.delete();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    mDbAdapter.open();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    mDbAdapter.close();
  }

  private void setupDatabase() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "matlista");
    long listid = DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    DatabaseAdapter.getTicklistsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Sak");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    DatabaseAdapter.getTicklistsTable().create(args);
  }

  public void testPreConditions() {
    assertTrue(mDbAdapter.getDB().isOpen());
  }

  public void testTickListCreate() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "matlista");
    long listid = DatabaseAdapter.getListsTable().create(args);

    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    DatabaseAdapter.getTicklistsTable().create(args);

    Cursor result =
        mDbAdapter.getDB().query(DatabaseAdapter.getTicklistsTable().getTableName(), null, null,
            null,
            null, null, null, null);
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 8, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Name of column 1 is wrong.", "itemid", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 1, result.getLong(2));
    assertEquals("Name of column 2 is wrong.", "listid", result.getColumnName(2));
    assertEquals("Value of column 3 is wrong.", (float) 0.0, result.getFloat(3));
    assertEquals("Name of column 3 is wrong.", "amount", result.getColumnName(3));
    assertEquals("Value of column 4 is wrong.", 1, result.getLong(4));
    assertEquals("Name of column 4 is wrong.", "unitid", result.getColumnName(4));
    assertEquals("Value of column 5 is wrong.", 1, result.getLong(5));
    assertEquals("Name of column 5 is wrong.", "catid", result.getColumnName(5));
    assertEquals("Value of column 6 is wrong.", 0, result.getLong(6));
    assertEquals("Name of column 6 is wrong.", "picked", result.getColumnName(6));
    assertEquals("Value of column 7 is wrong.", 0, result.getInt(7));
    assertEquals("Name of column 7 is wrong.", "status", result.getColumnName(7));
    result.close();
  }

  public void testTicklistDelete() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "matlista");
    long listid = DatabaseAdapter.getListsTable().create(args);

    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    DatabaseAdapter.getTicklistsTable().create(args);
    assertTrue(DatabaseAdapter.getTicklistsTable().delete(1));
    Cursor result =
        mDbAdapter.getDB().query(DatabaseAdapter.getTicklistsTable().getTableName(), null, null,
            null,
            null, null, null, null);
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 8, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Name of column 1 is wrong.", "itemid", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 1, result.getLong(2));
    assertEquals("Name of column 2 is wrong.", "listid", result.getColumnName(2));
    assertEquals("Value of column 3 is wrong.", (float) 0.0, result.getFloat(3));
    assertEquals("Name of column 3 is wrong.", "amount", result.getColumnName(3));
    assertEquals("Value of column 4 is wrong.", 1, result.getLong(4));
    assertEquals("Name of column 4 is wrong.", "unitid", result.getColumnName(4));
    assertEquals("Value of column 5 is wrong.", 1, result.getLong(5));
    assertEquals("Name of column 5 is wrong.", "catid", result.getColumnName(5));
    assertEquals("Value of column 6 is wrong.", 0, result.getLong(6));
    assertEquals("Name of column 6 is wrong.", "picked", result.getColumnName(6));
    assertEquals("Value of column 7 is wrong.", 512, result.getInt(7));
    assertEquals("Name of column 7 is wrong.", "status", result.getColumnName(7));
    result.close();
  }

  public void testTicklistDeleteNonExisting() {
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().delete(2);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Managed to delete non-existent entry.", isSuccess);
  }

  public void testTicklistUndelete() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "matlista");
    long listid = DatabaseAdapter.getListsTable().create(args);

    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    DatabaseAdapter.getTicklistsTable().create(args);
    assertTrue("Entry not deleted.", DatabaseAdapter.getTicklistsTable().delete(1));
    assertTrue("Entry not undeleted.", DatabaseAdapter.getTicklistsTable().undelete(1));
  }

  public void testTicklistFetch() {
    setupDatabase();
    Cursor result = DatabaseAdapter.getTicklistsTable().fetch();
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 8, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of column 1 is wrong.", "itemid", result.getColumnName(1));
    assertEquals("Name of column 2 is wrong.", "listid", result.getColumnName(2));
    assertEquals("Name of column 3 is wrong.", "amount", result.getColumnName(3));
    assertEquals("Name of column 4 is wrong.", "unitid", result.getColumnName(4));
    assertEquals("Name of column 5 is wrong.", "catid", result.getColumnName(5));
    assertEquals("Name of column 6 is wrong.", "picked", result.getColumnName(6));
    assertEquals("Name of column 7 is wrong.", "status", result.getColumnName(7));
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Value of column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Value of column 2 is wrong.", 1, result.getLong(2));
    assertEquals("Value of column 3 is wrong.", (float) 0.0, result.getFloat(3));
    assertEquals("Value of column 4 is wrong.", 1, result.getLong(4));
    assertEquals("Value of column 5 is wrong.", 1, result.getLong(5));
    assertEquals("Value of column 6 is wrong.", 0, result.getLong(6));
    assertEquals("Value of column 7 is wrong.", 0, result.getInt(7));
    assertTrue("Could not advance cursor.", result.moveToNext());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Value of column 1 is wrong.", 2, result.getLong(1));
    assertEquals("Value of column 2 is wrong.", 1, result.getLong(2));
    assertEquals("Value of column 3 is wrong.", (float) 0.0, result.getFloat(3));
    assertEquals("Value of column 4 is wrong.", 1, result.getLong(4));
    assertEquals("Value of column 5 is wrong.", 1, result.getLong(5));
    assertEquals("Value of column 6 is wrong.", 0, result.getLong(6));
    assertEquals("Value of column 7 is wrong.", 0, result.getInt(7));
    result.close();
  }

  public void testTicklistFetchOnName() {
    setupDatabase();
    boolean isSuccess = false;
    try {
      Cursor result = DatabaseAdapter.getTicklistsTable().fetch("Pryl");
      result.close();
    } catch (IllegalAccessError e) {
      isSuccess = true;
    }
    assertTrue("Shouldn't manage to fetch item on name.", isSuccess);
  }

  public void testTicklistFetchOnId() {
    setupDatabase();
    Cursor result = DatabaseAdapter.getTicklistsTable().fetch(1);
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 8, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of column 1 is wrong.", "itemid", result.getColumnName(1));
    assertEquals("Name of column 2 is wrong.", "listid", result.getColumnName(2));
    assertEquals("Name of column 3 is wrong.", "amount", result.getColumnName(3));
    assertEquals("Name of column 4 is wrong.", "unitid", result.getColumnName(4));
    assertEquals("Name of column 5 is wrong.", "catid", result.getColumnName(5));
    assertEquals("Name of column 6 is wrong.", "picked", result.getColumnName(6));
    assertEquals("Name of column 7 is wrong.", "status", result.getColumnName(7));
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Value of column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Value of column 2 is wrong.", 1, result.getLong(2));
    assertEquals("Value of column 3 is wrong.", (float) 0.0, result.getFloat(3));
    assertEquals("Value of column 4 is wrong.", 1, result.getLong(4));
    assertEquals("Value of column 5 is wrong.", 1, result.getLong(5));
    assertEquals("Value of column 6 is wrong.", 0, result.getLong(6));
    assertEquals("Value of column 7 is wrong.", 0, result.getInt(7));
    result.close();
  }

  public void testTicklistFetchAsStrings() {
    setupDatabase();
    Cursor result = DatabaseAdapter.getTicklistsTable().fetchAsStrings(1);
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 5, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Name of column 2 is wrong.", "amount", result.getColumnName(2));
    assertEquals("Name of column 3 is wrong.", "units_name", result.getColumnName(3));
    assertEquals("Name of column 4 is wrong.", "categories_name", result.getColumnName(4));
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Value of column 1 is wrong.", "Pryl", result.getString(1));
    assertEquals("Value of column 2 is wrong.", (float) 0.0, result.getFloat(2));
    assertEquals("Value of column 3 is wrong.", "", result.getString(3));
    assertEquals("Value of column 4 is wrong.", "", result.getString(4));
    result.close();
  }

  public void testTicklistFetchItemsAsStrings() {
    setupDatabase();
    Cursor result = DatabaseAdapter.getTicklistsTable().fetchItemsAsStrings(1);
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 6, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Name of column 2 is wrong.", "amount", result.getColumnName(2));
    assertEquals("Name of column 3 is wrong.", "units_name", result.getColumnName(3));
    assertEquals("Name of column 4 is wrong.", "categories_name", result.getColumnName(4));
    assertEquals("Name of column 5 is wrong.", "ticklist_picked", result.getColumnName(5));
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Value of column 1 is wrong.", "Pryl", result.getString(1));
    assertEquals("Value of column 2 is wrong.", (float) 0.0, result.getFloat(2));
    assertEquals("Value of column 3 is wrong.", "", result.getString(3));
    assertEquals("Value of column 4 is wrong.", "", result.getString(4));
    assertTrue("Could not advance cursor", result.moveToNext());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Value of column 1 is wrong.", "Sak", result.getString(1));
    assertEquals("Value of column 2 is wrong.", (float) 0.0, result.getFloat(2));
    assertEquals("Value of column 3 is wrong.", "", result.getString(3));
    assertEquals("Value of column 4 is wrong.", "", result.getString(4));
    assertEquals("Value of column 4 is wrong.", 0, result.getLong(5));
    result.close();
  }

  public void testTicklistFetchAll() {
    setupDatabase();
    DatabaseAdapter.getTicklistsTable().delete(1);

    Cursor result = DatabaseAdapter.getTicklistsTable().fetchAll(1);
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 8, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of column 1 is wrong.", "itemid", result.getColumnName(1));
    assertEquals("Name of column 2 is wrong.", "listid", result.getColumnName(2));
    assertEquals("Name of column 3 is wrong.", "amount", result.getColumnName(3));
    assertEquals("Name of column 4 is wrong.", "unitid", result.getColumnName(4));
    assertEquals("Name of column 5 is wrong.", "catid", result.getColumnName(5));
    assertEquals("Name of column 6 is wrong.", "picked", result.getColumnName(6));
    assertEquals("Name of column 7 is wrong.", "status", result.getColumnName(7));
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Value of column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Value of column 2 is wrong.", 1, result.getLong(2));
    assertEquals("Value of column 3 is wrong.", (float) 0.0, result.getFloat(3));
    assertEquals("Value of column 4 is wrong.", 1, result.getLong(4));
    assertEquals("Value of column 5 is wrong.", 1, result.getLong(5));
    assertEquals("Value of column 6 is wrong.", 0, result.getLong(6));
    assertEquals("Value of column 7 is wrong.", 512, result.getInt(7));
    result.close();

    result = DatabaseAdapter.getTicklistsTable().fetchAll(2);
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 8, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of column 1 is wrong.", "itemid", result.getColumnName(1));
    assertEquals("Name of column 2 is wrong.", "listid", result.getColumnName(2));
    assertEquals("Name of column 3 is wrong.", "amount", result.getColumnName(3));
    assertEquals("Name of column 4 is wrong.", "unitid", result.getColumnName(4));
    assertEquals("Name of column 5 is wrong.", "catid", result.getColumnName(5));
    assertEquals("Name of column 6 is wrong.", "picked", result.getColumnName(6));
    assertEquals("Name of column 7 is wrong.", "status", result.getColumnName(7));
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Value of column 1 is wrong.", 2, result.getLong(1));
    assertEquals("Value of column 2 is wrong.", 1, result.getLong(2));
    assertEquals("Value of column 3 is wrong.", (float) 0.0, result.getFloat(3));
    assertEquals("Value of column 4 is wrong.", 1, result.getLong(4));
    assertEquals("Value of column 5 is wrong.", 1, result.getLong(5));
    assertEquals("Value of column 6 is wrong.", 0, result.getLong(6));
    assertEquals("Value of column 7 is wrong.", 0, result.getInt(7));
    result.close();
  }

  public void testTicklistFetchStarting() {
    setupDatabase();
    boolean isSuccess = false;
    try {
      Cursor result = DatabaseAdapter.getTicklistsTable().fetch("Pryl");
      result.close();
    } catch (IllegalAccessError e) {
      isSuccess = true;
    }
    assertTrue("Shouldn't manage to fetch item on name.", isSuccess);
  }

  public void testTicklistIsDeleted() {
    setupDatabase();
    DatabaseAdapter.getTicklistsTable().delete(1);
    assertTrue("Entry is not deleted.", DatabaseAdapter.getTicklistsTable().isDeleted(1));
    assertFalse("Entry is deleted.", DatabaseAdapter.getTicklistsTable().isDeleted(2));
  }

  public void testTicklistUpdate() {
    setupDatabase();
    ContentValues args = new ContentValues();
    args.put(Key.AMOUNT, 2.0);
    assertEquals("Entry could not be updated.", 1, DatabaseAdapter.getTicklistsTable().update(1,
        args));
    Cursor result = DatabaseAdapter.getTicklistsTable().fetch(1);
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 8, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of column 1 is wrong.", "itemid", result.getColumnName(1));
    assertEquals("Name of column 2 is wrong.", "listid", result.getColumnName(2));
    assertEquals("Name of column 3 is wrong.", "amount", result.getColumnName(3));
    assertEquals("Name of column 4 is wrong.", "unitid", result.getColumnName(4));
    assertEquals("Name of column 5 is wrong.", "catid", result.getColumnName(5));
    assertEquals("Name of column 6 is wrong.", "picked", result.getColumnName(6));
    assertEquals("Name of column 7 is wrong.", "status", result.getColumnName(7));
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Value of column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Value of column 2 is wrong.", 1, result.getLong(2));
    assertEquals("Value of column 3 is wrong.", (float) 2.0, result.getFloat(3));
    assertEquals("Value of column 4 is wrong.", 1, result.getLong(4));
    assertEquals("Value of column 5 is wrong.", 1, result.getLong(5));
    assertEquals("Value of column 6 is wrong.", 0, result.getLong(6));
    assertEquals("Value of column 7 is wrong.", 0, result.getInt(7));
    result.close();
  }

  public void testTicklistUpdateFailOnStatus() {
    setupDatabase();
    ContentValues args = new ContentValues();
    args.put(Key.AMOUNT, 2.0);
    args.put(Key.STATUS, Status.ERROR);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().update(1, args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Shouldn't be allowed to update status.", isSuccess);
  }

  public void testUnitUpdateFailOnId() {
    setupDatabase();
    ContentValues args = new ContentValues();
    args.put(Key.AMOUNT, 2.0);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().update(5, args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Shouldn't be allowed to update this id.", isSuccess);
  }

  public void testTicklistUpdateFailOnPicked() {
    setupDatabase();
    ContentValues args = new ContentValues();
    args.put(Key.AMOUNT, 2.0);
    Date timestamp = new Date();
    args.put(Key.PICKED, timestamp.getTime());
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().update(1, args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Shouldn't be allowed to update PICKED.", isSuccess);
  }

  public void testTicklistUpdateFailOnName() {
    setupDatabase();
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Test");
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().update(1, args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Shouldn't be allowed to contain NAME.", isSuccess);
  }
}
