#!/bin/sh
MYSELF="$(cd "$(dirname "$0")" && pwd -P)/$(basename "$0")"
JAVA=java
if [ -n "$JAVA_HOME" -a -x "$JAVA_HOME/bin/java" ]; then
	JAVA="$JAVA_HOME/bin/java"
fi
if [ -z "$INSTALL_PATH" ]; then
	if [ "`expr substr "$(uname -s)" 1 6`" = "Darwin" ]; then
		INSTALL_PATH="-DINSTALL_PATH=/Applications/Alice 3.app/Contents/Resources/app"
	elif [ "`expr substr "$(uname -s)" 1 5`" = "Linux" ]; then
		INSTALL_PATH="-DINSTALL_PATH=$HOME/Alice3"
	fi
else
	INSTALL_PATH="-DINSTALL_PATH=$INSTALL_PATH"
fi
if [ -z "$INSTALL_PATH" ]; then
	exec "$JAVA" -jar "$MYSELF" "$@"
else
	exec "$JAVA" "$INSTALL_PATH" -jar "$MYSELF" "$@"
fi
exit 1