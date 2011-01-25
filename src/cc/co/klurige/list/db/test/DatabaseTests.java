package cc.co.klurige.list.db.test;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import cc.co.klurige.list.database.DatabaseAdapter;
import cc.co.klurige.list.database.Table.Key;

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
    assertEquals("Number of units is wrong.", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 3, result.getColumnCount());
    assertEquals("Value of unit column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Name of unit column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of unit column 1 is wrong.", "kg", result.getString(1));
    assertEquals("Name of unit column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of unit column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of unit column 2 is wrong.", "status", result.getColumnName(2));
    result.close();
  }

  public void testUnitDelete() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "kg");
    DatabaseAdapter.getUnitsTable().create(args);

    assertTrue(DatabaseAdapter.getUnitsTable().delete(1));
    Cursor result = DatabaseAdapter.getUnitsTable().fetch();
    assertFalse("Cursor is not empty.", result.moveToFirst());
    result.close();
  }

  public void testUnitUndelete() {
    assertFalse("Test not implemented.", true);
  }

  public void testUnitFetch() {
    assertFalse("Test not implemented.", true);
  }

  public void testUnitFetchOnName() {
    assertFalse("Test not implemented.", true);
  }

  public void testUnitFetchOnId() {
    assertFalse("Test not implemented.", true);
  }

  public void testUnitFetchAll() {
    assertFalse("Test not implemented.", true);
  }

  public void testUnitFetchDeleted() {
    assertFalse("Test not implemented.", true);
  }

  public void testUnitFetchDeletedOnName() {
    assertFalse("Test not implemented.", true);
  }

  public void testUnitFetchDeletedOnId() {
    assertFalse("Test not implemented.", true);
  }

  public void testUnitFetchStarting() {
    assertFalse("Test not implemented.", true);
  }

  public void testUnitIsDeleted() {
    assertFalse("Test not implemented.", true);
  }

  public void testUnitUpdate() {
    assertFalse("Test not implemented.", true);
  }

  public void testCategoryCreate() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "chark");
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
    assertEquals("Value of category column 1 is wrong.", "chark", result.getString(1));
    assertEquals("Name of category column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of category column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of category column 2 is wrong.", "status", result.getColumnName(2));
    result.close();
  }

  public void testCategoryDelete() {
    assertFalse("Test not implemented.", true);
  }

  public void testCategoryUndelete() {
    assertFalse("Test not implemented.", true);
  }

  public void testCategoryFetch() {
    assertFalse("Test not implemented.", true);
  }

  public void testCategoryFetchOnName() {
    assertFalse("Test not implemented.", true);
  }

  public void testCategoryFetchOnId() {
    assertFalse("Test not implemented.", true);
  }

  public void testCategoryFetchAll() {
    assertFalse("Test not implemented.", true);
  }

  public void testCategoryFetchDeleted() {
    assertFalse("Test not implemented.", true);
  }

  public void testCategoryFetchDeletedOnName() {
    assertFalse("Test not implemented.", true);
  }

  public void testCategoryFetchDeletedOnId() {
    assertFalse("Test not implemented.", true);
  }

  public void testCategoryFetchStarting() {
    assertFalse("Test not implemented.", true);
  }

  public void testCategoryIsDeleted() {
    assertFalse("Test not implemented.", true);
  }

  public void testCategoryUpdate() {
    assertFalse("Test not implemented.", true);
  }

  public void testListCreate() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "matlista");
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
    assertEquals("Value of list should column 1 is wrong.", "matlista", result.getString(1));
    assertEquals("Name of list column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Value of list column 2 is wrong.", 0, result.getInt(2));
    assertEquals("Name of list column 2 is wrong.", "status", result.getColumnName(2));
    assertTrue("Value of list column 3 is wrong.", (result.getLong(3) > ts_before.getTime())
        && (result.getLong(3) < ts_after.getTime()));
    assertEquals("Name of list column 3 is wrong.", "timestamp", result.getColumnName(3));
    result.close();
  }

  public void testListsDelete() {
    assertFalse("Test not implemented.", true);
  }

  public void testListsUndelete() {
    assertFalse("Test not implemented.", true);
  }

  public void testListsFetch() {
    assertFalse("Test not implemented.", true);
  }

  public void testListsFetchOnName() {
    assertFalse("Test not implemented.", true);
  }

  public void testListsFetchOnId() {
    assertFalse("Test not implemented.", true);
  }

  public void testListsFetchAll() {
    assertFalse("Test not implemented.", true);
  }

  public void testListsFetchDeleted() {
    assertFalse("Test not implemented.", true);
  }

  public void testListsFetchDeletedOnName() {
    assertFalse("Test not implemented.", true);
  }

  public void testListsFetchDeletedOnId() {
    assertFalse("Test not implemented.", true);
  }

  public void testListsFetchStarting() {
    assertFalse("Test not implemented.", true);
  }

  public void testListsIsDeleted() {
    assertFalse("Test not implemented.", true);
  }

  public void testListsUpdate() {
    assertFalse("Test not implemented.", true);
  }

  public void testItemCreate() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    Cursor result =
        mDbAdapter.getDB().query(DatabaseAdapter.getItemsTable().getTableName(), null, null, null,
            null, null, null, null);

    assertTrue(result.moveToFirst());
    assertEquals("Number of items is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 6, result.getColumnCount());
    assertEquals("Value of items column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Name of items column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of items column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Name of items column 1 is wrong.", "unitid", result.getColumnName(1));
    assertEquals("Value of items column 2 is wrong.", (float) 0.0, result.getFloat(2));
    assertEquals("Name of items column 2 is wrong.", "amount", result.getColumnName(2));
    assertEquals("Value of items column 3 is wrong.", 1, result.getLong(3));
    assertEquals("Name of items column 3 is wrong.", "catid", result.getColumnName(3));
    assertEquals("Value of items column 4 is wrong.", "Pryl", result.getString(4));
    assertEquals("Name of items column 4 is wrong.", "name", result.getColumnName(4));
    assertEquals("Value of items column 5 is wrong.", 0, result.getInt(5));
    assertEquals("Name of items column 5 is wrong.", "status", result.getColumnName(5));
    result.close();
  }

  public void testItemsDelete() {
    assertFalse("Test not implemented.", true);
  }

  public void testItemsUndelete() {
    assertFalse("Test not implemented.", true);
  }

  public void testItemsFetch() {
    assertFalse("Test not implemented.", true);
  }

  public void testItemsFetchOnName() {
    assertFalse("Test not implemented.", true);
  }

  public void testItemsFetchOnId() {
    assertFalse("Test not implemented.", true);
  }

  public void testItemsFetchAll() {
    assertFalse("Test not implemented.", true);
  }

  public void testItemsFetchDeleted() {
    assertFalse("Test not implemented.", true);
  }

  public void testItemsFetchDeletedOnName() {
    assertFalse("Test not implemented.", true);
  }

  public void testItemsFetchDeletedOnId() {
    assertFalse("Test not implemented.", true);
  }

  public void testItemsFetchStarting() {
    assertFalse("Test not implemented.", true);
  }

  public void testItemsIsDeleted() {
    assertFalse("Test not implemented.", true);
  }

  public void testItemsUpdate() {
    assertFalse("Test not implemented.", true);
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
    assertEquals("Number of items is wrong.", 1, result.getCount());
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
    assertFalse("Test not implemented.", true);
  }

  public void testTicklistUndelete() {
    assertFalse("Test not implemented.", true);
  }

  public void testTicklistFetch() {
    assertFalse("Test not implemented.", true);
  }

  public void testTicklistFetchOnName() {
    assertFalse("Test not implemented.", true);
  }

  public void testTicklistFetchOnId() {
    assertFalse("Test not implemented.", true);
  }

  public void testTicklistFetchAll() {
    assertFalse("Test not implemented.", true);
  }

  public void testTicklistFetchDeleted() {
    assertFalse("Test not implemented.", true);
  }

  public void testTicklistFetchDeletedOnName() {
    assertFalse("Test not implemented.", true);
  }

  public void testTicklistFetchDeletedOnId() {
    assertFalse("Test not implemented.", true);
  }

  public void testTicklistFetchStarting() {
    assertFalse("Test not implemented.", true);
  }

  public void testTicklistIsDeleted() {
    assertFalse("Test not implemented.", true);
  }

  public void testTicklistUpdate() {
    assertFalse("Test not implemented.", true);
  }
}
