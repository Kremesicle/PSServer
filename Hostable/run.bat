@echo off
title Runescape #317
java -Xmx3096m -cp bin;deps/poi.jar;deps/mysql.jar;deps/mssql-jdbc-8.2.2.jre8.jar;deps/mina.jar;deps/slf4j.jar;deps/slf4j-nop.jar;deps/jython.jar;log4j-1.2.15.jar; server.Server
pause