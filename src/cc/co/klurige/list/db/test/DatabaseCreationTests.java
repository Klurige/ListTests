package cc.co.klurige.list.db.test;

import java.sql.SQLException;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import cc.co.klurige.list.database.DatabaseAdapter;

public class DatabaseCreationTests extends AndroidTestCase {

  private DatabaseAdapter mDbHelper;
  private Context         mCtx;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mCtx = new IsolatedContext(null, this.getContext());
    setContext(mCtx);
    mDbHelper = DatabaseAdapter.getDatabaseAdapter(mCtx);
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

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    assertTrue("Database was not properly closed.", (mDbHelper.getDB() == null));
  }

  public void testPreConditions() {
    assertNotNull(mDbHelper);
  }
}
