package cc.co.klurige.list.db.test;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import cc.co.klurige.list.database.DatabaseAdapter;
import cc.co.klurige.list.database.Table.Key;
import cc.co.klurige.list.database.Table.Status;

public class DatabaseTests extends AndroidTestCase {

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
    assertEquals("entry with duplicated name created", -Status.ERROR, id);
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

  public void testCategoryCreate() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);
    Cursor result =
        mDbAdapter.getDB().query(DatabaseAdapter.getCategoriesTable().getTableName(), null, null,
            null, null, null, null, null);
    assertTrue(result.moveToFirst());
    // Upon creation, blank category is added.
    assertTrue("Newly created category should be at 1.", result.moveToPosition(1));
    assertEquals("Number of categories is wrong.", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 3, result.getColumnCount());
    assertEquals("Value of category column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of category column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of category column 1 is wrong.", "Chark", result.getString(1));
    assertEquals("Name of category column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of category column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of category column 2 is wrong.", "status", result.getColumnName(2));
    result.close();
  }

  public void testCategoryCreateMissingName() {
    boolean isSuccess = false;
    ContentValues args = new ContentValues();
    try {
      DatabaseAdapter.getCategoriesTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("entry with missing name created", isSuccess);
  }

  public void testCategoryCreateNullName() {
    boolean isSuccess = false;
    ContentValues args = new ContentValues();
    args.put(Key.NAME, (String) null);
    try {
      DatabaseAdapter.getCategoriesTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("entry with null name created", isSuccess);
  }

  public void testCategoryCreateDuplicate() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Chark");
    long id = DatabaseAdapter.getCategoriesTable().create(args);
    assertEquals("entry with duplicated name created", -Status.ERROR, id);
    Cursor result = DatabaseAdapter.getCategoriesTable().fetch("Chark");
    assertTrue("Cursor is empty", result.moveToFirst());
    assertEquals("Wrong number of entries in cursor", 1, result.getCount());
    result.close();
  }

  public void testCategoryCreateDeleted() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);
    DatabaseAdapter.getCategoriesTable().delete(2);
    args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);
    Cursor result =
        mDbAdapter.getDB().query(DatabaseAdapter.getCategoriesTable().getTableName(), null, null,
            null,
            null, null, null, null);
    assertTrue(result.moveToFirst());
    // Upon creation, blank category is added.
    assertTrue("Newly created item should be at 1.", result.moveToPosition(1));
    assertEquals("Number of entries is wrong.", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 3, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "Chark", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));
    result.close();
  }

  public void testCategoryDelete() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);

    assertTrue(DatabaseAdapter.getCategoriesTable().delete(2));
    Cursor result = DatabaseAdapter.getCategoriesTable().fetch(2);
    assertFalse("Cursor is not empty.", result.moveToFirst());
    result.close();
  }

  public void testCategoryDeleteNonExisting() {
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getCategoriesTable().delete(2);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Managed to delete non-existent entry.", isSuccess);
  }

  public void testCategoryDeleteFailedItem() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Skinka");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 2);
    DatabaseAdapter.getItemsTable().create(args);

    assertFalse(DatabaseAdapter.getCategoriesTable().delete(2));
    Cursor result = DatabaseAdapter.getCategoriesTable().fetch(2);
    assertTrue("Cursor is empty.", result.moveToFirst());
    result.close();
  }

  public void testCategoryDeleteFailedTicklistItem() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Skinka");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "matlista");
    long listid = DatabaseAdapter.getListsTable().create(args);

    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    args.put(Key.CATEGORY, 2);
    DatabaseAdapter.getTicklistsTable().create(args);

    assertFalse(DatabaseAdapter.getCategoriesTable().delete(2));
    Cursor result = DatabaseAdapter.getCategoriesTable().fetch(2);
    assertTrue("Cursor is empty.", result.moveToFirst());
    result.close();
  }

  public void testCategoryUndelete() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);
    DatabaseAdapter.getCategoriesTable().delete(2);

    assertTrue(DatabaseAdapter.getCategoriesTable().undelete(2));
    Cursor result = DatabaseAdapter.getCategoriesTable().fetch(2);
    assertTrue("Cursor is empty.", result.moveToFirst());
    assertEquals("Name of undeleted item is wrong.", "Chark", result.getString(1));
    result.close();
  }

  public void testCategoryFetch() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Mejeri");
    DatabaseAdapter.getCategoriesTable().create(args);
    assertTrue(DatabaseAdapter.getCategoriesTable().delete(2));

    Cursor result = DatabaseAdapter.getCategoriesTable().fetch();
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank category is added.
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
    assertEquals("Value of column 1 is wrong.", "Mejeri", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));
    result.close();
  }

  public void testCategoryFetchOnName() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);
    Cursor result = DatabaseAdapter.getCategoriesTable().fetch("Chark");
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank category is added.
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 1, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    result.close();
  }

  public void testCategoryFetchOnNameBlank() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);
    Cursor result = DatabaseAdapter.getCategoriesTable().fetch("");
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank category is added.
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 1, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    result.close();
  }

  public void testCategoryFetchEmpty() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);

    boolean isSuccess = false;
    try {
      Cursor result = DatabaseAdapter.getCategoriesTable().fetch(null);
      result.close();
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Fetch accepted null pointer", isSuccess);
  }

  public void testCategoryFetchOnId() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);
    Cursor result = DatabaseAdapter.getCategoriesTable().fetch(2);
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank category is added.
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 3, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "Chark", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));
    result.close();
  }

  public void testCategoryFetchAll() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Mejeri");
    DatabaseAdapter.getCategoriesTable().create(args);
    assertTrue(DatabaseAdapter.getCategoriesTable().delete(2));

    Cursor result = DatabaseAdapter.getCategoriesTable().fetchAll(2);
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank category is added.
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 3, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "Chark", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 512, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));
    result.close();

    result = DatabaseAdapter.getCategoriesTable().fetchAll(3);
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank category is added.
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 3, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 3, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "Mejeri", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));
    result.close();

  }

  public void testCategoryFetchStarting() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Mejeri");
    DatabaseAdapter.getCategoriesTable().create(args);

    Cursor result = DatabaseAdapter.getCategoriesTable().fetchStarting("C");
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank category is added.
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 2, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "Chark", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    result.close();
    result = DatabaseAdapter.getCategoriesTable().fetchStarting("X");
    assertFalse("Cursor is not empty", result.moveToFirst());
    result.close();
  }

  public void testCategoryFetchStartingLower() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);

    Cursor result = DatabaseAdapter.getCategoriesTable().fetchStarting("c");
    assertTrue("Cursor is empty", result.moveToFirst());
    result.close();
  }

  public void testCategoryFetchStartingUpper() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "chark");
    DatabaseAdapter.getCategoriesTable().create(args);

    Cursor result = DatabaseAdapter.getCategoriesTable().fetchStarting("C");
    assertTrue("Cursor is empty", result.moveToFirst());
    result.close();
  }

  public void testCategoryFetchStartingEmpty() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Mejeri");
    DatabaseAdapter.getCategoriesTable().create(args);

    Cursor result = DatabaseAdapter.getCategoriesTable().fetchStarting(null);
    assertTrue("Cursor is empty", result.moveToFirst());
    assertEquals("Number of entries is wrong.", 3, result.getCount());
    assertEquals("Number of columns is wrong.", 2, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    result.close();
  }

  public void testCategoryIsDeleted() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Mejeri");
    DatabaseAdapter.getCategoriesTable().create(args);
    DatabaseAdapter.getCategoriesTable().delete(2);
    assertTrue("Item is not deleted.", DatabaseAdapter.getCategoriesTable().isDeleted(2));
    assertFalse("Item is deleted.", DatabaseAdapter.getCategoriesTable().isDeleted(3));
  }

  public void testCategoryUpdate() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "chark");
    assertTrue(DatabaseAdapter.getCategoriesTable().update(2, args));

    Cursor result =
        mDbAdapter.getDB().query(DatabaseAdapter.getCategoriesTable().getTableName(), null, null,
            null,
            null, null, null, null);
    assertTrue(result.moveToFirst());
    // Upon creation, blank category is added.
    assertTrue("Newly created unit should be at 1.", result.moveToPosition(1));
    assertEquals("Number of entries is wrong.", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 3, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "chark", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));
    result.close();
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
    assertEquals("entry with duplicated name created", -Status.ERROR, id);
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
    assertTrue(DatabaseAdapter.getListsTable().update(1, args));

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

  public void testItemsCreate() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    Cursor result =
        mDbAdapter.getDB().query(DatabaseAdapter.getItemsTable().getTableName(), null, null, null,
            null, null, null, null);

    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 6, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Name of column 1 is wrong.", "unitid", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", (float) 0.0, result.getFloat(2));
    assertEquals("Name of column 2 is wrong.", "amount", result.getColumnName(2));
    assertEquals("Value of column 3 is wrong.", 1, result.getLong(3));
    assertEquals("Name of column 3 is wrong.", "catid", result.getColumnName(3));
    assertEquals("Value of column 4 is wrong.", "Pryl", result.getString(4));
    assertEquals("Name of column 4 is wrong.", "name", result.getColumnName(4));
    assertEquals("Value of column 5 is wrong.", 0, result.getInt(5));
    assertEquals("Name of column 5 is wrong.", "status", result.getColumnName(5));
    result.close();
  }

  public void testItemsCreateMissingUnit() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.CATEGORY, 1);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getItemsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Entry with missing unit created.", isSuccess);
  }

  public void testItemsCreateMissingCategory() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getItemsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Entry with missing category created.", isSuccess);
  }

  public void testItemsCreateMissingName() {
    boolean isSuccess = false;
    ContentValues args = new ContentValues();
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    try {
      DatabaseAdapter.getListsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("entry with missing name created", isSuccess);
  }

  public void testItemsCreateNullUnit() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, (Integer) null);
    args.put(Key.CATEGORY, 1);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getItemsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Entry with null unit created.", isSuccess);
  }

  public void testItemsCreateNullCategory() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, (Integer) null);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getItemsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Entry with null category created.", isSuccess);
  }

  public void testItemsCreateNullName() {
    boolean isSuccess = false;
    ContentValues args = new ContentValues();
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    try {
      DatabaseAdapter.getListsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("entry with null name created", isSuccess);
  }

  public void testItemsCreateDuplicate() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long id = DatabaseAdapter.getItemsTable().create(args);
    assertEquals("entry with duplicated name created", -Status.ERROR, id);
    Cursor result = DatabaseAdapter.getItemsTable().fetch("Pryl");
    assertTrue("Cursor is empty", result.moveToFirst());
    assertEquals("Wrong number of entries in cursor", 1, result.getCount());
    result.close();
  }

  public void testItemsCreateDeleted() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    DatabaseAdapter.getItemsTable().delete(1);
    args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    Cursor result =
        mDbAdapter.getDB().query(DatabaseAdapter.getItemsTable().getTableName(), null, null,
            null,
            null, null, null, null);
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 6, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Name of column 1 is wrong.", "unitid", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", (float) 0.0, result.getFloat(2));
    assertEquals("Name of column 2 is wrong.", "amount", result.getColumnName(2));
    assertEquals("Value of column 3 is wrong.", 1, result.getLong(3));
    assertEquals("Name of column 3 is wrong.", "catid", result.getColumnName(3));
    assertEquals("Value of column 4 is wrong.", "Pryl", result.getString(4));
    assertEquals("Name of column 4 is wrong.", "name", result.getColumnName(4));
    assertEquals("Value of column 5 is wrong.", 0, result.getInt(5));
    assertEquals("Name of column 5 is wrong.", "status", result.getColumnName(5));
    result.close();
  }

  public void testItemsCreateDeletedMissingUnit() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    DatabaseAdapter.getItemsTable().delete(1);
    args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.CATEGORY, 1);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getItemsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Entry with missing unit created.", isSuccess);
  }

  public void testItemsCreateDeletedMissingCategory() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    DatabaseAdapter.getItemsTable().delete(1);
    args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getItemsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Entry with missing unit created.", isSuccess);
  }

  public void testItemsDelete() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    DatabaseAdapter.getItemsTable().delete(1);
    Cursor result = DatabaseAdapter.getItemsTable().fetch();
    assertFalse("Cursor not empty", result.moveToFirst());
    result.close();
  }

  public void testItemsDeleteNonExisting() {
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getItemsTable().delete(2);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Managed to delete non-existent entry.", isSuccess);
  }

  public void testItemsDeleteTicklist() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "20110126");
    long listid = DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.ITEM, itemid);
    args.put(Key.LIST, listid);
    DatabaseAdapter.getTicklistsTable().create(args);

    assertFalse("Entry was deleted.", DatabaseAdapter.getItemsTable().delete(1));
  }

  public void testItemsUndelete() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    DatabaseAdapter.getItemsTable().delete(1);
    DatabaseAdapter.getItemsTable().undelete(1);
    Cursor result = DatabaseAdapter.getItemsTable().fetch(1);
    assertTrue("Cursor empty", result.moveToFirst());
    result.close();
  }

  public void testItemsFetch() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    Cursor result = DatabaseAdapter.getItemsTable().fetch();
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 6, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Name of column 1 is wrong.", "unitid", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", (float) 0.0, result.getFloat(2));
    assertEquals("Name of column 2 is wrong.", "amount", result.getColumnName(2));
    assertEquals("Value of column 3 is wrong.", 1, result.getLong(3));
    assertEquals("Name of column 3 is wrong.", "catid", result.getColumnName(3));
    assertEquals("Value of column 4 is wrong.", "Pryl", result.getString(4));
    assertEquals("Name of column 4 is wrong.", "name", result.getColumnName(4));
    assertEquals("Value of column 5 is wrong.", 0, result.getInt(5));
    assertEquals("Name of column 5 is wrong.", "status", result.getColumnName(5));
    result.close();
  }

  public void testItemsFetchOnName() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    Cursor result = DatabaseAdapter.getItemsTable().fetch("Pryl");
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 1, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    result.close();
  }

  public void testItemsFetchOnId() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    Cursor result = DatabaseAdapter.getItemsTable().fetch(1);
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 6, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Name of column 1 is wrong.", "unitid", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", (float) 0.0, result.getFloat(2));
    assertEquals("Name of column 2 is wrong.", "amount", result.getColumnName(2));
    assertEquals("Value of column 3 is wrong.", 1, result.getLong(3));
    assertEquals("Name of column 3 is wrong.", "catid", result.getColumnName(3));
    assertEquals("Value of column 4 is wrong.", "Pryl", result.getString(4));
    assertEquals("Name of column 4 is wrong.", "name", result.getColumnName(4));
    assertEquals("Value of column 5 is wrong.", 0, result.getInt(5));
    assertEquals("Name of column 5 is wrong.", "status", result.getColumnName(5));
    result.close();
  }

  public void testItemsFetchAll() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Sak");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    DatabaseAdapter.getItemsTable().delete(1);

    Cursor result = DatabaseAdapter.getItemsTable().fetchAll(1);
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 6, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Name of column 1 is wrong.", "unitid", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", (float) 0.0, result.getFloat(2));
    assertEquals("Name of column 2 is wrong.", "amount", result.getColumnName(2));
    assertEquals("Value of column 3 is wrong.", 1, result.getLong(3));
    assertEquals("Name of column 3 is wrong.", "catid", result.getColumnName(3));
    assertEquals("Value of column 4 is wrong.", "Pryl", result.getString(4));
    assertEquals("Name of column 4 is wrong.", "name", result.getColumnName(4));
    assertEquals("Value of column 5 is wrong.", 512, result.getInt(5));
    assertEquals("Name of column 5 is wrong.", "status", result.getColumnName(5));
    result.close();

    result = DatabaseAdapter.getItemsTable().fetchAll(2);
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 6, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Name of column 1 is wrong.", "unitid", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", (float) 0.0, result.getFloat(2));
    assertEquals("Name of column 2 is wrong.", "amount", result.getColumnName(2));
    assertEquals("Value of column 3 is wrong.", 1, result.getLong(3));
    assertEquals("Name of column 3 is wrong.", "catid", result.getColumnName(3));
    assertEquals("Value of column 4 is wrong.", "Sak", result.getString(4));
    assertEquals("Name of column 4 is wrong.", "name", result.getColumnName(4));
    assertEquals("Value of column 5 is wrong.", 0, result.getInt(5));
    assertEquals("Name of column 5 is wrong.", "status", result.getColumnName(5));
    result.close();
  }

  public void testItemsFetchStarting() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Sak");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);

    Cursor result = DatabaseAdapter.getItemsTable().fetchStarting("P");
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank unit is added.
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 2, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "Pryl", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    result.close();
    result = DatabaseAdapter.getItemsTable().fetchStarting("X");
    assertFalse("Cursor is not empty", result.moveToFirst());
    result.close();
  }

  public void testItemsFetchStartingLower() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Sak");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);

    Cursor result = DatabaseAdapter.getItemsTable().fetchStarting("p");
    assertTrue("Cursor is empty", result.moveToFirst());
    result.close();
  }

  public void testItemsFetchStartingUpper() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);

    Cursor result = DatabaseAdapter.getItemsTable().fetchStarting("P");
    assertTrue("Cursor is empty", result.moveToFirst());
    result.close();
  }

  public void testItemsFetchStartingEmpty() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "sak");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);

    Cursor result = DatabaseAdapter.getItemsTable().fetchStarting(null);
    assertTrue("Cursor is empty", result.moveToFirst());
    assertEquals("Number of entries is wrong.", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 2, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));

    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Value of column 1 is wrong.", "pryl", result.getString(1));
    assertTrue("Failed to advance cursor.", result.moveToNext());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Value of column 1 is wrong.", "sak", result.getString(1));
    result.close();
  }

  public void testItemsIsDeleted() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Sak");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    DatabaseAdapter.getItemsTable().delete(1);

    assertTrue("Entry is not deleted.", DatabaseAdapter.getItemsTable().isDeleted(1));
    assertFalse("Entry is deleted.", DatabaseAdapter.getItemsTable().isDeleted(2));
  }

  public void testItemsUpdate() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Sak");
    DatabaseAdapter.getItemsTable().update(1, args);
    Cursor result =
        mDbAdapter.getDB().query(DatabaseAdapter.getItemsTable().getTableName(), null, null, null,
            null, null, null, null);

    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 6, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Name of column 1 is wrong.", "unitid", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", (float) 0.0, result.getFloat(2));
    assertEquals("Name of column 2 is wrong.", "amount", result.getColumnName(2));
    assertEquals("Value of column 3 is wrong.", 1, result.getLong(3));
    assertEquals("Name of column 3 is wrong.", "catid", result.getColumnName(3));
    assertEquals("Value of column 4 is wrong.", "Sak", result.getString(4));
    assertEquals("Name of column 4 is wrong.", "name", result.getColumnName(4));
    assertEquals("Value of column 5 is wrong.", 0, result.getInt(5));
    assertEquals("Name of column 5 is wrong.", "status", result.getColumnName(5));
    result.close();
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
