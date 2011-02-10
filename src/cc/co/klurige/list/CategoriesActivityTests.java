package cc.co.klurige.list;

import java.sql.SQLException;

import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Context;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import cc.co.klurige.list.database.DatabaseAdapter;
import cc.co.klurige.list.database.Table.Key;

/**
 * Tests to verify and validate the Categories Activity
 * 
 * @author roni
 * 
 *         The activity shows a list of categories.
 *         - Short-click on an entry in the list:
 *         Bring up the edit dialogue.
 *         - Long-click on an entry in the list:
 *         Show a context menu, where it should be possible to
 *         edit or remove an entry.
 * 
 *         There is also an "Add"-button at the bottom of the screen. Pressing
 *         this will let the user add a new category.
 *         The activity is closed through the back-button.
 *         There is no input state, and no return value.
 * 
 *         Add:
 *         Pop up a dialogue where the user can type in a new category, followed
 *         by ok or enter.
 *         If the category already exists, the user will be notified through a
 *         message that needs to be acknowledged.
 * 
 *         Edit:
 *         Pop up a dialogue similar to the one for adding, where the user can
 *         edit the name, which is the only editable field. Upon pressing ok or
 *         enter, the updated name will be checked for duplicates.
 *         If the category already exists, the user will be notified through a
 *         message that needs to be acknowledged.
 * 
 *         Remove:
 *         Pop up a dialogue similar to the one for adding, where the user can
 *         only see the name, but not altering it. Pressing ok or enter will
 *         delete the category from the database.
 **/

public class CategoriesActivityTests extends ActivityInstrumentationTestCase2<CategoriesActivity> {
  CategoriesActivity mActivity;
  Instrumentation    mInstrumentation;
  DatabaseAdapter    mDbAdapter;

  public CategoriesActivityTests() {
    super("cc.co.klurige.list", CategoriesActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mInstrumentation = getInstrumentation();
    Context ctx = mInstrumentation.getTargetContext();
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(ctx);
    try {
      dba.delete();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    mActivity = getActivity();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    mInstrumentation = getInstrumentation();
    Context ctx = mInstrumentation.getTargetContext();
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(ctx);
    dba.close();
  }

  public void testPreconditions() {
    Context ctx = mActivity.getApplicationContext();
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(ctx);
    assertNotNull("DB is null", dba.getDB());
    assertTrue("Database is not open", dba.getDB().isOpen());
    Cursor result = DatabaseAdapter.getCategoriesTable().fetch();
    assertTrue("Categories table is empty", result.moveToFirst());
    assertEquals("Wrong number of entries", 1, result.getCount());
    assertEquals("Wrong value for entry.", "", result.getString(result
        .getColumnIndexOrThrow(Key.NAME)));
    result.close();
  }

  public void testAdd() {
    final View addButton = mActivity.findViewById(cc.co.klurige.list.R.id.categories_add);
    TouchUtils.clickView(this, addButton);
    final Dialog diag = mActivity.mDialog;

    final View input = diag.findViewById(cc.co.klurige.list.R.id.category_dialogue_name);

    TouchUtils.tapView(this, input);
    sendKeys(KeyEvent.KEYCODE_M, KeyEvent.KEYCODE_E, KeyEvent.KEYCODE_J, KeyEvent.KEYCODE_E,
        KeyEvent.KEYCODE_R, KeyEvent.KEYCODE_I);

    final Button okButton = (Button) diag.findViewById(android.R.id.button1);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        okButton.requestFocus();
        okButton.performClick();
      }
    });
    mInstrumentation.waitForIdleSync();

    ListView list = (ListView) mActivity.findViewById(cc.co.klurige.list.R.id.categories_list);
    assertEquals("Number of entries in list is wrong", 2, list.getCount());

    TextView t =
        (TextView) list.getChildAt(0).findViewById(cc.co.klurige.list.R.id.categories_row_name);
    assertEquals("First entry should be empty.", "(empty)", t.getText());
    t = (TextView) list.getChildAt(1).findViewById(cc.co.klurige.list.R.id.categories_row_name);
    assertEquals("Contents of entry is wrong.", "Mejeri", t.getText());
  }

  /**
   * Edit a category by long-clicking and selecting from the context menu.
   */
  public void testMenuEdit() {
    assertFalse("Not implemented.", true);
  }

  /**
   * Edit a category by short-clicking the entry in the list
   */
  public void testListEdit() {
    assertFalse("Not implemented.", true);
  }

  public void testMenuRemove() {
    assertFalse("Not implemented.", true);
  }

}
