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

public class DatabaseListsTests extends AndroidTestCase {

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

  public void testListCreate() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    final Date ts_before = new Date();
    DatabaseAdapter.getListsTable().create(args);
    final Date ts_after = new Date();
    Cursor result =
        mDbAdapter.getDB().query(DatabaseAdapter.getListsTable().getTableName(), null, null, null,
            null, null, null, null);
    assertTrue(result.moveToFirst());
    assertEquals("Number of lists is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 4, result.getColumnCount());
    assertEquals("Value of list column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of list column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of list should column 1 is wrong.", "20110126", result.getString(1));
    assertEquals("Name of list column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of list column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of list column 2 is wrong.", "status", result.getColumnName(2));
    assertTrue("Value of list column 3 is wrong.", (result.getLong(3) > ts_before.getTime())
        && (result.getLong(3) < ts_after.getTime()));
    assertEquals("Name of list column 3 is wrong.", "timestamp", result.getColumnName(3));
    result.close();
  }

  public void testListCreateMissingName() {
    boolean isSuccess = false;
    ContentValues args = new ContentValues();
    try {
      DatabaseAdapter.getListsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("entry with missing name created", isSuccess);
  }

  public void testListCreateNullName() {
    boolean isSuccess = false;
    ContentValues args = new ContentValues();
    args.put(Key.NAME, (String) null);
    try {
      DatabaseAdapter.getListsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("entry with null name created", isSuccess);
  }

  public void testListCreateDuplicate() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "20110126");
    long id = DatabaseAdapter.getListsTable().create(args);
    assertEquals("entry with duplicated name created", Table.Error.DUPLICATE_NAME, id);
    Cursor result = DatabaseAdapter.getListsTable().fetch("20110126");
    assertTrue("Cursor is empty", result.moveToFirst());
    assertEquals("Wrong number of entries in cursor", 1, result.getCount());
    result.close();

  }

  public void testListCreateDeleted() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    DatabaseAdapter.getListsTable().create(args);
    DatabaseAdapter.getListsTable().delete(1);
    args = new ContentValues();
    args.put(Key.NAME, "20110126");
    DatabaseAdapter.getListsTable().create(args);
    Cursor result =
        mDbAdapter.getDB().query(DatabaseAdapter.getListsTable().getTableName(), null, null,
            null,
            null, null, null, null);
    assertTrue(result.moveToFirst());
    assertEquals("Number of lists is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 4, result.getColumnCount());
    assertEquals("Value of list column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of list column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of list should column 1 is wrong.", "20110126", result.getString(1));
    assertEquals("Name of list column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of list column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of list column 2 is wrong.", "status", result.getColumnName(2));
    assertEquals("Name of list column 3 is wrong.", "timestamp", result.getColumnName(3));
    result.close();
  }

  public void testListDelete() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    DatabaseAdapter.getListsTable().create(args);

