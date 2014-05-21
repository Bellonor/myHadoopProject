-- Dataguru Hadoop Course
-- Code by James 

-- Load Data
data1 = LOAD '/user/huangjun/dataguru/wiki-Vote' AS ( source, target );

data2 = LOAD '/user/huangjun/dataguru/wiki-Vote' AS ( source, target );

-- Mine the common friends
common_jnd = JOIN data1 BY target, data2 BY target;

common_prj = FOREACH common_jnd GENERATE data1::target AS common_friend, data1::source AS user, data2::source AS candidate;

common_flt = FILTER common_prj BY user != candidate;

common = FOREACH ( GROUP common_flt BY (user,candidate) ) GENERATE FLATTEN(group) AS (user,candidate), COUNT(common_flt) AS cnt;

-- Recommendation
user = FOREACH ( GROUP common BY user ) 
{
    candidate_srt = ORDER common BY cnt DESC;
    candidate_lim = LIMIT candidate_srt 5;
    GENERATE FLATTEN(candidate_lim) AS ( user, candidate, cnt );
}

STORE user INTO '/user/huangjun/dataguru/result';