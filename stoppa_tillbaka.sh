#!/bin/bash
mv bortkopplade/src/cc/co/klurige/list/database src/cc/co/klurige/list/.
mv bortkopplade/src/cc/co/klurige/list/CategoriesActivityTests.java src/cc/co/klurige/list/.

rm -f bin
adb uninstall cc.co.klurige.list
adb uninstall cc.co.klurige.list.database



echo "Now go to Eclipse and refresh and rebuild."


