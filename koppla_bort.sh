#!/bin/bash
mkdir -p bortkopplade/src/cc/co/klurige/list/
mv src/cc/co/klurige/list/database bortkopplade/src/cc/co/klurige/list/.
mv src/cc/co/klurige/list/CategoriesActivityTests.java bortkopplade/src/cc/co/klurige/list/.
mv src/cc/co/klurige/list/UnitsActivityTests.java bortkopplade/src/cc/co/klurige/list/.
mv src/cc/co/klurige/list/ListsActivityTests.java bortkopplade/src/cc/co/klurige/list/.
mv src/cc/co/klurige/list/ItemEditActivityTests.java bortkopplade/src/cc/co/klurige/list/.

rm -rf bin gen/* coverage instrumented libs
adb uninstall cc.co.klurige.list
adb uninstall cc.co.klurige.list.database

echo "Now go to Eclipse and refresh and rebuild."


