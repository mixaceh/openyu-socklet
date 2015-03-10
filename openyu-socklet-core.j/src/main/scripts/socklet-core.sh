_CLASSPATH=.

for i in *.jar
do
    _CLASSPATH=$_CLASSPATH:"$i"
done

for i in ../*.jar
do
    _CLASSPATH=$_CLASSPATH:"$i"
done

for i in ../lib/*.jar
do
    _CLASSPATH=$_CLASSPATH:"$i"
done

for i in ../../*.jar
do
    _CLASSPATH=$_CLASSPATH:"$i"
done

for i in ../../lib/*.jar
do
    _CLASSPATH=$_CLASSPATH:"$i"
done

for i in ../../../*.jar
do
    _CLASSPATH=$_CLASSPATH:"$i"
done

for i in ../../../lib/*.jar
do
    _CLASSPATH=$_CLASSPATH:"$i"
done

for i in ../../../lib-provided/*.jar
do
    _CLASSPATH=$_CLASSPATH:"$i"
done

# convert the unix path to windows
if [ "$OSTYPE" = "cygwin32" ] || [ "$OSTYPE" = "cygwin" ] ; then
    _CLASSPATH=`cygpath --path --windows "$_CLASSPATH"`
fi

java -Xmn96m -Xms192m -Xmx192m -Xss128k -XX:PermSize=64m -cp $_CLASSPATH -XX:+UseParNewGC -Djava.net.preferIPv4Stack=true -verbose:gc org.openyu.socklet.core.App %1 %2 %3 %4 %5 %6 %7 %8 %9

_CLASSPATH=.
