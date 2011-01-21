package cc.co.klurige.list.test;

import java.io.File;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.test.mock.MockContext;
import android.util.Log;

public class DatabaseContext extends MockContext {
  private final static String  TAG   = "DatabaseContext";
  private final static boolean DEBUG = false;
  private final Object         mSync = new Object();
  private File                 mDatabasesDir;
  PackageInfo                  mPackageInfo;
  private File                 mFilesDir;

  @Override
  public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory) {
    File dir = getDatabasesDir();
    if (!dir.isDirectory() && dir.mkdir()) {
      FileUtils.setPermissions(dir.getPath(),
            FileUtils.S_IRWXU | FileUtils.S_IRWXG | FileUtils.S_IXOTH,
            -1, -1);
    }

    File f = makeFilename(dir, name);
    SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(f, factory);
    setFilePermissionsFromMode(f.getPath(), mode, 0);
    return db;
  }

  private File getDatabasesDir() {
    synchronized (mSync) {
      if (mDatabasesDir == null) {
        mDatabasesDir = new File(getFilesDir(), "databases");
      }
      if (mDatabasesDir.getPath().equals("databases")) {
        mDatabasesDir = new File("/data/system");
      }
      return mDatabasesDir;
    }
  }

  private File makeFilename(File base, String name) {
    if (name.indexOf(File.separatorChar) < 0) {
      return new File(base, name);
    }
    throw new IllegalArgumentException(
        "File " + name + " contains a path separator");
  }

  private static void setFilePermissionsFromMode(String name, int mode,
      int extraPermissions) {
    int perms = FileUtils.S_IRUSR | FileUtils.S_IWUSR
        | FileUtils.S_IRGRP | FileUtils.S_IWGRP
        | extraPermissions;
    if ((mode & MODE_WORLD_READABLE) != 0) {
      perms |= FileUtils.S_IROTH;
    }
    if ((mode & MODE_WORLD_WRITEABLE) != 0) {
      perms |= FileUtils.S_IWOTH;
    }
    if (DEBUG) {
      Log.i(TAG, "File " + name + ": mode=0x" + Integer.toHexString(mode)
            + ", perms=0x" + Integer.toHexString(perms));
    }
    FileUtils.setPermissions(name, perms, -1, -1);
  }

  @Override
  public File getFilesDir() {
    synchronized (mSync) {
      if (mFilesDir == null) {
        ApplicationInfo ai = getApplicationInfo();
        String path = ai.dataDir;
        mFilesDir = new File(path, "files");
      }
      if (!mFilesDir.exists()) {
        if (!mFilesDir.mkdirs()) {
          Log.w(TAG, "Unable to create files directory");
          return null;
        }
        FileUtils.setPermissions(
                  mFilesDir.getPath(),
                  FileUtils.S_IRWXU | FileUtils.S_IRWXG | FileUtils.S_IXOTH,
                  -1, -1);
      }
      return mFilesDir;
    }
  }
}
