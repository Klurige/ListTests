package cc.co.klurige.list;

import android.test.ActivityInstrumentationTestCase2;
import cc.co.klurige.list.UnitsActivity;

/**
 * Tests to verify and validate the Units Activity
 * 
 * @author roni
 * 
 *         The activity shows a list of units. Nothing should happen on
 *         short-click, but long-click should show a context menu, where it
 *         should be possible to edit or remove unit.
 *         There is also an "Add"-button at the bottom of the screen. Pressing
 *         this will let the user add a new unit.
 *         The activity is closed through the back-button.
 *         There is no input state, and no return value.
 * 
 *         Add:
 *         Pop up a dialogue where the user can type in a new unit, followed
 *         by ok or enter.
 *         If the unit already exists, the user will be notified through a
 *         message that needs to be acknowledged.
 * 
 *         Edit:
 *         Pop up a dialogue similar to the one for adding, where the user can
 *         edit the name, which is the only editable field. Upon pressing ok or
 *         enter, the updated name will be checked for duplicates.
 *         If the unit already exists, the user will be notified through a
 *         message that needs to be acknowledged.
 * 
 *         Remove:
 *         Pop up a dialogue similar to the one for adding, where the user can
 *         only see the name, but not altering it. Pressing ok or enter will
 *         delete the unit from the database.
 **/

public class UnitsActivityTests extends ActivityInstrumentationTestCase2<UnitsActivity> {
  UnitsActivity mActivity;

  public UnitsActivityTests() {
    super("cc.co.klurige.list", UnitsActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mActivity = getActivity();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testAddButton() {
    assertFalse("Not implemented.", true);
  }

  public void testMenuEdit() {
    assertFalse("Not implemented.", true);
  }

  public void testMenuRemove() {
    assertFalse("Not implemented.", true);
  }
}
