package cc.co.klurige.list;

import java.sql.SQLException;

import android.app.Instrumentation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import cc.co.klurige.list.database.DatabaseAdapter;
import cc.co.klurige.list.database.Table.Key;

public class ItemEditActivityTests extends ActivityInstrumentationTestCase2<ItemEditActivity> {
  ItemEditActivity mActivity;
  Instrumentation  mInstrumentation;
  DatabaseAdapter  mDbAdapter;
  Context          mCtx;

  public ItemEditActivityTests() {
    super("cc.co.klurige.list", ItemEditActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mInstrumentation = getInstrumentation();
    mCtx = mInstrumentation.getTargetContext();
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    try {
      dba.delete();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    mInstrumentation = getInstrumentation();
    mCtx = mInstrumentation.getTargetContext();
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.close();
  }

  public void testActivityModeCreate() {
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.CREATE);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);
    TextView name = (TextView) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_name);
    TextView amount = (TextView) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_amount);
    TextView category =
        (TextView) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_category);
    TextView unit = (TextView) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_unit);
    CheckBox add = (CheckBox) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_add_to_list);
    assertTrue("name is not focusable.", name.isFocusable());
    assertTrue("amount is not focusable.", amount.isFocusable());
    assertTrue("category is not focusable.", category.isFocusable());
    assertTrue("unit is not focusable.", unit.isFocusable());
    assertEquals("checkbox is not visible.", View.VISIBLE, add.getVisibility());
  }

  public void testActivityModeEdit() {
    long itemId = addItem("Pryl");
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT);
    i.putExtra(Key.ID, itemId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);
    TextView name = (TextView) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_name);
    TextView amount = (TextView) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_amount);
    TextView category =
        (TextView) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_category);
    TextView unit = (TextView) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_unit);
    CheckBox add = (CheckBox) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_add_to_list);
    assertTrue("name is not focusable.", name.isFocusable());
    assertTrue("amount is not focusable.", amount.isFocusable());
    assertTrue("category is not focusable.", category.isFocusable());
    assertTrue("unit is not focusable.", unit.isFocusable());
    assertEquals("checkbox is visible.", View.INVISIBLE, add.getVisibility());
  }

  public void testActivityModeRemove() {
    long itemId = addItem("Pryl");
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.REMOVE);
    i.putExtra(Key.ID, itemId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);
    TextView name = (TextView) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_name);
    TextView amount = (TextView) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_amount);
    TextView category =
        (TextView) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_category);
    TextView unit = (TextView) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_unit);
    CheckBox add = (CheckBox) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_add_to_list);
    assertFalse("name is focusable.", name.isFocusable());
    assertFalse("amount is focusable.", amount.isFocusable());
    assertFalse("category is focusable.", category.isFocusable());
    assertFalse("unit is focusable.", unit.isFocusable());
    assertEquals("checkbox is visible.", View.INVISIBLE, add.getVisibility());
  }

  public void testActivityModeEditTick() {
    long itemId = addItem("Pryl");
    long listId = addList("Bygg");
    long tickId = addTicklistItem(itemId, listId);

    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT_TICK);
    i.putExtra(Key.ID, tickId);
    i.putExtra(Key.ITEM, itemId);
    i.putExtra(Key.LIST, listId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);
    TextView name = (TextView) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_name);
    TextView amount = (TextView) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_amount);
    TextView category =
        (TextView) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_category);
    TextView unit = (TextView) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_unit);
    CheckBox add = (CheckBox) mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_add_to_list);
    assertFalse("name is focusable.", name.isFocusable());
    assertTrue("amount is not focusable.", amount.isFocusable());
    assertTrue("category is not focusable.", category.isFocusable());
    assertTrue("unit is not focusable.", unit.isFocusable());
    assertEquals("checkbox is visible.", View.INVISIBLE, add.getVisibility());
  }

  public void testPreconditions() {
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    assertNotNull("DB is null", dba.getDB());
    assertTrue("Database is not open", dba.getDB().isOpen());
    Cursor result = DatabaseAdapter.getItemsTable().fetch();
    assertFalse("Items table is not empty", result.moveToFirst());
    result.close();
  }

  public void testAddItemPlusList() {
    long listId = addList("bygg");
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.CREATE);
    i.putExtra(Key.LIST, listId);
    this.setActivityIntent(i);
    mActivity = getActivity();

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_S,
        KeyEvent.KEYCODE_P,
        KeyEvent.KEYCODE_I,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor result = DatabaseAdapter.getItemsTable().fetch();
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.UNIT));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.AMOUNT));
    assertEquals("column is missing.", 3, result.getColumnIndexOrThrow(Key.CATEGORY));
    assertEquals("column is missing.", 4, result.getColumnIndexOrThrow(Key.NAME));
    assertEquals("column is missing.", 5, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 1, result.getLong(0));
    assertEquals("Unit is wrong", 1, result.getLong(1));
    assertEquals("Amount is wrong", (float) 1.0, result.getFloat(2));
    assertEquals("Category is wrong", 1, result.getLong(3));
    assertEquals("Name is wrong", "Spik", result.getString(4));
    assertEquals("Status is wrong", 0, result.getInt(5));
    assertFalse("Wrong number of entries fetched.", result.moveToNext());
    result.close();
    result = DatabaseAdapter.getTicklistsTable().fetch();
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.ITEM));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.LIST));
    assertEquals("column is missing.", 3, result.getColumnIndexOrThrow(Key.AMOUNT));
    assertEquals("column is missing.", 4, result.getColumnIndexOrThrow(Key.UNIT));
    assertEquals("column is missing.", 5, result.getColumnIndexOrThrow(Key.CATEGORY));
    assertEquals("column is missing.", 6, result.getColumnIndexOrThrow(Key.PICKED));
    assertEquals("column is missing.", 7, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 1, result.getLong(0));
    assertEquals("Item is wrong", 1, result.getLong(1));
    assertEquals("List is wrong", 1, result.getLong(2));
    assertEquals("Amount is wrong", (float) 1.0, result.getFloat(3));
    assertEquals("Unit is wrong", 1, result.getLong(4));
    assertEquals("Category is wrong", 1, result.getLong(5));
    assertEquals("Picked is wrong", 0, result.getLong(6));
    assertEquals("Status is wrong", 0, result.getInt(7));
    assertFalse("Wrong number of entries fetched.", result.moveToNext());
    result.close();
  }

  public void testAddItemNotToList() {
    long listId = addList("bygg");
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.CREATE);
    i.putExtra(Key.LIST, listId);
    this.setActivityIntent(i);
    mActivity = getActivity();

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_S,
        KeyEvent.KEYCODE_P,
        KeyEvent.KEYCODE_I,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor result = DatabaseAdapter.getItemsTable().fetch();
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.UNIT));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.AMOUNT));
    assertEquals("column is missing.", 3, result.getColumnIndexOrThrow(Key.CATEGORY));
    assertEquals("column is missing.", 4, result.getColumnIndexOrThrow(Key.NAME));
    assertEquals("column is missing.", 5, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 1, result.getLong(0));
    assertEquals("Unit is wrong", 1, result.getLong(1));
    assertEquals("Amount is wrong", (float) 1.0, result.getFloat(2));
    assertEquals("Category is wrong", 1, result.getLong(3));
    assertEquals("Name is wrong", "Spik", result.getString(4));
    assertEquals("Status is wrong", 0, result.getInt(5));
    assertFalse("Wrong number of entries fetched.", result.moveToNext());
    result.close();
    result = DatabaseAdapter.getTicklistsTable().fetch();
    assertFalse("Could move to first.", result.moveToFirst());
    result.close();
  }

  public void testAddItemCancel() {
    long listId = addList("bygg");
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.CREATE);
    i.putExtra(Key.LIST, listId);
    this.setActivityIntent(i);
    mActivity = getActivity();

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor result = DatabaseAdapter.getItemsTable().fetch();
    assertFalse("Could move to first.", result.moveToFirst());
    result.close();
  }

  public void testAddItemNewCategory() {
    long listId = addList("bygg");
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.CREATE);
    i.putExtra(Key.LIST, listId);
    this.setActivityIntent(i);
    mActivity = getActivity();

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_S,
        KeyEvent.KEYCODE_P,
        KeyEvent.KEYCODE_I,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_P,
        KeyEvent.KEYCODE_L,
        KeyEvent.KEYCODE_O,
        KeyEvent.KEYCODE_C,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor result = DatabaseAdapter.getItemsTable().fetch();
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.UNIT));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.AMOUNT));
    assertEquals("column is missing.", 3, result.getColumnIndexOrThrow(Key.CATEGORY));
    assertEquals("column is missing.", 4, result.getColumnIndexOrThrow(Key.NAME));
    assertEquals("column is missing.", 5, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 1, result.getLong(0));
    assertEquals("Unit is wrong", 1, result.getLong(1));
    assertEquals("Amount is wrong", (float) 1.0, result.getFloat(2));
    assertEquals("Category is wrong", 2, result.getLong(3));
    assertEquals("Name is wrong", "Spik", result.getString(4));
    assertEquals("Status is wrong", 0, result.getInt(5));
    assertFalse("Wrong number of entries fetched.", result.moveToNext());
    result.close();

    result = DatabaseAdapter.getCategoriesTable().fetch(2);
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.NAME));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 2, result.getLong(0));
    assertEquals("Name is wrong", "Plock", result.getString(1));
    assertEquals("Status is wrong", 0, result.getInt(2));
    result.close();

    result = DatabaseAdapter.getTicklistsTable().fetch();
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.ITEM));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.LIST));
    assertEquals("column is missing.", 3, result.getColumnIndexOrThrow(Key.AMOUNT));
    assertEquals("column is missing.", 4, result.getColumnIndexOrThrow(Key.UNIT));
    assertEquals("column is missing.", 5, result.getColumnIndexOrThrow(Key.CATEGORY));
    assertEquals("column is missing.", 6, result.getColumnIndexOrThrow(Key.PICKED));
    assertEquals("column is missing.", 7, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 1, result.getLong(0));
    assertEquals("Item is wrong", 1, result.getLong(1));
    assertEquals("List is wrong", 1, result.getLong(2));
    assertEquals("Amount is wrong", (float) 1.0, result.getFloat(3));
    assertEquals("Unit is wrong", 1, result.getLong(4));
    assertEquals("Category is wrong", 2, result.getLong(5));
    assertEquals("Picked is wrong", 0, result.getLong(6));
    assertEquals("Status is wrong", 0, result.getInt(7));
    assertFalse("Wrong number of entries fetched.", result.moveToNext());
    result.close();
  }

  public void testAddItemExistingCategory() {
    long listId = addList("bygg");
    addCategory("Plock");
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.CREATE);
    i.putExtra(Key.LIST, listId);
    this.setActivityIntent(i);
    mActivity = getActivity();

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_S,
        KeyEvent.KEYCODE_P,
        KeyEvent.KEYCODE_I,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_P,
        KeyEvent.KEYCODE_L,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    int keys2[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys2) {
      sendKeys(key);
    }

    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor result = DatabaseAdapter.getItemsTable().fetch();
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.UNIT));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.AMOUNT));
    assertEquals("column is missing.", 3, result.getColumnIndexOrThrow(Key.CATEGORY));
    assertEquals("column is missing.", 4, result.getColumnIndexOrThrow(Key.NAME));
    assertEquals("column is missing.", 5, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 1, result.getLong(0));
    assertEquals("Unit is wrong", 1, result.getLong(1));
    assertEquals("Amount is wrong", (float) 1.0, result.getFloat(2));
    assertEquals("Category is wrong", 2, result.getLong(3));
    assertEquals("Name is wrong", "Spik", result.getString(4));
    assertEquals("Status is wrong", 0, result.getInt(5));
    assertFalse("Wrong number of entries fetched.", result.moveToNext());
    result.close();

    result = DatabaseAdapter.getCategoriesTable().fetch(2);
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.NAME));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 2, result.getLong(0));
    assertEquals("Name is wrong", "Plock", result.getString(1));
    assertEquals("Status is wrong", 0, result.getInt(2));
    result.close();

    result = DatabaseAdapter.getTicklistsTable().fetch();
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.ITEM));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.LIST));
    assertEquals("column is missing.", 3, result.getColumnIndexOrThrow(Key.AMOUNT));
    assertEquals("column is missing.", 4, result.getColumnIndexOrThrow(Key.UNIT));
    assertEquals("column is missing.", 5, result.getColumnIndexOrThrow(Key.CATEGORY));
    assertEquals("column is missing.", 6, result.getColumnIndexOrThrow(Key.PICKED));
    assertEquals("column is missing.", 7, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 1, result.getLong(0));
    assertEquals("Item is wrong", 1, result.getLong(1));
    assertEquals("List is wrong", 1, result.getLong(2));
    assertEquals("Amount is wrong", (float) 1.0, result.getFloat(3));
    assertEquals("Unit is wrong", 1, result.getLong(4));
    assertEquals("Category is wrong", 2, result.getLong(5));
    assertEquals("Picked is wrong", 0, result.getLong(6));
    assertEquals("Status is wrong", 0, result.getInt(7));
    assertFalse("Wrong number of entries fetched.", result.moveToNext());
    result.close();
  }

  public void testAddItemNewCategoryCancel() {
    long listId = addList("bygg");
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.CREATE);
    i.putExtra(Key.LIST, listId);
    this.setActivityIntent(i);
    mActivity = getActivity();

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_S,
        KeyEvent.KEYCODE_P,
        KeyEvent.KEYCODE_I,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_P,
        KeyEvent.KEYCODE_L,
        KeyEvent.KEYCODE_O,
        KeyEvent.KEYCODE_C,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertFalse("Activity should be finishing.", mActivity.isFinishing());
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor result = DatabaseAdapter.getItemsTable().fetch();
    assertFalse("Could move to first.", result.moveToFirst());
    result.close();

    result = DatabaseAdapter.getCategoriesTable().fetch(2);
    assertFalse("Could move to first.", result.moveToFirst());
    result.close();

    result = DatabaseAdapter.getTicklistsTable().fetch();
    assertFalse("Could move to first.", result.moveToFirst());
    result.close();
  }

  public void testAddItemNewUnit() {
    long listId = addList("bygg");
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.CREATE);
    i.putExtra(Key.LIST, listId);
    this.setActivityIntent(i);
    mActivity = getActivity();

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_S,
        KeyEvent.KEYCODE_P,
        KeyEvent.KEYCODE_I,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_S,
        KeyEvent.KEYCODE_T,
        KeyEvent.KEYCODE_Y,
        KeyEvent.KEYCODE_C,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor result = DatabaseAdapter.getItemsTable().fetch();
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.UNIT));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.AMOUNT));
    assertEquals("column is missing.", 3, result.getColumnIndexOrThrow(Key.CATEGORY));
    assertEquals("column is missing.", 4, result.getColumnIndexOrThrow(Key.NAME));
    assertEquals("column is missing.", 5, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 1, result.getLong(0));
    assertEquals("Unit is wrong", 2, result.getLong(1));
    assertEquals("Amount is wrong", (float) 1.0, result.getFloat(2));
    assertEquals("Category is wrong", 1, result.getLong(3));
    assertEquals("Name is wrong", "Spik", result.getString(4));
    assertEquals("Status is wrong", 0, result.getInt(5));
    assertFalse("Wrong number of entries fetched.", result.moveToNext());
    result.close();

    result = DatabaseAdapter.getUnitsTable().fetch(2);
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.NAME));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 2, result.getLong(0));
    assertEquals("Name is wrong", "styck", result.getString(1));
    assertEquals("Status is wrong", 0, result.getInt(2));
    result.close();

    result = DatabaseAdapter.getTicklistsTable().fetch();
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.ITEM));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.LIST));
    assertEquals("column is missing.", 3, result.getColumnIndexOrThrow(Key.AMOUNT));
    assertEquals("column is missing.", 4, result.getColumnIndexOrThrow(Key.UNIT));
    assertEquals("column is missing.", 5, result.getColumnIndexOrThrow(Key.CATEGORY));
    assertEquals("column is missing.", 6, result.getColumnIndexOrThrow(Key.PICKED));
    assertEquals("column is missing.", 7, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 1, result.getLong(0));
    assertEquals("Item is wrong", 1, result.getLong(1));
    assertEquals("List is wrong", 1, result.getLong(2));
    assertEquals("Amount is wrong", (float) 1.0, result.getFloat(3));
    assertEquals("Unit is wrong", 2, result.getLong(4));
    assertEquals("Category is wrong", 1, result.getLong(5));
    assertEquals("Picked is wrong", 0, result.getLong(6));
    assertEquals("Status is wrong", 0, result.getInt(7));
    assertFalse("Wrong number of entries fetched.", result.moveToNext());
    result.close();
  }

  public void testAddItemExistingUnit() {
    long listId = addList("bygg");
    addUnit("styck");
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.CREATE);
    i.putExtra(Key.LIST, listId);
    this.setActivityIntent(i);
    mActivity = getActivity();

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_S,
        KeyEvent.KEYCODE_P,
        KeyEvent.KEYCODE_I,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_S,
        KeyEvent.KEYCODE_T,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    int keys2[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys2) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor result = DatabaseAdapter.getItemsTable().fetch();
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.UNIT));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.AMOUNT));
    assertEquals("column is missing.", 3, result.getColumnIndexOrThrow(Key.CATEGORY));
    assertEquals("column is missing.", 4, result.getColumnIndexOrThrow(Key.NAME));
    assertEquals("column is missing.", 5, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 1, result.getLong(0));
    assertEquals("Unit is wrong", 2, result.getLong(1));
    assertEquals("Amount is wrong", (float) 1.0, result.getFloat(2));
    assertEquals("Category is wrong", 1, result.getLong(3));
    assertEquals("Name is wrong", "Spik", result.getString(4));
    assertEquals("Status is wrong", 0, result.getInt(5));
    assertFalse("Wrong number of entries fetched.", result.moveToNext());
    result.close();

    result = DatabaseAdapter.getUnitsTable().fetch(2);
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.NAME));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 2, result.getLong(0));
    assertEquals("Name is wrong", "styck", result.getString(1));
    assertEquals("Status is wrong", 0, result.getInt(2));
    result.close();

    result = DatabaseAdapter.getTicklistsTable().fetch();
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.ITEM));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.LIST));
    assertEquals("column is missing.", 3, result.getColumnIndexOrThrow(Key.AMOUNT));
    assertEquals("column is missing.", 4, result.getColumnIndexOrThrow(Key.UNIT));
    assertEquals("column is missing.", 5, result.getColumnIndexOrThrow(Key.CATEGORY));
    assertEquals("column is missing.", 6, result.getColumnIndexOrThrow(Key.PICKED));
    assertEquals("column is missing.", 7, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 1, result.getLong(0));
    assertEquals("Item is wrong", 1, result.getLong(1));
    assertEquals("List is wrong", 1, result.getLong(2));
    assertEquals("Amount is wrong", (float) 1.0, result.getFloat(3));
    assertEquals("Unit is wrong", 2, result.getLong(4));
    assertEquals("Category is wrong", 1, result.getLong(5));
    assertEquals("Picked is wrong", 0, result.getLong(6));
    assertEquals("Status is wrong", 0, result.getInt(7));
    assertFalse("Wrong number of entries fetched.", result.moveToNext());
    result.close();
  }

  public void testAddItemNewUnitCancel() {
    long listId = addList("bygg");
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.CREATE);
    i.putExtra(Key.LIST, listId);
    this.setActivityIntent(i);
    mActivity = getActivity();

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_S,
        KeyEvent.KEYCODE_P,
        KeyEvent.KEYCODE_I,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_S,
        KeyEvent.KEYCODE_T,
        KeyEvent.KEYCODE_Y,
        KeyEvent.KEYCODE_C,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertFalse("Activity should be finishing.", mActivity.isFinishing());
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor result = DatabaseAdapter.getItemsTable().fetch();
    assertFalse("Could move to first.", result.moveToFirst());
    result.close();

    result = DatabaseAdapter.getUnitsTable().fetch(2);
    assertFalse("Could move to first.", result.moveToFirst());
    result.close();

    result = DatabaseAdapter.getTicklistsTable().fetch();
    assertFalse("Could move to first.", result.moveToFirst());
    result.close();
  }

  public void testAddItemNewUnitNewCategory() {
    long listId = addList("bygg");
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.CREATE);
    i.putExtra(Key.LIST, listId);
    this.setActivityIntent(i);
    mActivity = getActivity();

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_S,
        KeyEvent.KEYCODE_P,
        KeyEvent.KEYCODE_I,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_S,
        KeyEvent.KEYCODE_T,
        KeyEvent.KEYCODE_Y,
        KeyEvent.KEYCODE_C,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_P,
        KeyEvent.KEYCODE_L,
        KeyEvent.KEYCODE_O,
        KeyEvent.KEYCODE_C,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor result = DatabaseAdapter.getItemsTable().fetch();
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.UNIT));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.AMOUNT));
    assertEquals("column is missing.", 3, result.getColumnIndexOrThrow(Key.CATEGORY));
    assertEquals("column is missing.", 4, result.getColumnIndexOrThrow(Key.NAME));
    assertEquals("column is missing.", 5, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 1, result.getLong(0));
    assertEquals("Unit is wrong", 2, result.getLong(1));
    assertEquals("Amount is wrong", (float) 1.0, result.getFloat(2));
    assertEquals("Category is wrong", 2, result.getLong(3));
    assertEquals("Name is wrong", "Spik", result.getString(4));
    assertEquals("Status is wrong", 0, result.getInt(5));
    assertFalse("Wrong number of entries fetched.", result.moveToNext());
    result.close();

    result = DatabaseAdapter.getUnitsTable().fetch(2);
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.NAME));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 2, result.getLong(0));
    assertEquals("Name is wrong", "styck", result.getString(1));
    assertEquals("Status is wrong", 0, result.getInt(2));
    result.close();

    result = DatabaseAdapter.getCategoriesTable().fetch(2);
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.NAME));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 2, result.getLong(0));
    assertEquals("Name is wrong", "Plock", result.getString(1));
    assertEquals("Status is wrong", 0, result.getInt(2));
    result.close();

    result = DatabaseAdapter.getTicklistsTable().fetch();
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.ITEM));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.LIST));
    assertEquals("column is missing.", 3, result.getColumnIndexOrThrow(Key.AMOUNT));
    assertEquals("column is missing.", 4, result.getColumnIndexOrThrow(Key.UNIT));
    assertEquals("column is missing.", 5, result.getColumnIndexOrThrow(Key.CATEGORY));
    assertEquals("column is missing.", 6, result.getColumnIndexOrThrow(Key.PICKED));
    assertEquals("column is missing.", 7, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 1, result.getLong(0));
    assertEquals("Item is wrong", 1, result.getLong(1));
    assertEquals("List is wrong", 1, result.getLong(2));
    assertEquals("Amount is wrong", (float) 1.0, result.getFloat(3));
    assertEquals("Unit is wrong", 2, result.getLong(4));
    assertEquals("Category is wrong", 2, result.getLong(5));
    assertEquals("Picked is wrong", 0, result.getLong(6));
    assertEquals("Status is wrong", 0, result.getInt(7));
    assertFalse("Wrong number of entries fetched.", result.moveToNext());
    result.close();
  }

  public void testAddItemNewUnitCancelNewCategory() {
    long listId = addList("bygg");
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.CREATE);
    i.putExtra(Key.LIST, listId);
    this.setActivityIntent(i);
    mActivity = getActivity();

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_S,
        KeyEvent.KEYCODE_P,
        KeyEvent.KEYCODE_I,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_S,
        KeyEvent.KEYCODE_T,
        KeyEvent.KEYCODE_Y,
        KeyEvent.KEYCODE_C,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_P,
        KeyEvent.KEYCODE_L,
        KeyEvent.KEYCODE_O,
        KeyEvent.KEYCODE_C,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertFalse("Activity should be finishing.", mActivity.isFinishing());
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor result = DatabaseAdapter.getItemsTable().fetch();
    assertFalse("Could move to first.", result.moveToFirst());
    result.close();

    result = DatabaseAdapter.getUnitsTable().fetch(2);
    assertFalse("Could move to first.", result.moveToFirst());
    result.close();

    result = DatabaseAdapter.getTicklistsTable().fetch();
    assertFalse("Could move to first.", result.moveToFirst());
    result.close();
  }

  public void testAddItemAmount() {
    long listId = addList("bygg");
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.CREATE);
    i.putExtra(Key.LIST, listId);
    this.setActivityIntent(i);
    mActivity = getActivity();

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_S,
        KeyEvent.KEYCODE_P,
        KeyEvent.KEYCODE_I,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_1,
        KeyEvent.KEYCODE_0,
        KeyEvent.KEYCODE_0,
        KeyEvent.KEYCODE_0,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor result = DatabaseAdapter.getItemsTable().fetch();
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.UNIT));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.AMOUNT));
    assertEquals("column is missing.", 3, result.getColumnIndexOrThrow(Key.CATEGORY));
    assertEquals("column is missing.", 4, result.getColumnIndexOrThrow(Key.NAME));
    assertEquals("column is missing.", 5, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 1, result.getLong(0));
    assertEquals("Unit is wrong", 1, result.getLong(1));
    assertEquals("Amount is wrong", (float) 10001.0, result.getFloat(2));
    assertEquals("Category is wrong", 1, result.getLong(3));
    assertEquals("Name is wrong", "Spik", result.getString(4));
    assertEquals("Status is wrong", 0, result.getInt(5));
    assertFalse("Wrong number of entries fetched.", result.moveToNext());
    result.close();
    result = DatabaseAdapter.getTicklistsTable().fetch();
    assertTrue("Could not move to first.", result.moveToFirst());
    assertEquals("column is missing.", 0, result.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, result.getColumnIndexOrThrow(Key.ITEM));
    assertEquals("column is missing.", 2, result.getColumnIndexOrThrow(Key.LIST));
    assertEquals("column is missing.", 3, result.getColumnIndexOrThrow(Key.AMOUNT));
    assertEquals("column is missing.", 4, result.getColumnIndexOrThrow(Key.UNIT));
    assertEquals("column is missing.", 5, result.getColumnIndexOrThrow(Key.CATEGORY));
    assertEquals("column is missing.", 6, result.getColumnIndexOrThrow(Key.PICKED));
    assertEquals("column is missing.", 7, result.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 1, result.getLong(0));
    assertEquals("Item is wrong", 1, result.getLong(1));
    assertEquals("List is wrong", 1, result.getLong(2));
    assertEquals("Amount is wrong", (float) 10001.0, result.getFloat(3));
    assertEquals("Unit is wrong", 1, result.getLong(4));
    assertEquals("Category is wrong", 1, result.getLong(5));
    assertEquals("Picked is wrong", 0, result.getLong(6));
    assertEquals("Status is wrong", 0, result.getInt(7));
    assertFalse("Wrong number of entries fetched.", result.moveToNext());
    result.close();
  }

  public void testAddItemExistingName() {
    long createId = addItem("Spik");
    long listId = addList("bygg");
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getItemsTable().fetch();
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Wrong number of entries", 1, added.getCount());
    assertEquals("Id is wrong", createId, added.getLong(0));
    long itemId = added.getLong(0);
    long itemUnit = added.getLong(1);
    float itemAmount = added.getFloat(2);
    long itemCategory = added.getLong(3);
    int itemStatus = added.getInt(5);
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.CREATE);
    i.putExtra(Key.LIST, listId);
    this.setActivityIntent(i);
    mActivity = getActivity();

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_S,
        KeyEvent.KEYCODE_P,
        KeyEvent.KEYCODE_I,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    added.requery();
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Wrong number of entries", 1, added.getCount());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    assertEquals("Unit is wrong", itemUnit, added.getLong(1));
    assertEquals("Amount is wrong", itemAmount, added.getFloat(2));
    assertEquals("Category is wrong", itemCategory, added.getLong(3));
    assertEquals("Name is wrong", "Spik", added.getString(4));
    assertEquals("Status is wrong", itemStatus, added.getInt(5));
    assertFalse("Wrong number of entries fetched.", added.moveToNext());
    added.close();
  }

  public void testEditItemName() {
    long itemId = addItem("Prl");
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getItemsTable().fetch(itemId);
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    long itemUnit = added.getLong(1);
    float itemAmount = added.getFloat(2);
    long itemCategory = added.getLong(3);
    int itemStatus = added.getInt(5);
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT);
    i.putExtra(Key.ID, itemId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_Y,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    assertTrue("Could not requery.", added.requery());
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    assertEquals("Unit is wrong", itemUnit, added.getLong(1));
    assertEquals("Amount is wrong", itemAmount, added.getFloat(2));
    assertEquals("Category is wrong", itemCategory, added.getLong(3));
    assertEquals("Name is wrong", "Pryl", added.getString(4));
    assertEquals("Status is wrong", itemStatus, added.getLong(5));
    added.close();
  }

  public void testEditItemNewUnit() {
    long itemId = addItem("Pryl");
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getItemsTable().fetch(itemId);
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    float itemAmount = added.getFloat(2);
    long itemCategory = added.getLong(3);
    int itemStatus = added.getInt(5);
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT);
    i.putExtra(Key.ID, itemId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_G,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    assertTrue("Could not requery.", added.requery());
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    assertEquals("Unit is wrong", 2, added.getLong(1));
    assertEquals("Amount is wrong", itemAmount, added.getFloat(2));
    assertEquals("Category is wrong", itemCategory, added.getLong(3));
    assertEquals("Name is wrong", "Pryl", added.getString(4));
    assertEquals("Status is wrong", itemStatus, added.getLong(5));
    added.close();
  }

  public void testEditItemNewCategory() {
    long itemId = addItem("Pryl");
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getItemsTable().fetch(itemId);
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    long itemUnit = added.getLong(1);
    float itemAmount = added.getFloat(2);
    int itemStatus = added.getInt(5);
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT);
    i.putExtra(Key.ID, itemId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_V,
        KeyEvent.KEYCODE_E,
        KeyEvent.KEYCODE_R,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_T,
        KeyEvent.KEYCODE_Y,
        KeyEvent.KEYCODE_G,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    assertTrue("Could not requery.", added.requery());
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    assertEquals("Unit is wrong", itemUnit, added.getLong(1));
    assertEquals("Amount is wrong", itemAmount, added.getFloat(2));
    assertEquals("Category is wrong", 2, added.getLong(3));
    assertEquals("Name is wrong", "Pryl", added.getString(4));
    assertEquals("Status is wrong", itemStatus, added.getLong(5));
    added.close();
  }

  public void testEditItemExistingUnit() {
    long itemId = addItem("Pryl");
    long unitid = addUnit("liter");
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getItemsTable().fetch(itemId);
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    float itemAmount = added.getFloat(2);
    long itemCategory = added.getLong(3);
    int itemStatus = added.getInt(5);
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT);
    i.putExtra(Key.ID, itemId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_L,
        KeyEvent.KEYCODE_I,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    int keys2[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys2) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    assertTrue("Could not requery.", added.requery());
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    assertEquals("Unit is wrong", unitid, added.getLong(1));
    assertEquals("Amount is wrong", itemAmount, added.getFloat(2));
    assertEquals("Category is wrong", itemCategory, added.getLong(3));
    assertEquals("Name is wrong", "Pryl", added.getString(4));
    assertEquals("Status is wrong", itemStatus, added.getLong(5));
    added.close();
  }

  public void testEditItemExistingCategory() {
    long listId = addList("bygg");
    long itemId = addItem("Pryl");
    long categoryId = addCategory("Plock");
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getItemsTable().fetch(itemId);
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    long itemUnit = added.getLong(1);
    float itemAmount = added.getFloat(2);
    int itemStatus = added.getInt(5);
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT);
    i.putExtra(Key.LIST, listId);
    i.putExtra(Key.ID, itemId);
    this.setActivityIntent(i);
    mActivity = getActivity();

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_P,
        KeyEvent.KEYCODE_L,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    int keys2[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys2) {
      sendKeys(key);
    }

    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    assertTrue("Could not requery.", added.requery());
    assertTrue("Could not move to first.", added.moveToFirst());
    assertEquals("column is missing.", 0, added.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, added.getColumnIndexOrThrow(Key.UNIT));
    assertEquals("column is missing.", 2, added.getColumnIndexOrThrow(Key.AMOUNT));
    assertEquals("column is missing.", 3, added.getColumnIndexOrThrow(Key.CATEGORY));
    assertEquals("column is missing.", 4, added.getColumnIndexOrThrow(Key.NAME));
    assertEquals("column is missing.", 5, added.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", itemId, added.getLong(0));
    assertEquals("Unit is wrong", itemUnit, added.getLong(1));
    assertEquals("Amount is wrong", itemAmount, added.getFloat(2));
    assertEquals("Category is wrong", categoryId, added.getLong(3));
    assertEquals("Name is wrong", "Pryl", added.getString(4));
    assertEquals("Status is wrong", itemStatus, added.getInt(5));
    assertFalse("Wrong number of entries fetched.", added.moveToNext());
    added.close();

    added = DatabaseAdapter.getCategoriesTable().fetch(2);
    assertTrue("Could not move to first.", added.moveToFirst());
    assertEquals("column is missing.", 0, added.getColumnIndexOrThrow(Key.ID));
    assertEquals("column is missing.", 1, added.getColumnIndexOrThrow(Key.NAME));
    assertEquals("column is missing.", 2, added.getColumnIndexOrThrow(Key.STATUS));
    assertEquals("Id is wrong", 2, added.getLong(0));
    assertEquals("Name is wrong", "Plock", added.getString(1));
    assertEquals("Status is wrong", 0, added.getInt(2));
    added.close();
  }

  public void testEditItemNewCategoryCancel() {
    long itemId = addItem("Pryl");
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getItemsTable().fetch(itemId);
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    long itemUnit = added.getLong(1);
    long itemCategory = added.getLong(3);
    float itemAmount = added.getFloat(2);
    int itemStatus = added.getInt(5);
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT);
    i.putExtra(Key.ID, itemId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_V,
        KeyEvent.KEYCODE_E,
        KeyEvent.KEYCODE_R,
        KeyEvent.KEYCODE_K,
        KeyEvent.KEYCODE_T,
        KeyEvent.KEYCODE_Y,
        KeyEvent.KEYCODE_G,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    assertTrue("Could not requery.", added.requery());
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    assertEquals("Unit is wrong", itemUnit, added.getLong(1));
    assertEquals("Amount is wrong", itemAmount, added.getFloat(2));
    assertEquals("Category is wrong", itemCategory, added.getLong(3));
    assertEquals("Name is wrong", "Pryl", added.getString(4));
    assertEquals("Status is wrong", itemStatus, added.getLong(5));
    added.close();
  }

  public void testEditItemAmount() {
    long itemId = addItem("Pryl");
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getItemsTable().fetch(itemId);
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    long itemUnit = added.getLong(1);
    long itemCategory = added.getLong(3);
    int itemStatus = added.getInt(5);
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT);
    i.putExtra(Key.ID, itemId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_DEL,
        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_DEL,
        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_DEL,
        KeyEvent.KEYCODE_7,
        KeyEvent.KEYCODE_PERIOD,
        KeyEvent.KEYCODE_2,
        KeyEvent.KEYCODE_5,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    assertTrue("Could not requery.", added.requery());
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    assertEquals("Unit is wrong", itemUnit, added.getLong(1));
    assertEquals("Amount is wrong", (float) 7.25, added.getFloat(2));
    assertEquals("Category is wrong", itemCategory, added.getLong(3));
    assertEquals("Name is wrong", "Pryl", added.getString(4));
    assertEquals("Status is wrong", itemStatus, added.getLong(5));
    added.close();
  }

  public void testEditItemCancel() {
    long itemId = addItem("Pryl");
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getItemsTable().fetch(itemId);
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    long itemUnit = added.getLong(1);
    float itemAmount = added.getFloat(2);
    long itemCategory = added.getLong(3);
    int itemStatus = added.getInt(5);
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT);
    i.putExtra(Key.ID, itemId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);

    // Name should have focus.
    int keys[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    assertTrue("Could not requery.", added.requery());
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Cursor contains wrong number of entries.", 1, added.getCount());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    assertEquals("Unit is wrong", itemUnit, added.getLong(1));
    assertEquals("Amount is wrong", itemAmount, added.getFloat(2));
    assertEquals("Category is wrong", itemCategory, added.getLong(3));
    assertEquals("Name is wrong", "Pryl", added.getString(4));
    assertEquals("Status is wrong", itemStatus, added.getLong(5));
    added.close();
  }

  public void testEditItemAddToList() {
    long itemId = addItem("Pryl");
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getItemsTable().fetch(itemId);
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT);
    i.putExtra(Key.ID, itemId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);

    // Add To List not visible.
    View add = mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_add_to_list);
    assertEquals("Add to list is visible", View.INVISIBLE, add.getVisibility());
  }

  public void testRemoveItem() {
    long itemId = addItem("Pryl");
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getItemsTable().fetch();
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.REMOVE);
    i.putExtra(Key.ID, itemId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);
    final View btn = mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_confirm);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        btn.requestFocus();
        btn.performClick();
      }
    });
    mInstrumentation.waitForIdleSync();
    assertTrue("Could not requery", added.requery());
    assertFalse("Cursor not empty", added.moveToFirst());
    added.close();
  }

  public void testRemoveItemCancel() {
    long itemId = addItem("Pryl");
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getItemsTable().fetch(itemId);
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    long itemUnit = added.getLong(1);
    float itemAmount = added.getFloat(2);
    long itemCategory = added.getLong(3);
    int itemStatus = added.getInt(5);
    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT);
    i.putExtra(Key.ID, itemId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);
    final View btn = mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_cancel);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        btn.requestFocus();
        btn.performClick();
      }
    });
    mInstrumentation.waitForIdleSync();
    assertTrue("Could not requery", added.requery());
    assertTrue("Cursor empty", added.moveToFirst());
    assertEquals("Cursor contains wrong number of entries.", 1, added.getCount());
    assertEquals("Id is wrong", itemId, added.getLong(0));
    assertEquals("Unit is wrong", itemUnit, added.getLong(1));
    assertEquals("Amount is wrong", itemAmount, added.getFloat(2));
    assertEquals("Category is wrong", itemCategory, added.getLong(3));
    assertEquals("Name is wrong", "Pryl", added.getString(4));
    assertEquals("Status is wrong", itemStatus, added.getLong(5));
    added.close();
  }

  public void testEditTicklistItemName() {
    long itemId = addItem("Pryl");
    long listId = addList("bygg");
    long ticklistId = addTicklistItem(itemId, listId);
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getTicklistsTable().fetch(itemId);
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", ticklistId, added.getLong(0));
    assertEquals("Item id is wrong", itemId, added.getLong(1));
    assertEquals("List id is wrong", listId, added.getLong(2));

    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT_TICK);
    i.putExtra(Key.ID, ticklistId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);
    View name = mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_name);
    assertFalse("Name is focusable", name.isFocusable());
  }

  public void testEditTicklistItemNewUnit() {
    long itemId = addItem("Pryl");
    long listId = addList("bygg");
    long ticklistId = addTicklistItem(itemId, listId);
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getTicklistsTable().fetch(itemId);
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", ticklistId, added.getLong(0));
    assertEquals("Item id is wrong", itemId, added.getLong(1));
    assertEquals("List id is wrong", listId, added.getLong(2));
    float amount = added.getFloat(3);
    long unitId = added.getLong(4);
    long catId = added.getLong(5);
    long picked = added.getLong(6);
    int itemStatus = added.getInt(7);

    assertEquals("Entry got the wrong unit", 1, unitId);

    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT_TICK);
    i.putExtra(Key.ID, ticklistId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);

    // Amount should have focus. Name is not focusable.
    int keys[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_L,
        KeyEvent.KEYCODE_I,
        KeyEvent.KEYCODE_T,
        KeyEvent.KEYCODE_E,
        KeyEvent.KEYCODE_R,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    assertTrue("Could not requery.", added.requery());
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Ticklist Id is wrong", ticklistId, added.getLong(0));
    assertEquals("Item id is wrong", itemId, added.getLong(1));
    assertEquals("List id is wrong", listId, added.getLong(2));
    assertEquals("Amount is wrong", amount, added.getFloat(3));
    assertEquals("Unit is wrong", 2, added.getLong(4));
    assertEquals("Category is wrong", catId, added.getLong(5));
    assertEquals("Picked is wrong", picked, added.getLong(6));
    assertEquals("Status is wrong", itemStatus, added.getLong(7));
    added.close();
  }

  public void testEditTicklistItemNewCategory() {
    long itemId = addItem("Pryl");
    long listId = addList("bygg");
    long ticklistId = addTicklistItem(itemId, listId);
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getTicklistsTable().fetch(itemId);
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", ticklistId, added.getLong(0));
    assertEquals("Item id is wrong", itemId, added.getLong(1));
    assertEquals("List id is wrong", listId, added.getLong(2));
    float amount = added.getFloat(3);
    long unitId = added.getLong(4);
    long catId = added.getLong(5);
    long picked = added.getLong(6);
    int itemStatus = added.getInt(7);

    assertEquals("Entry got the wrong category", 1, catId);

    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT_TICK);
    i.putExtra(Key.ID, ticklistId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);

    // Amount should have focus. Name is not focusable.
    int keys[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_A,
        KeyEvent.KEYCODE_N,
        KeyEvent.KEYCODE_N,
        KeyEvent.KEYCODE_A,
        KeyEvent.KEYCODE_T,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    assertTrue("Could not requery.", added.requery());
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Ticklist Id is wrong", ticklistId, added.getLong(0));
    assertEquals("Item id is wrong", itemId, added.getLong(1));
    assertEquals("List id is wrong", listId, added.getLong(2));
    assertEquals("Amount is wrong", amount, added.getFloat(3));
    assertEquals("Unit is wrong", unitId, added.getLong(4));
    assertEquals("Category is wrong", 2, added.getLong(5));
    assertEquals("Picked is wrong", picked, added.getLong(6));
    assertEquals("Status is wrong", itemStatus, added.getLong(7));
    added.close();
  }

  public void testEditTicklistItemExistingUnit() {
    long itemId = addItem("Pryl");
    long listId = addList("bygg");
    long ticklistId = addTicklistItem(itemId, listId);
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getTicklistsTable().fetch(itemId);
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", ticklistId, added.getLong(0));
    assertEquals("Item id is wrong", itemId, added.getLong(1));
    assertEquals("List id is wrong", listId, added.getLong(2));
    float amount = added.getFloat(3);
    long unitId = added.getLong(4);
    long catId = added.getLong(5);
    long picked = added.getLong(6);
    int itemStatus = added.getInt(7);

    assertEquals("Entry got the wrong unit", 1, unitId);

    unitId = addUnit("liter");

    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT_TICK);
    i.putExtra(Key.ID, ticklistId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);

    // Amount should have focus. Name is not focusable.
    int keys[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_L,
        KeyEvent.KEYCODE_I,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    int keys2[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys2) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    assertTrue("Could not requery.", added.requery());
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Ticklist Id is wrong", ticklistId, added.getLong(0));
    assertEquals("Item id is wrong", itemId, added.getLong(1));
    assertEquals("List id is wrong", listId, added.getLong(2));
    assertEquals("Amount is wrong", amount, added.getFloat(3));
    assertEquals("Unit is wrong", unitId, added.getLong(4));
    assertEquals("Category is wrong", catId, added.getLong(5));
    assertEquals("Picked is wrong", picked, added.getLong(6));
    assertEquals("Status is wrong", itemStatus, added.getLong(7));
    added.close();
  }

  public void testEditTicklistItemExistingCategory() {
    long itemId = addItem("Pryl");
    long listId = addList("bygg");
    long ticklistId = addTicklistItem(itemId, listId);
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getTicklistsTable().fetch(itemId);
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", ticklistId, added.getLong(0));
    assertEquals("Item id is wrong", itemId, added.getLong(1));
    assertEquals("List id is wrong", listId, added.getLong(2));
    float amount = added.getFloat(3);
    long unitId = added.getLong(4);
    long catId = added.getLong(5);
    long picked = added.getLong(6);
    int itemStatus = added.getInt(7);

    assertEquals("Entry got the wrong category", 1, catId);

    catId = addCategory("annat");

    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT_TICK);
    i.putExtra(Key.ID, ticklistId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);

    // Amount should have focus. Name is not focusable.
    int keys[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_A,
        KeyEvent.KEYCODE_N,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    int keys2[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys2) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    assertTrue("Could not requery.", added.requery());
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Ticklist Id is wrong", ticklistId, added.getLong(0));
    assertEquals("Item id is wrong", itemId, added.getLong(1));
    assertEquals("List id is wrong", listId, added.getLong(2));
    assertEquals("Amount is wrong", amount, added.getFloat(3));
    assertEquals("Unit is wrong", unitId, added.getLong(4));
    assertEquals("Category is wrong", catId, added.getLong(5));
    assertEquals("Picked is wrong", picked, added.getLong(6));
    assertEquals("Status is wrong", itemStatus, added.getLong(7));
    added.close();
  }

  public void testEditTicklistItemAmount() {
    long itemId = addItem("Pryl");
    long listId = addList("bygg");
    long ticklistId = addTicklistItem(itemId, listId);
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getTicklistsTable().fetch(itemId);
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", ticklistId, added.getLong(0));
    assertEquals("Item id is wrong", itemId, added.getLong(1));
    assertEquals("List id is wrong", listId, added.getLong(2));
    long unitId = added.getLong(4);
    long catId = added.getLong(5);
    long picked = added.getLong(6);
    int itemStatus = added.getInt(7);

    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT_TICK);
    i.putExtra(Key.ID, ticklistId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);

    // Amount should have focus. Name is not focusable.
    int keys[] = {
        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_DEL,
        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_DEL,
        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_DEL,
        KeyEvent.KEYCODE_3,
        KeyEvent.KEYCODE_PERIOD,
        KeyEvent.KEYCODE_1,
        KeyEvent.KEYCODE_4,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    assertTrue("Could not requery.", added.requery());
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Ticklist Id is wrong", ticklistId, added.getLong(0));
    assertEquals("Item id is wrong", itemId, added.getLong(1));
    assertEquals("List id is wrong", listId, added.getLong(2));
    assertEquals("Amount is wrong", (float) 3.14, added.getFloat(3));
    assertEquals("Unit is wrong", unitId, added.getLong(4));
    assertEquals("Category is wrong", catId, added.getLong(5));
    assertEquals("Picked is wrong", picked, added.getLong(6));
    assertEquals("Status is wrong", itemStatus, added.getLong(7));
    added.close();
  }

  public void testEditTicklistItemCancel() {
    long itemId = addItem("Pryl");
    long listId = addList("bygg");
    long ticklistId = addTicklistItem(itemId, listId);
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getTicklistsTable().fetch(itemId);
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", ticklistId, added.getLong(0));
    assertEquals("Item id is wrong", itemId, added.getLong(1));
    assertEquals("List id is wrong", listId, added.getLong(2));
    float amount = added.getFloat(3);
    long unitId = added.getLong(4);
    long catId = added.getLong(5);
    long picked = added.getLong(6);
    int itemStatus = added.getInt(7);

    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT_TICK);
    i.putExtra(Key.ID, ticklistId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);

    // Amount should have focus. Name is not focusable.
    int keys[] = {
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_DPAD_CENTER,
    };
    for (int key : keys) {
      sendKeys(key);
    }
    assertTrue("Activity should be finishing.", mActivity.isFinishing());
    assertTrue("Could not requery.", added.requery());
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Ticklist Id is wrong", ticklistId, added.getLong(0));
    assertEquals("Item id is wrong", itemId, added.getLong(1));
    assertEquals("List id is wrong", listId, added.getLong(2));
    assertEquals("Amount is wrong", amount, added.getFloat(3));
    assertEquals("Unit is wrong", unitId, added.getLong(4));
    assertEquals("Category is wrong", catId, added.getLong(5));
    assertEquals("Picked is wrong", picked, added.getLong(6));
    assertEquals("Status is wrong", itemStatus, added.getLong(7));
    added.close();
  }

  public void testEditTicklistItemAddToList() {
    long itemId = addItem("Pryl");
    long listId = addList("bygg");
    long ticklistId = addTicklistItem(itemId, listId);
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    Cursor added = DatabaseAdapter.getTicklistsTable().fetch(itemId);
    assertTrue("Cursor is empty", added.moveToFirst());
    assertEquals("Id is wrong", ticklistId, added.getLong(0));
    assertEquals("Item id is wrong", itemId, added.getLong(1));
    assertEquals("List id is wrong", listId, added.getLong(2));

    Intent i = new Intent(mCtx, ItemEditActivity.class);
    i.putExtra(ItemEditActivity.MODE, ItemEditActivity.EDIT_TICK);
    i.putExtra(Key.ID, ticklistId);
    this.setActivityIntent(i);
    mActivity = getActivity();
    assertNotNull("Could not find activity", mActivity);
    View addCheckBox = mActivity.findViewById(cc.co.klurige.list.R.id.item_edit_add_to_list);
    assertEquals("Checkbox is visible", View.INVISIBLE, addCheckBox.getVisibility());
  }

  private long addItem(String name) {
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    ContentValues args = new ContentValues();
    args.put(Key.NAME, name);
    args.put(Key.CATEGORY, 1);
    args.put(Key.UNIT, 1);
    return DatabaseAdapter.getItemsTable().create(args);
  }

  private long addList(String name) {
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    ContentValues args = new ContentValues();
    args.put(Key.NAME, name);
    return DatabaseAdapter.getListsTable().create(args);
  }

  private long addCategory(String name) {
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    ContentValues args = new ContentValues();
    args.put(Key.NAME, name);
    return DatabaseAdapter.getCategoriesTable().create(args);
  }

  private long addUnit(String name) {
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    ContentValues args = new ContentValues();
    args.put(Key.NAME, name);
    return DatabaseAdapter.getUnitsTable().create(args);
  }

  private long addTicklistItem(long itemId, long listId) {
    DatabaseAdapter dba = DatabaseAdapter.getDatabaseAdapter(mCtx);
    dba.open();
    ContentValues args = new ContentValues();
    args.put(Key.ITEM, itemId);
    args.put(Key.LIST, listId);
    return DatabaseAdapter.getTicklistsTable().create(args);
  }
}
