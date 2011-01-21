package cc.co.klurige.list.db.test;

import java.sql.SQLException;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import cc.co.klurige.list.database.Categories;
import cc.co.klurige.list.database.DatabaseAdapter;
import cc.co.klurige.list.database.Items;
import cc.co.klurige.list.database.Lists;
import cc.co.klurige.list.database.Ticklists;
import cc.co.klurige.list.database.Units;

public class DatabaseTests extends AndroidTestCase {

  private DatabaseAdapter mDbHelper;
  private Context         mCtx;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mCtx = new IsolatedContext(null, this.getContext());
    setContext(mCtx);
    mDbHelper = DatabaseAdapter.getDatabaseAdapter(mCtx);

    mDbHelper.open();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    mDbHelper.close();
    mDbHelper.delete();
  }

  public void testPreConditions() {
    assertNotNull(mDbHelper);
    assertTrue(mDbHelper.getDB().isOpen());
  }

  public void testDeleteNotAllowed() {
    String msg = null;
    try {
      mDbHelper.delete();
    } catch (SQLException e) {
      // Database is open, so it should throw.
      msg = e.getMessage();
    }

    assertEquals("Should not have succeeded", "Could not delete database. Not closed.", msg);
  }

  public void testGetCategoriesTable() {
    Categories tbl = mDbHelper.getCategoriesTable();
    assertNotNull(tbl);
  }

  public void testGetUnitsTable() {
    Units tbl = mDbHelper.getUnitsTable();
    assertNotNull(tbl);
  }

  public void testGetItemsTable() {
    Items tbl = mDbHelper.getItemsTable();
    assertNotNull(tbl);
  }

  public void testGetTicklistsTable() {
    Ticklists tbl = mDbHelper.getTicklistsTable();
    assertNotNull(tbl);
  }

  public void testGetListsTable() {
    Lists tbl = mDbHelper.getListsTable();
    assertNotNull(tbl);
  }
}
