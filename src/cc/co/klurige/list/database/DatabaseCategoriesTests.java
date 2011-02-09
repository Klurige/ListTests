package cc.co.klurige.list.database;

import java.sql.SQLException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import cc.co.klurige.list.database.Table.Key;
import cc.co.klurige.list.database.Table.Status;

public class DatabaseCategoriesTests extends AndroidTestCase {

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
    args.put(Key.NAME, "Chark");
    DatabaseAdapter.getCategoriesTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Mejeri");
    DatabaseAdapter.getCategoriesTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Bröd");
    DatabaseAdapter.getCategoriesTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 2);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "matlista");
    long listid = DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    args.put(Key.CATEGORY, 3);
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

  public void testCategoryCreate() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Annat");
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
    assertEquals("Value of category column 1 is wrong.", "Annat", result.getString(1));
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
    setupDatabase();
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Chark");
    long id = DatabaseAdapter.getCategoriesTable().create(args);
    assertEquals("entry with duplicated name created", -Status.ERROR, id);
    Cursor result = DatabaseAdapter.getCategoriesTable().fetch("Chark");
    assertTrue("Cursor is empty", result.moveToFirst());
    assertEquals("Wrong number of entries in cursor", 1, result.getCount());
    result.close();
  }

  public void testCategoryCreateDeleted() {
    setupDatabase();
    DatabaseAdapter.getCategoriesTable().delete(4);
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Bröd");
    DatabaseAdapter.getCategoriesTable().create(args);
    Cursor result =
        mDbAdapter.getDB().query(DatabaseAdapter.getCategoriesTable().getTableName(), null, null,
            null,
            null, null, null, null);
    assertTrue(result.moveToFirst());
    // Upon creation, blank category is added.
    assertTrue("Newly created item should be at 1.", result.moveToPosition(1));
    assertEquals("Number of entries is wrong.", 4, result.getCount());
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
    setupDatabase();
    assertTrue(DatabaseAdapter.getCategoriesTable().delete(4));
    Cursor result = DatabaseAdapter.getCategoriesTable().fetch(4);
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

  public void testCategoryDeleteFailedOnItem() {
    setupDatabase();
    assertFalse("Should not have succeeded.", DatabaseAdapter.getCategoriesTable().delete(2));
    Cursor result = DatabaseAdapter.getCategoriesTable().fetch(2);
    assertTrue("Cursor is empty.", result.moveToFirst());
    result.close();
  }

  public void testCategoryDeleteFailedTicklistItem() {
    setupDatabase();
    assertFalse(DatabaseAdapter.getCategoriesTable().delete(3));
    Cursor result = DatabaseAdapter.getCategoriesTable().fetch(3);
    assertTrue("Cursor is empty.", result.moveToFirst());
    result.close();
  }

  public void testCategoryUndelete() {
    setupDatabase();
    DatabaseAdapter.getCategoriesTable().delete(4);

    assertTrue(DatabaseAdapter.getCategoriesTable().undelete(4));
    Cursor result = DatabaseAdapter.getCategoriesTable().fetch(4);
    assertTrue("Cursor is empty.", result.moveToFirst());
    assertEquals("Name of undeleted item is wrong.", "Bröd", result.getString(1));
    result.close();
  }

  public void testCategoryFetch() {
    setupDatabase();
    assertTrue(DatabaseAdapter.getCategoriesTable().delete(4));
    Cursor result = DatabaseAdapter.getCategoriesTable().fetch();
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank category is added.
    assertEquals("Number of entries is wrong.", 3, result.getCount());
    assertEquals("Number of columns is wrong.", 3, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));

    assertTrue("Cursor cannot advance.", result.moveToNext());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "Chark", result.getString(1));
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
    setupDatabase();
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
    setupDatabase();
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
    setupDatabase();
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
    setupDatabase();
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
    setupDatabase();
    assertTrue(DatabaseAdapter.getCategoriesTable().delete(4));

    Cursor result = DatabaseAdapter.getCategoriesTable().fetchAll(4);
    assertTrue("Cursor is empty", result.moveToFirst());
    // Upon creation, blank category is added.
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 3, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 4, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "Bröd", result.getString(1));
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
    setupDatabase();
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
    setupDatabase();
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Annat");
    DatabaseAdapter.getCategoriesTable().create(args);

    Cursor result = DatabaseAdapter.getCategoriesTable().fetchStarting("c");
    assertTrue("Cursor is empty", result.moveToFirst());
    result.close();
  }

  public void testCategoryFetchStartingUpper() {
    setupDatabase();
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "annat");
    DatabaseAdapter.getCategoriesTable().create(args);

    Cursor result = DatabaseAdapter.getCategoriesTable().fetchStarting("a");
    assertTrue("Cursor is empty", result.moveToFirst());
    result.close();
  }

  public void testCategoryFetchStartingEmpty() {
    setupDatabase();
    Cursor result = DatabaseAdapter.getCategoriesTable().fetchStarting(null);
    assertTrue("Cursor is empty", result.moveToFirst());
    assertEquals("Number of entries is wrong.", 4, result.getCount());
    assertEquals("Number of columns is wrong.", 2, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    result.close();
  }

  public void testCategoryIsDeleted() {
    setupDatabase();
    DatabaseAdapter.getCategoriesTable().delete(4);
    assertTrue("Item is not deleted.", DatabaseAdapter.getCategoriesTable().isDeleted(4));
    assertFalse("Item is deleted.", DatabaseAdapter.getCategoriesTable().isDeleted(3));
  }

  public void testCategoryIsDeletedNonExisting() {
    assertFalse("Item is deleted.", DatabaseAdapter.getCategoriesTable().isDeleted(4));
  }

  public void testCategoryUpdate() {
    setupDatabase();
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "chark");
    assertTrue(DatabaseAdapter.getCategoriesTable().update(2, args));

    Cursor result =
        mDbAdapter.getDB().query(DatabaseAdapter.getCategoriesTable().getTableName(), null, null,
            null,
            null, null, null, null);
    assertTrue(result.moveToFirst());
    // Upon creation, blank category is added.
    assertTrue("Newly created unit should be at 1.", result.moveToPosition(1));
    assertEquals("Number of entries is wrong.", 4, result.getCount());
    assertEquals("Number of columns is wrong.", 3, result.getColumnCount());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 1 is wrong.", "chark", result.getString(1));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of column 2 is wrong.", "status", result.getColumnName(2));
    result.close();
  }

  public void testCategoryUpdateFailOnStatus() {
    setupDatabase();
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "chark");
    args.put(Key.STATUS, Status.ERROR);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getCategoriesTable().update(2, args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Shouldn't be allowed to update status.", isSuccess);
  }

  public void testCategoryUpdateFailOnId() {
    setupDatabase();
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "chark");
    assertFalse("Should no succeed.", DatabaseAdapter.getCategoriesTable().update(5, args));
  }
}
