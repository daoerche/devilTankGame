const net = require('net');

var async = require('async');

var connPool = require('./connPool.js');

const server = net.createServer((c) => {
    // 'connection' listener
    console.log('client connected');
    c.setEncoding('utf8');

    var newRanks = "";

    c.on('data', (chunk) => {
        newRanks += chunk;

        newRanks = newRanks.trim().replace(/[\r\n\u0000-\u0010]/g, '');
        var newRanksArr = newRanks.split(" ");

        console.log(newRanksArr);

        var newName = newRanksArr[0];
        var newScore = newRanksArr[1];

        var pool = connPool();
        pool.getConnection(function (err, conn) {
            if(err) {
                console.log(err.message);
                return;
            }

            var SelectSql = 'SELECT name,score FROM tanksRank WHERE name=?;';
            var param = [newName];

            conn.query(SelectSql, param, function (err, rs) {
                if(err) {
                    conn.release();
                    console.log(err.message);
                    return;
                }

                async.series({
                    one: function(done) {
                        if(rs.length<=0) {
                            SelectSql = 'INSERT tanksRank(name,score,createTime) VALUES(?, ?, NOW());';
                            param = [newName, newScore];

                            conn.query(SelectSql, param, (err, rs) => {
                                if(err) {
                                    done(err, 'oneEnd');
                                    return;
                                }
                                done(null, 'oneEnd');
                            })
                        }else {
                            done(null, 'oneEnd');
                        }
                    },

                    two: function (done) {
                        if(rs.length == 1) {
                            var res = rs[0];
                            if(res.score < Number(newScore)) {
                                SelectSql = 'UPDATE tanksRank SET score=?, createTime=NOW() WHERE name=?;';
                                param = [newScore, newName];

                                conn.query(SelectSql, param, (err, rs) => {
                                    if(err) {
                                        done(err, 'twoEnd');
                                        return;
                                    }
                                    done(null, 'twoEnd');
                                })
                            }else {
                                done(null, 'twoEnd');
                            }
                        }else {
                            done(null, 'twoEnd');
                        }
                    },

                    end: function (done) {
                        SelectSql = 'SELECT name FROM tanksRank ORDER BY score DESC;';
                        param = [];

                        conn.query(SelectSql, param, (err, rs) => {
                            if(err) {
                                done(err, 'endEnd');
                                return;
                            }


                            let rank = 0;
                            for(let i=0; i<rs.length; i++) {
                                if(rs[i].name == newName) {
                                    rank = i+1;
                                    break;
                                }
                            }
                            done(null, rank);
                        })
                    }

                }, function (err, rs) {
                    if(err) {
                        console.log(err.message);
                        conn.release();
                        return;
                    }

                    var userRank = rs.end;

                    SelectSql = 'SELECT name, score FROM tanksRank ORDER BY score DESC LIMIT 7;';
                    param = [];

                    conn.query(SelectSql, param, (err, rs) => {
                        if(err) {
                            console.log(err.message);
                            conn.release();
                            return;
                        }

                        conn.release();

                        var resultRanks = "";
                        for(let i=0; i<rs.length; i++) {
                            resultRanks += rs[i].name + " ";
                            resultRanks += rs[i].score + " ";
                            resultRanks += (i+1) + " ";
                        }

                        resultRanks += newName + " ";
                        resultRanks += newScore + " ";
                        resultRanks += userRank;
                        resultRanks = resultRanks.trim();

                        console.log(resultRanks);

                        c.write(resultRanks);
                        c.end();
                    })
                });

            });
        });
    });

    c.on('end', () => {
        console.log("client end.")
    });

    c.on('error', (err) => {
        console.log(err.message);
    });
});

server.on('error', (err) => {
    console.log(err.message);
});

server.listen(3000, () => {
    console.log('server in 3000...');
});