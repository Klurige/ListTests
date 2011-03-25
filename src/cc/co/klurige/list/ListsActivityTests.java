package cc.co.klurige.list;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Context;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import cc.co.klurige.list.database.DatabaseAdapter;

/**
 * Tests to verify and validate the Lists Activity
 * 
 * @author roni
 * 
 *         The activity shows a list of lists.
 *         - Short-click on an entry in the list:
 *         Bring up the edit dialogue.
 *         - Long-click on an entry in the list:
 *         Show a context menu, where it should be possible to
 *         edit or remove an entry.
 * 
 *         There is also an "Add"-button at the bottom of the screen. Pressing
 *         this will let the user add a new list.
 *         The activity is closed through the back-button.
 *         There is no input state, and no return value.
 * 
 *         Add:
 *         Pop up a dialogue where the user can type in a new list, followed
 *         by ok or enter.
 *         A name will be proposed, which is the date, plus possibly a
 *         sequence number.
 *         If the list already exists, the user will be notified through a
 *         message that needs to be acknowledged.
 * 
 *         Edit:
 *         Pop up a dialogue similar to the one for adding, where the user can
 *         edit the name, which is the only editable field. Upon pressing ok or
 *         enter, the updated name will be checked for duplicates.
 *         If the list already exists, the user will be notified through a
 *         message that needs to be acknowledged.
 * 
 *         Remove:
 *         Pop up a dialogue similar to the one for adding, where the user can
 *         only see the name, but not altering it. Pressing ok or enter will
 *         delete the list from the database.
 **/

public class ListsActivityTests extends ActivityInstrumentationTestCase2<ListsActivity> {
  ListsActivity   mActivity;
  Instrumentation mInstrumentation;
  DatabaseAdapter mDbAdapter;

  public ListsActivityTests() {
    super("cc.co.klurige.list", ListsActivity.class);
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
    assertNotNull("Context is null", ListsActivity.getContext());
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    mActivity = null;
    Thread.sleep(1000);
    mInstrumentation = getInstrumentation();
    Context ctx = mInstrumentation.getTargetContext();
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(ctx);
    dba.close();
    assertNull("Context is not null", ListsActivity.getContext());
  }

