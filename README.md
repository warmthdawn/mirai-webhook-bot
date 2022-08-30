# Mirai Webhook插件

将你的机器人连接上git的webhook

### 配置说明
port 机器人开放的端口(默认8022)
rootUrl 机器人的web地址，仅用于显示(默认 http://localhost:8022)
secretKey 私钥(github/gitee)或密码(gitlab)， 留空未不设置
qq 机器人绑定通知的qq
hooks  机器人开放的钩子列表
groups 机器人启用的群
tokens github token
repos 群绑定的repo

### 命令说明
/webhook add name [Github|Gitee|Gitlab]: 开放一个webhook地址
/webhook remove name : 删除群的webhook监听
/webhook info： 查看本群监听的webhook地址

### 注意事项
1. 仅支持 push 和 tagpush 事件
2. Github需要设置ContentType为 application/json
3. Gitee 使用的不是密码而是签名密钥