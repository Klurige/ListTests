#!/bin/bash
mv bortkopplade/src/cc/co/klurige/list/* src/cc/co/klurige/list/.

rm -rf bin gen/* coverage instrumented libs
adb uninstall cc.co.klurige.list
adb uninstall cc.co.klurige.list.database



echo "Now go to Eclipse and refresh and rebuild."