    assertTrue(DatabaseAdapter.getListsTable().delete(1));
    Cursor result = DatabaseAdapter.getListsTable().fetch(1);
    assertFalse("Cursor is not empty.", result.moveToFirst());
    result.close();
  }

  public void testListDeleteNonExisting() {
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getListsTable().delete(2);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Managed to delete non-existent entry.", isSuccess);
  }

  public void testListDeleteTicklistItemNotPicked() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    long listid = DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.ITEM, itemid);
    args.put(Key.LIST, listid);
    DatabaseAdapter.getTicklistsTable().create(args);

    Cursor result = DatabaseAdapter.getTicklistsTable().fetch(1);
    assertTrue("Cursor empty", result.moveToFirst());
    result.close();
    assertTrue(DatabaseAdapter.getListsTable().delete(1));
    result = DatabaseAdapter.getTicklistsTable().fetch(1);
    assertFalse("Cursor not empty", result.moveToFirst());
    result.close();
  }

  public void testListDeleteTicklistItemPicked() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    long listid = DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.ITEM, itemid);
    args.put(Key.LIST, listid);
    DatabaseAdapter.getTicklistsTable().create(args);
    DatabaseAdapter.getTicklistsTable().pickItem(1);

    Cursor result = DatabaseAdapter.getTicklistsTable().fetch(1);
    assertTrue("Cursor empty", result.moveToFirst());
    result.close();
    assertTrue(DatabaseAdapter.getListsTable().delete(1));
    result = DatabaseAdapter.getTicklistsTable().fetch(1);
    assertFalse("Cursor not empty", result.moveToFirst());
    result.close();
  }

  public void testListUndelete() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    long listid = DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.ITEM, itemid);
    args.put(Key.LIST, listid);
    DatabaseAdapter.getTicklistsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Sak");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.ITEM, itemid);
    args.put(Key.LIST, listid);
    DatabaseAdapter.getTicklistsTable().create(args);
    DatabaseAdapter.getTicklistsTable().delete(1);
    assertTrue("Entry not deleted.", DatabaseAdapter.getListsTable().delete(1));

    assertTrue("Entry not undeleted.", DatabaseAdapter.getListsTable().undelete(1));

    // Both ticklist items should be undeleted.
    Cursor result = DatabaseAdapter.getListsTable().fetch(1);
    assertTrue("Cursor empty", result.moveToFirst());
    result.close();
    result = DatabaseAdapter.getTicklistsTable().fetch();
    assertEquals("Wrong number of entries in cursor", 2, result.getCount());
    result.close();
  }

  public void testListFetch() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "20110127");
    DatabaseAdapter.getListsTable().create(args);

    Cursor result = DatabaseAdapter.getListsTable().fetch();
    assertTrue("Cursor is empty", result.moveToFirst());
    assertEquals("Number of lists is wrong.", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 4, result.getColumnCount());
    assertEquals("Value of list column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of list column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of list should column 1 is wrong.", "20110126", result.getString(1));
    assertEquals("Name of list column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of list column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of list column 2 is wrong.", "status", result.getColumnName(2));
    assertEquals("Name of list column 3 is wrong.", "timestamp", result.getColumnName(3));

    assertTrue("Cursor cannot advance.", result.moveToNext());
    assertEquals("Value of list column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of list column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of list should column 1 is wrong.", "20110127", result.getString(1));
    assertEquals("Name of list column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of list column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of list column 2 is wrong.", "status", result.getColumnName(2));
    assertEquals("Name of list column 3 is wrong.", "timestamp", result.getColumnName(3));
    result.close();
  }

  public void testListFetchOnName() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    DatabaseAdapter.getListsTable().create(args);
    Cursor result = DatabaseAdapter.getListsTable().fetch("20110126");
    assertTrue("Cursor is empty", result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 1, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    result.close();
  }

  public void testListFetchOnNameBlank() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    DatabaseAdapter.getListsTable().create(args);
    Cursor result = DatabaseAdapter.getListsTable().fetch("");
    assertFalse("Cursor is not empty", result.moveToFirst());
    result.close();
  }

  public void testListFetchEmpty() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    DatabaseAdapter.getListsTable().create(args);

    boolean isSuccess = false;
    try {
      Cursor result = DatabaseAdapter.getListsTable().fetch(null);
      result.close();
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Fetch accepted null pointer", isSuccess);
  }

  public void testListFetchOnId() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    DatabaseAdapter.getListsTable().create(args);
    Cursor result = DatabaseAdapter.getListsTable().fetch(1);
    assertTrue("Cursor is empty", result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 4, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "20110126", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));
    result.close();
  }

  public void testListFetchAll() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "20110127");
    DatabaseAdapter.getListsTable().create(args);
    assertTrue(DatabaseAdapter.getListsTable().delete(1));

    Cursor result = DatabaseAdapter.getListsTable().fetchAll(1);
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank category is added.
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 4, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "20110126", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 512, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));
    result.close();

    result = DatabaseAdapter.getListsTable().fetchAll(2);
    assertTrue("Cursor is empty", result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 4, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "20110127", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));
    result.close();

  }

  public void testListFetchStarting() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "20110127");
    DatabaseAdapter.getListsTable().create(args);

    Cursor result = DatabaseAdapter.getListsTable().fetchStarting("2");
    assertTrue("Cursor is empty", result.moveToFirst());
    assertEquals("Number of entries is wrong.", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 2, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "20110126", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    result.close();
    result = DatabaseAdapter.getListsTable().fetchStarting("X");
    assertFalse("Cursor is not empty", result.moveToFirst());
    result.close();
  }

  public void testListFetchStartingLower() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Första listan");
    DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "finallistan");
    DatabaseAdapter.getListsTable().create(args);

    Cursor result = DatabaseAdapter.getListsTable().fetchStarting("f");
    assertTrue("Cursor is empty", result.moveToFirst());
    assertEquals("Number of entries is wrong.", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 2, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    result.close();
  }

  public void testListFetchStartingUpper() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Första listan");
    DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "finallistan");
    DatabaseAdapter.getListsTable().create(args);

    Cursor result = DatabaseAdapter.getListsTable().fetchStarting("F");
    assertTrue("Cursor is empty", result.moveToFirst());
    assertEquals("Number of entries is wrong.", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 2, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    result.close();
  }

  public void testListFetchStartingEmpty() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "20110127");
    DatabaseAdapter.getListsTable().create(args);

    Cursor result = DatabaseAdapter.getListsTable().fetchStarting(null);
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank unit is added.
    assertEquals("Number of entries is wrong.", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 2, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    result.close();
  }

  public void testListIsDeleted() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "20110127");
    DatabaseAdapter.getListsTable().create(args);
    DatabaseAdapter.getListsTable().delete(2);
    assertTrue("Item is not deleted.", DatabaseAdapter.getListsTable().isDeleted(2));
    assertFalse("Item is deleted.", DatabaseAdapter.getListsTable().isDeleted(1));
  }

  public void testListUpdate() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "20110127");
    assertEquals("Update should have succeeded.", 1, DatabaseAdapter.getListsTable()
        .update(1, args));

    Cursor result =
        mDbAdapter.getDB().query(DatabaseAdapter.getListsTable().getTableName(), null, null,
            null,
            null, null, null, null);
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 4, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "20110127", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));
    assertEquals("Name of column 3 is wrong.", "timestamp", result.getColumnName(3));
    result.close();
  }

  public void testListUpdateDuplicateName() {
    setupDatabase();

    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "20110127");
    long e2 = DatabaseAdapter.getListsTable().create(args);

    args = new ContentValues();
    args.put(Key.NAME, "20110126");

    assertEquals("Update did not behave.", Table.Error.DUPLICATE_NAME, DatabaseAdapter
        .getListsTable().update(e2, args));
  }

  public void testListUpdateToDeleted() {
    setupDatabase();

    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    long e1 = DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "20110127");
    long e2 = DatabaseAdapter.getListsTable().create(args);

    DatabaseAdapter.getListsTable().delete(e1);

    args = new ContentValues();
    args.put(Key.NAME, "20110126");

    assertEquals("Update did not behave.", Table.Error.DUPLICATE_DELETED_NAME, DatabaseAdapter
        .getListsTable().update(e2, args));
  }

  public void testListUpdateFailOnStatus() {
    setupDatabase();
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    long e1 = DatabaseAdapter.getListsTable().create(args);

    args = new ContentValues();
    args.put(Key.NAME, "20110127");
    args.put(Key.STATUS, Status.ERROR);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getListsTable().update(e1, args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Shouldn't be allowed to update status.", isSuccess);
  }

  public void testListUpdateFailOnId() {
    setupDatabase();
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "20110126");
    DatabaseAdapter.getListsTable().create(args);

    args = new ContentValues();
    args.put(Key.NAME, "20110127");
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getListsTable().update(5, args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Shouldn't be allowed to update this id.", isSuccess);
  }
}
