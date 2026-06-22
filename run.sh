if [ -f "./jre/bin/java" ]; then
    ./jre/bin/java -jar SyntaxN.jar
else
    echo "Bundled JRE not found! Please ensure the jre/ folder exists."
fi