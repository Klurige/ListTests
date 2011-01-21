package cc.co.klurige.list.test;

import android.test.AndroidTestCase;
import android.test.mock.MockContext;
import cc.co.klurige.list.database.DatabaseAdapter;

public class DatabaseTests extends AndroidTestCase {

  private DatabaseAdapter mDbHelper;
  private MockContext     mCtx;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mCtx = new MockContext();
    setContext(mCtx);
    // mDbHelper = DatabaseAdapter.getDatabaseAdapter(mCtx);

    // mDbHelper.open();

  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    // mDbHelper.delete();
  }

  public void testPreConditions() {
    assertFalse(true);
  }
}
