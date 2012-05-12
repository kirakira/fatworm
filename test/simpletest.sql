create database db
use db
create table first(a int, b int)
create table second(a int, b int)
insert into first values(1,2)
insert into first values(2,3)
insert into first values(1,100)
insert into first (b) values(4)
select distinct a from first 
select * from first order by b 
insert into second(select * from first where a = 1)
select * from second
select avg(a) , sum(a), max(a),min(a), count(a) from first
select avg(b) , sum(b), max(b),min(b), count(b) from first
select avg(a), * from first 
select avg(a) from first where b =1
select a+b ,* from first
select * from first where a = 1 and b = 2 or a = 2
delete from first where a = 1
select * from first
update first set a = a+1, b = b-1
select * from first
update first set a = a+1 where b = 2
select * from first
select avg(a), b from first group by b

