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
    args = new ContentValues();
    args.put(Key.NAME, "Grej");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    itemid = DatabaseAdapter.getItemsTable().create(args);
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

  public void testTickListCreatePicked() {
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
    args.put(Key.PICKED, 0);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Should not have succeeded", isSuccess);
  }

  public void testTickListCreateStatus() {
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
    args.put(Key.STATUS, 0);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Should not have succeeded", isSuccess);
  }

  public void testTickListCreateNoItem() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "matlista");
    long listid = DatabaseAdapter.getListsTable().create(args);

    args = new ContentValues();
    args.put(Key.LIST, listid);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Should not have succeeded", isSuccess);
  }

  public void testTickListCreateNoList() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "matlista");
    DatabaseAdapter.getListsTable().create(args);

    args = new ContentValues();
    args.put(Key.ITEM, itemid);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Should not have succeeded", isSuccess);
  }

  public void testTickListCreateIllegalItem() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "matlista");
    long listid = DatabaseAdapter.getListsTable().create(args);

    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, 1241235);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Should not have succeeded", isSuccess);
  }

  public void testTickListCreateIllegalList() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "matlista");
    DatabaseAdapter.getListsTable().create(args);

    args = new ContentValues();
    args.put(Key.LIST, 12415);
    args.put(Key.ITEM, itemid);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Should not have succeeded", isSuccess);
  }

  public void testTickListCreateIllegalUnit() {
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
    args.put(Key.UNIT, 151351);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Should not have succeeded", isSuccess);
  }

  public void testTickListCreateIllegalCategory() {
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
    args.put(Key.CATEGORY, 151351);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Should not have succeeded", isSuccess);
  }

  public void testTickListCreateDuplicate() {
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
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().create(args);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Should not have succeeded", isSuccess);
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

  // Fetch the entry matching both list and item.
  public void testTicklistFetchListItem() {
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
    long id = DatabaseAdapter.getTicklistsTable().create(args);

    long gotId = DatabaseAdapter.getTicklistsTable().fetchListItem(itemid, listid);
    assertEquals("Entry id is wrong.", id, gotId);
  }

  // Fetch picked entries for all lists.
  public void testTicklistFetchPicked() {
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
    long id = DatabaseAdapter.getTicklistsTable().create(args);
    DatabaseAdapter.getTicklistsTable().pickItem(id);

    args = new ContentValues();
    args.put(Key.NAME, "Grej");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    id = DatabaseAdapter.getTicklistsTable().create(args);

    args = new ContentValues();
    args.put(Key.NAME, "Sak");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "bygglista");
    listid = DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    id = DatabaseAdapter.getTicklistsTable().create(args);
    DatabaseAdapter.getTicklistsTable().pickItem(id);

    args = new ContentValues();
    args.put(Key.NAME, "Mojt");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    id = DatabaseAdapter.getTicklistsTable().create(args);

    Cursor result = DatabaseAdapter.getTicklistsTable().fetchPickedItems(listid);
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("Wrong number of entries", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 1, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 0 is wrong.", 3, result.getLong(0));
    result.close();

    result = DatabaseAdapter.getTicklistsTable().fetchPickedItems(null);
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("Wrong number of entries", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 1, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertTrue("Could not advance cursor", result.moveToNext());
    assertEquals("Value of column 0 is wrong.", 3, result.getLong(0));
    result.close();
  }

  // Fetch picked entries for all lists.
  public void testTicklistFetchUnPicked() {
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
    long id = DatabaseAdapter.getTicklistsTable().create(args);
    DatabaseAdapter.getTicklistsTable().pickItem(id);

    args = new ContentValues();
    args.put(Key.NAME, "Grej");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    id = DatabaseAdapter.getTicklistsTable().create(args);

    args = new ContentValues();
    args.put(Key.NAME, "Sak");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "bygglista");
    listid = DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    id = DatabaseAdapter.getTicklistsTable().create(args);
    DatabaseAdapter.getTicklistsTable().pickItem(id);

    args = new ContentValues();
    args.put(Key.NAME, "Mojt");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    id = DatabaseAdapter.getTicklistsTable().create(args);

    Cursor result = DatabaseAdapter.getTicklistsTable().fetchUnpickedItems();
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("Wrong number of entries", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 1, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertTrue("Could not advance cursor", result.moveToNext());
    assertEquals("Value of column 0 is wrong.", 4, result.getLong(0));
    result.close();
  }

  // Fetch entries for all lists.
  public void testTicklistFetchItemAllLists() {
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
    long id = DatabaseAdapter.getTicklistsTable().create(args);
    DatabaseAdapter.getTicklistsTable().pickItem(id);

    args = new ContentValues();
    args.put(Key.NAME, "Grej");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    id = DatabaseAdapter.getTicklistsTable().create(args);

    args = new ContentValues();
    args.put(Key.NAME, "Sak");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "bygglista");
    listid = DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    id = DatabaseAdapter.getTicklistsTable().create(args);
    DatabaseAdapter.getTicklistsTable().pickItem(id);

    args = new ContentValues();
    args.put(Key.NAME, "Mojt");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    id = DatabaseAdapter.getTicklistsTable().create(args);

    Cursor result = DatabaseAdapter.getTicklistsTable().fetchPickedItems(listid);
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("Wrong number of entries", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 1, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    result.close();
  }

  public void testTicklistFetchItemAsStrings() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "styck");
    long unitId = DatabaseAdapter.getUnitsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "blandat");
    long catId = DatabaseAdapter.getCategoriesTable().create(args);

    args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, unitId);
    args.put(Key.CATEGORY, catId);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "matlista");
    long listid = DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    DatabaseAdapter.getTicklistsTable().create(args);

    Cursor result = DatabaseAdapter.getTicklistsTable().fetchItemAsStrings(listid, itemid);
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("Wrong number of entries", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 6, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of column 1 is wrong.", "items_name", result.getColumnName(1));
    assertEquals("Name of column 2 is wrong.", "ticklist_amount", result.getColumnName(2));
    assertEquals("Name of column 3 is wrong.", "units_name", result.getColumnName(3));
    assertEquals("Name of column 4 is wrong.", "categories_name", result.getColumnName(4));
    assertEquals("Name of column 5 is wrong.", "ticklist_picked", result.getColumnName(5));
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Value of column 1 is wrong.", "Pryl", result.getString(1));
    assertEquals("Value of column 2 is wrong.", (float) 0.0, result.getFloat(2));
    assertEquals("Value of column 3 is wrong.", "styck", result.getString(3));
    assertEquals("Value of column 4 is wrong.", "blandat", result.getString(4));
    assertEquals("Value of column 5 is wrong.", 0, result.getLong(5));
    result.close();
  }

  public void testTicklistFetchUnpickedItemAsStrings() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "styck");
    long unitId = DatabaseAdapter.getUnitsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "blandat");
    long catId = DatabaseAdapter.getCategoriesTable().create(args);

    args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, unitId);
    args.put(Key.CATEGORY, catId);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "matlista");
    long listid = DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    DatabaseAdapter.getTicklistsTable().create(args);

    Cursor result = DatabaseAdapter.getTicklistsTable().fetchUnpickedItemsAsStrings(listid);
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("Wrong number of entries", 1, result.getCount());
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
    assertEquals("Value of column 3 is wrong.", "styck", result.getString(3));
    assertEquals("Value of column 4 is wrong.", "blandat", result.getString(4));
    assertEquals("Value of column 5 is wrong.", 0, result.getLong(5));
    result.close();
  }

  // Fetch deleted entries for all lists.
  public void testTicklistFetchDeletedItemAllLists() {
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
    long id = DatabaseAdapter.getTicklistsTable().create(args);
    DatabaseAdapter.getTicklistsTable().pickItem(id);

    args = new ContentValues();
    args.put(Key.NAME, "Grej");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    id = DatabaseAdapter.getTicklistsTable().create(args);
    DatabaseAdapter.getTicklistsTable().delete(id);

    args = new ContentValues();
    args.put(Key.NAME, "Sak");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "bygglista");
    listid = DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    id = DatabaseAdapter.getTicklistsTable().create(args);
    DatabaseAdapter.getTicklistsTable().pickItem(id);
    DatabaseAdapter.getTicklistsTable().delete(id);

    args = new ContentValues();
    args.put(Key.NAME, "Mojt");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    id = DatabaseAdapter.getTicklistsTable().create(args);

    Cursor result = DatabaseAdapter.getTicklistsTable().fetchDeletedItems(listid);
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("Wrong number of entries", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 1, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 0 is wrong", 3, result.getLong(0));
    result.close();
    result = DatabaseAdapter.getTicklistsTable().fetchDeletedItems(null);
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("Wrong number of entries", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 1, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 0 is wrong", 2, result.getLong(0));
    assertTrue("Could not advance cursor", result.moveToNext());
    assertEquals("Value of column 0 is wrong", 3, result.getLong(0));
    result.close();
  }

  public void testTicklistFetchDeletedString() {
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().fetchDeleted("Anything");
    } catch (IllegalAccessError e) {
      isSuccess = true;
    }
    assertTrue("FetchDeleted should not succeed.", isSuccess);
  }

  public void testTicklistFetchStartingString() {
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().fetchStarting("Anything");
    } catch (IllegalAccessError e) {
      isSuccess = true;
    }
    assertTrue("FetchStarting should not succeed.", isSuccess);
  }

  public void testTicklistIsItemInList() {
    setupDatabase();
    assertTrue("Item is not in list", DatabaseAdapter.getTicklistsTable().isItemInTicklist(1, 1));
  }

  public void testTicklistIsItemInListFalse() {
    setupDatabase();
    assertFalse("Item is in list", DatabaseAdapter.getTicklistsTable().isItemInTicklist(3, 1));
  }

  public void testTicklistIsItemPicked() {
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
    long id = DatabaseAdapter.getTicklistsTable().create(args);
    assertFalse("Picked.", DatabaseAdapter.getTicklistsTable().isItemPicked(id));
    DatabaseAdapter.getTicklistsTable().pickItem(id);
    assertTrue("Not picked.", DatabaseAdapter.getTicklistsTable().isItemPicked(id));
  }

  public void testTicklistIsItemPickedIllegal() {
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().isItemPicked(515135);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Could not detect illegal entry id.", isSuccess);
  }

  public void testTicklistIsItemPickedInAnyList() {
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
    long id = DatabaseAdapter.getTicklistsTable().create(args);
    assertTrue("Not picked.", DatabaseAdapter.getTicklistsTable().isItemUnPickedInAnyList(itemid));
    DatabaseAdapter.getTicklistsTable().pickItem(id);
    assertFalse("Picked.", DatabaseAdapter.getTicklistsTable().isItemUnPickedInAnyList(itemid));
  }

  // Fetch the entry matching both list and item and is deleted.
  public void testTicklistFetchListItemDeleted() {
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
    long id = DatabaseAdapter.getTicklistsTable().create(args);
    DatabaseAdapter.getTicklistsTable().delete(id);
    long gotId = DatabaseAdapter.getTicklistsTable().fetchListItemDeleted(itemid, listid);
    assertEquals("Entry id is wrong.", id, gotId);
  }

  // Delete an already deleted entry.
  public void testTicklistDeleteDeleted() {
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
    long id = DatabaseAdapter.getTicklistsTable().create(args);
    assertTrue("Could not delete", DatabaseAdapter.getTicklistsTable().delete(id));
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().delete(id);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Managed to delete deleted entry.", isSuccess);
  }

  // Undelete an entry when the list was also deleted.
  public void testTicklistUndeleteDeletedList() {
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
    long id = DatabaseAdapter.getTicklistsTable().create(args);
    assertTrue("Could not delete", DatabaseAdapter.getTicklistsTable().delete(id));
    assertTrue("Could not delete list", DatabaseAdapter.getListsTable().delete(listid));
    assertTrue("Could not delete", DatabaseAdapter.getTicklistsTable().undelete(id));
  }

  // Undelete a picked entry into an empty list.
  public void testTicklistUndeletePickedEmpty() {
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
    assertTrue("Not empty", DatabaseAdapter.getListsTable().isEmpty(listid));
    long id = DatabaseAdapter.getTicklistsTable().create(args);
    assertFalse("Empty", DatabaseAdapter.getListsTable().isEmpty(listid));
    assertFalse("Completed", DatabaseAdapter.getListsTable().isCompleted(listid));
    DatabaseAdapter.getTicklistsTable().pickItem(id);
    assertTrue("Not completed", DatabaseAdapter.getListsTable().isCompleted(listid));
    assertTrue("Could not delete", DatabaseAdapter.getTicklistsTable().delete(id));
    assertTrue("Not empty", DatabaseAdapter.getListsTable().isEmpty(listid));
    assertTrue("Could not undelete", DatabaseAdapter.getTicklistsTable().undelete(id));
    assertTrue("Not completed", DatabaseAdapter.getListsTable().isCompleted(listid));
  }

  // Undelete a picked entry into an empty list. Same again, but this time
  // another
  // entry is added and then removed, to validate list states.
  public void testTicklistUndeletePickedEmpty2() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Sak");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long item2id = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "matlista");
    long listid = DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    assertTrue("Not empty", DatabaseAdapter.getListsTable().isEmpty(listid));
    long id = DatabaseAdapter.getTicklistsTable().create(args);
    assertFalse("Empty", DatabaseAdapter.getListsTable().isEmpty(listid));
    assertFalse("Completed", DatabaseAdapter.getListsTable().isCompleted(listid));
    DatabaseAdapter.getTicklistsTable().pickItem(id);
    assertTrue("Not completed", DatabaseAdapter.getListsTable().isCompleted(listid));
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, item2id);
    long id2 = DatabaseAdapter.getTicklistsTable().create(args);
    assertFalse("Empty", DatabaseAdapter.getListsTable().isEmpty(listid));
    assertFalse("Completed", DatabaseAdapter.getListsTable().isCompleted(listid));
    assertTrue("Could not delete", DatabaseAdapter.getTicklistsTable().delete(id));
    assertTrue("Could not delete", DatabaseAdapter.getTicklistsTable().delete(id2));
    assertTrue("Not empty", DatabaseAdapter.getListsTable().isEmpty(listid));
    assertFalse("Completed", DatabaseAdapter.getListsTable().isCompleted(listid));
    assertTrue("Could not undelete", DatabaseAdapter.getTicklistsTable().undelete(id));
    assertFalse("Empty", DatabaseAdapter.getListsTable().isEmpty(listid));
    assertTrue("Not completed", DatabaseAdapter.getListsTable().isCompleted(listid));
  }

  // Undelete a picked entry.
  public void testTicklistUndeletePicked() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Sak");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long item2id = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "matlista");
    long listid = DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    assertTrue("Not empty", DatabaseAdapter.getListsTable().isEmpty(listid));
    long id = DatabaseAdapter.getTicklistsTable().create(args);
    assertFalse("Empty", DatabaseAdapter.getListsTable().isEmpty(listid));
    assertFalse("Completed", DatabaseAdapter.getListsTable().isCompleted(listid));
    DatabaseAdapter.getTicklistsTable().pickItem(id);
    assertTrue("Not completed", DatabaseAdapter.getListsTable().isCompleted(listid));
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, item2id);
    DatabaseAdapter.getTicklistsTable().create(args);
    assertFalse("Empty", DatabaseAdapter.getListsTable().isEmpty(listid));
    assertFalse("Completed", DatabaseAdapter.getListsTable().isCompleted(listid));
    assertTrue("Could not delete", DatabaseAdapter.getTicklistsTable().delete(id));
    assertFalse("Empty", DatabaseAdapter.getListsTable().isEmpty(listid));
    assertFalse("Completed", DatabaseAdapter.getListsTable().isCompleted(listid));
    assertTrue("Could not undelete", DatabaseAdapter.getTicklistsTable().undelete(id));
    assertFalse("Empty", DatabaseAdapter.getListsTable().isEmpty(listid));
    assertFalse("Completed", DatabaseAdapter.getListsTable().isCompleted(listid));
  }

  // Undelete a picked entry into a completed list.
  public void testTicklistUndeletePickedCompleted() {
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
    long id = DatabaseAdapter.getTicklistsTable().create(args);
    DatabaseAdapter.getTicklistsTable().pickItem(id);

    args = new ContentValues();
    args.put(Key.NAME, "Sak");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    id = DatabaseAdapter.getTicklistsTable().create(args);
    DatabaseAdapter.getTicklistsTable().pickItem(id);

    assertTrue("Not completed", DatabaseAdapter.getListsTable().isCompleted(listid));
    assertTrue("Could not delete", DatabaseAdapter.getTicklistsTable().delete(id));
    assertTrue("Not completed", DatabaseAdapter.getListsTable().isCompleted(listid));
    assertTrue("Could not undelete", DatabaseAdapter.getTicklistsTable().undelete(id));
    assertTrue("Not completed", DatabaseAdapter.getListsTable().isCompleted(listid));
  }

  // Create an already deleted entry.
  public void testTicklistCreateDeleted() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Sak");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "matlista");
    long listid = DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    long id = DatabaseAdapter.getTicklistsTable().create(args);
    assertTrue("Could not delete", DatabaseAdapter.getTicklistsTable().delete(id));
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    long id2 = DatabaseAdapter.getTicklistsTable().create(args);
    assertEquals("Ids don't match.", id, id2);
  }

  // Delete one of a few entries from a list, making the list completed.
  public void testTicklistDeleteListComplete() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Sak");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long item2id = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "matlista");
    long listid = DatabaseAdapter.getListsTable().create(args);
    assertTrue("Not empty", DatabaseAdapter.getListsTable().isEmpty(listid));
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    long id = DatabaseAdapter.getTicklistsTable().create(args);
    assertFalse("Empty", DatabaseAdapter.getListsTable().isEmpty(listid));
    assertFalse("Completed", DatabaseAdapter.getListsTable().isCompleted(listid));
    DatabaseAdapter.getTicklistsTable().pickItem(id);
    assertTrue("Not completed", DatabaseAdapter.getListsTable().isCompleted(listid));
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, item2id);
    DatabaseAdapter.getTicklistsTable().create(args);
    assertFalse("Empty", DatabaseAdapter.getListsTable().isEmpty(listid));
    assertFalse("Completed", DatabaseAdapter.getListsTable().isCompleted(listid));
    assertTrue("Could not delete", DatabaseAdapter.getTicklistsTable().delete(id));
    assertFalse("Empty", DatabaseAdapter.getListsTable().isEmpty(listid));
    assertFalse("Completed", DatabaseAdapter.getListsTable().isCompleted(listid));
  }

  // Pick a non-existent entry.
  public void testTicklistPickIllegal() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    long itemid = DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "Sak");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    args = new ContentValues();
    args.put(Key.NAME, "matlista");
    long listid = DatabaseAdapter.getListsTable().create(args);
    args = new ContentValues();
    args.put(Key.LIST, listid);
    args.put(Key.ITEM, itemid);
    DatabaseAdapter.getTicklistsTable().create(args);
    boolean isSuccess = false;
    try {
      DatabaseAdapter.getTicklistsTable().pickItem(5);
    } catch (IllegalArgumentException e) {
      isSuccess = true;
    }
    assertTrue("Managed to pick non-existent entry.", isSuccess);
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

  public void testTicklistFetchAllItemsAllLists() {
    setupDatabase();
    Cursor result = DatabaseAdapter.getTicklistsTable().fetchItems(null);
    assertTrue(result.moveToFirst());
    assertEquals("Wrong number of entries", 2, result.getCount());
    assertEquals("Wrong number of columns", 1, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Value of column 0 is wrong", 1, result.getLong(0));
    assertTrue("Could not advance the cursor", result.moveToNext());
    assertEquals("Value of column 0 is wrong", 2, result.getLong(0));
    assertFalse("Could advance the cursor", result.moveToNext());
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
    Cursor result = DatabaseAdapter.getTicklistsTable().fetchTicklistEntryAsStrings(1);
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 1, result.getCount());
    assertEquals("Number of columns is wrong.", 6, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of column 1 is wrong.", "name", result.getColumnName(1));
    assertEquals("Name of column 2 is wrong.", "amount", result.getColumnName(2));
    assertEquals("Name of column 3 is wrong.", "units_name", result.getColumnName(3));
    assertEquals("Name of column 4 is wrong.", "categories_name", result.getColumnName(4));
    assertEquals("Name of column 5 is wrong.", "picked", result.getColumnName(5));
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Value of column 1 is wrong.", "Pryl", result.getString(1));
    assertEquals("Value of column 2 is wrong.", (float) 0.0, result.getFloat(2));
    assertEquals("Value of column 3 is wrong.", "", result.getString(3));
    assertEquals("Value of column 4 is wrong.", "", result.getString(4));
    assertEquals("Value of column 5 is wrong.", 0, result.getLong(5));
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
