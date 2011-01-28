package cc.co.klurige.list.db.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import cc.co.klurige.list.database.DatabaseAdapter;
import cc.co.klurige.list.database.Table.Key;

public class DatabaseTicklistsTests extends AndroidTestCase {

  private DatabaseAdapter mDbAdapter;
  private Context         mCtx;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mCtx = new IsolatedContext(null, this.getContext());
    setContext(mCtx);
    mDbAdapter = DatabaseAdapter.getDatabaseAdapter(mCtx);

    mDbAdapter.open();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    mDbAdapter.close();
    mDbAdapter.delete();
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
    assertEquals("Value of items column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of items column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of items column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Name of items column 1 is wrong.", "itemid", result.getColumnName(1));
    assertEquals("Value of items column 2 is wrong.", 1, result.getLong(2));
    assertEquals("Name of items column 2 is wrong.", "listid", result.getColumnName(2));
    assertEquals("Value of items column 3 is wrong.", (float) 0.0, result.getFloat(3));
    assertEquals("Name of items column 3 is wrong.", "amount", result.getColumnName(3));
    assertEquals("Value of items column 4 is wrong.", 1, result.getLong(4));
    assertEquals("Name of items column 4 is wrong.", "unitid", result.getColumnName(4));
    assertEquals("Value of items column 5 is wrong.", 1, result.getLong(5));
    assertEquals("Name of items column 5 is wrong.", "catid", result.getColumnName(5));
    assertEquals("Value of items column 6 is wrong.", 0, result.getLong(6));
    assertEquals("Name of items column 6 is wrong.", "picked", result.getColumnName(6));
    assertEquals("Value of items column 7 is wrong.", 0, result.getInt(7));
    assertEquals("Name of items column 7 is wrong.", "status", result.getColumnName(7));
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
    assertEquals("Value of items column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of items column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of items column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Name of items column 1 is wrong.", "itemid", result.getColumnName(1));
    assertEquals("Value of items column 2 is wrong.", 1, result.getLong(2));
    assertEquals("Name of items column 2 is wrong.", "listid", result.getColumnName(2));
    assertEquals("Value of items column 3 is wrong.", (float) 0.0, result.getFloat(3));
    assertEquals("Name of items column 3 is wrong.", "amount", result.getColumnName(3));
    assertEquals("Value of items column 4 is wrong.", 1, result.getLong(4));
    assertEquals("Name of items column 4 is wrong.", "unitid", result.getColumnName(4));
    assertEquals("Value of items column 5 is wrong.", 1, result.getLong(5));
    assertEquals("Name of items column 5 is wrong.", "catid", result.getColumnName(5));
    assertEquals("Value of items column 6 is wrong.", 0, result.getLong(6));
    assertEquals("Name of items column 6 is wrong.", "picked", result.getColumnName(6));
    assertEquals("Value of items column 7 is wrong.", 512, result.getInt(7));
    assertEquals("Name of items column 7 is wrong.", "status", result.getColumnName(7));
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
    assertEquals("Name of items column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of items column 1 is wrong.", "itemid", result.getColumnName(1));
    assertEquals("Name of items column 2 is wrong.", "listid", result.getColumnName(2));
    assertEquals("Name of items column 3 is wrong.", "amount", result.getColumnName(3));
    assertEquals("Name of items column 4 is wrong.", "unitid", result.getColumnName(4));
    assertEquals("Name of items column 5 is wrong.", "catid", result.getColumnName(5));
    assertEquals("Name of items column 6 is wrong.", "picked", result.getColumnName(6));
    assertEquals("Name of items column 7 is wrong.", "status", result.getColumnName(7));
    assertEquals("Value of items column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Value of items column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Value of items column 2 is wrong.", 1, result.getLong(2));
    assertEquals("Value of items column 3 is wrong.", (float) 0.0, result.getFloat(3));
    assertEquals("Value of items column 4 is wrong.", 1, result.getLong(4));
    assertEquals("Value of items column 5 is wrong.", 1, result.getLong(5));
    assertEquals("Value of items column 6 is wrong.", 0, result.getLong(6));
    assertEquals("Value of items column 7 is wrong.", 0, result.getInt(7));
    assertTrue("Could not advance cursor.", result.moveToNext());
    assertEquals("Value of items column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Value of items column 1 is wrong.", 2, result.getLong(1));
    assertEquals("Value of items column 2 is wrong.", 1, result.getLong(2));
    assertEquals("Value of items column 3 is wrong.", (float) 0.0, result.getFloat(3));
    assertEquals("Value of items column 4 is wrong.", 1, result.getLong(4));
    assertEquals("Value of items column 5 is wrong.", 1, result.getLong(5));
    assertEquals("Value of items column 6 is wrong.", 0, result.getLong(6));
    assertEquals("Value of items column 7 is wrong.", 0, result.getInt(7));
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
    assertEquals("Name of items column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of items column 1 is wrong.", "itemid", result.getColumnName(1));
    assertEquals("Name of items column 2 is wrong.", "listid", result.getColumnName(2));
    assertEquals("Name of items column 3 is wrong.", "amount", result.getColumnName(3));
    assertEquals("Name of items column 4 is wrong.", "unitid", result.getColumnName(4));
    assertEquals("Name of items column 5 is wrong.", "catid", result.getColumnName(5));
    assertEquals("Name of items column 6 is wrong.", "picked", result.getColumnName(6));
    assertEquals("Name of items column 7 is wrong.", "status", result.getColumnName(7));
    assertEquals("Value of items column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Value of items column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Value of items column 2 is wrong.", 1, result.getLong(2));
    assertEquals("Value of items column 3 is wrong.", (float) 0.0, result.getFloat(3));
    assertEquals("Value of items column 4 is wrong.", 1, result.getLong(4));
    assertEquals("Value of items column 5 is wrong.", 1, result.getLong(5));
    assertEquals("Value of items column 6 is wrong.", 0, result.getLong(6));
    assertEquals("Value of items column 7 is wrong.", 0, result.getInt(7));
    result.close();
  }

  public void testTicklistFetchAll() {
    setupDatabase();
    DatabaseAdapter.getTicklistsTable().delete(1);

    Cursor result = DatabaseAdapter.getTicklistsTable().fetchAll(1);
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 8, result.getColumnCount());
    assertEquals("Name of items column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of items column 1 is wrong.", "itemid", result.getColumnName(1));
    assertEquals("Name of items column 2 is wrong.", "listid", result.getColumnName(2));
    assertEquals("Name of items column 3 is wrong.", "amount", result.getColumnName(3));
    assertEquals("Name of items column 4 is wrong.", "unitid", result.getColumnName(4));
    assertEquals("Name of items column 5 is wrong.", "catid", result.getColumnName(5));
    assertEquals("Name of items column 6 is wrong.", "picked", result.getColumnName(6));
    assertEquals("Name of items column 7 is wrong.", "status", result.getColumnName(7));
    assertEquals("Value of items column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Value of items column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Value of items column 2 is wrong.", 1, result.getLong(2));
    assertEquals("Value of items column 3 is wrong.", (float) 0.0, result.getFloat(3));
    assertEquals("Value of items column 4 is wrong.", 1, result.getLong(4));
    assertEquals("Value of items column 5 is wrong.", 1, result.getLong(5));
    assertEquals("Value of items column 6 is wrong.", 0, result.getLong(6));
    assertEquals("Value of items column 7 is wrong.", 512, result.getInt(7));
    result.close();

    result = DatabaseAdapter.getTicklistsTable().fetchAll(2);
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 8, result.getColumnCount());
    assertEquals("Name of items column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of items column 1 is wrong.", "itemid", result.getColumnName(1));
    assertEquals("Name of items column 2 is wrong.", "listid", result.getColumnName(2));
    assertEquals("Name of items column 3 is wrong.", "amount", result.getColumnName(3));
    assertEquals("Name of items column 4 is wrong.", "unitid", result.getColumnName(4));
    assertEquals("Name of items column 5 is wrong.", "catid", result.getColumnName(5));
    assertEquals("Name of items column 6 is wrong.", "picked", result.getColumnName(6));
    assertEquals("Name of items column 7 is wrong.", "status", result.getColumnName(7));
    assertEquals("Value of items column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Value of items column 1 is wrong.", 2, result.getLong(1));
    assertEquals("Value of items column 2 is wrong.", 1, result.getLong(2));
    assertEquals("Value of items column 3 is wrong.", (float) 0.0, result.getFloat(3));
    assertEquals("Value of items column 4 is wrong.", 1, result.getLong(4));
    assertEquals("Value of items column 5 is wrong.", 1, result.getLong(5));
    assertEquals("Value of items column 6 is wrong.", 0, result.getLong(6));
    assertEquals("Value of items column 7 is wrong.", 0, result.getInt(7));
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
    assertTrue("Entry could not be updated.", DatabaseAdapter.getTicklistsTable().update(1, args));
    Cursor result = DatabaseAdapter.getTicklistsTable().fetch(1);
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 8, result.getColumnCount());
    assertEquals("Name of items column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of items column 1 is wrong.", "itemid", result.getColumnName(1));
    assertEquals("Name of items column 2 is wrong.", "listid", result.getColumnName(2));
    assertEquals("Name of items column 3 is wrong.", "amount", result.getColumnName(3));
    assertEquals("Name of items column 4 is wrong.", "unitid", result.getColumnName(4));
    assertEquals("Name of items column 5 is wrong.", "catid", result.getColumnName(5));
    assertEquals("Name of items column 6 is wrong.", "picked", result.getColumnName(6));
    assertEquals("Name of items column 7 is wrong.", "status", result.getColumnName(7));
    assertEquals("Value of items column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Value of items column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Value of items column 2 is wrong.", 1, result.getLong(2));
    assertEquals("Value of items column 3 is wrong.", (float) 2.0, result.getFloat(3));
    assertEquals("Value of items column 4 is wrong.", 1, result.getLong(4));
    assertEquals("Value of items column 5 is wrong.", 1, result.getLong(5));
    assertEquals("Value of items column 6 is wrong.", 0, result.getLong(6));
    assertEquals("Value of items column 7 is wrong.", 0, result.getInt(7));
    result.close();
  }
}
