#!/bin/bash
#git init .
#git remote add origin git@github.com:doobo/OkHttpTools.git
#git add .
#git commit -m "版本更新"
#git push origin master --force
#git branch --set-upstream-to=origin/master master

#git remote add gitee git@gitee.com:doobo/OkHttpTools.git
#git push gitee master --force

#git tag -d v1.1
#git push origin :v1.1


mvn clean
mvn release:prepare
mvn release:perform

#mvn clean deploy -P release
