# app_slient_upgrade
//upgrade_monitor 这个modules是静默升级的sdk
第三方调用 将所有的jar统一打包成一个jar包 app-upgrade-monitor-x.x.jar
app/libs/kdxc_upgrade_monitor.jar 是由upgrade_monitor生成的，对应的在upgrade_monitor/build/intermediates/bundles/release/classes.jar 

app/libs/ 下面除了app-upgrade-monitor-x.x.jar 和 classes.jar 其余都是用到的第三方jar包

将所有jar包打包成一个jar包的命令为 ant -buildfile makeJar.xml
