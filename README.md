# This is a Tank Game.
----------
### The client code is java. The server code is nodejs.
---------
### Usage:
1. If you have java environment and node.js environment and MySQL.(otherwise please download those)
2. Connect mysql code in ./TanksServer/connPool.js . You can make changes to your configuration. And create TABLE sql in ./TanksServer/mysql.txt .
3. Then if you finish please cd ./TanksServer && npm install && node Tank.js (To start server)
4. Make changes ./src/TankClient.java in line 19 to your server address and port.
5. running ./src/TankClient.java

### Others:
<p>
Of course, you can make your server in java.<br/> 
The socket data is simple string.<br/>
<string>Example:</string><br/>
<ul>
<li>
Server accept data "daoerche 86"; 
<i>(username score)</i>
</li>
<li>
Server send data "dxm 195 1 txc 189 2 baba 188 3 hhh 111 4 daoerche 76 5 123456 70 6 thq 38 7 daoerche 0 5"; 
<i>(user1 score1 1[This is Rank] user2 score2 2 ... user7 score7 7 currentUser score highestRank)</i>
</li>
</p>
<p>
The project code have a sufficient number of notes. If you want you can look over code in yourself.
</p>
</ul>

### PS:
<ul>
<li>这是大学时期在校期间做的最后一个项目了，项目内容为‘鬼畜版坦克大战’。</li>
<li>代码不值得借鉴，写的很渣，本身不会java，只是为了完成课程设计，有需要应付作业的尽管拿去，游戏内容借鉴了别人的一个基本架构，其他的完全自己扩展写的，不存在网上一模一样，放心拿。</li>
<li>这辈子怕是不会再写这么多java代码了。</li>
</ul>
