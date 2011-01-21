package cc.co.klurige.list.db.test;

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

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testPreConditions() {
    assertNotNull(mDbHelper);
  }
}
