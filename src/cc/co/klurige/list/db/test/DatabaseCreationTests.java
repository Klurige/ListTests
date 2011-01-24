package cc.co.klurige.list.db.test;

import java.sql.SQLException;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import cc.co.klurige.list.database.DatabaseAdapter;

public class DatabaseCreationTests extends AndroidTestCase {

  private DatabaseAdapter mDbAdapter;
  private Context         mCtx;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mCtx = new IsolatedContext(null, this.getContext());
    setContext(mCtx);
    mDbAdapter = DatabaseAdapter.getDatabaseAdapter(mCtx);
  }

  public void testCreation() {
    boolean isSucceeded;
    try {
      mDbHelper.delete();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    mDbHelper.open();
    isSucceeded = mDbHelper.getDB().isOpen();
    mDbHelper.close();
    assertTrue("Couldn't create the database.", isSucceeded);
  }

  public void testDeleteNotAllowed() {
    mDbHelper.open();
    String msg = null;
    try {
      mDbHelper.delete();
    } catch (SQLException e) {
      // Database is open, so it should throw.
      msg = e.getMessage();
    }

    mDbHelper.close();

    assertEquals("Should not have succeeded", "Could not delete database. Not closed.", msg);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    assertTrue("Database was not properly closed.", (mDbAdapter.getDB() == null));
  }

  public void testPreConditions() {
    assertNotNull(mDbAdapter);
  }

  public void testUpgradeFrom1() {
    try {
      mDbAdapter.delete();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    DatabaseHelper_ver1 mDbHelper = new DatabaseHelper_ver1(mCtx);
    mDbHelper.getWritableDatabase();
    mDbHelper.close();

    mDbAdapter.open();
    boolean isSucceeded = mDbAdapter.getDB().isOpen();
    mDbAdapter.close();
    assertTrue("Couldn't upgrade the database from  version 1.", isSucceeded);
  }

  public void testUpgradeFrom2() {
    try {
      mDbAdapter.delete();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    DatabaseHelper_ver2 mDbHelper = new DatabaseHelper_ver2(mCtx);
    mDbHelper.getWritableDatabase();
    mDbHelper.close();

    mDbAdapter.open();
    boolean isSucceeded = mDbAdapter.getDB().isOpen();
    mDbAdapter.close();
    assertTrue("Couldn't upgrade the database from  version 2.", isSucceeded);
  }

}
