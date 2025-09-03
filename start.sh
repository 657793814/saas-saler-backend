#!/bin/bash
# 设置脚本执行权限
chmod 0755 /app/start.sh
chmod +x /app/start.sh

# 打印启动信息
echo "=========================================="
echo "Starting SaaS Saler Admin Application"
echo "=========================================="

# 打印环境变量
echo "Environment Variables:"
echo "------------------------------------------"
echo "JAVA_OPTS: $JAVA_OPTS"
echo "SPRING_PROFILES_ACTIVE: $SPRING_PROFILES_ACTIVE"
echo "SERVER_PORT: $SERVER_PORT"
echo "Working Directory: $(pwd)"
echo "Java Version:"
java -version
echo "------------------------------------------"

# 打印步骤信息
echo "Step 1: Checking application jar file..."
if [ -f "/app/app.jar" ]; then
    echo "  ✓ app.jar found"
    ls -lh /app/app.jar
else
    echo "  ✗ app.jar not found!"
    exit 1
fi

echo "Step 2: Preparing to start application..."
echo "  - Setting file encoding to UTF-8"
echo "  - Using JVM options: $JAVA_OPTS"
echo "  - Active profile: $SPRING_PROFILES_ACTIVE"
echo "  - Server port: $SERVER_PORT"

echo "Step 3: Starting application..."
echo "Executing command: java $JAVA_OPTS -Dfile.encoding=UTF-8 -jar /app/app.jar --spring.profiles.active=$SPRING_PROFILES_ACTIVE --server.port=$SERVER_PORT"
echo "------------------------------------------"

# 执行Java应用
exec java $JAVA_OPTS -Dfile.encoding=UTF-8 -jar /app/app.jar \
  --spring.profiles.active=$SPRING_PROFILES_ACTIVE \
  --server.port=$SERVER_PORT
