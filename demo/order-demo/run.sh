#!/usr/bin/env bash
# 直接用 javac 编译仓库里的 COLA 源码（节选子集）+ stubs + Demo 并运行。
# 为什么不用 Maven：cola-core 的 pom 把 gpg 签名绑在 verify 阶段，且私服/中央仓库不一定可达；
# 本脚本保证只要有一套 JDK 8+ 就能跑，编的是同一批真实 COLA 源码。
set -euo pipefail
cd "$(dirname "$0")"
DEMO_DIR="$(pwd)"
REPO_ROOT="$(cd ../.. && pwd)"

# --- 找到一套 JDK ---
if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
  JAVAC="$JAVA_HOME/bin/javac"; JAVA="$JAVA_HOME/bin/java"
elif command -v javac >/dev/null 2>&1; then
  JAVAC="$(command -v javac)"; JAVA="$(command -v java)"
elif [ -x "$HOME/.local/jdk8src/linux-x86/bin/javac" ]; then   # 本仓库沙箱演示环境内置的 JDK8（如有）
  JAVAC="$HOME/.local/jdk8src/linux-x86/bin/javac"; JAVA="$HOME/.local/jdk8src/linux-x86/bin/java"
else
  echo "未找到 JDK。请先安装 JDK 8+ 并设置 JAVA_HOME，或保证 javac 在 PATH 里。" >&2; exit 1
fi
echo "Using javac: $JAVAC"
env -i HOME="$HOME" PATH=/usr/bin:/bin "$JAVAC" -version 2>&1 || "$JAVAC" -version 2>&1

CORE="$REPO_ROOT/cola-framework-core/src/main/java/com/alibaba/cola"
CORE_FILES="
$CORE/boot/AbstractComponentExecutor.java
$CORE/boot/ExtensionRegister.java
$CORE/boot/EventRegister.java
$CORE/common/ApplicationContextHelper.java
$CORE/common/ColaConstant.java
$CORE/domain/Entity.java
$CORE/domain/EntityObject.java
$CORE/domain/ValueObject.java
$CORE/event/EventBus.java
$CORE/event/EventBusI.java
$CORE/event/EventHandler.java
$CORE/event/EventHandlerI.java
$CORE/event/EventHub.java
$CORE/exception/BizException.java
$CORE/exception/SysException.java
$CORE/exception/ExceptionHandlerI.java
$CORE/exception/framework/BaseException.java
$CORE/exception/framework/BasicErrorCode.java
$CORE/exception/framework/ColaException.java
$CORE/exception/framework/DefaultExceptionHandler.java
$CORE/extension/Extension.java
$CORE/extension/ExtensionCoordinate.java
$CORE/extension/ExtensionExecutor.java
$CORE/extension/ExtensionPointI.java
$CORE/extension/ExtensionRepository.java
$CORE/logger/Logger.java
$CORE/logger/LoggerFactory.java
$CORE/logger/SLFJLogger.java
$CORE/logger/SysLogger.java
"

rm -rf target
mkdir -p target/classes
{
  echo "$CORE_FILES"
  find "$REPO_ROOT/cola-framework-common/src/main/java" -name '*.java'
  find "$REPO_ROOT/cola-statemachine/src/main/java" -name '*.java'
  find "$DEMO_DIR/stubs/src/main/java" -name '*.java'
  find "$DEMO_DIR/src/main/java" -name '*.java'
} > target/sources.txt
echo "Compiling $(grep -c . target/sources.txt) java files ..."
env -i HOME="$HOME" PATH=/usr/bin:/bin "$JAVAC" -encoding UTF-8 -nowarn -d target/classes @target/sources.txt
echo "Compile OK. Running demo ..."
env -i HOME="$HOME" PATH=/usr/bin:/bin "$JAVA" -Dfile.encoding=UTF-8 -cp target/classes com.example.cola.order.start.Main
