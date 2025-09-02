#!/bin/bash

# API模块发布脚本
echo "=== Car Model Service API 发布脚本 ==="

# 检查是否在scripts目录或项目根目录
if [ -f "pom.xml" ]; then
    # 在项目根目录
    PROJECT_ROOT="."
elif [ -f "../pom.xml" ]; then
    # 在scripts目录
    PROJECT_ROOT=".."
    cd ..
else
    echo "❌ 错误: 请在项目根目录或scripts目录执行此脚本"
    exit 1
fi

# 检查GitHub Token
if [ -z "$GITHUB_TOKEN" ]; then
    echo "❌ 错误: 请设置GITHUB_TOKEN环境变量"
    echo ""
    echo "📋 创建GitHub Token步骤:"
    echo "   1. 访问: https://github.com/settings/tokens"
    echo "   2. 点击 'Generate new token (classic)'"
    echo "   3. 选择权限: write:packages, read:packages, repo"
    echo "   4. 设置环境变量: export GITHUB_TOKEN=your_token"
    echo "   5. 设置用户名: export GITHUB_ACTOR=JerryHorton"
    exit 1
fi

# 检查GitHub Actor
if [ -z "$GITHUB_ACTOR" ]; then
    echo "⚠️  GITHUB_ACTOR未设置，使用git配置"
    export GITHUB_ACTOR=$(git config user.name)
    if [ -z "$GITHUB_ACTOR" ]; then
        export GITHUB_ACTOR="JerryHorton"
    fi
    echo "设置GITHUB_ACTOR为: $GITHUB_ACTOR"
fi

# 设置版本号
VERSION=${1:-"1.0-SNAPSHOT"}
echo "📦 发布版本: $VERSION"
echo "✅ GitHub认证信息:"
echo "   - GITHUB_ACTOR: $GITHUB_ACTOR"
echo "   - GITHUB_TOKEN: ${GITHUB_TOKEN:0:10}..."

# 更新整个项目的版本号（因为API模块继承父版本）
echo "🔄 更新项目版本号..."
mvn versions:set -DnewVersion=$VERSION -q
mvn versions:commit -q

# 清理并编译
echo "🧹 清理项目..."
mvn clean -pl car-model-service-types,car-model-service-api

echo "🔨 编译types模块..."
mvn compile -pl car-model-service-types -DskipTests

echo "🔨 编译API模块..."
mvn compile -pl car-model-service-api -DskipTests

# 配置Maven settings
echo "⚙️  配置Maven认证..."
mkdir -p ~/.m2
cat > ~/.m2/settings.xml << EOF
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
          http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>github</id>
      <username>\${env.GITHUB_ACTOR}</username>
      <password>\${env.GITHUB_TOKEN}</password>
    </server>
  </servers>
</settings>
EOF

# 发布到GitHub Packages
echo "🚀 发布API模块到GitHub Packages..."
export GITHUB_ACTOR=${GITHUB_ACTOR:-$(git config user.name)}

mvn deploy -pl car-model-service-api -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ API模块发布成功!"
    echo ""
    echo "📋 使用说明:"
    echo "   1. 在其他项目中添加依赖:"
    echo "      <dependency>"
    echo "          <groupId>cn.cug.sxy</groupId>"
    echo "          <artifactId>car-model-service-api</artifactId>"
    echo "          <version>$VERSION</version>"
    echo "      </dependency>"
    echo ""
    echo "   2. 配置GitHub Packages仓库:"
    echo "      <repository>"
    echo "          <id>github</id>"
    echo "          <url>https://maven.pkg.github.com/JerryHorton/car-model-service</url>"
    echo "      </repository>"
    echo ""
    echo "   3. 查看详细使用文档:"
    echo "      cat car-model-service-docs/API使用指南.md"
else
    echo "❌ API模块发布失败!"
    exit 1
fi

# 创建Git标签
if [ "$VERSION" != "1.0-SNAPSHOT" ]; then
    echo "🏷️  创建Git标签..."
    git tag -a "api-v$VERSION" -m "API version $VERSION"
    git push origin "api-v$VERSION"
    echo "✅ Git标签创建成功: api-v$VERSION"
fi

echo ""
echo "🎉 发布完成!"
