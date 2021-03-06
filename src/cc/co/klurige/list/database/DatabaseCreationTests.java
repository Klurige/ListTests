package cc.co.klurige.list.database;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;

public class DatabaseCreationTests extends AndroidTestCase {

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
  }

  public void testCreation() {
    boolean isSucceeded;
    try {
      mDbAdapter.delete();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    mDbAdapter.open();
    isSucceeded = mDbAdapter.getDB().isOpen();
    mDbAdapter.close();
    assertTrue("Couldn't create the database.", isSucceeded);
  }

  public void testDeleteNotAllowed() {
    mDbAdapter.open();
    String msg = null;
    try {
      mDbAdapter.delete();
    } catch (SQLException e) {
      // Database is open, so it should throw.
      msg = e.getMessage();
    }

    mDbAdapter.close();

    assertEquals("Should not have succeeded", "Could not delete database. Not closed.", msg);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    assertTrue("Database was not properly closed.", (mDbAdapter.getDB() == null));
    try {
      mDbAdapter.delete();
    } catch (SQLException e) {
      e.printStackTrace();
    }
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

  private class DatabaseHelperIllegalVersion extends SQLiteOpenHelper {
    DatabaseHelperIllegalVersion(final Context context) {
      super(context, "ticklist_db", null, Integer.MAX_VALUE);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {}

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
        final int newVersion) {}
  }

  public void testUpgradeToIllegal() {
    try {
      mDbAdapter.delete();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    mDbAdapter.open();
    mDbAdapter.close();

    SQLiteOpenHelper mDbHelper = new DatabaseHelperIllegalVersion(mCtx);
    mDbHelper.getWritableDatabase();
    mDbHelper.close();

    boolean isSuccess = false;
    try {
      mDbAdapter.open();
    } catch (IllegalStateException e) {
      isSuccess = true;
    }
    mDbAdapter.close();
    try {
      mDbAdapter.delete();
    } catch (SQLException e) {
      assertTrue("Could not delete database.", true);
      e.printStackTrace();
    }
    assertTrue("Upgrade should have failed.", isSuccess);
  }

  public void testStaticClasses() {
    Table.Key key = new Table.Key();
    assertNotNull("Object could not be instantiated", key);
    Table.Error error = new Table.Error();
    assertNotNull("Object could not be instantiated", error);
    Table.Status status = new Table.Status();
    assertNotNull("Object could not be instantiated", status);
  }
}
