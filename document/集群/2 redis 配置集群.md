环境介绍 三台主机 一台主节点 这样的构架 （还有3台作为备份）

<img src="2 redis 配置集群.assets/image-20220420202759632.png" alt="image-20220420202759632" style="zoom:50%;" />



1. 命令行安装

   ```shell
   curl -fsSL https://packages.redis.io/gpg | sudo gpg --dearmor -o /usr/share/keyrings/redis-archive-keyring.gpg
   
   echo "deb [signed-by=/usr/share/keyrings/redis-archive-keyring.gpg] https://packages.redis.io/deb $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/redis.list
   
   sudo apt-get update
   sudo apt-get install redis
   ```

   

2. 源码安装

   ~~~shell
   make && make install
   cd src
   cp redis-trib.rb /usr/local/bin
   mkdir redis_cluster
   cd ./redis_cluster
   mkdir 8000 8001 8002
   cd ..
   cp redis.conf redis_cluster/8000
   cp redis.conf redis_cluster/8001
   cp redis.conf redis_cluster/8002
   cd ./redis_cluster/8000
   ```配置文件修改
   port 8000			# 端口 8000 8001 8002
   bind localhost		# 本地ip
   damonize yes 		# redis 后台运行
   appendonly yes		# aof日志开启 每次写操作都会记录
   pidfile /var/run/redis_8000.pid # process id
   cluster-config-file nodes_8000.conf	# 集群配置 首次运行 自动生成
   cluster-node-timeout 15000 # 首次请求超时时间
   ```
   ~~~

3. 启动

   ```shell
   # redis服务器配置 3 + 3 3 台主机 3 台备份
   redis-server redis_cluster/8000/redis.conf
   redis-server redis_cluster/8001/redis.conf
   redis-server redis_cluster/8002/redis.conf
   
   redis-server redis_cluster/8000/redis.conf
   redis-server redis_cluster/8001/redis.conf
   redis-server redis_cluster/8002/redis.conf
   ```

4. 检查启动情况

   ```shell
   ps -ef | grep redis
   netstat -tnlp | grep redis
   ```

5. ruby 管理集群

   ```shell
   apt install -y ruby
   redis-trib.rb  create  --replicas  1  192.168.255.128:800   192.168.255.128:8002   192.168.255.128:8003  192.168.255.3:8004  192.168.255.3:8005  192.168.255.3:8006
   # 如果开启了ufw 记得在ufw中allow上面的端口
   # cd /etc/sysconfig/可以使用vi iptables 先查看一下 目前只是开放了 22 端口。
   iptables -A INPUT -p tcp --dport 17001 -j ACCEPT
   iptables -A INPUT -p tcp --dport 17002 -j ACCEPT
   iptables -A INPUT -p tcp --dport 17000 -j ACCEPT
   iptables -A INPUT -p tcp --dport 7001 -j ACCEPT
   iptables -A INPUT -p tcp --dport 7002 -j ACCEPT
   iptables -A INPUT -p tcp --dport 7000 -j ACCEPT
   service iptables save #保存文件
   iptables -L -n #查看哪些端口开放了
   
   -----------------------------------------------
   iptables -A INPUT -p tcp --dport 17003 -j ACCEPT
   iptables -A INPUT -p tcp --dport 17004 -j ACCEPT
   iptables -A INPUT -p tcp --dport 17005 -j ACCEPT
   iptables -A INPUT -p tcp --dport 7003 -j ACCEPT
   iptables -A INPUT -p tcp --dport 7004 -j ACCEPT
   iptables -A INPUT -p tcp --dport 7005 -j ACCEPT
   service iptables save #保存文件
   iptables -L -n #查看哪些端口开放了
   ```

   

[redis 集群](https://blog.csdn.net/YangzaiLeHeHe/article/details/93618559)