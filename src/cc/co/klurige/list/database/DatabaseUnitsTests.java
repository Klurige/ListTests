package cc.co.klurige.list.database;

import java.sql.SQLException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import cc.co.klurige.list.database.Table.Key;
import cc.co.klurige.list.database.Table.Status;

public class DatabaseUnitsTests extends AndroidTestCase {

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

  public void testUnitCreate() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);
    Cursor result =
        mDbAdapter.getDB().query(DatabaseAdapter.getUnitsTable().getTableName(), null, null, null,
            null, null, null, null);
    assertTrue(result.moveToFirst());
    // Upon creation, blank unit is added.
    assertTrue("Newly created unit should be at 1.", result.moveToPosition(1));
    assertEquals("Number of entries is wrong.", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 3, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "kg", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));
    result.close();
  }

  public void testUnitCreateMissingName() {
    boolean isSuccess = false;
    ContentValues args = new ContentValues();
    try {
      DatabaseAdapter.getUnitsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("entry with missing name created", isSuccess);
  }

  public void testUnitCreateNullName() {
    boolean isSuccess = false;
    ContentValues args = new ContentValues();
    args.put(Key.NAME, (String) null);
    try {
      DatabaseAdapter.getUnitsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("entry with null name created", isSuccess);
  }

  public void testUnitCreateDuplicate() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "kg");
    long id = DatabaseAdapter.getUnitsTable().create(args);
    assertEquals("entry with duplicated name created", Table.Error.DUPLICATE_NAME, id);
    Cursor result = DatabaseAdapter.getUnitsTable().fetch();
    assertTrue("Cursor is not empty", result.moveToFirst());
    assertEquals("Wrong number of entries in cursor", 2, result.getCount()); // Including
                                                                             // the
                                                                             // blank.
    result.close();
  }

  public void testUnitCreateDeleted() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);
    DatabaseAdapter.getUnitsTable().delete(2);
    args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);
    Cursor result =
        mDbAdapter.getDB().query(DatabaseAdapter.getUnitsTable().getTableName(), null, null, null,
            null, null, null, null);
    assertTrue(result.moveToFirst());
    // Upon creation, blank unit is added.
    assertTrue("Newly created unit should be at 1.", result.moveToPosition(1));
    assertEquals("Number of entries is wrong.", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 3, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "kg", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));
    result.close();
  }

  public void testUnitDelete() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);

    assertTrue(DatabaseAdapter.getUnitsTable().delete(2));
    Cursor result = DatabaseAdapter.getUnitsTable().fetch(2);
    assertFalse("Cursor is not empty.", result.moveToFirst());
    result.close();
  }

  public void testUnitDeleteNonExisting() {
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getUnitsTable().delete(2);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Managed to delete non-existent entry.", isSuccess);
  }

  public void testUnitDeleteFailedItem() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Mjöl");
    args.put(Key.UNIT, 2);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);

    assertFalse(DatabaseAdapter.getUnitsTable().delete(2));
    Cursor result = DatabaseAdapter.getUnitsTable().fetch(2);
    assertTrue("Cursor is empty.", result.moveToFirst());
    result.close();
  }

  public void testUnitDeleteFailedTicklistItem() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Mjöl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "matlista");
    long listid = DatabaseAdapter.getListsTable().create(args);

    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    args.put(Key.UNIT, 2);
    DatabaseAdapter.getTicklistsTable().create(args);

    assertFalse(DatabaseAdapter.getUnitsTable().delete(2));
    Cursor result = DatabaseAdapter.getUnitsTable().fetch(2);
    assertTrue("Cursor is empty.", result.moveToFirst());
    result.close();
  }

  public void testUnitUndelete() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);
    DatabaseAdapter.getUnitsTable().delete(2);

    assertTrue(DatabaseAdapter.getUnitsTable().undelete(2));
    Cursor result = DatabaseAdapter.getUnitsTable().fetch(2);
    assertTrue("Cursor is empty.", result.moveToFirst());
    assertEquals("Name of undeleted unit is wrong.", "kg", result.getString(1));
    result.close();
  }

  public void testUnitFetch() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "liter");
    DatabaseAdapter.getUnitsTable().create(args);
    assertTrue(DatabaseAdapter.getUnitsTable().delete(2));

    Cursor result = DatabaseAdapter.getUnitsTable().fetch();
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank unit is added.
    assertEquals("Number of entries is wrong.", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 3, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));

    assertTrue("Cursor cannot advance.", result.moveToNext());
    assertEquals("Value of column 0 is wrong.", 3, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "liter", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));
    result.close();
  }

  public void testUnitFetchOnName() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);
    Cursor result = DatabaseAdapter.getUnitsTable().fetch("kg");
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank unit is added.
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 1, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    result.close();
  }

  public void testUnitFetchOnNameBlank() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);
    Cursor result = DatabaseAdapter.getUnitsTable().fetch("");
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank unit is added.
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 1, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    result.close();
  }

  public void testUnitFetchEmpty() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);

    boolean isSuccess = false;
    try {
      Cursor result = DatabaseAdapter.getUnitsTable().fetch(null);
      result.close();
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Fetch accepted null pointer", isSuccess);
  }

  public void testUnitFetchOnId() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);
    Cursor result = DatabaseAdapter.getUnitsTable().fetch(2);
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank unit is added.
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 3, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "kg", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));
    result.close();
  }

  public void testUnitFetchAll() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "liter");
    DatabaseAdapter.getUnitsTable().create(args);
    assertTrue(DatabaseAdapter.getUnitsTable().delete(2));

    Cursor result = DatabaseAdapter.getUnitsTable().fetchAll(2);
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank unit is added.
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 3, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "kg", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 512, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));
    result.close();

    result = DatabaseAdapter.getUnitsTable().fetchAll(3);
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank unit is added.
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 3, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 3, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "liter", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));
    result.close();

  }

  public void testUnitFetchStarting() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "liter");
    DatabaseAdapter.getUnitsTable().create(args);

    Cursor result = DatabaseAdapter.getUnitsTable().fetchStarting("k");
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank unit is added.
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 2, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "kg", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    result.close();
    result = DatabaseAdapter.getUnitsTable().fetchStarting("X");
    assertFalse("Cursor is not empty", result.moveToFirst());
    result.close();
  }

  public void testUnitFetchStartingLower() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Kg");
    DatabaseAdapter.getUnitsTable().create(args);

    Cursor result = DatabaseAdapter.getUnitsTable().fetchStarting("k");
    assertTrue("Cursor is empty", result.moveToFirst());
    result.close();
    result = DatabaseAdapter.getUnitsTable().fetchStarting("K");
    assertTrue("Cursor is empty", result.moveToFirst());
    result.close();
  }

  public void testUnitFetchStartingUpper() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);

    Cursor result = DatabaseAdapter.getUnitsTable().fetchStarting("k");
    assertTrue("Cursor is empty", result.moveToFirst());
    result.close();
    result = DatabaseAdapter.getUnitsTable().fetchStarting("K");
    assertTrue("Cursor is empty", result.moveToFirst());
    result.close();
  }

  public void testUnitFetchStartingEmpty() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "liter");
    DatabaseAdapter.getUnitsTable().create(args);

    Cursor result = DatabaseAdapter.getUnitsTable().fetchStarting(null);
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank unit is added.
    assertEquals("Number of entries is wrong.", 3, result.getCount());
    assertEquals("Number of columns is wrong.", 2, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    result.close();
  }

  public void testUnitIsDeleted() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "liter");
    DatabaseAdapter.getUnitsTable().create(args);
    DatabaseAdapter.getUnitsTable().delete(2);
    assertTrue("Item is not deleted.", DatabaseAdapter.getUnitsTable().isDeleted(2));
    assertFalse("Item is deleted.", DatabaseAdapter.getUnitsTable().isDeleted(3));
  }

  public void testUnitUpdate() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Kg");
    assertTrue(DatabaseAdapter.getUnitsTable().update(2, args));

    Cursor result =
        mDbAdapter.getDB().query(DatabaseAdapter.getUnitsTable().getTableName(), null, null, null,
            null, null, null, null);
    assertTrue(result.moveToFirst());
    // Upon creation, blank unit is added.
    assertTrue("Newly created unit should be at 1.", result.moveToPosition(1));
    assertEquals("Number of entries is wrong.", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 3, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "Kg", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));
    result.close();
  }
}
