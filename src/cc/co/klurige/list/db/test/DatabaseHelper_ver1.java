package cc.co.klurige.list.db.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cc.co.klurige.list.database.Categories;
import cc.co.klurige.list.database.Items;
import cc.co.klurige.list.database.Lists;
import cc.co.klurige.list.database.Table.Key;
import cc.co.klurige.list.database.Table.Status;
import cc.co.klurige.list.database.Ticklists;
import cc.co.klurige.list.database.Units;

/**
 * @author roni
 *         Wrapper helper class for database creation.
 */
final class DatabaseHelper_ver1 extends SQLiteOpenHelper {
  /**
   * Log Tag.
   */
  private static final String TAG              = DatabaseHelper_ver1.class.getName();

  /**
   * Database name. Same as file name.
   */
  private static final String DATABASE_NAME    = "ticklist_db";

  /**
   * Database version.
   * Remember to update onUpgrade() whenever this is incremented.
   */
  private static final int    DATABASE_VERSION = 1;

  /**
   * Constructor for helper.
   * Calls the super with database name and version constants.
   * 
   * @param context same as super.
   */
  DatabaseHelper_ver1(final Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(final SQLiteDatabase db) {
    db.execSQL(Categories.TABLE_CREATE);
    db.execSQL(Units.TABLE_CREATE);
    db.execSQL(Lists.TABLE_CREATE);
    db.execSQL(Items.TABLE_CREATE);
    db.execSQL(Ticklists.TABLE_CREATE);
    //
    // Add empty unit and category. These are often used as defaults.
    final ContentValues args = new ContentValues();
    args.put(Key.NAME, "");
    args.put(Key.STATUS, Status.NORMAL);
    db.insertOrThrow(Categories.TABLE_NAME, null, args);
    db.insertOrThrow(Units.TABLE_NAME, null, args);
  }

  @Override
  public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
      final int newVersion) {
    switch (oldVersion) {
      case 1:
      case 2:
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + Ticklists.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Items.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Lists.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Units.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Categories.TABLE_NAME);
        onCreate(db);
      default:
        Log.e(TAG, "Upgrading database from " + oldVersion + " to "
            + newVersion + " is not implemented");
        assert (false);
        break;
    }
  }
}