  public void testAddCancel() {
    final View addButton = mActivity.findViewById(cc.co.klurige.list.R.id.lists_add);
    TouchUtils.clickView(this, addButton);
    final Dialog diag = mActivity.getDialog();

    final Button cancelButton = (Button) diag.findViewById(android.R.id.button3);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        cancelButton.requestFocus();
        cancelButton.performClick();
      }
    });
    mInstrumentation.waitForIdleSync();

    ListView list = (ListView) mActivity.findViewById(cc.co.klurige.list.R.id.lists_list);
    assertEquals("Number of entries in list is wrong", 0, list.getCount());
    assertEquals("List should be GONE", View.GONE, list.getVisibility());
  }

  public void testPreconditions() {
    Context ctx = mActivity.getApplicationContext();
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(ctx);
    assertNotNull("DB is null", dba.getDB());
    assertTrue("Database is not open", dba.getDB().isOpen());
    Cursor result = DatabaseAdapter.getListsTable().fetch();
    assertFalse("Lists table is not empty", result.moveToFirst());
    assertEquals("Wrong number of entries", 0, result.getCount());
    result.close();
  }

  public void testVisibility() {
    TextView msg = (TextView) mActivity.findViewById(cc.co.klurige.list.R.id.lists_list_empty);
    ListView list = (ListView) mActivity.findViewById(cc.co.klurige.list.R.id.lists_list);
    assertEquals("list is not GONE", View.GONE, list.getVisibility());
    assertEquals("msg is not VISIBLE", View.VISIBLE, msg.getVisibility());
    addList("bygg");
    assertEquals("msg is not GONE", View.GONE, msg.getVisibility());
    assertEquals("list is not VISIBLE", View.VISIBLE, list.getVisibility());
  }

  public void testAdd() {
    final View addButton = mActivity.findViewById(cc.co.klurige.list.R.id.lists_add);
    TouchUtils.clickView(this, addButton);
    final Dialog diag = mActivity.getDialog();
    final View input = diag.findViewById(cc.co.klurige.list.R.id.lists_dialogue_name);

    TouchUtils.longClickView(this, input);
    sendKeys(KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_CENTER);
    sendKeys(KeyEvent.KEYCODE_B, KeyEvent.KEYCODE_Y, KeyEvent.KEYCODE_G, KeyEvent.KEYCODE_G);

    final Button okButton = (Button) diag.findViewById(android.R.id.button1);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        okButton.requestFocus();
        okButton.performClick();
      }
    });
    mInstrumentation.waitForIdleSync();

    ListView list = (ListView) mActivity.findViewById(cc.co.klurige.list.R.id.lists_list);
    assertEquals("Number of entries in list is wrong", 1, list.getCount());

    TextView t =
        (TextView) list.getChildAt(0).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    assertEquals("First entry is wrong.", "bygg", t.getText());
  }

  public void testAddProposed() {
    final View addButton = mActivity.findViewById(cc.co.klurige.list.R.id.lists_add);
    TouchUtils.clickView(this, addButton);
    final Dialog diag = mActivity.getDialog();
    final Button okButton = (Button) diag.findViewById(android.R.id.button1);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        okButton.requestFocus();
        okButton.performClick();
      }
    });
    mInstrumentation.waitForIdleSync();

    ListView list = (ListView) mActivity.findViewById(cc.co.klurige.list.R.id.lists_list);
    assertEquals("Number of entries in list is wrong", 1, list.getCount());

    TextView t =
        (TextView) list.getChildAt(0).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    String name = new String(SimpleDateFormat.getDateInstance().format(new Date()));
    assertEquals("First entry is wrong.", name, t.getText());
  }

  public void testAddProposedSecond() {
    addList(null);
    final View addButton = mActivity.findViewById(cc.co.klurige.list.R.id.lists_add);
    TouchUtils.clickView(this, addButton);
    final Dialog diag = mActivity.getDialog();
    final Button okButton = (Button) diag.findViewById(android.R.id.button1);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        okButton.requestFocus();
        okButton.performClick();
      }
    });
    mInstrumentation.waitForIdleSync();

    ListView list = (ListView) mActivity.findViewById(cc.co.klurige.list.R.id.lists_list);
    assertEquals("Number of entries in list is wrong", 2, list.getCount());

    TextView t =
        (TextView) list.getChildAt(0).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    String name = new String(SimpleDateFormat.getDateInstance().format(new Date()));
    assertEquals("First entry is wrong.", name, t.getText());
    t = (TextView) list.getChildAt(1).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    assertEquals("Second entry is wrong.", name + "-1", t.getText());
  }

  public void testAddProposedThird() {
    addList(null);
    addList(null);
    final View addButton = mActivity.findViewById(cc.co.klurige.list.R.id.lists_add);
    TouchUtils.clickView(this, addButton);
    final Dialog diag = mActivity.getDialog();
    final Button okButton = (Button) diag.findViewById(android.R.id.button1);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        okButton.requestFocus();
        okButton.performClick();
      }
    });
    mInstrumentation.waitForIdleSync();

    ListView list = (ListView) mActivity.findViewById(cc.co.klurige.list.R.id.lists_list);
    assertEquals("Number of entries in list is wrong", 3, list.getCount());

    TextView t =
        (TextView) list.getChildAt(0).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    String name = new String(SimpleDateFormat.getDateInstance().format(new Date()));
    assertEquals("First entry is wrong.", name, t.getText());
    t = (TextView) list.getChildAt(1).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    assertEquals("Second entry is wrong.", name + "-1", t.getText());
    t = (TextView) list.getChildAt(2).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    assertEquals("Third entry is wrong.", name + "-2", t.getText());
  }

  public void testAddDeleted() {
    addList("bygg");
    deleteList("bygg");

    final View addButton = mActivity.findViewById(cc.co.klurige.list.R.id.lists_add);
    TouchUtils.clickView(this, addButton);
    final Dialog diag = mActivity.getDialog();
    final View input = diag.findViewById(cc.co.klurige.list.R.id.lists_dialogue_name);

    TouchUtils.longClickView(this, input);
    sendKeys(KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER);
    sendKeys(KeyEvent.KEYCODE_B, KeyEvent.KEYCODE_Y, KeyEvent.KEYCODE_G, KeyEvent.KEYCODE_G);

    final Button okButton = (Button) diag.findViewById(android.R.id.button1);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        okButton.requestFocus();
        okButton.performClick();
      }
    });
    mInstrumentation.waitForIdleSync();

    ListView list = (ListView) mActivity.findViewById(cc.co.klurige.list.R.id.lists_list);
    assertEquals("Number of entries in list is wrong", 1, list.getCount());

    TextView t =
        (TextView) list.getChildAt(0).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    assertEquals("First entry is wrong.", "bygg", t.getText());
  }

  public void testAddExisting() {
    addList("bygg");

    final View addButton = mActivity.findViewById(cc.co.klurige.list.R.id.lists_add);
    TouchUtils.clickView(this, addButton);
    final Dialog diag = mActivity.getDialog();
    final View input = diag.findViewById(cc.co.klurige.list.R.id.lists_dialogue_name);

    TouchUtils.longClickView(this, input);
    sendKeys(KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER);
    sendKeys(KeyEvent.KEYCODE_B, KeyEvent.KEYCODE_Y, KeyEvent.KEYCODE_G, KeyEvent.KEYCODE_G);

    final Button okButton = (Button) diag.findViewById(android.R.id.button1);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        okButton.requestFocus();
        okButton.performClick();
      }
    });
    mInstrumentation.waitForIdleSync();
    // Toast should have been visible.

    ListView list = (ListView) mActivity.findViewById(cc.co.klurige.list.R.id.lists_list);
    assertEquals("Number of entries in list is wrong", 1, list.getCount());

    TextView t =
        (TextView) list.getChildAt(0).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    assertEquals("First entry is wrong.", "bygg", t.getText());
  }

  public void testEdit() {
    addList("bgg");
    ListView list = (ListView) mActivity.findViewById(cc.co.klurige.list.R.id.lists_list);
    TextView t =
        (TextView) list.getChildAt(0).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    TouchUtils.longClickView(this, t);
    sendKeys(KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_CENTER);

    final Dialog diag = mActivity.getDialog();
    final View input = diag.findViewById(cc.co.klurige.list.R.id.lists_dialogue_name);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        input.requestFocus();
      }
    });
    mInstrumentation.waitForIdleSync();
    sendKeys("3*DPAD_LEFT");
    sendKeys(KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_Y);

    final Button okButton = (Button) diag.findViewById(android.R.id.button1);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        okButton.requestFocus();
        okButton.performClick();
      }
    });
    mInstrumentation.waitForIdleSync();

    assertEquals("Number of entries in list is wrong", 1, list.getCount());

    t = (TextView) list.getChildAt(0).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    assertEquals("First entry should be edited.", "bygg", t.getText());
  }

  public void testEditCancel() {
    addList("bgg");
    ListView list = (ListView) mActivity.findViewById(cc.co.klurige.list.R.id.lists_list);
    TextView t =
        (TextView) list.getChildAt(0).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    TouchUtils.longClickView(this, t);
    sendKeys(KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_CENTER);

    final Dialog diag = mActivity.getDialog();
    final View input = diag.findViewById(cc.co.klurige.list.R.id.lists_dialogue_name);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        input.requestFocus();
      }
    });
    mInstrumentation.waitForIdleSync();
    sendKeys(KeyEvent.KEYCODE_E);

    final Button cancelButton = (Button) diag.findViewById(android.R.id.button3);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        cancelButton.requestFocus();
        cancelButton.performClick();
      }
    });
    mInstrumentation.waitForIdleSync();

    assertEquals("Number of entries in list is wrong", 1, list.getCount());

    t = (TextView) list.getChildAt(0).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    assertEquals("First entry should be unedited.", "bgg", t.getText());
  }

  public void testEditNoChange() {
    addList("bgg");
    ListView list = (ListView) mActivity.findViewById(cc.co.klurige.list.R.id.lists_list);
    TextView t =
        (TextView) list.getChildAt(0).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    TouchUtils.longClickView(this, t);
    sendKeys(KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_CENTER);

    final Dialog diag = mActivity.getDialog();
    final View input = diag.findViewById(cc.co.klurige.list.R.id.lists_dialogue_name);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        input.requestFocus();
      }
    });
    mInstrumentation.waitForIdleSync();

    final Button okButton = (Button) diag.findViewById(android.R.id.button1);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        okButton.requestFocus();
        okButton.performClick();
      }
    });
    mInstrumentation.waitForIdleSync();

    assertEquals("Number of entries in list is wrong", 1, list.getCount());

    t = (TextView) list.getChildAt(0).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    assertEquals("First entry should be unchanged.", "bgg", t.getText());
  }

  public void testRemove() {
    addList("bygg");
    ListView list = (ListView) mActivity.findViewById(cc.co.klurige.list.R.id.lists_list);
    TextView t =
        (TextView) list.getChildAt(0).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    TouchUtils.longClickView(this, t);
    sendKeys(KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_CENTER);

    final Dialog diag = mActivity.getDialog();
    final Button okButton = (Button) diag.findViewById(android.R.id.button1);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        okButton.requestFocus();
        okButton.performClick();
      }
    });
    mInstrumentation.waitForIdleSync();

    assertEquals("Number of entries in list is wrong", 0, list.getCount());
    TextView msg = (TextView) mActivity.findViewById(cc.co.klurige.list.R.id.lists_list_empty);
    assertEquals("list is not GONE", View.GONE, list.getVisibility());
    assertEquals("msg is not VISIBLE", View.VISIBLE, msg.getVisibility());

  }

  public void testMenuRemoveCancel() {
    addList("bygg");
    ListView list = (ListView) mActivity.findViewById(cc.co.klurige.list.R.id.lists_list);
    TextView t =
        (TextView) list.getChildAt(0).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    TouchUtils.longClickView(this, t);
    sendKeys(KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_CENTER);

    final Dialog diag = mActivity.getDialog();
    final Button cancelButton = (Button) diag.findViewById(android.R.id.button3);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        cancelButton.requestFocus();
        cancelButton.performClick();
      }
    });
    mInstrumentation.waitForIdleSync();

    assertEquals("Number of entries in list is wrong", 1, list.getCount());

    t = (TextView) list.getChildAt(0).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    assertEquals("First entry should be bygg.", "bygg", t.getText());
  }

  public void testMenuEditCategories() {
    sendKeys(KeyEvent.KEYCODE_MENU);
    sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);

    CategoriesActivity c = CategoriesActivity.getContext();
    assertTrue("CategoriesActivity window out of focus.", c.hasWindowFocus());

    sendKeys(KeyEvent.KEYCODE_BACK);
    c = CategoriesActivity.getContext();
    if (c != null) {
      assertTrue("CategoriesActivity should be finishing.", c.isFinishing());
    } else {
      assertNull("CategoriesActivity should no longer exist.", c);
    }
  }

  public void testMenuEditUnits() {
    sendKeys(KeyEvent.KEYCODE_MENU);
    sendKeys(KeyEvent.KEYCODE_DPAD_RIGHT);
    sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);

    UnitsActivity c = UnitsActivity.getContext();
    assertTrue("UnitsActivity window out of focus.", c.hasWindowFocus());

    sendKeys(KeyEvent.KEYCODE_BACK);
    c = UnitsActivity.getContext();
    if (c != null) {
      assertTrue("UnitsActivity should be finishing.", c.isFinishing());
    } else {
      assertNull("UnitsActivity should no longer exist.", c);
    }
  }

  public void testAddToList() {
    addList("bygg");
    ListView list = (ListView) mActivity.findViewById(cc.co.klurige.list.R.id.lists_list);
    TextView t =
        (TextView) list.getChildAt(0).findViewById(cc.co.klurige.list.R.id.lists_row_name);
    TouchUtils.clickView(this, t);

    TicklistActivity c = TicklistActivity.getContext();
    assertTrue("TicklistActivity window out of focus.", c.hasWindowFocus());

    sendKeys(KeyEvent.KEYCODE_BACK);
    c = TicklistActivity.getContext();
    if (c != null) {
      assertTrue("TicklistActivity should be finishing.", c.isFinishing());
    } else {
      assertNull("TicklistActivity should no longer exist.", c);
    }
  }

  private void addList(final String str) {
    final View addButton = mActivity.findViewById(cc.co.klurige.list.R.id.lists_add);
    TouchUtils.clickView(this, addButton);
    final Dialog diag = mActivity.getDialog();
    final EditText input =
        (EditText) diag.findViewById(cc.co.klurige.list.R.id.lists_dialogue_name);
    final Button okButton = (Button) diag.findViewById(android.R.id.button1);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (str != null) {
          input.setText(str);
        }
        okButton.requestFocus();
        okButton.performClick();
      }
    });
    mInstrumentation.waitForIdleSync();
  }

  private void deleteList(final String str) {
    ListView list = (ListView) mActivity.findViewById(cc.co.klurige.list.R.id.lists_list);
    TextView t;
    int child = -1;
    for (int i = 0; i < list.getChildCount(); i++) {
      t = (TextView) list.getChildAt(i).findViewById(cc.co.klurige.list.R.id.lists_row_name);
      if (str.contentEquals(t.getText())) {
        child = i;
      }
    }
    if (child >= 0) {
      t =
          (TextView) list.getChildAt(child).findViewById(
              cc.co.klurige.list.R.id.lists_row_name);
      TouchUtils.longClickView(this, t);
      sendKeys(KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_CENTER);

      final Dialog diag = mActivity.getDialog();
      final Button okButton = (Button) diag.findViewById(android.R.id.button1);
      mActivity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          okButton.requestFocus();
          okButton.performClick();
        }
      });
      mInstrumentation.waitForIdleSync();
    }
  }
}
