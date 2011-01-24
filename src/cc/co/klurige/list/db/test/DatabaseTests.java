package cc.co.klurige.list.db.test;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import cc.co.klurige.list.database.DatabaseAdapter;

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
