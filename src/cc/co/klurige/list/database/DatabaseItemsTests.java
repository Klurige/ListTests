package cc.co.klurige.list.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import cc.co.klurige.list.database.DatabaseAdapter;
import cc.co.klurige.list.database.Table.Key;
import cc.co.klurige.list.database.Table.Status;

public class DatabaseItemsTests extends AndroidTestCase {

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

  public void testItemsCreate() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Mojäng");
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
    assertEquals("Value of column 4 is wrong.", "Mojäng", result.getString(4));
    assertEquals("Name of column 4 is wrong.", "name", result.getColumnName(4));
    assertEquals("Value of column 5 is wrong.", 0, result.getInt(5));
    assertEquals("Name of column 5 is wrong.", "status", result.getColumnName(5));
    result.close();
  }

  public void testItemsCreateMissingUnit() {
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Mojäng");
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
    args.put(Key.NAME, "Mojäng");
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
    args.put(Key.NAME, "Mojäng");
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
    args.put(Key.NAME, "Mojäng");
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
    setupDatabase();
    ContentValues args = new ContentValues();
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
    setupDatabase();
    DatabaseAdapter.getItemsTable().delete(1);
    ContentValues args = new ContentValues();
    args.put(Key.NAME, "Pryl");
    args.put(Key.UNIT, 1);
    args.put(Key.CATEGORY, 1);
    DatabaseAdapter.getItemsTable().create(args);
    Cursor result =
        mDbAdapter.getDB().query(DatabaseAdapter.getItemsTable().getTableName(), null, null,
            null,
            null, null, null, null);
    assertTrue(result.moveToFirst());
    assertEquals("Number of entries is wrong.", 2, result.getCount());
    assertEquals("Number of columns is wrong.", 6, result.getColumnCount());
    assertEquals("Name of column 0 is wrong.", "_id", result.getColumnName(0));
    assertEquals("Name of column 1 is wrong.", "unitid", result.getColumnName(1));
    assertEquals("Name of column 2 is wrong.", "amount", result.getColumnName(2));
    assertEquals("Name of column 3 is wrong.", "catid", result.getColumnName(3));
    assertEquals("Name of column 4 is wrong.", "name", result.getColumnName(4));
    assertEquals("Name of column 5 is wrong.", "status", result.getColumnName(5));
    assertEquals("Value of column 0 is wrong.", 1, result.getLong(0));
    assertEquals("Value of column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Value of column 2 is wrong.", (float) 0.0, result.getFloat(2));
    assertEquals("Value of column 3 is wrong.", 1, result.getLong(3));
    assertEquals("Value of column 4 is wrong.", "Pryl", result.getString(4));
    assertEquals("Value of column 5 is wrong.", 0, result.getInt(5));

    assertTrue("Could not advance the cursor.", result.moveToNext());
    assertEquals("Value of column 0 is wrong.", 2, result.getLong(0));
    assertEquals("Value of column 1 is wrong.", 1, result.getLong(1));
    assertEquals("Value of column 2 is wrong.", (float) 0.0, result.getFloat(2));
    assertEquals("Value of column 3 is wrong.", 1, result.getLong(3));
    assertEquals("Value of column 4 is wrong.", "Sak", result.getString(4));
    assertEquals("Value of column 5 is wrong.", 0, result.getInt(5));
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
}
