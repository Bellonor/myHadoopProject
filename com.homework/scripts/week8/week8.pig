processed= load '/user/hdfs/week8/teacher/in/processed' as (category:chararray,doc:chararray);
test = sample processed 0.2;

--测试用
processed= load '/user/mypig/lefta.txt' as (a1:chararray,a2:chararray,a3:chararray);
test = sample processed 0.2;

tfull= JOIN processed BY (a1,a2,a3) LEFT OUTER,test BY (a1,a2,a3);
t8=  filter tfull BY test::a1 is null;
train= foreach t8 generate processed::a1 as a1,processed::a2 as a2,processed::a3 as a3;
store test into '/user/mypig/test';
store train into '/user/mypig/train';
--正式
processed= load '/user/hdfs/week8/teacher/in/processed' as (category:chararray,doc:chararray);
test = sample processed 0.2;
tfull= JOIN processed BY (category,doc) LEFT OUTER,test BY (category,doc);
t8=  filter tfull BY test::category is null;
train= foreach t8 generate processed::category as category,processed::doc as doc;
store test into '/user/hdfs/week8/teacher/test';
store train into '/user/hdfs/week8/teacher/train';
--统计
test_count = foreach ( group test by category) generate group,COUNT(test.category);
DUMP test_count;
train_count = foreach ( group train by category) generate group,COUNT(train.category);
DUMP train_count;
--mahout-0.6,0.8不行
mahout trainclassifier -i /user/hdfs/week8/teacher/train -o /user/hdfs/week8/model-bayes -type bayes -ng 1 -source hdfs

mahout testclassifier -d /user/hdfs/week8/teacher/test -m /user/hdfs/week8/model-bayes -type bayes -ng 1 -source hdfs -method mapreduce 




